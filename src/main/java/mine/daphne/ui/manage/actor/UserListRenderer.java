package mine.daphne.ui.manage.actor;

import java.util.ArrayList;
import java.util.List;

import mine.daphne.model.entity.SysGroup;
import mine.daphne.model.entity.SysUser;
import mine.daphne.security.core.AuthorityParser;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class UserListRenderer implements ListitemRenderer<SysUser> {

	private String getGroups(List<String> groupList, SysUser user) {
		List<String> groupFgNames = new ArrayList<String>();
		for (SysGroup group : user.getGroups()) {
			for (String groupId : groupList) {
				if (group.getId().toString().equals(groupId)) {
					groupFgNames.add(group.getName());
				}
			}
		}
		return StringUtils.join(groupFgNames.toArray(), ",");
	}

	@Override
	public void render(Listitem item, SysUser user, int index) throws Exception {
		item.setValue(user);
		item.appendChild(new Listcell());
		item.appendChild(new Listcell(user.getLoginName()));
		item.appendChild(new Listcell(user.getTrueName()));
		item.appendChild(new Listcell(this.getGroups(AuthorityParser.parseGetGroupByRoleKey(user.getAuthority(), user.getCurrentRoleKey()), user)));
		((Listbox) item.getParent()).setMultiple(true);
		((Listbox) item.getParent()).setCheckmark(true);
	}
}
