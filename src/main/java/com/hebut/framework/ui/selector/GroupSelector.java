package com.hebut.framework.ui.selector;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.hebut.framework.entity.FwkGroup;
import com.hebut.framework.service.ManageService;

@SuppressWarnings("serial")
public class GroupSelector extends Listbox {

	private ManageService manageService;

	public GroupSelector(ManageService manageService, Component parent) {
		super();
		this.manageService = manageService;
		this.setParent(parent);
		this.setMold("select");
		this.setWidth("250px");
		this.setItemRenderer(new ListitemRenderer() {

			@Override
			public void render(Listitem item, Object data) throws Exception {
				FwkGroup group = (FwkGroup) data;
				String blank = "| ";
				for (int i = 0; i < group.getFgLevel(); i++) {
					blank += "----";
				}
				item.setLabel(blank + group.getFgName());
				item.setValue(group);
			}
		});
	}

	public void initAllGroup() {
		this.setModel(new ListModelList(this.manageService.buildGroupTree()));
		if (this.getItemCount() != 0) {
			this.getItemAtIndex(0).setSelected(true);
			Events.postEvent(Events.ON_SELECT, this, null);
		}
	}
}
