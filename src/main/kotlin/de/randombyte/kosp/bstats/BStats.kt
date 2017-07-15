package de.randombyte.kosp.bstats

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.inject.Inject
import de.randombyte.kosp.bstats.charts.CustomChart
import de.randombyte.kosp.config.ConfigManager
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.Task
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.net.URL
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPOutputStream
import javax.net.ssl.HttpsURLConnection

/**
 * A port of [bStats for Sponge](https://github.com/BtoBastian/bStats-Metrics/blob/master/bstats-sponge/src/main/java/org/bstats/sponge/Metrics.java).
 */
class BStats @Inject constructor(
        private val logger: Logger,
        private val plugin: PluginContainer,
        @ConfigDir(sharedRoot = true) private val configDir: Path) {

    private val bStatsConfigDir = configDir.resolve("bStats")
    private val tempFile = bStatsConfigDir.resolve("temp.txt").toFile()

    private val configManager = ConfigManager(
            bStatsConfigDir.resolve("config.conf").toConfigLoader(),
            Config::class.java,
            hyphenSeparatedKeys = false)

    private val charts = mutableListOf<CustomChart>()

    init {
        if (created) throw RuntimeException("bStats instance already created!")
        created = true

        bStatsConfigDir.toFile().mkdirs()

        if (configManager.get().enabled) {
            val baseMetricsClass = getBaseBStatsClass()
            if (baseMetricsClass == javaClass) {
                linkMetrics(this)
                startSubmitting()
            } else {
                // This class has to be linked to the baseMetricsClass
                baseMetricsClass.getMethod("linkMetrics", Any::class.java).invoke(null, this)
            }
        }
    }

    fun addCustomChart(chart: CustomChart) = charts.add(chart)

    /**
     * Attempts to get an already written bStats class. If there is none write this class name
     * to the [tempFile].
     *
     * @return the base bStats class, may be this class
     */
    private fun getBaseBStatsClass(): Class<*> {
        if (tempFile.exists()) tempFile.readLines().firstOrNull()?.let { lines ->
            if (lines.isNotEmpty()) {
                // some class name was written to the file
                try {
                    return Class.forName(lines)
                } catch (ignored: ClassNotFoundException) {
                    // Class couldn't be found because the plugin was removed or something else.
                    // The next thing the code does is writing its own class name in the
                    // file to fix the issue.
                }
            }
        }
        // No class was written by another plugin or the class couldn't be found
        tempFile.writeText(javaClass.name)
        return javaClass
    }

    /**
     * Called using Reflection.
     */
    fun getPluginData(): JsonObject = JsonObject().apply {
        addProperty("pluginName", plugin.name)
        addProperty("pluginVersion", plugin.version.orElse("unknown"))
        add("customCharts", JsonArray().apply {
            charts.mapNotNull { it.getRequestedJsonObject() }.forEach { add(it) }
        })
    }

    private fun startSubmitting() {
        Task.builder()
                .async()
                .delay(5, TimeUnit.MINUTES)
                .interval(30, TimeUnit.MINUTES)
                .execute { ->
                    Task.builder().execute { -> submitData() }.submit(plugin)
                }.submit(plugin)
    }

    private fun submitData() {
        val data = createServerData()
        data.add("plugins", collectPluginData())
        Thread { sendData(data) }.start()
    }

    private fun createServerData(): JsonObject = JsonObject().apply {
        addProperty("serverUUID", configManager.get().serverUuid.toString())

        addProperty("playerAmount", Sponge.getServer().onlinePlayers.size)
        addProperty("onlineMode", Sponge.getServer().onlineMode.toInt())
        addProperty("minecraftVersion", Sponge.getGame().platform.minecraftVersion.name)
        addProperty("spongeImplementation", Sponge.getGame().platform.implementation.name)

        addProperty("javaVersion", System.getProperty("java.version"))
        addProperty("osName", System.getProperty("os.name"))
        addProperty("osArch", System.getProperty("os.arch"))
        addProperty("osVersion", System.getProperty("os.version"))
        addProperty("coreCount", Runtime.getRuntime().availableProcessors())
    }

    private fun collectPluginData(): JsonArray = JsonArray().apply {
        knownMetricsInstances.mapNotNull {
            it.javaClass.getMethod("getPluginData").invoke(it) as? JsonObject
        }.forEach { add(it) }
    }

    companion object {
        const val B_STATS_VERSION = 1
        const private val URL = "https://bStats.org/submitData/sponge"

        private var created = false

        private val knownMetricsInstances = mutableListOf<Any>()

        /**
         * Called using Reflection.
         */
        @JvmStatic
        fun linkMetrics(metrics: Any) {
            knownMetricsInstances.add(metrics)
        }

        private fun sendData(data: JsonObject) {
            val connection = URL(URL).openConnection() as HttpsURLConnection
            val compressedData = compress(data.toString())

            with(connection) {
                requestMethod = "POST"
                addRequestProperty("Accept", "application/json")
                addRequestProperty("Connection", "close")
                addRequestProperty("Content-Encoding", "gzip")
                addRequestProperty("Content-Length", Integer.toString(compressedData.size))
                addRequestProperty("Content-Type", "application/json")
                addRequestProperty("User-Agent", "MC-Server/$B_STATS_VERSION")
            }

            connection.doOutput = true
            with(DataOutputStream(connection.outputStream)) {
                write(compressedData)
                flush()
                close()
            }

            connection.inputStream.close()
        }

        private fun compress(string: String): ByteArray {
            val outputStream = ByteArrayOutputStream()
            with(GZIPOutputStream(outputStream)) {
                write(string.toByteArray())
                close()
            }
            return outputStream.toByteArray()
        }

        private fun Path.toConfigLoader() = HoconConfigurationLoader.builder().setPath(this).build()
        private fun Boolean.toInt() = if (this) 1 else 0
    }
}