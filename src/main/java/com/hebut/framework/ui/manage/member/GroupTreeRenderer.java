package com.hebut.framework.ui.manage.member;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

import com.hebut.framework.entity.FwkGroup;
import com.hebut.framework.service.ManageService;

public class GroupTreeRenderer implements TreeitemRenderer {

	private ManageService manageService;

	private EventListener clickEventListener;

	private EventListener doubleClickEventListener;

	public GroupTreeRenderer(ManageService manageService, EventListener clickEventListener, EventListener doubleClickEventListener) {
		super();
		this.manageService = manageService;
		this.clickEventListener = clickEventListener;
		this.doubleClickEventListener = doubleClickEventListener;
	}

	@Override
	public void render(Treeitem item, Object data) throws Exception {
		FwkGroup group = (FwkGroup) data;
		Treerow row = new Treerow();
		{
			Treecell c0 = new Treecell();
			c0.appendChild(new Label(group.getFgName()));
			c0.setParent(row);
			Treecell c1 = new Treecell();
			Integer number = this.manageService.countUsersByGroup(group);
			c1.appendChild(new Label(number.toString()));
			c1.setParent(row);
			item.setAttribute("number", number);
		}
		item.appendChild(row);
		item.setValue(group);
		item.setOpen(true);
		if (group.getSelected().booleanValue()) {
			item.setSelected(true);
		}
		if (this.clickEventListener != null) {
			item.addEventListener(Events.ON_CLICK, this.clickEventListener);
		}
		if (this.doubleClickEventListener != null) {
			item.addEventListener(Events.ON_DOUBLE_CLICK, this.doubleClickEventListener);
		}
	}
}
