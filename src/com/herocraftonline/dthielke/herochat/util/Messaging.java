/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herochat.util;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;
import com.herocraftonline.dthielke.herochat.channels.Channel;

public class Messaging {
    private static final String[] HEALTH_COLORS = { "�0", "�4", "�6", "�e", "�2" };

    public static String format(HeroChat plugin, Channel channel, String format, String name, String msg, boolean sentByPlayer) {
        msg = msg.replaceAll("\u00a7", "");
        String leader = createLeader(plugin, channel, format, name, msg, sentByPlayer);
        return leader + msg;
    }

    private static String createLeader(HeroChat plugin, Channel channel, String format, String name, String msg, boolean sentByPlayer) {
        String prefix = "";
        String suffix = "";
        String world = "";
        String healthBar = "";
        if (sentByPlayer) {
            try {
                Player sender = plugin.getServer().getPlayer(name);
                if (sender != null) {
                    prefix = plugin.getPermissions().getPrefix(sender);
                    suffix = plugin.getPermissions().getSuffix(sender);
                    world = sender.getWorld().getName();
                    name = sender.getDisplayName();
                    healthBar = createHealthBar(sender);
                }
            } catch (Exception e) {
                e.printStackTrace();
                plugin.log(Level.WARNING,
                        "Error encountered while fetching prefixes/suffixes from Permissions. Is Permissions properly configured and up to date?");
            }
        }

        String leader = format;
        leader = leader.replaceAll("\\{default\\}", plugin.getChannelManager().getDefaultMsgFormat());
        leader = leader.replaceAll("\\{prefix\\}", prefix);
        leader = leader.replaceAll("\\{suffix\\}", suffix);
        leader = leader.replaceAll("\\{nick\\}", channel.getNick());
        leader = leader.replaceAll("\\{name\\}", channel.getName());
        leader = leader.replaceAll("\\{player\\}", name);
        leader = leader.replaceAll("\\{healthbar\\}", healthBar);
        leader = leader.replaceAll("\\{color.CHANNEL\\}", channel.getColor().str);
        leader = leader.replaceAll("\\{world\\}", world);

        Matcher matcher = Pattern.compile("\\{color.[a-zA-Z]+\\}").matcher(leader);
        while (matcher.find()) {
            String match = matcher.group();
            String colorString = match.substring(7, match.length() - 1);
            leader = leader.replaceAll("\\Q" + match + "\\E", ChatColor.valueOf(colorString).str);
        }

        return leader;
    }

    private static String createHealthBar(Player player) {
        int health = player.getHealth();
        if (health < 0) {
            health = 0;
        }
        int fullBars = health / 4;
        int remainder = health % 4;
        String healthBar = "";
        for (int i = 0; i < fullBars; i++) {
            healthBar += HEALTH_COLORS[4] + "|";
        }
        int barsLeft = 5 - fullBars;
        if (barsLeft > 0) {
            healthBar += HEALTH_COLORS[remainder] + "|";
            barsLeft--;
            for (int i = 0; i < barsLeft; i++) {
                healthBar += HEALTH_COLORS[0] + "|";
            }
        }
        return healthBar;
    }
}
