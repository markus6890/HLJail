package com.gmail.markushygedombrowski.jailTime;

import java.text.DecimalFormat;

public class UtilMath {

    public static double trim(double untrimmeded, int decimal) {
        String format = "#.#";

        for(int i = 1; i < decimal; i++) {
            format = format + "#";
        }
        DecimalFormat twoDec = new DecimalFormat(format);
        return Double.valueOf(twoDec.format(untrimmeded)).doubleValue();
    }
}
