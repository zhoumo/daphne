package com.hebut.framework.ui.manage.member;

import java.util.List;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.hebut.framework.model.entity.FwkGroup;
import com.hebut.framework.model.entity.FwkUser;
import com.hebut.framework.service.ManageService;
import com.hebut.framework.ui.common.PopWindow;
import com.hebut.rbac.core.AuthorityParser;
import com.hebut.rbac.core.CommonUtil;

@SuppressWarnings("serial")
public class RoleWindow extends PopWindow {

	private ManageService manageService;

	private Listbox roleList;

	public void initWindow() {
		String[] roleKeys = new String[] {};
		if (Boolean.parseBoolean(this.getAttribute("single").toString())) {
			roleKeys = AuthorityParser.parseGetRoleKeysByGroup(((FwkUser) this.getAttribute("user")).getFuAuthority(), ((FwkGroup) this.getAttribute("group")).getFgId().toString()).split("\\+");
		}
		for (String roleKey : CommonUtil.getRoleKeySet()) {
			Listitem item = new Listitem();
			item.setLabel(CommonUtil.getRoleShowByRoleKey(roleKey));
			item.setValue(roleKey);
			for (String key : roleKeys) {
				if (roleKey.equals(key)) {
					item.setSelected(true);
				}
			}
			this.roleList.appendChild(item);
		}
	}

	public void onClick$submit() {
		String roles = "";
		FwkGroup group = (FwkGroup) this.getAttribute("group");
		for (Object object : this.roleList.getSelectedItems()) {
			roles += ((Listitem) object).getValue().toString() + "+";
		}
		if (Boolean.parseBoolean(this.getAttribute("single").toString())) {
			FwkUser user = (FwkUser) this.getAttribute("user");
			user.setFuAuthority(AuthorityParser.appendRoleKeys(user.getFuAuthority(), group.getFgId().toString(), roles));
			this.manageService.saveOrUpdate(user);
		} else {
			@SuppressWarnings("unchecked")
			List<FwkUser> userList = (List<FwkUser>) this.getAttribute("user");
			for (FwkUser user : userList) {
				user.setFuAuthority(AuthorityParser.appendRoleKeys(user.getFuAuthority(), group.getFgId().toString(), roles));
				this.manageService.saveOrUpdate(user);
			}
		}
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}

	public void onClick$cancel() {
		this.detach();
	}
}
