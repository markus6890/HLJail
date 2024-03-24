package com.gmail.markushygedombrowski.jailTime;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class AbilityCooldown {
    public String ability = "";
    public Player player;
    public long seconds;
    public long systime;
    public String reason = "";

    public AbilityCooldown(Player player, long seconds, long systime,String reason) {
        this.player = player;
        this.seconds = seconds;
        this.systime = systime;
        this.reason = reason;
    }
    public AbilityCooldown(Player player) {
        this.player = player;
    }
    public HashMap<String, AbilityCooldown> cooldownMap = new HashMap<>();
    public String getAbility() {
        return ability;
    }
    public String getReason() {
        return reason;
    }


}
