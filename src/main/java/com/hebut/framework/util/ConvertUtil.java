package com.hebut.framework.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtil {

	private static DateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static Date getDate(String str) {
		try {
			return defaultDateFormat.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getDateString(Date date) {
		return defaultDateFormat.format(date);
	}
}
