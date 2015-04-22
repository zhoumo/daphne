package com.hebut.framework.ui.manage.member;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import com.hebut.framework.model.entity.FwkGroup;
import com.hebut.framework.service.ManageService;
import com.hebut.framework.ui.common.PopWindow;

@SuppressWarnings("serial")
public class GroupWindow extends PopWindow {

	private Textbox groupName;

	private Listbox groupType;

	private ManageService manageService;

	public void initWindow() {
		if (!Boolean.parseBoolean(this.getAttribute("isNew").toString())) {
			FwkGroup group = (FwkGroup) this.getAttribute("group");
			this.groupName.setValue(group.getFgName());
			this.groupType.setSelectedIndex(group.getFgType().intValue());
		}
	}

	public void onClick$submit() {
		FwkGroup group = (FwkGroup) this.getAttribute("group");
		group.setFgName(this.groupName.getValue());
		group.setFgType(Short.parseShort(this.groupType.getSelectedIndex() + ""));
		if (Boolean.parseBoolean(this.getAttribute("isNew").toString())) {
			FwkGroup parent = (FwkGroup) this.getAttribute("parent");
			group.setParent(parent);
			if (parent == null) {
				group.setFgLevel(0);
			} else {
				group.setFgLevel(parent.getFgLevel() + 1);
			}
		}
		this.manageService.saveOrUpdate(group);
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}

	public void onClick$cancel() {
		this.detach();
	}
}
