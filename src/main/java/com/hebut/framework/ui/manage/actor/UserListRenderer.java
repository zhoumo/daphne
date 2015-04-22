package com.hebut.framework.ui.manage.actor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.hebut.framework.entity.FwkGroup;
import com.hebut.framework.entity.FwkUser;
import com.hebut.rbac.core.AuthorityParser;

public class UserListRenderer implements ListitemRenderer<FwkUser> {

	public UserListRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, FwkUser user, int index) throws Exception {
		Listcell c0 = new Listcell();
		Listcell c1 = new Listcell(user.getFuLoginName());
		Listcell c2 = new Listcell(user.getFuTrueName());
		Listcell c3 = new Listcell(this.getGroups(AuthorityParser.parseGetGroupByRoleKey(user.getFuAuthority(), user.getCurrentRoleKey()), user));
		item.setValue(user);
		item.appendChild(c0);
		item.appendChild(c1);
		item.appendChild(c2);
		item.appendChild(c3);
	}

	private String getGroups(List<String> groupList, FwkUser user) {
		List<String> groupFgNames = new ArrayList<String>();
		for (FwkGroup group : user.getGroups()) {
			for (String groupId : groupList) {
				if (group.getFgId().toString().equals(groupId)) {
					groupFgNames.add(group.getFgName());
				}
			}
		}
		return StringUtils.join(groupFgNames.toArray(), ",");
	}
}
