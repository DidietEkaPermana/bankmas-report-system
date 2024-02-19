package com.bankmas.report.webapi.common;

public class StringUtil {

	public static boolean isNullOrEmpty(String string) {
		boolean result = false;

		if (string == null) {
			result = true;
		} else {
			if (string.isEmpty() || "".equals(string))
				result = true;
		}

		return result;
	}
	
}
