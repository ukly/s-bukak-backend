package com.sbukak.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

public class Utils {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    // LocalDateTime -> "yyyy.MM.dd HH:mm" 형식으로 변환
    public static String dateTimeToFormat(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return dateTime.format(formatter);
    }

    // LocalDateTime -> "yyyy.MM.dd" 형식으로 변환
    public static String dateTimeToDateFormat(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
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

    public static String createRedirectUrl(String clientUrl, String path, Map<String, String> dataMap) {
        try {
            String jsonData = objectMapper.writeValueAsString(dataMap);
            String encodedData = Base64.getUrlEncoder().encodeToString(jsonData.getBytes(StandardCharsets.UTF_8));

            return UriComponentsBuilder.fromHttpUrl(clientUrl + path)
                    .queryParam("data", encodedData)
                    .build().toUriString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create redirect URL", e);
        }
    }

    public static LocalDateTime StringToDateTime(String startDate, String startTime) {
        return LocalDateTime.of(LocalDate.parse(startDate), LocalTime.parse(startTime));
    }

    // LocalDate -> 24/11/05
    public static String dateToStringFormat(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
        return localDate.format(formatter);
    }

    // 24/11/05 -> LocalDate
    public static LocalDate dateStringToDateFormat(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
        return LocalDate.parse(date, formatter);
    }

    // LocaDateTime -> 24/11/18 (월) 17시
    public static String dateTimeToBoardFormat(LocalDateTime dateTime) {
        String[] koreanDays = {"월", "화", "수", "목", "금", "토", "일"};
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        String koreanDay = koreanDays[dayOfWeek.getValue() - 1];

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yy/MM/dd");
        String formattedDate = dateTime.format(dateFormatter);

        return String.format("%s (%s) %d시", formattedDate, koreanDay, dateTime.getHour());
    }
}
