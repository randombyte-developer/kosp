package de.randombyte.kosptestplugin

import com.google.inject.Inject
import de.randombyte.kosp.bstats.BStatsMetrics
import de.randombyte.kosp.bstats.Country
import de.randombyte.kosp.bstats.charts.*
import de.randombyte.kosp.config.ConfigManager
import de.randombyte.kosp.extensions.*
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.TextTemplate.arg
import org.spongepowered.api.text.TextTemplate.of
import java.util.*

@Plugin(id = "kosp-test-plugin", name = "KospTestPlugin", version = "v1.0")
class TestPlugin @Inject constructor(
        @DefaultConfig(sharedRoot = true)
        val configurationLoader: ConfigurationLoader<CommentedConfigurationNode>,
        val metrics: BStatsMetrics) {

    @ConfigSerializable
    data class TestConfig(
            @Setting val testNumber: Int = 42,
            @Setting val testUUID: UUID = UUID.randomUUID(),
            @Setting val testText: Text = "Green".green(),
            @Setting val testTextTemplate: TextTemplate = of("[", arg("prefix"), "] ", "The number is ".red(), arg("number"), ".")
    )

    fun testCommand(func: (src: CommandSource, ctx: CommandContext) -> Unit) = CommandSpec.builder()
            .executor { src, ctx ->
                func(src, ctx)
                CommandResult.success()
            }.build()

    @Listener
    fun onInit(event: GameInitializationEvent) {
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .child(testCommand { src, ctx -> testConfig() }, "config")
                .child(testCommand { src, ctx -> testUser() }, "user")
                .child(testCommand { src, ctx -> testPlayer(src) }, "player")
                .build(), "test")

        metrics.addCustomChart(object : SimplePie("simpleTestPie") {
            override fun getValue(): String? = "testValue"
        })

        metrics.addCustomChart(object : AdvancedPie("advTestPie") {
            override fun getValue(): Map<String, Int>? = mapOf("testV" to 3, "testV2" to 42)
        })

        metrics.addCustomChart(object : SingleLineChart("singleLineChart") {
            override fun getValue(): Int? = 2
        })

        metrics.addCustomChart(object : MutliLineChart("multiLineChart") {
            override fun getValue(): Map<String, Int>?  = mapOf("testV" to 3, "testV2" to 5)
        })

        metrics.addCustomChart(object : SimpleMapChart("simpleMapChart") {
            override fun getValue(): Country? = Country.GERMANY
        })

        metrics.addCustomChart(object : AdvancedMapChart("advMapChart") {
            override fun getValue(): Map<Country, Int>? = mapOf(Country.GERMANY to 3, Country.UNITED_STATES to 1)
        })
    }

    fun testConfig() {
        val configManager = ConfigManager(configurationLoader, TestConfig::class)

        val config = configManager.get()
        val newConfig = config.copy(testNumber = config.testNumber + 5)

        Sponge.getServer().broadcastChannel.send(config.testTextTemplate.apply(
                mapOf("prefix" to "MyPrefix", "number" to "myNumber123")).build())

        configManager.save(newConfig)
    }

    fun testUser() {
        "069a79f4-44e9-4726-a5be-fca90e38aaf5".toUUID().getUser()
    }

    fun testPlayer(src: CommandSource) {
        if (src !is Player) throw CommandException("Must be executed by a player!".toText())
        val player = src.uniqueId.getPlayer()
        if (player != null) {
            src.sendMessage("${player.name} is online: ${player.isOnline}".toText())
        } else {
            src.sendMessage("Player not found!".toText())
        }
    }
}