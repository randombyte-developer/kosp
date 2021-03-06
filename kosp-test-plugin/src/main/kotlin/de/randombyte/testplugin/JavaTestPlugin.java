package de.randombyte.testplugin;

import static org.spongepowered.api.text.TextTemplate.arg;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import de.randombyte.kosp.UtilsKt;
import de.randombyte.kosp.config.ConfigManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

@Plugin(id = "java-kosp-test-plugin", name = "JavaKospTestPlugin", version = "1.0")
public class JavaTestPlugin {

    @ConfigSerializable
    public static class Config {
        @Setting private int testNumber = 0;
        @Setting private Text testText = Text.of(TextColors.RED, "Red text");
        @Setting private TextTemplate testTextTemplate = UtilsKt.fixedTextTemplateOf(
                Text.of("You won "),
                arg("money").color(TextColors.GOLD),
                arg("currencySymbol"),
                Text.of("!"));

        public Config() {
        }

        // Normally all getters and setters follow here but for this example "direct access" is okay
    }

    private final ConfigManager<Config> configManager;

    @Inject
    public JavaTestPlugin(@DefaultConfig(sharedRoot = true) ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        configManager = new ConfigManager<>(
                configLoader,
                Config.class,
                true,
                true,
                typeSerializerCollection -> null);
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .executor((src, args) -> {
                    Config config = configManager.load();

                    config.testNumber += 7;
                    broadcast(config.testText);
                    broadcast(config.testTextTemplate
                            .apply(ImmutableMap.of("money", 42, "currencySymbol", "$"))
                            .toText());

                    broadcast(UtilsKt.fixedTextTemplateOf(
                            Text.of("You won "),
                            arg("money").color(TextColors.GOLD),
                            arg("currencySymbol"),
                            Text.of("!"))
                            .apply(ImmutableMap.of("money", 42, "currencySymbol", "$"))
                            .toText());


                    configManager.save(config);

                    return CommandResult.success();
                }).build(), "testconfig");
    }

    private void broadcast(Text text) {
        Sponge.getServer().getBroadcastChannel().send(text);
    }
}
