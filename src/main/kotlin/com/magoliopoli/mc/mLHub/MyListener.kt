package com.magoliopoli.mc.mLHub

import com.magoliopoli.mc.mLHub.MLHub.Companion.HUB_INVENTORY_ACCESS
import com.magoliopoli.mc.mLHub.MLHub.Companion.PERMS_PREFIX
import com.magoliopoli.mc.mLHub.MLHub.Companion.navigationItem
import com.magoliopoli.mc.mLHub.config.MLHubConfig
import org.bukkit.ChatColor
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack

class MyListener(private val mlConfig: MLHubConfig): Listener {

    fun getNavigationItem(): ItemStack {
        val navigationItem = mlConfig.navigationItem
        val meta = navigationItem.itemMeta
        meta?.lore = mlConfig.navigationLore.map {
            ChatColor.translateAlternateColorCodes('&', it)
        }

        val navigationName = if (mlConfig.navigationName == "default-name") navigationItem.type.name else mlConfig.navigationName
        meta?.setDisplayName(ChatColor.translateAlternateColorCodes('&', navigationName))

        navigationItem.itemMeta = meta

        return navigationItem
    }

    fun hubLogic(player: HumanEntity, provideNavigationItem: ItemStack? = null) {
        player.inventory.clear()

        val navigationItem = provideNavigationItem ?: navigationItem

        if (mlConfig.navigationPosition != -1) {
            player.inventory.setItem(mlConfig.navigationPosition, navigationItem)
        } else {
            player.inventory.addItem(navigationItem)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.world == mlConfig.hubWorld) {
            hubLogic(event.player)
        }
    }

    @EventHandler
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        if (event.player.world == mlConfig.hubWorld) {
            hubLogic(event.player)
        }
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        if (event.player.world == mlConfig.hubWorld) {
            hubLogic(event.player)
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if ((event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_AIR) && event.player.world.name == mlConfig.hubWorld?.name) {

            val item = event.item
            if (item == navigationItem) {
                event.player.openInventory(MLHub.inventory)
            }
        }
    }

    @EventHandler
    fun onEntityDropItem(event: EntityDropItemEvent) {
        if (event.entity is HumanEntity) {
            val player = event.entity as HumanEntity

            if (player.world == mlConfig.hubWorld && event.itemDrop.itemStack.hasItemMeta()) {
                val droppedItem = event.itemDrop.itemStack
                if (droppedItem == navigationItem) {
                    event.isCancelled = true
                } else {
                    for (world in mlConfig.worlds) {
                        if (world.isItemEquals(droppedItem)) {
                            event.isCancelled = true
                        }
                    }
                }

            }
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked

        if (player.world == mlConfig.hubWorld) {
            if (!player.hasPermission("$PERMS_PREFIX.$HUB_INVENTORY_ACCESS")) {
                event.isCancelled = true
            }

            if (!event.inventory.contains(navigationItem)) {
                hubLogic(player)
            }
        }

        if (event.inventory == MLHub.inventory) {
            event.isCancelled = true

            val clickedItem = event.currentItem
            if (
                clickedItem != null &&
                clickedItem.hasItemMeta()
            ) {
                mlConfig.worlds.forEach {world ->
                    if (world.isItemEquals(clickedItem)) {

                        world.safeTpByEngine(mlConfig, player)
                    }
                }
            }
        }
    }
}