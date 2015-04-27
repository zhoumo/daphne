package mine.daphne.security;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;

public class ComponentCheck {

	public static Component createComponents(Component component, String uri, Component parent, Map<Object, Object> arg) {
		if (UrlCheck.check(uri, (HttpSession) Sessions.getCurrent().getNativeSession())) {
			return org.zkoss.zk.ui.Executions.createComponents(uri, parent, arg);
		} else {
			throw new WrongValueException(component, "无权访问！");
		}
	}
}
