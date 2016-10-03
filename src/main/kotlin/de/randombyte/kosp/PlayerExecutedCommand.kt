package de.randombyte.kosp

import de.randombyte.kosp.StringExtensions.red
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.entity.living.player.Player

/**
 * A command that can only be executed by a [Player]. Otherwise it will throw a [CommandException].
 */
abstract class PlayerExecutedCommand : CommandExecutor {
    abstract fun executedByPlayer(player: Player, args: CommandContext): CommandResult

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) throw CommandException("Command must be executed by a player!".red())
        return executedByPlayer(src, args)
    }
}