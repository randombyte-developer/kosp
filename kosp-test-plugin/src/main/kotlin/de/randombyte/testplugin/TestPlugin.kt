package de.randombyte.testplugin

import com.google.inject.Inject
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
import org.spongepowered.api.text.action.TextActions.*
import java.net.URL
import java.time.Duration
import java.util.*

@Plugin(id = "kosp-test-plugin", name = "KospTestPlugin", version = "1.0")
class TestPlugin @Inject constructor(
        @DefaultConfig(sharedRoot = true) private val configurationLoader: ConfigurationLoader<CommentedConfigurationNode>
) {

    @ConfigSerializable
    data class TestConfig(
            @Setting val testPair: Pair<Int, String> = 1 to "ds",
            @Setting val testNumber: Int = 42,
            @Setting val testUUID: UUID = UUID.randomUUID(),
            @Setting val testText1: Text = "Green".green(),
            @Setting val testText2: Text = "G ".green() + "b".blue().action(runCommand("cmd")) + " g".green(),
            @Setting val testDuration: Duration = Duration.ofHours(2),
            @Setting(comment = "%arg1,arg2;Cool comment") val testTextTemplate1: TextTemplate = fixedTextTemplateOf(
                    "[Click]".red().action(suggestCommand("/say <hi>")),
                    " or ", "[here]".action(openUrl(URL("https://www.google.de"))), "!".reset()
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
    }

    fun testConfig() {
        val configManager = ConfigManager(configurationLoader, TestConfig::class.java)

        val config = configManager.load()

        val arguments = mapOf("prefix" to "MyPrefix", "number" to "myNumber123")
        val textTemplate = config.testTextTemplate1.apply("prefix" to "MyPrefix", "number" to "myNumber123")

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

        val clickText = Text.of(suggestCommand("Clicked!"), "[Click]")
        val noClickText = Text.of("[NoClick]")

        Text.of(clickText, noClickText).broadcast()
        Text.builder().append(clickText).append(noClickText).build().broadcast()
        fixedTextTemplateOf(clickText, noClickText).apply().build().broadcast()
        TextTemplate.of(clickText, noClickText).apply().build().broadcast()

        val string = "&cRed text [click](&/cmd)"
        string.deserialize(deserializeTextActions = false).broadcast()
        string.deserialize().broadcast()
        // wrong behaviour: https://github.com/SpongePowered/SpongeCommon/issues/1152
    }
}