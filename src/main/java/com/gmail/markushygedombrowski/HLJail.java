package com.gmail.markushygedombrowski;

import com.gmail.markushygedombrowski.commands.JailCommand;
import com.gmail.markushygedombrowski.commands.Jailing;
import com.gmail.markushygedombrowski.commands.KauktionCommand;
import com.gmail.markushygedombrowski.gui.KauktionsGUI;
import com.gmail.markushygedombrowski.jailTime.CooldownJail;
import com.gmail.markushygedombrowski.jailUtils.HotBarMessage;
import com.gmail.markushygedombrowski.jailUtils.JailUtils;
import com.gmail.markushygedombrowski.listener.JailListner;
import com.gmail.markushygedombrowski.warp.WarpManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HLJail extends JavaPlugin {
    public Economy econ = null;
    private JailUtils jailUtils;
    private HLWarp hlWarp;
    private WarpManager warpManager;
    private CooldownJail cooldown;
    private HLUtils hlUtils;

    public static StateFlag JAIL_FLAG;

    @Override
    public void onLoad() {
        super.onLoad();
        FlagRegistry registry = WorldGuardPlugin.inst().getFlagRegistry();
        try {
            JAIL_FLAG = new StateFlag("jail", true);
            registry.register(JAIL_FLAG);
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("jail");
            if (existing instanceof StateFlag) {
                JAIL_FLAG = (StateFlag) existing;
            } else {
                // oh no! some other plugin registered a non-state flag with the same name!
                // you should probably disable this plugin or something
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        hlWarp = HLWarp.getInstance();
        warpManager = hlWarp.getWarpManager();
        jailUtils = new JailUtils();
        HotBarMessage hotBarMessage = new HotBarMessage();
        cooldown = new CooldownJail(warpManager, hotBarMessage, jailUtils);
        getServer().getPluginManager().registerEvents(new JailListner(jailUtils, cooldown,hlWarp, this), this);
        Jailing jailing = new Jailing(hlWarp, this, jailUtils, cooldown);
        getCommand("jail").setExecutor(new JailCommand(jailUtils, hlWarp, this, jailing));
        KauktionsGUI kauktionsGUI = new KauktionsGUI(this, hlWarp, jailUtils);
        getServer().getPluginManager().registerEvents(kauktionsGUI, this);

        KauktionCommand kauktionCommand = new KauktionCommand(kauktionsGUI, jailUtils);
        getCommand("k").setExecutor(kauktionCommand);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                cooldown.handleCooldownsJail();
            }
        }, 0L, 1L);

        getLogger().info("HLJail has been enabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void unJailAllOnDisable() {
        CooldownJail.cooldownPlayers.forEach((player, abilityCooldown) -> {
            String block = jailUtils.getBlock(player);
            CooldownJail.removeCooldown(player, block + "jail");
            player.teleport(hlWarp.getWarpManager().getWarpInfo(block + "-spawn").getLocation());
        });

    }

    @Override
    public void onDisable() {
        unJailAllOnDisable();
        getLogger().info("HLJail has been disabled!");
    }
}
