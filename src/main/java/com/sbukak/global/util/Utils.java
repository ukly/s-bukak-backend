package com.sbukak.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {

    // LocalDateTime -> "yyyy.MM.dd HH:mm" 형식으로 변환
    public static String dateTimeToFormat(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return dateTime.format(formatter);
    }

    // LocalDateTime -> "MM/DD HH:mm" 형식으로 변환
    public static String dateTimeToChatFormat(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
        return dateTime.format(formatter);
    }

    // LocalDatetime -> 05월 03일 (금)
    public static String dateTimeToKoreanDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일 (E)", Locale.KOREAN);
        return dateTime.format(formatter);
    }

    // LocalDatetime -> 13:30
    public static String dateTimeToTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }
}
