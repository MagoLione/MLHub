package com.magoliopoli.mLHub.com.magoliopoli.mLHub.cmds

import com.magoliopoli.mLHub.com.magoliopoli.mLHub.Log.Companion.fullELog
import com.magoliopoli.mLHub.com.magoliopoli.mLHub.cmds.MainCommands.Companion.HUB
import com.magoliopoli.mLHub.com.magoliopoli.mLHub.cmds.MainCommands.Companion.PERMS_PREFIX
import com.magoliopoli.mLHub.config.MLHubConfig
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HubCommand(private val mlConfig: MLHubConfig): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {

        return if (sender is Player && mlConfig.hubCommand && command.name.equals(HUB, ignoreCase = true) && sender.hasPermission("$PERMS_PREFIX.$HUB")) {
            sender.sendMessage("${ChatColor.GOLD}Teleporting to the hub...")

            if (mlConfig.hubWarp != null) {
                mlConfig.hubTpEngine.tpByEngine(mlConfig, sender, mlConfig.hubWarp)
            } else fullELog(
                consoleMessage = "An error occurred trying to use EssentialsX as hub teleport engine: hub-warp is not setted.",
                humanEntity = sender
            ).addReturn()
        } else false
    }
}