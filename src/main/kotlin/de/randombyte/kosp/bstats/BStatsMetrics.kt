package de.randombyte.kosp.bstats

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.inject.Inject
import de.randombyte.kosp.bstats.charts.CustomChart
import de.randombyte.kosp.config.ConfigManager
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.Task
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.net.URL
import java.nio.file.Path
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPOutputStream
import javax.net.ssl.HttpsURLConnection

/**
 * A port of [Metrics from bStats](https://gist.github.com/BtoBastian/53023a4ce88df29f4acadc37ddb21c54)
 */
class BStatsMetrics @Inject constructor(private val logger: Logger, private val plugin: PluginContainer,
                                        @ConfigDir(sharedRoot = true) private val configDir: Path) {

    private val bStatsConfigDir = configDir.resolve("bStats")
    private val tempFile = bStatsConfigDir.resolve("temp.txt").toFile()

    private val configManager = ConfigManager(
            bStatsConfigDir.resolve("config.conf").toConfigLoader(),
            Config::class,
            hyphenSeparatedKeys = false)

    private val charts = mutableListOf<CustomChart<*>>()

    init {
        if (created) throw RuntimeException("BStatsMetrics instance already created!")
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

    fun addCustomChart(chart: CustomChart<*>) = charts.add(chart)

    /**
     * Attempts to get an already written bStats class. If there is none write this class name
     * to the [tempFile].
     *
     * @return the base bStats class, may be this class
     */
    private fun getBaseBStatsClass(): Class<*> {
        if (tempFile.exists()) tempFile.readText().apply { if (!isEmpty()) return Class.forName(this) }
        tempFile.writeText(javaClass.name)
        return javaClass
    }

    /**
     * Called using Reflection.
     */
    fun getPluginData(): JsonObject = JsonObject().apply {
        val config = configManager.get()
        addProperty("pluginName", plugin.name)
        addProperty("pluginVersion", plugin.version.orElse("unknown"))
        add("customCharts", JsonArray().apply {
            charts.mapNotNull { it.getRequestedJsonObject(logger, config.logFailedRequests) }.forEach { add(it) }
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

        addProperty("javaVersion", System.getProperty("java.version"))
        addProperty("osName", System.getProperty("os.name"))
        addProperty("osArch", System.getProperty("os.arch"))
        addProperty("osVersion", System.getProperty("os.version"))
        addProperty("coreCount", Runtime.getRuntime().availableProcessors())
    }

    private fun collectPluginData(): JsonArray = JsonArray().apply {
        knownMetricsInstances.mapNotNull {
            val pluginData = it.javaClass.getMethod("getPluginData").invoke(it)
            if (pluginData is JsonObject) pluginData else null
        }.forEach { add(it) }
    }

    companion object {
        const val B_STATS_VERSION = 1
        const private val URL = "https://bStats.org/submitData/sponge"

        private var created = false

        private val knownMetricsInstances = mutableListOf<Any>()

        @ConfigSerializable
        class Config(
                @Setting(comment = ENABLED_COMMENT) val enabled: Boolean = true,
                @Setting val serverUuid: UUID = UUID.randomUUID(),
                @Setting val logFailedRequests: Boolean = false) {
            companion object {
                const private val ENABLED_COMMENT =
                        "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
                                "To honor their work, you should not disable it.\n" +
                                "This has nearly no effect on the server performance!\n" +
                                "Check out https://bStats.org/ to learn more :)"
            }
        }

        /**
         * Called using Reflection.
         */
        fun linkMetrics(metrics: Any) {
            knownMetricsInstances.add(metrics)
        }

        private fun sendData(data: JsonObject) {
            val connection = URL(URL).openConnection() as HttpsURLConnection
            val compressedData = compress(data.toString())

            connection.apply {
                requestMethod = "POST"
                addRequestProperty("Accept", "application/json")
                addRequestProperty("Connection", "close")
                addRequestProperty("Content-Encoding", "gzip")
                addRequestProperty("Content-Length", Integer.toString(compressedData.size))
                addRequestProperty("Content-Type", "application/json")
                addRequestProperty("User-Agent", "MC-Server/${B_STATS_VERSION}")
            }

            connection.doOutput = true
            DataOutputStream(connection.outputStream).apply {
                write(compressedData)
                flush()
                close()
            }

            connection.inputStream.close()
        }

        private fun compress(string: String): ByteArray {
            val outputStream = ByteArrayOutputStream()
            val gzip = GZIPOutputStream(outputStream)
            gzip.write(string.toByteArray())
            gzip.close()
            return outputStream.toByteArray()
        }

        private fun Path.toConfigLoader() = HoconConfigurationLoader.builder().setPath(this).build()
        private fun Boolean.toInt() = if (this) 1 else 0
    }
}