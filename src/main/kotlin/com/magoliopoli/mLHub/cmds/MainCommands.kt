package com.magoliopoli.mLHub.com.magoliopoli.mLHub.cmds

import com.magoliopoli.mLHub.MLHub
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MainCommands(private val mlHub: MLHub): CommandExecutor, TabCompleter {
    companion object {
        data class CommandInfo(
            val sender: CommandSender,
            val args: Array<out String>
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as CommandInfo

                if (sender != other.sender) return false
                if (!args.contentEquals(other.args)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = sender.hashCode()
                result = 31 * result + args.contentHashCode()
                return result
            }

            fun conditions(
                command: String,
                argsSize: Int = 1,
                tab: Boolean = false,
                permsPrefix: String? = PERMS_PREFIX
            ): Boolean {
                return if (permsPrefix != null) {
                    if (tab) {
                        args.size == argsSize &&
                                sender.hasPermission("$permsPrefix.$command")
                    } else {
                        args.size == argsSize &&
                                args[0].equals(command, ignoreCase = true) &&
                                sender.hasPermission("$permsPrefix.$command")
                    }
                } else {
                    if (tab) {
                        args.size == argsSize &&
                                sender.hasPermission(command)
                    } else {
                        args.size == argsSize &&
                                args[0].equals(command, ignoreCase = true) &&
                                sender.hasPermission(command)
                    }
                }
            }
        }

        const val PERMS_PREFIX = "${MLHub.PERMS_PREFIX}.command"
        const val CMDS_PREFIX = "mlhub"

        // RELOAD
        const val RELOAD = "reload"

        // HUB
        const val HUB = "hub"
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val cmdInfo = CommandInfo(sender, args)

        return if (command.name.equals(CMDS_PREFIX, ignoreCase = true)) {

            // RELOAD
            if (cmdInfo.conditions(RELOAD, 1)) {
                mlHub.reloadConfig()

                mlHub.enable()

                sender.sendMessage("${ChatColor.GREEN}MLHub successfully reloaded.")
                true
            } else false
        } else false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {
        val suggestions = mutableListOf<String>()
        if (command.name.equals(CMDS_PREFIX, ignoreCase = true)) {
            val cmdInfo = CommandInfo(sender, args)

            // RELOAD
            if (cmdInfo.conditions(RELOAD, 1,true)) {
                suggestions.add(RELOAD)
            }
        }

        return if (suggestions.isEmpty()) null else suggestions
    }
}