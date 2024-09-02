package com.magoliopoli.mc.mLHub

import org.bukkit.Bukkit.getLogger
import org.bukkit.ChatColor
import org.bukkit.entity.HumanEntity
import java.util.*

class Log {
    fun <T> addReturn(obj: T): T {

        return obj
    }

    fun addReturn(obj: Boolean = false): Boolean {

        return obj
    }

    enum class Level {
        V,
        W,
        E
    }

    class Color {
        companion object {
            const val RESET = "\u001B[0m"
            const val YELLOW = "\u001B[93m"
            const val GOLD = "\u001B[33m"
            const val RED = "\u001B[91m"
        }
    }

    companion object {
        private const val LOG_PREFIX = "[MLHub]"

        private fun getMessage(
            message: Any?,
            invalidValue: String? = null
        ): String {
            return if (invalidValue != null) {
                "$LOG_PREFIX Invalid value: $invalidValue. $message"
            } else {
                "$LOG_PREFIX $message"
            }
        }

        fun v(
            message: Any?,
            invalidValue: String? = null
        ): Log {
            getLogger().info(getMessage(message, invalidValue))
            return Log()
        }

        fun w(
            message: Any?,
            invalidValue: String? = null
        ): Log {
            getLogger().warning(getMessage(message, invalidValue))
            return Log()
        }

        fun e(
            message: Any?,
            invalidValue: String? = null
        ): Log {
            getLogger().severe(getMessage(message, invalidValue))
            return Log()
        }

        fun fullELog(
            consoleMessage: Any?,
            humanEntity: HumanEntity,
            level: Level = Level.E,
            playerMessage: String = "An error occurred. Please contact the staff and remember the error code."
        ): Log {
            val uuid = UUID.randomUUID().toString().subSequence(0, 5)

            val color = when (level) {
                Level.V -> {
                    v(
                        "[$uuid] $consoleMessage"
                    )
                    ChatColor.YELLOW
                }
                Level.W -> {
                    w(
                        "[$uuid] $consoleMessage"
                    )
                    ChatColor.GOLD
                }
                Level.E -> {
                    e(
                        "[$uuid] $consoleMessage"
                    )
                    ChatColor.RED
                }
            }
            humanEntity.sendMessage("$color[Server] ($uuid) $playerMessage")

            return Log()
        }
    }
}