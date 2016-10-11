package de.randombyte.kosptestplugin

import com.google.inject.Inject
import de.randombyte.kosp.config.ConfigManager
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Plugin

@Plugin(id = "test-plugin", name = "TestPlugin", version = "v1.0")
class TestPlugin @Inject constructor(
        @DefaultConfig(sharedRoot = true)
        val configurationLoader: ConfigurationLoader<CommentedConfigurationNode>) {

    @ConfigSerializable
    data class TestConfig(
            @Setting val testNumber: Int = 42
    )

    @Listener
    fun onInit(event: GameInitializationEvent) {
        val configManager = ConfigManager(configurationLoader, TestConfig::class.java)

        val config = configManager.get()
        val newConfig = config.copy(testNumber = config.testNumber + 5)

        configManager.save(newConfig)
    }
}