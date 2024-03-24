package com.gmail.markushygedombrowski.listener;

import com.gmail.markushygedombrowski.HLJail;
import com.gmail.markushygedombrowski.HLWarp;
import com.gmail.markushygedombrowski.jailTime.CooldownJail;
import com.gmail.markushygedombrowski.jailUtils.JailUtils;

import com.gmail.markushygedombrowski.utils.Utils;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class JailListner implements Listener {
    private final JailUtils jailUtils;
    private final CooldownJail cooldown;
    private final HLWarp hlWarp;
    private final HLJail hlJail;


    public JailListner(JailUtils jailUtils, CooldownJail cooldown, HLWarp hlWarp, HLJail hlJail) {
        this.jailUtils = jailUtils;
        this.cooldown = cooldown;
        this.hlWarp = hlWarp;

        this.hlJail = hlJail;
    }

    @EventHandler
    public void onQuiting(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("jail.bypass")) {
            return;
        }

        String block = jailUtils.getBlock(player);
        if (CooldownJail.isCooling(player, block + "jail")) {
            jailUtils.addJailedOfflinePlayer(player, cooldown.getAbilityCooldown(player).cooldownMap.get(block + "jail"));
            jailUtils.addJailedOfflinePlayerTime(player.getName(), (int) CooldownJail.getRemaining(player, block + "jail"));
            CooldownJail.removeCooldown(player, block + "jail");
        }
        jailUtils.addOfflinePlayer(player);

    }

    @EventHandler
    public void onJoining(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("jail.bypass")) {
            return;
        }
        if (jailUtils.isOfflinePlayer(player)) {
            jailUtils.removeOfflinePlayer(player);
        }

        if (jailUtils.isJailedOfflinePlayer(player)) {
            jailOffline(player);
            return;
        }
        if (Utils.regionHasFlag(player.getLocation(), HLJail.JAIL_FLAG, StateFlag.State.ALLOW)) {
            player.teleport(hlWarp.getWarpManager().getWarpInfo(jailUtils.getBlock(player).toLowerCase() + "-spawn").getLocation());
        }


    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("jail.bypass")) {
            return;
        }
        String block = jailUtils.getBlock(player);
        if (CooldownJail.isCooling(player, block + "jail")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(hlJail, new Runnable() {
                @Override
                public void run() {
                    player.teleport(hlWarp.getWarpManager().getWarpInfo(block + "-jail").getLocation());
                }
            }, 2);
        }

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        if (player.hasPermission("jail.bypass")) {
            return;
        }
        String block = jailUtils.getBlock(player);
        if (CooldownJail.isCooling(player, block + "jail")) {
            event.setCancelled(true);
            player.sendMessage("§8[§4§lISOLATION§8] §cDu kan ikke slå andre i JAIL!");
        }
    }

    private void jailOffline(Player player) {
        Bukkit.getServer().getScheduler().runTaskLater(hlJail, new Runnable() {
            @Override
            public void run() {
                player.teleport(hlWarp.getWarpManager().getWarpInfo(jailUtils.getBlock(player) + "-jail").getLocation());
                CooldownJail.add(player, jailUtils.getBlock(player) + "jail", jailUtils.getJailedOfflinePlayerTime(player.getName()), System.currentTimeMillis(), jailUtils.getJailedOfflinePlayer(player).reason);
                jailUtils.removeJailedOfflinePlayer(player);
                jailUtils.removeJailedOfflinePlayerTime(player.getName());
            }
        }, 10L);


    }

}
