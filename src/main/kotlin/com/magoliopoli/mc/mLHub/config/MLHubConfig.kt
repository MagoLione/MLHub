package com.magoliopoli.mc.mLHub.config

import com.magoliopoli.mc.mLHub.Log
import com.magoliopoli.mc.mLHub.config.Engine.Companion.getEngine
import org.bukkit.Bukkit.getServer
import org.bukkit.Bukkit.getWorld
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.ItemStack

class MLHubConfig(private val config: FileConfiguration) {

    companion object {
        const val MAIN_PATH = "MLHub"
        const val HUB_WORLD_NAME = "hub-world-name"
        const val HUB_COMMAND = "hub-command"
        const val HUB_TP_ENGINE = "hub-tp-engine"
        const val HUB_TP_BYPASS_LAST_POSITION = "hub-tp-bypass-last-position"
        const val HUB_WARP = "hub-warp"
        const val NAVIGATION_ITEM = "navigation-item"
        const val NAVIGATION_NAME = "navigation-name"
        const val NAVIGATION_LORE = "navigation-lore"
        const val NAVIGATION_POSITION = "navigation-position"
        const val INVENTORY_NAME = "inventory-name"
        const val INVENTORY_SIZE = "inventory-size"
        const val WORLDS = "worlds"

        const val COLOR_PREFIX = '&'

        private fun subPath(string: String): String {
            return "$MAIN_PATH.$string"
        }

        fun available(config: FileConfiguration): Boolean {
            return getWorld(config.getString("$MAIN_PATH.$HUB_WORLD_NAME").toString()) != null
        }

        fun FileConfiguration.getItemStackByString(path: String): ItemStack? {
            return Material.matchMaterial(this.getString(path).toString())?.let { ItemStack(it) }
        }

        fun String?.stripColorCodes(): String? {
            return ChatColor.stripColor(this?.let { ChatColor.translateAlternateColorCodes(COLOR_PREFIX, it) })
        }
    }

    val hubWorld: org.bukkit.World? = getWorld(config.getString((subPath(HUB_WORLD_NAME))).toString()) ?: Log.e(
        message = "World '${config.getString((subPath(HUB_WORLD_NAME))).toString()}' not found. Using the first world.",
        invalidValue = "hub-world-name"
    ).addReturn(
        getServer().worlds.firstOrNull() ?: Log.e(
            message = "No worlds found."
        ).addReturn(null))
    val hubCommand: Boolean = config.getBoolean(subPath(HUB_COMMAND))
    val hubTpEngine: Engine = config.getEngine(subPath(HUB_TP_ENGINE))
    val hubTpBypassLastPosition: Boolean = config.getBoolean(subPath(HUB_TP_BYPASS_LAST_POSITION))
    val hubWarp: String? = config.getString(subPath(HUB_WARP))
    val navigationItem: ItemStack = config.getItemStackByString(subPath(NAVIGATION_ITEM)) ?: Log.w(
        message = "Using compass.",
        invalidValue = "navigation-item"
    ).addReturn(ItemStack(Material.COMPASS))
    val navigationName: String = config.getString(subPath(NAVIGATION_NAME)) ?: Log.w(
        message = "Using item's default.",
        invalidValue = "navigation-name"
    ).addReturn(navigationItem.type.name)
    val navigationLore: List<String> = config.getList(subPath(NAVIGATION_LORE))?.map {
        it.toString()
    } ?: Log.v(
        message = "Leaving it empty.",
        invalidValue = "navigation-lore"
    ).addReturn(emptyList())
    val navigationPosition: Int = config.getInt(subPath(NAVIGATION_POSITION))
    val inventoryName: String = config.getString(subPath(INVENTORY_NAME)) ?: Log.w(
        message = "Leaving it empty.",
        invalidValue = "inventory-name"
    ).addReturn("")
    val inventorySize: Int = config.getInt(subPath(INVENTORY_SIZE))
    val worlds: List<World>

    init {
        if (hubWarp == null && hubTpEngine == Engine.ESSENTIALS) {
            Log.w(
                message = "You can't use EssentialsX as hub teleport engine.",
                invalidValue = "hub-warp"
            )
        }

        val keys = config.getKeys(true)
        val worldsStrings = mutableSetOf<String>()
        keys.map {
            val strList = it.split(".")
            if (strList.elementAtOrNull(0) == MAIN_PATH && strList.elementAtOrNull(1) == WORLDS) {
                if (strList.elementAtOrNull(2) != null) {
                    worldsStrings.add(strList.elementAtOrNull(2)!!)
                }
            }
        }
        this.worlds = worldsStrings.map {
            World(config = config, mainPath = "$MAIN_PATH.$WORLDS", world = it)
        }
    }

    fun isNavigationItemEquals(displayName: String?, lore: List<String>?, material: Material): Boolean {

        return navigationItem.type == material &&
                navigationName.stripColorCodes() == displayName.stripColorCodes() &&
                this.navigationLore.map { it.stripColorCodes() } == lore?.map { it.stripColorCodes() }
    }

    fun isNavigationItemEquals(item: ItemStack): Boolean {
        return item.hasItemMeta() &&
                isNavigationItemEquals(item.itemMeta?.displayName, item.itemMeta?.lore, item.type)
    }
}