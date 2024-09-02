package com.magoliopoli.mc.mLHub.cmds

import com.magoliopoli.mc.mLHub.MLHub
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
                commands: List<String>,
                argsSize: Int = 1,
                tab: Boolean = false,
                permsPrefix: String? = PERMS_PREFIX
            ): Boolean {
                val senderHasPermission = if (permsPrefix != null) {
                    sender.hasPermission("$permsPrefix.${commands.joinToString(separator = ".")}")
                } else {
                    sender.hasPermission(commands.joinToString(separator = "."))
                }
                return if (tab) {
                    args.size == argsSize &&
                            senderHasPermission
                } else {
                    args.size == argsSize &&
                            args.map { it.lowercase() } == commands.map { it.lowercase() } &&
                            senderHasPermission
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
        return if (command.name.equals(CMDS_PREFIX, ignoreCase = true)) {
            val cmdInfo = CommandInfo(sender, args)

            // RELOAD
            if (cmdInfo.conditions(listOf(RELOAD), 1)) {
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
            if (cmdInfo.conditions(listOf(RELOAD), 1,true)) {
                suggestions.add(RELOAD)
            }
        }

        return if (suggestions.isEmpty()) null else suggestions
    }
}