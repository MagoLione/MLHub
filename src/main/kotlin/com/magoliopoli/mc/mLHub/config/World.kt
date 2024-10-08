package com.magoliopoli.mc.mLHub.config

import com.magoliopoli.mc.mLHub.Log
import com.magoliopoli.mc.mLHub.config.Engine.Companion.getEngine
import com.magoliopoli.mc.mLHub.config.MLConfig.Companion.getItemStackByString
import com.magoliopoli.mc.mLHub.config.MLConfig.Companion.stripColorCodes
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

class World(config: FileConfiguration, mainPath: String, world: String) {

    companion object {
        const val ITEM_NAME = "item-name"
        const val ITEM_LORE = "item-lore"
        const val ITEM = "item"
        const val POSITION = "position"
        const val ENABLED = "enabled"
        const val ENGINE = "engine"
        const val BYPASS_LAST_POSITION = "bypass-last-position"
    }

    val worldOrWarpName: String = world
    val itemName: String
    val itemLore: List<String>
    val item: ItemStack
    val position: Int
    val enabled: Boolean
    val engine: Engine
    val bypassLastPosition: Boolean


    init {
        fun subPath(string: String): String {
            return "$mainPath.$world.$string"
        }
        this.itemName = config.getString(subPath(ITEM_NAME)) ?: Log.w(
            message = "Using world name.",
            invalidValue = "item-name (in $worldOrWarpName)"
        ).addReturn(worldOrWarpName)

        this.itemLore = config.getList(subPath(ITEM_LORE))?.map {
            it.toString()
        } ?: Log.w(
            message = "Leaving it empty.",
            invalidValue = "item-lore (in $worldOrWarpName)"
        ).addReturn(emptyList())

        this.item = config.getItemStackByString(subPath(ITEM)) ?: Log.w(
            message = "Using grass_block.",
            invalidValue = "item (in $worldOrWarpName)"
        ).addReturn(ItemStack(Material.GRASS_BLOCK))

        this.position = config.getInt(subPath(POSITION))
        this.enabled = config.getBoolean(subPath(ENABLED))
        this.engine = config.getEngine(subPath(ENGINE), "$ENGINE (in $worldOrWarpName)")
        this.bypassLastPosition = config.getBoolean(subPath(BYPASS_LAST_POSITION))
    }

    fun safeTpByEngine(mlConfig: MLConfig, humanEntity: HumanEntity): Boolean {
        return if (this.enabled) {
            this.engine.tpByEngine(mlConfig, humanEntity, worldOrWarpName, bypassLastPosition)
        } else false
    }

    fun isItemEquals(displayName: String?, lore: List<String>?, material: Material): Boolean {
        return item.type == material &&
                itemName.stripColorCodes() == displayName.stripColorCodes() &&
                itemLore.map { it.stripColorCodes() } == lore?.map { it.stripColorCodes() }
    }

    fun isItemEquals(item: ItemStack): Boolean {
        return item.hasItemMeta() &&
                isItemEquals(item.itemMeta?.displayName, item.itemMeta?.lore, item.type)
    }
}