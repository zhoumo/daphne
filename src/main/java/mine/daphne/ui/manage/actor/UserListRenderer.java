package mine.daphne.ui.manage.actor;

import java.util.ArrayList;
import java.util.List;

import mine.daphne.model.entity.Group;
import mine.daphne.model.entity.User;
import mine.daphne.security.core.AuthorityParser;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class UserListRenderer implements ListitemRenderer<User> {

	@Override
	public void render(Listitem item, User user, int index) throws Exception {
		Listcell c0 = new Listcell();
		Listcell c1 = new Listcell(user.getLoginName());
		Listcell c2 = new Listcell(user.getTrueName());
		Listcell c3 = new Listcell(this.getGroups(AuthorityParser.parseGetGroupByRoleKey(user.getAuthority(), user.getCurrentRoleKey()), user));
		item.setValue(user);
		item.appendChild(c0);
		item.appendChild(c1);
		item.appendChild(c2);
		item.appendChild(c3);
	}

	private String getGroups(List<String> groupList, User user) {
		List<String> groupFgNames = new ArrayList<String>();
		for (Group group : user.getGroups()) {
			for (String groupId : groupList) {
				if (group.getId().toString().equals(groupId)) {
					groupFgNames.add(group.getName());
				}
			}
		}
		return StringUtils.join(groupFgNames.toArray(), ",");
	}
}
