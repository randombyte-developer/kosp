package de.randombyte.kosp.extensions

import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource

fun CommandSource.executeCommand(command: String) = Sponge.getCommandManager().process(this, command)