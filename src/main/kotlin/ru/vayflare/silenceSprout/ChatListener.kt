package ru.vayflare.silenceSprout

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListener(private val plugin: SilenceSprout) : Listener {

    /**
     * Handles the AsyncPlayerChatEvent to filter and censor player chat messages.
     * If the plugin is enabled, checks if the message contains banned words and censors it if necessary.
     *
     * @param event The AsyncPlayerChatEvent containing the player's message.
     */
    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (plugin.config.getBoolean("enabled")) {
            val message = event.message
            val player = event.player
            if (plugin.isBanned(message, player)) {
                event.message = plugin.censorMessage(message)
            }
        }
    }
}