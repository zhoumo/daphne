package com.hebut.framework.ui.manage.actor;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

import com.hebut.framework.service.ManageService;

public class RoleTreeRenderer implements TreeitemRenderer {

	private ManageService manageService;

	private EventListener clickEventListener;

	public RoleTreeRenderer(ManageService manageService, EventListener clickEventListener) {
		super();
		this.manageService = manageService;
		this.clickEventListener = clickEventListener;
	}

	@Override
	public void render(Treeitem item, Object data) throws Exception {
		String[] roleInfo = (String[]) data;
		Treerow row = new Treerow();
		{
			Treecell c0 = new Treecell();
			c0.appendChild(new Label(roleInfo[0]));
			c0.setParent(row);
			Treecell c1 = new Treecell();
			Integer number = manageService.countUsersByRoleKey(roleInfo[1]);
			c1.appendChild(new Label(number.toString()));
			c1.setParent(row);
			item.setAttribute("number", number);
		}
		item.appendChild(row);
		item.setValue(roleInfo);
		item.setOpen(true);
		item.setSelected(Boolean.parseBoolean(roleInfo[2]));
		if (this.clickEventListener != null) {
			item.addEventListener(Events.ON_CLICK, this.clickEventListener);
		}
	}
}
