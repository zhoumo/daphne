package com.hebut.framework.ui.manage.member;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.hebut.framework.entity.FwkUser;
import com.hebut.rbac.core.AuthorityParser;
import com.hebut.rbac.core.CommonUtil;

public class UserListRenderer implements ListitemRenderer {

	private EventListener doubleClickEventListener;

	public UserListRenderer(EventListener doubleClickEventListener) {
		super();
		this.doubleClickEventListener = doubleClickEventListener;
	}

	@Override
	public void render(Listitem item, Object data) throws Exception {
		FwkUser user = (FwkUser) data;
		Listcell c0 = new Listcell();
		Listcell c1 = new Listcell(user.getFuLoginName());
		Listcell c2 = new Listcell(user.getFuTrueName());
		String roles = "";
		for (String role : AuthorityParser.parseGetRoleKeysByGroup(user.getFuAuthority(), user.getCurrentGroup()).split("\\+")) {
			roles += CommonUtil.getRoleShowByRoleKey(role) + " ";
		}
		Listcell c3 = new Listcell(roles);
		item.setValue(user);
		item.appendChild(c0);
		item.appendChild(c1);
		item.appendChild(c2);
		item.appendChild(c3);
		if (this.doubleClickEventListener != null) {
			item.addEventListener(Events.ON_DOUBLE_CLICK, this.doubleClickEventListener);
		}
	}
}
