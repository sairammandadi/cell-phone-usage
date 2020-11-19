package com.company.cellphone.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class DateFormatter {

	private static final Logger logger = Logger.getLogger(DateFormatter.class);
	private static final String[] DATE_FORMATTES = { "yyyy-MM-dd'T'hh:mm:ss", "yyyy-MM-dd HH:mm:ss", "MM/dd/yy hh:mm a", "MM/dd/yy  hh:mm a",
			"MM/dd/yy", "yyyy-MM-dd hh:mm a", "yyyy-MM-dd hh a", "yyyy-MM-dd","MM-dd-yyyy" };

	private DateFormatter() {
		/**
		 * default
		 */
	}

	public static Date convertToDate(String dateStr) {
		if(StringUtils.isBlank(dateStr)) {
			return null;
		}
		for (String formatString : DATE_FORMATTES) {
			try {
				return new SimpleDateFormat(formatString).parse(dateStr);
			} catch (ParseException e) {
				// intentionally left empty
				// because if it fails, it will try another format from an array
			}
		}
		return null;
	}
}
