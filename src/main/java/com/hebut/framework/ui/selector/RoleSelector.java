package com.hebut.framework.ui.selector;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.hebut.rbac.core.CommonUtil;

@SuppressWarnings("serial")
public class RoleSelector extends Listbox {

	public RoleSelector(Component parent) {
		super();
		this.setParent(parent);
		this.setMold("select");
		this.setWidth("250px");
	}

	public void initAllRole() {
		for (String roleKey : CommonUtil.getRoleKeySet()) {
			Listitem item = new Listitem(CommonUtil.getRoleShowByRoleKey(roleKey));
			item.setValue(roleKey);
			item.setParent(this);
		}
		if (this.getItemCount() != 0) {
			this.getItemAtIndex(0).setSelected(true);
			Events.postEvent(Events.ON_SELECT, this, null);
		}
	}
}
