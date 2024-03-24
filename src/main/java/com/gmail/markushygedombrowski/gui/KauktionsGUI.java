package com.gmail.markushygedombrowski.gui;

import com.gmail.markushygedombrowski.HLJail;
import com.gmail.markushygedombrowski.HLWarp;
import com.gmail.markushygedombrowski.jailTime.AbilityCooldown;
import com.gmail.markushygedombrowski.jailTime.CooldownJail;
import com.gmail.markushygedombrowski.jailUtils.JailUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KauktionsGUI implements org.bukkit.event.Listener {
    private HLJail plugin;
    private HLWarp hlWarp;
    private JailUtils jailUtils;

    public KauktionsGUI(HLJail plugin, HLWarp hlWarp, JailUtils jailUtils) {
        this.plugin = plugin;
        this.hlWarp = hlWarp;
        this.jailUtils = jailUtils;
    }

    public void create(Player player) {
        String block = jailUtils.getBlock(player);
        if(CooldownJail.getCooldownPlayers(block + "jail").isEmpty()) {
            player.sendMessage("§7[§a§lKauktion§7] §cDer er ikke nogle i jail");
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 54, "Kauktion");
        int[] playerMoney = new int[1];
        setPrice(player, playerMoney);
        setSkulls(inventory, playerMoney,player);
        player.openInventory(inventory);
        update(player,inventory);

    }

    private void setPrice(Player player, int[] playerMoney) {

        if(player.hasPermission("a-fange")) {
            playerMoney[0] = 1000;
        } else if(player.hasPermission("b-fange")) {
            playerMoney[0] = 350;
        } else {
            playerMoney[0] = 150;
        }

    }

    private void setSkulls(Inventory inventory, int[] playerMoney,Player player) {
        String block = jailUtils.getBlock(player);
        Map<Player, AbilityCooldown> jailedPlayers = CooldownJail.getCooldownPlayers(block + "jail");
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        int[] invint = {0};
        jailedPlayers.forEach((jailedPlayer, ability) -> {
            int time = (int) CooldownJail.getRemaining(jailedPlayer, block + "jail");
            SkullMeta skullMeta = (SkullMeta)  skull.getItemMeta();
            skullMeta.setOwner(jailedPlayer.getName());
            skullMeta.setDisplayName("§c" + jailedPlayer.getName());

            String jailedBlock = jailUtils.getBlock(jailedPlayer);

            int minutes = time / 60;

            skullMeta.setLore(setLore(time,minutes,playerMoney,ability,jailedBlock));
            skull.setItemMeta(skullMeta);
            ItemStack air = inventory.getItem(invint[0]);
            if (air == null || air.getType() == Material.AIR) {
                inventory.setItem(invint[0], skull);
            }
            invint[0] = invint[0] + 1;
        });

    }

    private void update(Player player,Inventory inventory) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!player.getOpenInventory().getTitle().equals("Kauktion")) {
                    cancel();
                    return;
                }

                updateMeta(inventory,player);
            }
        }.runTaskTimer(plugin, 0, 20);


    }
    private void updateMeta(Inventory inventory,Player player) {
        inventory.forEach(itemStack -> {
            if(itemStack != null && itemStack.getType() != Material.AIR) {
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                Player jailedPlayer = Bukkit.getPlayer(skullMeta.getDisplayName().substring(2));
                if(jailedPlayer == null) {
                    return;
                }
                if(!CooldownJail.isCooling(jailedPlayer, jailUtils.getBlock(player) + "jail")) {
                    inventory.remove(itemStack);
                    return;
                }

                int[] playerMoney = new int[1];
                setPrice(player, playerMoney);

                int seconds = (int) CooldownJail.getRemaining(Bukkit.getPlayer(skullMeta.getDisplayName().substring(2)), jailUtils.getBlock(jailedPlayer) + "jail");
                int minutes = seconds / 60;
                AbilityCooldown ability = CooldownJail.getCooldownPlayers(jailUtils.getBlock(jailedPlayer) + "jail").get(jailedPlayer);
                String jailedBlock = jailUtils.getBlock(jailedPlayer);
                skullMeta.setLore(setLore(seconds,minutes,playerMoney,ability,jailedBlock));
                itemStack.setItemMeta(skullMeta);
            }
        });
    }


    private List<String> setLore(int seconds,int miniutes,int[] playerMoney,AbilityCooldown ability,String jailedBlock) {
        List<String> lore = new ArrayList<>();
        playerMoney[0] = playerMoney[0] * seconds;
        seconds = seconds % 60;
        lore.add(0,"§7Grund: §c" + ability.cooldownMap.get(jailedBlock + "jail").getReason());
        lore.add(1,"§7Kauktion: §a" + playerMoney[0] + "$");
        if(miniutes > 0) {
            lore.add(2,"§7Tid: §e" + miniutes + " minuter " + seconds + " sekunder");
        } else {
            lore.add(2,"§7Tid: §e" + seconds + " sekunder");
        }
        return lore;
    }
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack clickeditem = event.getCurrentItem();
        int clickedSlot = event.getRawSlot();
        if (clickeditem == null) {
            return;
        }
        if(inventory.getTitle().equalsIgnoreCase("Kauktion")) {
            event.setCancelled(true);
            if(clickeditem.getType() == Material.SKULL_ITEM) {
                SkullMeta skullMeta = (SkullMeta) clickeditem.getItemMeta();
                Player jailedPlayer = Bukkit.getPlayer(skullMeta.getDisplayName().substring(2));
                if(jailedPlayer == null) {
                    p.sendMessage("§7[§a§lKauktion§7] §cSpilleren er ikke online");
                    return;
                }
                int seconds = (int) CooldownJail.getRemaining(jailedPlayer, jailUtils.getBlock(jailedPlayer) + "jail");
                int[] playerMoney = new int[1];
                setPrice(p, playerMoney);
                int price = playerMoney[0] * seconds;
                if(plugin.econ.getBalance(p) < price) {
                    p.sendMessage("§7[§a§lKauktion§7] §cDu har ikke nok penge");
                    return;
                }
                plugin.econ.withdrawPlayer(p, price);
                CooldownJail.removeCooldown(jailedPlayer, jailUtils.getBlock(jailedPlayer) + "jail");
                p.sendMessage("§7[§a§lKauktion§7] §7Du har betalt §a§l" + price + "$ §7for at få §c§l" + jailedPlayer.getName() + " §7ud af jail");
                jailedPlayer.sendMessage("§§7[§a§lKauktion§7] §2§l" + p.getName() + " §7har betalt §a§l" + price + "$ §7for at få dig ud af jail");
                Bukkit.broadcastMessage("§7[§a§lKauktion§7] §2§l" + p.getName() + " §7har betalt §a§l" + price + "$ §7for at få §c§l" + jailedPlayer.getName() + " §7ud af jail");
                jailedPlayer.teleport(hlWarp.getWarpManager().getWarpInfo(jailUtils.getBlock(jailedPlayer).toLowerCase() + "-spawn").getLocation());
                jailedPlayer.removeMetadata("jail", plugin);
                p.closeInventory();
            }
        }

    }


}
