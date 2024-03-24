package com.gmail.markushygedombrowski.jailUtils;

import com.gmail.markushygedombrowski.jailTime.AbilityCooldown;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JailUtils {
    private Map<UUID, Player> offlinePlayers = new HashMap<>();
    private Map<UUID, AbilityCooldown> jailedOfflinePlayer = new HashMap<>();

    private Map<String, Integer> jailedOfflinePlayerTime = new HashMap<>();

    public void addOfflinePlayer(Player player) {
        offlinePlayers.put(player.getUniqueId(), player);
    }

    public void removeOfflinePlayer(Player player) {
        offlinePlayers.remove(player.getUniqueId());
    }

    public boolean isOfflinePlayer(Player player) {
        return offlinePlayers.containsKey(player.getUniqueId());
    }

    public Player getOfflinePlayer(UUID uuid) {
        return offlinePlayers.get(uuid);
    }

    public Player getOfflinePlayer(String name) {
        for (UUID uuid : offlinePlayers.keySet()) {
            if (offlinePlayers.get(uuid).getName().equals(name)) {
                return offlinePlayers.get(uuid);
            }
        }
        return null;
    }


    public void addJailedOfflinePlayer(Player player, AbilityCooldown abilityCooldown) {
        jailedOfflinePlayer.put(player.getUniqueId(), abilityCooldown);
    }

    public void removeJailedOfflinePlayer(Player player) {
        jailedOfflinePlayer.remove(player.getUniqueId());
    }

    public boolean isJailedOfflinePlayer(Player player) {
        return jailedOfflinePlayer.containsKey(player.getUniqueId());
    }

    public AbilityCooldown getJailedOfflinePlayer(Player player) {
        return jailedOfflinePlayer.get(player.getUniqueId());
    }
    public Map<UUID, AbilityCooldown> getJailedOfflinePlayerMap() {
        return jailedOfflinePlayer;
    }

    public void addJailedOfflinePlayerTime(String player, int time) {
        jailedOfflinePlayerTime.put(player, time);
    }

    public void removeJailedOfflinePlayerTime(String player) {
        jailedOfflinePlayerTime.remove(player);
    }

    public boolean isJailedOfflinePlayerTime(String player) {
        return jailedOfflinePlayerTime.containsKey(player);
    }

    public int getJailedOfflinePlayerTime(String player) {
        return jailedOfflinePlayerTime.get(player);
    }

    public Map<String, Integer> getJailedOfflinePlayerTimeMap() {
        return jailedOfflinePlayerTime;
    }


    public String getBlock(Player p) {
        String block = "C";
        if (p.hasPermission("a-fange")) {
            block = "A";
        } else if (p.hasPermission("b-fange")) {
            block = "B";
        }
        return block;
    }



}
