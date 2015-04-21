package com.hebut.framework.ui.common;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Window;

import com.hebut.framework.util.SessionUtil;
import com.hebut.framework.vo.UserInfo;

@SuppressWarnings("serial")
public abstract class PopWindow extends Window implements AfterCompose {

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
		if (this.getFellowIfAny("submit") != null) {
			this.addForward(Events.ON_OK, this.getFellow("submit"), Events.ON_CLICK);
		}
		if (this.getFellowIfAny("cancel") != null) {
			this.addForward(Events.ON_CANCEL, this.getFellow("cancel"), Events.ON_CLICK);
		}
	}
}
