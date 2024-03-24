package com.gmail.markushygedombrowski.commands;

import com.gmail.markushygedombrowski.HLJail;
import com.gmail.markushygedombrowski.HLWarp;
import com.gmail.markushygedombrowski.jailTime.CooldownJail;
import com.gmail.markushygedombrowski.jailUtils.JailReason;
import com.gmail.markushygedombrowski.jailUtils.JailUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JailCommand implements CommandExecutor {
    private JailUtils jailUtils;
    private HLWarp hlWarp;
    private HLJail pl;
    private Jailing jailPlayer;

    public JailCommand(JailUtils jailUtils, HLWarp hlWarp, HLJail pl, Jailing jailPlayer) {
        this.jailUtils = jailUtils;
        this.hlWarp = hlWarp;
        this.pl = pl;
        this.jailPlayer = jailPlayer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("jail")) {
            player.sendMessage("§cDet har du ikke permission til!");
            return true;

        }
        if (args.length == 0) {
            JailReason.printReasons(player);
            return true;
        }

        if (unJail(alias, args, player)) return true;
        if (args.length <= 1) {
            player.sendMessage("§8[§4§lISOLATION§8] §cBrug /jail <spiller> <grund>");
            player.sendMessage("§8[§4§lISOLATION§8] §cBrug /unjail <spiller>");
            return true;
        }
        if(alias.equalsIgnoreCase("removejailmeta")) {
            Player target = player.getServer().getPlayer(args[0]);
            if (target == null) {
                target = jailPlayer.getOfflinePlayer(args, player);
                if (target == null) {
                    return true;
                }
                target.removeMetadata("jail", pl);
                return true;
            }
            target.removeMetadata("jail", pl);
            return true;
        }
        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            target = jailPlayer.getOfflinePlayer(args, player);
            if (target == null) {
                return true;
            }
            String block = jailUtils.getBlock(target);
            String reason = args[1];
            JailReason jailReason = JailReason.getJailReason(reason);
            jailPlayer.jailPlayerOffline(target, jailReason, player, block);
            return true;
        }


        if (target.hasPermission("jail.bypass")) {
            player.sendMessage("§cDen spiller kan ikke blive jailed!");
            return true;
        }
        String block = jailUtils.getBlock(target);
        if (CooldownJail.isCooling(target, block + "jail")) {
            player.sendMessage("§8[§4§lISOLATION§8] §cDen spiller er allerede jailed!");
            return true;
        }
        jailPlayer.JailPlayer(args, player, target);

        return true;
    }

    private boolean unJail(String alias, String[] args, Player player) {
        if (alias.equalsIgnoreCase("unjail")) {
            if (args[0].equalsIgnoreCase("all")) {
                if (player.hasPermission("jail.unjailall")) {
                    unJailAll();
                    return true;
                }
                player.sendMessage("§cDu har ikke permission til at unjail alle!");
                return true;
            }
            if (jailPlayer.unJailPlayer(args, player)) return true;
            player.sendMessage("§cBrug /unjail <spiller>");
            return true;
        }
        return false;
    }



    private void unJailAll() {
        CooldownJail.cooldownPlayers.forEach((target, cooldown) -> {
            if (target == null) return;
            String block = jailUtils.getBlock(target);
            target.teleport(hlWarp.getWarpManager().getWarpInfo(block.toLowerCase() + "-spawn").getLocation());
            CooldownJail.removeCooldown(target, block + "jail");
            target.removeMetadata("jail", pl);
        });
        Bukkit.broadcastMessage("§8[§4§lISOLATION§8] §7Alle i Jail er blevet §2§lunJailed!");
    }




}
