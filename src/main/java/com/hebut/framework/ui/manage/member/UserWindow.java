package com.hebut.framework.ui.manage.member;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;

import com.hebut.framework.model.entity.FwkGroup;
import com.hebut.framework.model.entity.FwkUser;
import com.hebut.framework.service.ManageService;
import com.hebut.framework.ui.common.PopWindow;

@SuppressWarnings("serial")
public class UserWindow extends PopWindow {

	private Textbox userLoginName, userName, userPassword;

	private ManageService manageService;

	public void initWindow() {
		if (!Boolean.parseBoolean(this.getAttribute("isNew").toString())) {
			FwkUser user = (FwkUser) this.getAttribute("user");
			this.userLoginName.setValue(user.getFuLoginName());
			this.userName.setValue(user.getFuTrueName());
			this.userPassword.setValue(user.getFuPassword());
		}
	}

	public void onClick$submit() {
		FwkUser user = (FwkUser) this.getAttribute("user");
		FwkGroup group = (FwkGroup) this.getAttribute("group");
		user.setFuLoginName(this.userLoginName.getValue());
		user.setFuTrueName(this.userName.getValue());
		user.setFuPassword(this.userPassword.getValue());
		user.setFuAuthority("");
		List<FwkGroup> groupList = new ArrayList<FwkGroup>();
		if (user.getGroups() == null) {
			groupList.add(group);
		} else {
			groupList = user.getGroups();
			if (!this.validator(group, user.getGroups())) {
				groupList.add(group);
			}
		}
		user.setGroups(groupList);
		this.manageService.saveOrUpdate(user);
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}

	public void onClick$cancel() {
		this.detach();
	}

	private boolean validator(FwkGroup tester, List<FwkGroup> groupList) {
		for (FwkGroup group : groupList) {
			if (group.getFgId().longValue() == group.getFgId().longValue()) {
				return true;
			}
		}
		return false;
	}
}
