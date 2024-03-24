package com.gmail.markushygedombrowski.jailTime;




public class UtilTime {


    private static String timestamp;

    public static double convert(long time, TimeUnit unit, int decPoint) {
        if(unit == TimeUnit.BEST) {
            if(time < 60000L) {
                unit = TimeUnit.SECONDS;
                timestamp = "sekunder";
            }
            else if(time < 3600000L){
                unit = TimeUnit.MINUTES;
                timestamp = "minutter";
            }
            else if(time < 86400000L) {
                unit = TimeUnit.HOURS;
                timestamp = "timer";
            }
            else unit = TimeUnit.DAYS;
        }
        if(unit == TimeUnit.SECONDS) return UtilMath.trim(time / 1000.0D, decPoint);
        if(unit == TimeUnit.MINUTES) return UtilMath.trim(time / 60000.0D, decPoint);
        if(unit == TimeUnit.HOURS) return UtilMath.trim(time / 3600000.0D, decPoint);
        if(unit == TimeUnit.DAYS) return UtilMath.trim(time / 86400000.0D, decPoint);
        return UtilMath.trim(time, decPoint);
    }


    public static String getTimestamp() {
        return timestamp;
    }


}



