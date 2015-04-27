package mine.daphne.service;

import java.util.Date;
import java.util.Iterator;

import mine.daphne.model.vo.UserInfo;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

public class SessionService {

	public static void createUserInfoSession(UserInfo userInfo) {
		Sessions.getCurrent().setAttribute("userInfo", userInfo);
	}

	public static UserInfo getUserInfoSession() {
		return (UserInfo) Sessions.getCurrent().getAttribute("userInfo");
	}

	public static void createWindowSession(String key, Window window) {
		Sessions.getCurrent().setAttribute(key, window);
	}

	public static void removeWindowSession(String key) {
		Sessions.getCurrent().removeAttribute(key);
	}

	public static void clearWindowSessions() {
		Iterator<?> iterator = Sessions.getCurrent().getAttributes().keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			if (Sessions.getCurrent().getAttributes().get(key) instanceof Window) {
				Sessions.getCurrent().removeAttribute(key.toString());
			}
		}
	}

	public static Window getWindowInSession(String key) {
		return (Window) Sessions.getCurrent().getAttribute(key);
	}

	public static void createLoginTimestampSession(Date loginTimestamp) {
		Sessions.getCurrent().setAttribute("loginTimestamp", loginTimestamp);
	}

	public static int getZindex() {
		Object obj = Sessions.getCurrent().getAttribute("loginTimestamp");
		if (obj == null) {
			return 0;
		} else {
			Long diff = new Date().getTime() - ((Date) Sessions.getCurrent().getAttribute("loginTimestamp")).getTime();
			return diff.intValue();
		}
	}
}
