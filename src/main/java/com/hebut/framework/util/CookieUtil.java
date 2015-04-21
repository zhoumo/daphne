package com.hebut.framework.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Executions;

public class CookieUtil {

	public static String BROWSER_TYPE = "BROWSER_TYPE";

	public static String BROWSER_HEIGHT = "BROWSER_HEIGHT";

	public static String getCookie(String key) {
		Cookie[] cookies = ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (key.equals(cookies[i].getName())) {
					String value = cookies[i].getValue();
					return value;
				}
			}
		}
		return "";
	}

	public static void setCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60 * 24 * 30);
		String path = Executions.getCurrent().getContextPath();
		cookie.setPath(path);
		((HttpServletResponse) Executions.getCurrent().getNativeResponse()).addCookie(cookie);
	}

	public static String getBrowserType() {
		return getCookie(BROWSER_TYPE);
	}

	public static int getMaxRowNumber() {
		int maxNum = 6;
		if (!"".equals(getCookie(BROWSER_HEIGHT))) {
			maxNum = (Integer.parseInt(getCookie(BROWSER_HEIGHT)) - 30) / 85;
		}
		return maxNum;
	}
}
