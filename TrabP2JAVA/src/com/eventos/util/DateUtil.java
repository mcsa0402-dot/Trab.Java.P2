package com.eventos.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DB_FMT      = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtil() {}

    public static String format(LocalDateTime dt) {
        return dt == null ? "" : dt.format(DISPLAY_FMT);
    }

    public static LocalDateTime parse(String text) {
        if (text == null || text.isBlank()) return null;
        return LocalDateTime.parse(text.trim(), DISPLAY_FMT);
    }
}