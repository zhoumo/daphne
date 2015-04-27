package mine.daphne.ui.manage.member;

import mine.daphne.model.entity.User;
import mine.daphne.security.core.AuthorityParser;
import mine.daphne.security.core.CommonUtil;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class UserListRenderer implements ListitemRenderer<User> {

	private EventListener<Event> doubleClickEventListener;

	public UserListRenderer(EventListener<Event> doubleClickEventListener) {
		super();
		this.doubleClickEventListener = doubleClickEventListener;
	}

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
		if (this.doubleClickEventListener != null) {
			item.addEventListener(Events.ON_DOUBLE_CLICK, this.doubleClickEventListener);
		}
	}
}
