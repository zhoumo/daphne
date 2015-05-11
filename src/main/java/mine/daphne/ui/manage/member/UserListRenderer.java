package mine.daphne.ui.manage.member;

import mine.daphne.model.entity.SysUser;
import mine.daphne.security.core.AuthorityParser;
import mine.daphne.security.core.CommonUtil;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class UserListRenderer implements ListitemRenderer<SysUser> {

	@Override
	public void render(Listitem item, SysUser user, int index) throws Exception {
		String roles = "";
		for (String role : AuthorityParser.parseGetRoleKeysByGroup(user.getAuthority(), user.getCurrentGroup()).split("\\+")) {
			roles += CommonUtil.getRoleShowByRoleKey(role) + " ";
		}
		item.setValue(user);
		item.appendChild(new Listcell());
		item.appendChild(new Listcell(user.getLoginName()));
		item.appendChild(new Listcell(user.getTrueName()));
		item.appendChild(new Listcell(roles));
		((Listbox) item.getParent()).setMultiple(true);
		((Listbox) item.getParent()).setCheckmark(true);
	}
}
