package com.magoliopoli.mLHub

import com.magoliopoli.mLHub.com.magoliopoli.mLHub.cmds.HubCommand
import com.magoliopoli.mLHub.com.magoliopoli.mLHub.cmds.MainCommands
import com.magoliopoli.mLHub.com.magoliopoli.mLHub.cmds.MainCommands.Companion.CMDS_PREFIX
import com.magoliopoli.mLHub.com.magoliopoli.mLHub.cmds.MainCommands.Companion.HUB
import com.magoliopoli.mLHub.config.MLHubConfig
import com.magoliopoli.mLHub.config.MLHubConfig.Companion.INVENTORY_SIZE
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class MLHub : JavaPlugin() {

    companion object {
        lateinit var inventory: Inventory
        lateinit var navigationItem: ItemStack

        const val PERMS_PREFIX = "MLHub"
        const val HUB_INVENTORY_ACCESS = "hubInventoryAccess"
    }

    fun enable(
        onInventoryCreation: (mlConfig: MLHubConfig) -> MyListener = {mlConfig ->
            val listener = MyListener(mlConfig)
            server.pluginManager.registerEvents(listener, this)
            listener
        }
    ) {

        if (MLHubConfig.available(config)) {
            val mlConfig = MLHubConfig(config)

            val inventorySize = if (mlConfig.inventorySize % 9 == 0 && mlConfig.inventorySize / 9 <= 6) {
                mlConfig.inventorySize
            } else  Log.w(
                message = "Using default size (27).",
                invalidValue = INVENTORY_SIZE
            ).addReturn(27)

            inventory = Bukkit.createInventory(null, inventorySize, mlConfig.inventoryName)

            val listener = onInventoryCreation(mlConfig)

            navigationItem = listener.getNavigationItem()

            val worlds = mlConfig.worlds

            worlds.forEach {
                val item = it.item

                val meta = item.itemMeta
                meta?.lore = it.itemLore.map {
                    ChatColor.translateAlternateColorCodes('&', it)
                }

                meta?.setDisplayName(ChatColor.translateAlternateColorCodes('&', it.itemName))
                item.itemMeta = meta

                if (it.position != -1) {
                    inventory.setItem(it.position, it.item)
                } else {
                    inventory.addItem(it.item)
                }
            }

            server.onlinePlayers.forEach {player ->
                if (player.world == mlConfig.hubWorld) {
                    listener.hubLogic(player)
                }
            }
        } else {
            Log.e(
                message = "The plugin won't work.",
                invalidValue = "hub-world-name"
            )
        }
    }

    override fun onLoad() {
        super.onLoad()
        saveDefaultConfig()
    }

    override fun onEnable() {

        val commandsManager = MainCommands(this)
        getCommand(CMDS_PREFIX)?.setExecutor(commandsManager)
        getCommand(CMDS_PREFIX)?.tabCompleter = commandsManager

        enable {mlConfig ->

            getCommand(HUB)?.setExecutor(HubCommand(mlConfig))

            val listener = MyListener(mlConfig)
            server.pluginManager.registerEvents(listener, this)
            return@enable listener
        }

        Log.v("MLHub has started.")
    }

    override fun onDisable() {

        Log.v("MLHub has stopped.")
    }
}
