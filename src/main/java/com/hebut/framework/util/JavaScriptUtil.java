package com.hebut.framework.util;

import org.zkoss.zk.ui.util.Clients;

public class JavaScriptUtil {

	public static String IE6 = "MSIE6.0";

	public static String FIREFOX = "Firefox";

	public static String DEFAULT = "";

	public static void setBrowserParameters() {
		StringBuilder javascript = new StringBuilder();
		javascript.append("if(navigator.userAgent.indexOf('MSIE 6.0') > 0) document.cookie = \"BROWSER_TYPE=" + IE6 + "\";");
		javascript.append("else if(navigator.userAgent.indexOf('Firefox') > 0) document.cookie = \"BROWSER_TYPE=" + FIREFOX + "\";");
		javascript.append("else document.cookie = \"" + CookieUtil.BROWSER_TYPE + "=" + DEFAULT + "\";");
		javascript.append("document.cookie = \"" + CookieUtil.BROWSER_HEIGHT + "=\" + document.body.clientHeight;");
		Clients.evalJavaScript(javascript.toString());
	}
}
