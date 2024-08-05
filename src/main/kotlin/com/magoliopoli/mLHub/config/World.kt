package com.magoliopoli.mLHub.config

import com.magoliopoli.mLHub.com.magoliopoli.mLHub.Log
import com.magoliopoli.mLHub.config.Engine.Companion.getEngine
import com.magoliopoli.mLHub.config.MLHubConfig.Companion.getItemStackByString
import com.magoliopoli.mLHub.config.MLHubConfig.Companion.stripColorCodes
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
        const val ENGINE = "engine"
    }

    val worldOrWarpName: String = world
    val itemName: String
    val itemLore: List<String>
    val item: ItemStack
    val position: Int
    val engine: Engine


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
        this.engine = config.getEngine(subPath(ENGINE), "$ENGINE (in $worldOrWarpName)")
    }

    fun tpByEngine(mlConfig: MLHubConfig, humanEntity: HumanEntity): Boolean {
        return this.engine.tpByEngine(mlConfig, humanEntity, worldOrWarpName)
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