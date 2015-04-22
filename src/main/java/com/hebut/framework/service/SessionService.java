package com.hebut.framework.service;

import java.util.Date;
import java.util.Iterator;

import org.zkoss.zk.ui.Sessions;

import com.hebut.framework.model.vo.UserInfo;
import com.hebut.framework.ui.common.BaseWindow;

public class SessionService {

	public static void createUserInfoSession(UserInfo userInfo) {
		Sessions.getCurrent().setAttribute("userInfo", userInfo);
	}

	public static UserInfo getUserInfoSession() {
		return (UserInfo) Sessions.getCurrent().getAttribute("userInfo");
	}

	public static void createWindowSession(String key, BaseWindow baseWindow) {
		Sessions.getCurrent().setAttribute(key, baseWindow);
	}

	public static void removeWindowSession(String key) {
		Sessions.getCurrent().removeAttribute(key);
	}

	public static void clearWindowSessions() {
		Iterator<?> iterator = Sessions.getCurrent().getAttributes().keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			if (Sessions.getCurrent().getAttributes().get(key) instanceof BaseWindow) {
				Sessions.getCurrent().removeAttribute(key.toString());
			}
		}
	}

	public static BaseWindow getWindowInSession(String key) {
		return (BaseWindow) Sessions.getCurrent().getAttribute(key);
	}

	public static void createLoginTimestampSession(Date loginTimestamp) {
		Sessions.getCurrent().setAttribute("loginTimestamp", loginTimestamp);
	}

	public static int getZindex() {
		Long diff = new Date().getTime() - ((Date) Sessions.getCurrent().getAttribute("loginTimestamp")).getTime();
		return diff.intValue();
	}
}
