package com.hebut.framework.security;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.util.AntUrlPathMatcher;
import org.springframework.security.web.util.UrlMatcher;

import com.hebut.framework.ui.common.UrlManager;
import com.hebut.framework.vo.UserInfo;
import com.hebut.rbac.core.Validator;

public class UrlCheck {

	private static String[] defaultURL = { UrlManager.SYSTEM_LOGIN, UrlManager.SYSTEM_DESKTOP, UrlManager.SYSTEM_TIMEOUT };

	public static boolean check(String requestUrl, HttpSession session) {
		UrlMatcher urlMatcher = new AntUrlPathMatcher();
		String authority = "";
		if (session.getAttribute("userInfo") != null) {
			authority = ((UserInfo) session.getAttribute("userInfo")).getAuthority();
		}
		if (!StringUtils.isEmpty(authority) && Validator.urlValidator(authority, requestUrl)) {
			return true;
		}
		for (int i = 0; i < defaultURL.length; i++) {
			if (urlMatcher.pathMatchesUrl(defaultURL[i], requestUrl)) {
				return true;
			}
		}
		return false;
	}
}
