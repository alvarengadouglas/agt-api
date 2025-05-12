package com.betmotion.agentsmanagement.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Optional;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateFormatUtils {

	public static final String DATE_TIME_FORMAT_WITH_MILLISECONDS_GMT_UTC = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat formatter = new SimpleDateFormat();

	public static String formatDate(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	public static String format(String pattern, Temporal dateTime) {
		formatter.applyPattern(pattern);
		return DateTimeFormatter.ofPattern(pattern).format(dateTime);
	}
	public static String formatDateTime(LocalDateTime localDateTime) {
		if (Optional.ofNullable(localDateTime).isPresent()) {
			return localDateTime.format(CUSTOM_FORMATTER);
		}
		return null;
	}
	public static Date convertLocalDateTimeToDate(LocalDateTime dateToConvert) {
		if(dateToConvert == null) {
			return new Date();
		}

		return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
	}

}