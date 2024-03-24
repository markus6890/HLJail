package com.gmail.markushygedombrowski.commands;

import com.gmail.markushygedombrowski.gui.KauktionsGUI;
import com.gmail.markushygedombrowski.jailTime.CooldownJail;
import com.gmail.markushygedombrowski.jailUtils.JailUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KauktionCommand implements CommandExecutor {
    private KauktionsGUI kauktionsGUI;
    private JailUtils jailUtils;

    public KauktionCommand(KauktionsGUI kauktionsGUI, JailUtils jailUtils) {
        this.kauktionsGUI = kauktionsGUI;
        this.jailUtils = jailUtils;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDu skal være en spiller for at kunne bruge denne kommando");
            return true;
        }
        Player player = (Player) sender;
        String block = jailUtils.getBlock(player);
        if (player.hasPermission("vagt") || CooldownJail.isCooling(player, block + "jail")) {
            player.sendMessage("§cDu har ikke tilladelse til at bruge denne kommando");
            return true;
        }

        kauktionsGUI.create(player);

        return true;
    }
}
