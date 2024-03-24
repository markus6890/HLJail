package com.gmail.markushygedombrowski.jailUtils;

import org.bukkit.entity.Player;

public enum JailReason {
    SLAG_VAGT("Slag på vagt",3, "sv"),
    SLAG_FANGE("Slag på fange",2, "s"),
    ARMOR("Armor",3, "a"),
    WEAPON("Våben",2, "v"),
    WEAPON_ARMOR("Våben og armor",5, "va"),
    ANDEN_UDLOVLIG_GENSTAND("Anden ulovlig genstand",5, "aug"),
    PROVOKERENDE_OPFORSLE("Provokerende opførsel",3, "pro"),
    VAGT_VAULT("Vagt vault",5, "vv"),
    NARKO("Narko",4, "n"),
    NOPVP("NoPvP",5, "np"),
    OVERTAGELSE("Overtrædelse",4, "o");



    private final String reason;
    private final int time;
    private final String shortReason;

    JailReason(String reason, int time, String shortReason) {
        this.reason = reason;
        this.time = time;
        this.shortReason = shortReason;
    }

    public static JailReason getJailReason(String reason) {
        for(JailReason jailReason : JailReason.values()) {
            if(jailReason.getReason().equalsIgnoreCase(reason) || jailReason.getShortReason().equalsIgnoreCase(reason)) {
                return jailReason;
            }
        }
        return null;
    }
    public static  void printReasons(Player player) {
        for(JailReason jailReason : JailReason.values()) {
            player.sendMessage("§8[§4§lISOLATION§8] §c" + jailReason.getReason() + " §7short: §c" + jailReason.getShortReason());
        }
    }
    public String getShortReason() {
        return shortReason;
    }

    public String getReason() {
        return reason;
    }
    public int getTime() {
        return time;
    }




}
