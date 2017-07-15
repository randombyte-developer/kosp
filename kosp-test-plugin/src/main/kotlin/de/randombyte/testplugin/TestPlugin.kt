package de.randombyte.testplugin

import com.google.inject.Inject
import de.randombyte.kosp.bstats.BStats
import de.randombyte.kosp.bstats.charts.*
import de.randombyte.kosp.config.ConfigManager
import de.randombyte.kosp.extensions.*
import de.randombyte.kosp.fixedTextTemplateOf
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
import org.spongepowered.api.text.action.TextActions
import java.net.URL
import java.time.Duration
import java.util.*

@Plugin(id = "kosp-test-plugin", name = "KospTestPlugin", version = "1.0")
class TestPlugin @Inject constructor(
        @DefaultConfig(sharedRoot = true)
        val configurationLoader: ConfigurationLoader<CommentedConfigurationNode>,
        val metrics: BStats) {

    @ConfigSerializable
    data class TestConfig(
            @Setting val testNumber: Int = 42,
            @Setting val testUUID: UUID = UUID.randomUUID(),
            @Setting val testText1: Text = "Green".green(),
            @Setting val testText2: Text = "G ".green() + "b".blue().action(TextActions.runCommand("cmd")) + " g".green(),
            @Setting val testDuration: Duration = Duration.ofHours(2),
            @Setting(comment = "%arg1,arg2;Cool comment") val testTextTemplate1: TextTemplate = fixedTextTemplateOf(
                    "[Click]".red().action(TextActions.suggestCommand("/say <hi>")),
                    " or ", "[here]".action(TextActions.openUrl(URL("https://www.google.de"))), "!".reset()
            ),
            @Setting val testTextTemplate2: TextTemplate = fixedTextTemplateOf(
                    "Green ".green(), "grayText".toArg().gray(), " still green".green()
            ),
            @Setting val testDate: Date = Date()
    )

    fun testCommand(func: (src: CommandSource, ctx: CommandContext) -> Unit) = CommandSpec.builder()
            .executor { src, ctx ->
                func(src, ctx)
                CommandResult.success()
            }.build()

    @Listener
    fun onInit(event: GameInitializationEvent) {
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .child(testCommand { _, _ -> testConfig() }, "config")
                .child(testCommand { _, _ -> testUser() }, "user")
                .child(testCommand { src, _ -> testPlayer(src) }, "player")
                .child(testCommand { _, _ -> testText() }, "text")
                .build(), "test")

        // BStats
        val testMap = mapOf("test1" to 3, "test2" to 42)

        with(metrics) {
            addCustomChart(SimplePie("simpleTestPie") { "testValue" })

            addCustomChart(AdvancedPie("advTestPie") { testMap })

            addCustomChart(SingleLineChart("singleLineChart") { 2 })

            addCustomChart(MultiLineChart("multiLineChart") { testMap })

            addCustomChart(SimpleBarChart("simpleBarChart") { testMap })

            addCustomChart(AdvancedBarChart("advBarChart") { mapOf("testV" to listOf(1,2,3), "testV2" to listOf(3,2,3)) })

            addCustomChart(DrilldownPie("drilldownPie") {
                mapOf("test1" to mapOf("innerTest1" to 3, "innerTest2" to 30), "test2" to mapOf("innerTest3" to 42)) })
        }
    }

    fun testConfig() {
        val configManager = ConfigManager(configurationLoader, TestConfig::class.java)

        val config = configManager.get()

        val arguments = mapOf("prefix" to "MyPrefix", "number" to "myNumber123")
        val textTemplate = config.testTextTemplate1.apply(arguments).build()

        Sponge.getServer().broadcastChannel.send(textTemplate)
        Sponge.getServer().broadcastChannel.send(config.testText1)
        Sponge.getServer().broadcastChannel.send(config.testText2)

        val duration = config.testDuration
        val newConfig = config.copy(testNumber = config.testNumber + 5)

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

    fun testText() {
        fun Text.broadcast() = Sponge.getServer().broadcastChannel.send(this)

        ("gray ".gray() + "Nothing " + "red".red()).broadcast()

        val greenText = "green".green()
        val greenToRedText = greenText.red()

        val blueText = "blue".blue()

        val combined1 = greenText + blueText
        val combined2 = greenToRedText + combined1

        greenText.broadcast()
        greenToRedText.broadcast()
        blueText.broadcast()
        combined1.broadcast()
        combined2.broadcast()

        val clickText = Text.of(TextActions.suggestCommand("Clicked!"), "[Click]")
        val noClickText = Text.of("[NoClick]")

        Text.of(clickText, noClickText).broadcast()
        Text.builder().append(clickText).append(noClickText).build().broadcast()
        fixedTextTemplateOf(clickText, noClickText).apply().build().broadcast()
        // wrong behaviour: https://github.com/SpongePowered/SpongeCommon/issues/1152
        TextTemplate.of(clickText, noClickText).apply().build().broadcast()

        val string = "&cRed text [click](&/cmd)"
        string.deserialize(deserializeTextActions = false).broadcast()
        string.deserialize().broadcast()
    }
}