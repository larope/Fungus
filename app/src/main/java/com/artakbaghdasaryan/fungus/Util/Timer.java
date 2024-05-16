package com.artakbaghdasaryan.fungus.Util;

import java.util.concurrent.TimeUnit;

public class Timer {
    public static String FormatTime(long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
