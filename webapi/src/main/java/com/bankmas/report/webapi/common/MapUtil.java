package com.bankmas.report.webapi.common;

import java.util.Map;

public class MapUtil {

	public static String getStringObject(Map<String, Object> map, String key, boolean allowNull) throws Exception {
		String result = "";

		Object obj = getObject(map, key, allowNull);
		try {
			if (obj != null)
				result = obj.toString();
		} catch (Exception ex) {
			throw new Exception(String.format("Cannot parse %s into string.", (String) obj));
		}

		return result;
	}
	
	public static Object getObject(Map<String, Object> map, String key, boolean allowNull) throws Exception {
		Object obj = null;

		// Check object map null or empty
		if (map == null || map.isEmpty() || map.size() == 0) {
			return null;
		}

		// try to get object
		obj = map.get(key);

		if (obj == null && !allowNull) {
			// check if data allowing null value
			throw new Exception("Value of " + key + " cannot null");

		}

		return obj;
	}
}
