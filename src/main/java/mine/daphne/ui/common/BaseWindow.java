package mine.daphne.ui.common;

import mine.daphne.model.vo.UserInfo;
import mine.daphne.service.SessionService;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public abstract class BaseWindow extends Window implements AfterCompose {

	public static String SYSTEM_LOGIN = "/login.zul";

	public static String SYSTEM_DESKTOP = "/index.zul";

	public static String SYSTEM_IFRAME = "/content/iframe.zul";

	public static String SYSTEM_TIMEOUT = "/content/timeout.zul";

	public static String MANAGE_GROUP = "/content/manage/member/group.zul";

	public static String MANAGE_ROLE = "/content/manage/member/role.zul";

	public static String MANAGE_USER = "/content/manage/member/user.zul";

	public static String SELECTOR_USER = "/content/selector/user.zul";

	public static String PLANNING_CREATE = "/content/scrum/planning/create.zul";

	public static String PLANNING_POINT = "/content/scrum/planning/point.zul";

	public abstract void initWindow();

	@Override
	public void afterCompose() {
		ConventionWires.wireVariables(this, this);
		ConventionWires.addForwards(this, this);
		this.setClosable(true);
		this.setBorder("normal");
		this.setPosition("center,center");
		this.setZIndex(SessionService.getZindex());
		this.initWindow();
		this.addEventListener(Events.ON_CLOSE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Window window = (Window) event.getTarget();
				SessionService.removeWindowSession(window.getTitle());
			}
		});
	}

	public UserInfo getUserInfo() {
		return SessionService.getUserInfoSession();
	}

	public boolean hasItemSelected(Object object) {
		if (object instanceof Listbox) {
			if (((Listbox) object).getSelectedCount() != 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if (((Tree) object).getSelectedCount() != 0) {
				return true;
			} else {
				return false;
			}
		}
	}
}
