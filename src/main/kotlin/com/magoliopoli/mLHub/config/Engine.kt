package com.magoliopoli.mLHub.config

import com.earth2me.essentials.Essentials
import com.earth2me.essentials.commands.WarpNotFoundException
import com.magoliopoli.mLHub.Log
import com.magoliopoli.mLHub.Log.Companion.fullELog
import com.magoliopoli.mLHub.config.MLHubConfig.Companion.HUB_TP_ENGINE
import com.onarandombox.MultiverseCore.MultiverseCore
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

enum class Engine {
    STANDARD,
    MULTIVERSE,
    MULTIVERSE_LASTLOCATION,
    ESSENTIALS;

    companion object {
        private const val STANDARD_STR = "standard"
        private const val MULTIVERSE_STR = "multiverse"
        private const val MULTIVERSE_LASTLOCATION_STR = "multiverse-lastLocation"
        private const val ESSENTIALS_STR = "essentials"

        private fun getEngineByString(type: String, invalidValue: String): Engine {
            return when (type.lowercase()) {
                STANDARD_STR -> STANDARD
                MULTIVERSE_STR -> MULTIVERSE
                MULTIVERSE_LASTLOCATION_STR -> MULTIVERSE_LASTLOCATION
                ESSENTIALS_STR -> ESSENTIALS
                else -> {
                    Log.v(
                        message = "$type is not a valid value.",
                        invalidValue = invalidValue
                    )
                    STANDARD
                }
            }
        }

        fun FileConfiguration.getEngine(path: String, invalidValue: String = HUB_TP_ENGINE): Engine {
            return getEngineByString(this.getString(path).toString(), invalidValue)
        }

    }

    fun tpByEngine(mlConfig: MLHubConfig, humanEntity: HumanEntity, worldOrWarpName: String): Boolean {
        val status = when (this) {
            STANDARD -> {
                val world = Bukkit.getWorld(worldOrWarpName)
                if (world != null) {
                    humanEntity.teleport(world.spawnLocation)
                    true
                } else fullELog(
                    consoleMessage = "An error occurred getting bukkit world. Check worlds' names in the config.yml.",
                    humanEntity = humanEntity
                ).addReturn()
            }

            MULTIVERSE -> {
                val multiverse = Bukkit.getServer().pluginManager.getPlugin("Multiverse-Core")
                if (multiverse != null && multiverse is MultiverseCore) {

                    val mvWorld = multiverse.mvWorldManager.mvWorlds.firstOrNull {
                        it.name == worldOrWarpName
                    }

                    if (mvWorld != null) {
                        humanEntity.teleport(mvWorld.spawnLocation)
                        true
                    } else fullELog(
                        consoleMessage = "An error occurred getting mvWorld.",
                        humanEntity = humanEntity
                    ).addReturn()
                } else fullELog(
                    consoleMessage = "An error occurred getting Multiverse-Core instance. Verify the installation of the plugin.",
                    humanEntity = humanEntity
                ).addReturn()
            }

            MULTIVERSE_LASTLOCATION -> {
                val multiverse = Bukkit.getServer().pluginManager.getPlugin("Multiverse-Core")
                if (multiverse != null && multiverse is MultiverseCore) {

                    val mvWorld = multiverse.mvWorldManager.mvWorlds.firstOrNull {
                        it.name == worldOrWarpName
                    }

                    if (mvWorld != null) {
                        if (humanEntity is Player) {
                            multiverse.teleportPlayer(humanEntity, humanEntity, mvWorld.spawnLocation)
                            true
                        } else fullELog(
                            consoleMessage = "An error occurred casting HumanEntity to Player. Check if the action performer is a player.",
                            humanEntity = humanEntity
                        ).addReturn()
                    } else fullELog(
                        consoleMessage = "An error occurred getting mvWorld.",
                        humanEntity = humanEntity
                    ).addReturn()
                } else fullELog(
                    consoleMessage = "An error occurred getting Multiverse-Core instance. Verify the installation of the plugin.",
                    humanEntity = humanEntity
                ).addReturn()
            }

            ESSENTIALS -> {
                val essentials = Bukkit.getServer().pluginManager.getPlugin("Essentials")

                if (mlConfig.hubWarp != null) {
                    if (essentials != null && essentials is Essentials) {
                        val warpName = worldOrWarpName

                        try {
                            val warp = essentials.warps.getWarp(warpName)
                            humanEntity.teleport(warp)
                            true
                        } catch (e: WarpNotFoundException) {
                            fullELog(
                                consoleMessage = "An error occurred searching \"${warpName}\" warp.\n$e",
                                humanEntity = humanEntity
                            ).addReturn()
                        }
                    } else fullELog(
                        consoleMessage = "An error occurred getting Essentials instance. Verify the installation of the plugin.",
                        humanEntity = humanEntity
                    ).addReturn()
                } else fullELog(
                    consoleMessage = "Trying to use EssentialsX as hub teleport engine with no hub-warp setted.",
                    humanEntity = humanEntity
                ).addReturn()
            }
        }

        if (humanEntity is Player) {
            humanEntity.playSound(humanEntity.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 1f)
        }

        return status
    }
}
