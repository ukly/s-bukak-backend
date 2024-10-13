package com.sbukak.global.util;

import java.time.LocalDateTime;

public class Utils {

    // LocalDatetime(2024-05-22T12:47:17.554149) -> 2024.03.23 19:33
    public static String dateTimeToBoardFormat(LocalDateTime dateTime) {
        String str = dateTime.toString();
        return new StringBuilder()
            .append(str, 0, 10)
            .append(" ")
            .append(str, 11, 16)
            .toString();
    }

    // LocalDatetime -> 03/23 15:35
    public static String dateTimeToChatFormat(LocalDateTime dateTime) {
        String str = dateTime.toString();
        return new StringBuilder()
            .append(str, 5, 7)
            .append("/")
            .append(str, 8, 10)
            .append(" ")
            .append(str, 11, 16)
            .toString();
    }
}
