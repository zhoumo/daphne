package mine.daphne.ui.manage.member;

import java.util.List;

import mine.daphne.model.entity.SysGroup;
import mine.daphne.model.entity.SysUser;
import mine.daphne.security.core.AuthorityParser;
import mine.daphne.security.core.CommonUtil;
import mine.daphne.service.ManageService;
import mine.daphne.ui.common.PopWindow;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

@SuppressWarnings("serial")
public class RoleWindow extends PopWindow {

	private ManageService manageService;

	private Listbox roleList;

	@Override
	public void initPop() {
		String[] roleKeys = new String[] {};
		if (Boolean.parseBoolean(this.getAttribute("single").toString())) {
			roleKeys = AuthorityParser.parseGetRoleKeysByGroup(((SysUser) this.getAttribute("user")).getAuthority(), ((SysGroup) this.getAttribute("group")).getId().toString()).split("\\+");
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
		SysGroup group = (SysGroup) this.getAttribute("group");
		for (Object object : this.roleList.getSelectedItems()) {
			roles += ((Listitem) object).getValue().toString() + "+";
		}
		if (Boolean.parseBoolean(this.getAttribute("single").toString())) {
			SysUser user = (SysUser) this.getAttribute("user");
			user.setAuthority(AuthorityParser.appendRoleKeys(user.getAuthority(), group.getId().toString(), roles));
			this.manageService.saveOrUpdate(user);
		} else {
			@SuppressWarnings("unchecked")
			List<SysUser> userList = (List<SysUser>) this.getAttribute("user");
			for (SysUser user : userList) {
				user.setAuthority(AuthorityParser.appendRoleKeys(user.getAuthority(), group.getId().toString(), roles));
				this.manageService.saveOrUpdate(user);
			}
		}
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}
}
