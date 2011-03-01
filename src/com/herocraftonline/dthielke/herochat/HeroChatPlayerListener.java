package com.herocraftonline.dthielke.herochat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.channels.ChannelManager;

public class HeroChatPlayerListener extends PlayerListener {

    private HeroChat plugin;

    public HeroChatPlayerListener(HeroChat plugin) {
        this.plugin = plugin;
    }

    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled())
            return;

        Player sender = event.getPlayer();
        String name = sender.getName();
        ChannelManager cm = plugin.getChannelManager();
        Channel c = cm.getActiveChannel(name);

        if (c != null) {
            String group = plugin.getPermissions().getGroup(sender);
            if (c.getVoicelist().contains(group) || c.getVoicelist().isEmpty()) {
                if (!c.getPlayers().contains(name)) {
                    c.addPlayer(name);
                }
                c.sendMessage(sender.getDisplayName(), event.getMessage());
            } else {
                sender.sendMessage(plugin.getTag() + "You cannot speak in this channel");
            }
        }
        event.setCancelled(true);
    }

    public void onPlayerJoin(PlayerEvent event) {
        Player joiner = event.getPlayer();
        String name = joiner.getName();
        plugin.getConfigManager().loadPlayer(name);
    }

    public void onPlayerQuit(PlayerEvent event) {
        Player quitter = event.getPlayer();
        String name = quitter.getName();
        plugin.getConfigManager().savePlayer(name);
        plugin.getChannelManager().removeFromAll(name);
    }

}
