package ru.vayflare.silenceSprout

/*
 * Copyright (c) 2025 by Vayflare
 *
 * WHAT A FUCKIN' KOTLIN, WHY DID I DO THAT?
 */

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SilenceSprout : JavaPlugin() {
    /**
     * A list of banned words loaded from the configuration.
     */
    private lateinit var bannedWords: List<String>

    /**
     * Called when the plugin is enabled.
     * Initializes the configuration, loads banned words, and registers an event listener.
     */
    override fun onEnable() {
        val chatListener = ChatListener(this)
        createConfig()
        loadConfig()
        getCommand("silencesprout")?.setExecutor(this)
        server.pluginManager.registerEvents(chatListener, this)
    }

    /**
     * Called when the plugin is disabled.
     */
    override fun onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Handles commands sent by players.
     * Currently, supports the "silencesprout reload" command to reload the configuration.
     *
     * @param sender The sender of the command.
     * @param command The command object.
     * @param label The command label.
     * @param args The command arguments.
     * @return true if the command is handled successfully, otherwise false.
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("silencesprout", true)) {
            if (args.size == 1 && args[0].equals("reload", true)) {
                if (sender.hasPermission("silencesprout.reload")) {
                    reloadConfig()
                    loadConfig()
                    sender.sendMessage(ChatColor.GREEN.toString() + config.getString("messages.reload.success"))
                    return true
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + config.getString("messages.reload.failed"))
                    return true
                }
            }
        }
        return false
    }

    /**
     * Loads the plugin configuration and updates the list of banned words.
     */
    private fun loadConfig() {
        val config = config
        bannedWords = config.getStringList("words")
        saveConfig()
    }

    /**
     * Creates the configuration file if it does not exist.
     */
    private fun createConfig() {
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            saveResource("config.yml", false)
        }
    }

    /**
     * Reloads the plugin configuration and updates the list of banned words.
     * Calls the parent method to reload the configuration, then loads the updated settings.
     */
    override fun reloadConfig() {
        super.reloadConfig()
        loadConfig()
    }

    /**
     * Checks if a message contains banned words.
     * If the plugin is enabled and the sender does not have permission to bypass the filter, returns true if the message contains banned words.
     *
     * @param message The message to check.
     * @param sender The sender of the message.
     * @return true if the message contains banned words, otherwise false.
     */
    fun isBanned(message: String, sender: CommandSender): Boolean {
        if (config.getBoolean("enabled")) {
            if (sender.hasPermission("silencesprout.bypass")) {
                return false
            }
            for (word in bannedWords) {
                if (message.lowercase().contains(word.lowercase())) {
                    return true
                }
            }
            return false
        }
        return false
    }

    /**
     * Censors a message by replacing banned words with "#" characters.
     * If the plugin is enabled, returns the censored message; otherwise returns the original message.
     *
     * @param message The message to censor.
     * @return The censored message.
     */
    fun censorMessage(message: String): String {
        if (config.getBoolean("enabled")) {
            var censoredMessage = message
            for (word in bannedWords) {
                censoredMessage = censoredMessage.replace(Regex("(?i)$word"), "#".repeat(word.length))
            }
            return censoredMessage
        }
        return message
    }
}