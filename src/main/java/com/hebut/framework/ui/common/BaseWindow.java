package com.hebut.framework.ui.common;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

import com.hebut.framework.util.SessionUtil;
import com.hebut.framework.vo.UserInfo;

@SuppressWarnings("serial")
public abstract class BaseWindow extends Window implements AfterCompose {

	public static String SYSTEM_LOGIN = "/login.zul";

	public static String SYSTEM_DESKTOP = "/index.zul";

	public static String SYSTEM_IFRAME = "/framework/iframe.zul";

	public static String SYSTEM_TIMEOUT = "/framework/timeout.zul";

	public static String MANAGE_GROUP = "/framework/manage/member/group.zul";

	public static String MANAGE_ROLE = "/framework/manage/member/role.zul";

	public static String MANAGE_USER = "/framework/manage/member/user.zul";

	public static String SELECTOR_USER = "/framework/selector/userSelector.zul";

	private UserInfo userInfo = SessionUtil.getUserInfoSession();

	public UserInfo getUserInfo() {
		return userInfo;
	}

	@Override
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		this.setClosable(true);
		this.setBorder("normal");
		this.setPosition("center,center");
		this.setZIndex(SessionUtil.getZindex());
		this.addEventListener(Events.ON_CLOSE, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				Window window = (Window) event.getTarget();
				SessionUtil.removeWindowSession(window.getTitle());
			}
		});
		this.initWindow();
	}

	public abstract void initWindow();

	public boolean hasItemSelected(Object object) {
		if (object instanceof Listbox) {
			if (((Listbox) object).getSelectedCount() != 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if (((Tree) object).getSelectedCount() != 0) {
				return true;
			} else {
				return false;
			}
		}
	}
}
