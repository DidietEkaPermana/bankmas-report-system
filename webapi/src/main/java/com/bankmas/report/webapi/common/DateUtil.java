package com.bankmas.report.webapi.common;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

public class DateUtil {
	public static Timestamp getTodayDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
		
		return new Timestamp(calendar.getTimeInMillis());
    }
}
