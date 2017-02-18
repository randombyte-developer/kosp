package de.randombyte.kosp.extensions

import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player

fun Player.executeCommand(command: String) = Sponge.getCommandManager().process(this, command)