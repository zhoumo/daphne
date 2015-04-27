package mine.daphne.ui.selector;

import mine.daphne.model.entity.User;
import mine.daphne.security.core.AuthorityParser;
import mine.daphne.security.core.CommonUtil;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class UserListRenderer implements ListitemRenderer<User> {

	@Override
	public void render(Listitem item, User user, int index) throws Exception {
		Listcell c0 = new Listcell();
		Listcell c1 = new Listcell(user.getLoginName());
		Listcell c2 = new Listcell(user.getTrueName());
		String roles = "";
		for (String role : AuthorityParser.parseGetRoleKeysByGroup(user.getAuthority(), user.getCurrentGroup()).split("\\+")) {
			roles += CommonUtil.getRoleShowByRoleKey(role) + " ";
		}
		Listcell c3 = new Listcell(roles);
		item.setValue(user);
		item.appendChild(c0);
		item.appendChild(c1);
		item.appendChild(c2);
		item.appendChild(c3);
	}
}
