package com.gmail.markushygedombrowski.commands;

import com.gmail.markushygedombrowski.HLJail;
import com.gmail.markushygedombrowski.HLWarp;
import com.gmail.markushygedombrowski.jailTime.AbilityCooldown;
import com.gmail.markushygedombrowski.jailTime.CooldownJail;
import com.gmail.markushygedombrowski.jailUtils.JailReason;
import com.gmail.markushygedombrowski.jailUtils.JailUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class Jailing {

    private final HLWarp hlWarp;
    private final HLJail pl;
    private final JailUtils jailUtils;
    private CooldownJail cooldown;

    public Jailing(HLWarp hlWarp, HLJail pl, JailUtils jailUtils, CooldownJail cooldown) {
        this.hlWarp = hlWarp;
        this.pl = pl;
        this.jailUtils = jailUtils;
        this.cooldown = cooldown;
    }

    public void JailPlayer(String[] args, Player player, Player target) {
        String reason = args[1];
        JailReason jailReason = JailReason.getJailReason(reason);
        if (jailReason == null) {
            player.sendMessage("§cDen grund findes ikke!");
            return;
        }
        int time = jailReason.getTime();
        String block = "offline";
        if (target.hasPermission("a-fange")) {
            time = time + 3;
            block = "A";
        } else if (target.hasPermission("b-fange")) {
            time = time + 2;
            block = "B";
        } else if (target.hasPermission("c-fange")) {
            block = "C";
        }
        target.teleport(hlWarp.getWarpManager().getWarpInfo(block + "-jail").getLocation());
        Bukkit.broadcastMessage("§8[§4§lISOLATION§8] §c" + target.getName() + " §7er blevet jailed af §c" + player.getName() + " §7i §c" + time + " minutter!§7for §c" + jailReason.getReason() + "§c(" + block + ")!");
        CooldownJail.add(target, block + "jail", (time * 60), System.currentTimeMillis(), jailReason.getReason());
        player.setMetadata("jail", new FixedMetadataValue(pl, true));
    }

    public boolean unJailPlayer(String[] args, Player player) {
        if (args.length == 1) {
            Player target = player.getServer().getPlayer(args[0]);


            if (target == null) {
                target = getOfflinePlayer(args, player);
                if (target == null) return true;
            }
            String block = jailUtils.getBlock(target);
            if (!CooldownJail.isCooling(target, block + "jail")) {
                player.sendMessage("§cDen spiller er ikke jailed!");
                return true;
            }
            CooldownJail.removeCooldown(target, block + "jail");
            player.sendMessage("§8[§4§lISOLATION§8] §aDu har unjailet " + target.getName());
            if (target.hasPermission("silentunjailed")) {
                target.sendMessage("§8[§4§lISOLATION§8] §aDu er blevet unjailet af §a" + player.getName());

            } else {
                Bukkit.broadcastMessage("§8[§4§lISOLATION§8] §a" + player.getName() + " §7har unJailed §a" + target.getName());
            }
            target.teleport(hlWarp.getWarpManager().getWarpInfo(block.toLowerCase() + "-spawn").getLocation());
            target.removeMetadata("jail", pl);

            return true;
        }
        return false;
    }

    public Player getOfflinePlayer(String[] args, Player player) {
        Player target;
        OfflinePlayer offlinePlayer = player.getServer().getOfflinePlayer(args[0]);
        if (!offlinePlayer.hasPlayedBefore()) {
            player.sendMessage("§cDen spiller har ikke spillet før!");
            return null;
        }
        target = jailUtils.getOfflinePlayer(offlinePlayer.getUniqueId());
        if (target == null) {
            player.sendMessage("§cDen spiller har ikke spillet før!");
            return null;
        }
        return target;
    }

    public void jailPlayerOffline(Player target, JailReason reason, Player player, String block) {
        String blockMessage = "offline";
        int time = reason.getTime() + 8;

        CooldownJail.add(target, block + "jail", (time * 60), System.currentTimeMillis(), reason.getReason());
        AbilityCooldown abilityCooldown = cooldown.getAbilityCooldown(target);
        jailUtils.addJailedOfflinePlayer(target, abilityCooldown);
        jailUtils.addJailedOfflinePlayerTime(target.getName(), (int) CooldownJail.getRemaining(target, block + "jail"));
        CooldownJail.removeCooldown(target, block + "jail");
        Bukkit.broadcastMessage("§8[§4§lISOLATION§8] §c" + target.getName() + " §7er blevet jailed af §c" + player.getName() + " §7i §c" + time + " minutter!§7for §c" + reason.getReason() + "§c(" + blockMessage + ")!");


    }
}
