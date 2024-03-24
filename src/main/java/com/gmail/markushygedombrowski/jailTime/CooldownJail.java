package com.gmail.markushygedombrowski.jailTime;

import com.gmail.markushygedombrowski.HLWarp;
import com.gmail.markushygedombrowski.jailUtils.HotBarMessage;
import com.gmail.markushygedombrowski.jailUtils.JailUtils;
import com.gmail.markushygedombrowski.warp.WarpManager;
import org.bukkit.entity.Player;

import java.util.*;

public class CooldownJail {

    private WarpManager hlWarp;
    private HotBarMessage hotBarMessage;
    private JailUtils jailUtils;
    public static HashMap<Player, AbilityCooldown> cooldownPlayers = new HashMap<>();

    public CooldownJail(WarpManager hlWarp, HotBarMessage hotBarMessage, JailUtils jailUtils) {
        this.hlWarp = hlWarp;
        this.hotBarMessage = hotBarMessage;
        this.jailUtils = jailUtils;
    }


    public static void add(Player player, String ability, long seconds, long systime,String reason) {
        if (!cooldownPlayers.containsKey(player)) cooldownPlayers.put(player, new AbilityCooldown(player));
        if (isCooling(player, ability)) return;
        cooldownPlayers.get(player).cooldownMap.put(ability, new AbilityCooldown(player, seconds * 1000, System.currentTimeMillis(),reason));
    }

    public static boolean isCooling(Player player, String ability) {
        if (!cooldownPlayers.containsKey(player)) return false;
        return cooldownPlayers.get(player).cooldownMap.containsKey(ability);
    }

    public static double getRemaining(Player player, String ability) {
        if (!cooldownPlayers.containsKey(player)) return 0.0;
        if (!cooldownPlayers.get(player).cooldownMap.containsKey(ability)) return 0.0;
        return UtilTime.convert((cooldownPlayers.get(player).cooldownMap.get(ability).seconds + cooldownPlayers.get(player).cooldownMap.get(ability).systime) - System.currentTimeMillis(), TimeUnit.SECONDS, 0);
    }


    public static void removeCooldown(Player player, String ability) {
        if (!cooldownPlayers.containsKey(player)) {
            return;
        }
        if (!cooldownPlayers.get(player).cooldownMap.containsKey(ability)) {
            return;
        }
        cooldownPlayers.get(player).cooldownMap.remove(ability);

    }

    public static Map<Player,AbilityCooldown> getCooldownPlayers(String ability) {
        Map<Player,AbilityCooldown> players = new HashMap<>();
        cooldownPlayers.forEach((player, abilityCooldown) -> {
            if (abilityCooldown.cooldownMap.containsKey(ability)) {
                players.put(player, abilityCooldown);
            }
        });
        return players;

    }
    public AbilityCooldown getAbilityCooldown(Player player) {
        if (!cooldownPlayers.containsKey(player)) {
            return null;
        }
        return cooldownPlayers.get(player);
    }


    public void handleCooldownsJail() {
        if (cooldownPlayers.isEmpty()) {
            return;
        }
        cooldownPlayers.forEach((player, abilityCooldown) -> {
            abilityCooldown.cooldownMap.forEach((ability, cooldown) -> {
                int seconds = (int) getRemaining(player, ability);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                if (minutes > 0) {
                    hotBarMessage.sendActionbar(player, "§eDu løslades som §6§l" + minutes + " min og §6§l" + seconds + "§e sek!");
                } else {
                    hotBarMessage.sendActionbar(player, "§eDu løslades som §6§l" + seconds + "§e sekunder!");
                }

                if (getRemaining(player, ability) <= 0.0) {
                    removeCooldown(player, ability);
                    player.sendMessage("§7Du er blevet §aLøsladt§7!");
                    String block = String.valueOf(ability.charAt(0));


                    player.teleport(hlWarp.getWarpInfo(block.toLowerCase() + "-spawn").getLocation());
                }

            });
        });

    }
}
