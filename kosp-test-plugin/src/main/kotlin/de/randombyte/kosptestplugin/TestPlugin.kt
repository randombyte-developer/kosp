package de.randombyte.kosptestplugin

import com.google.inject.Inject
import de.randombyte.kosp.config.ConfigManager
import de.randombyte.kosp.getPlayer
import de.randombyte.kosp.getUser
import de.randombyte.kosp.toText
import de.randombyte.kosp.toUUID
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
import java.util.*

@Plugin(id = "test-plugin", name = "TestPlugin", version = "v1.0")
class TestPlugin @Inject constructor(
        @DefaultConfig(sharedRoot = true)
        val configurationLoader: ConfigurationLoader<CommentedConfigurationNode>) {

    @ConfigSerializable
    data class TestConfig(
            @Setting val testNumber: Int = 42,
            @Setting val testUUID: UUID = UUID.randomUUID()
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
    }

    fun testConfig() {
        val configManager = ConfigManager(configurationLoader, TestConfig::class.java)

        val config = configManager.get()
        val newConfig = config.copy(testNumber = config.testNumber + 5)

        configManager.save(newConfig)
    }

    fun testUser() {
        "069a79f4-44e9-4726-a5be-fca90e38aaf5".toUUID().getUser().apply {  }
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