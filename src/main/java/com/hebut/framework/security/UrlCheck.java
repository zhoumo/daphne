package com.hebut.framework.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.util.AntUrlPathMatcher;
import org.springframework.security.web.util.UrlMatcher;

import com.hebut.framework.ui.common.BaseWindow;
import com.hebut.framework.vo.UserInfo;
import com.hebut.rbac.core.Validator;

public class UrlCheck {

	private static List<String> defaultURL = new ArrayList<String>(Arrays.asList(BaseWindow.SYSTEM_LOGIN, BaseWindow.SYSTEM_TIMEOUT));

	public static boolean check(String requestUrl, HttpSession session) {
		UrlMatcher urlMatcher = new AntUrlPathMatcher();
		String authority = "";
		if (session.getAttribute("userInfo") != null) {
			authority = ((UserInfo) session.getAttribute("userInfo")).getAuthority();
			defaultURL.add(BaseWindow.SYSTEM_DESKTOP);
		} else {
			defaultURL.remove(BaseWindow.SYSTEM_DESKTOP);
		}
		if (!StringUtils.isEmpty(authority) && Validator.urlValidator(authority, requestUrl)) {
			return true;
		}
		for (String url : defaultURL) {
			if (urlMatcher.pathMatchesUrl(url, requestUrl)) {
				return true;
			}
		}
		return false;
	}
}
