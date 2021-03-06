package mine.daphne.ui.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mine.daphne.model.vo.Menu;
import mine.daphne.model.vo.UserInfo;
import mine.daphne.security.ComponentCheck;
import mine.daphne.security.core.AuthorityParser;
import mine.daphne.security.core.CommonUtil;
import mine.daphne.security.model.application.Function;
import mine.daphne.security.model.application.Module;
import mine.daphne.ui.common.BaseWindow;
import mine.daphne.ui.common.CustomDiv;
import mine.daphne.ui.desktop.Shortcut;
import mine.daphne.ui.desktop.StartPanel;
import mine.daphne.utils.SessionUtil;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Toolbarbutton;

@SuppressWarnings("serial")
public class DesktopWindow extends BaseWindow {

	private CustomDiv toolPanel;

	protected Map<String, String[]> shortcut = new TreeMap<String, String[]>();

	protected List<Menu> menu = new ArrayList<Menu>();

	protected String[] portrait;

	private Toolbarbutton reset;

	private Toolbarbutton quit;

	private void modelConvert(Menu menu, List<Function> functionList) {
		List<Menu> menuList = new ArrayList<Menu>();
		for (Function function : functionList) {
			Menu newMenu = new Menu(function.getShow());
			if (function.getConstraints().size() != 0) {
				newMenu.setValue(CommonUtil.getIndexByFunction(function));
			}
			menuList.add(newMenu);
			modelConvert(newMenu, function.getFunctions());
		}
		menu.setChildren(menuList);
	}

	private void initDesktop() {
		this.setBorder(false);
		UserInfo userInfo = SessionUtil.getUserInfoSession();
		String authority = (userInfo == null ? "" : userInfo.getAuthority());
		if (StringUtils.isEmpty(authority)) {
			onClick$quit();
		}
		for (Object key : AuthorityParser.parseGetRoleKeys(authority)) {
			for (Function function : CommonUtil.getFunctionList(key.toString())) {
				this.shortcut.put(function.getName(), new String[] { function.getShow(), CommonUtil.getIndexByFunction(function) });
			}
		}
		for (Module module : AuthorityParser.parseGetModules(SessionUtil.getUserInfoSession().getAuthority())) {
			Menu menu = new Menu(module.getShow());
			if (this.menu.contains(menu)) {
				continue;
			}
			this.modelConvert(menu, module.getFunctions());
			this.menu.add(menu);
		}
		String userName = "系统管理员";
		if (SessionUtil.getUserInfoSession().getUser() != null) {
			userName = SessionUtil.getUserInfoSession().getUser().getTrueName();
		}
		this.portrait = new String[] { "/images/desktop/portrait.png", userName };
	}

	private void createDesktopArea() {
		CustomDiv desktopArea = new CustomDiv("desktop", this);
		int index = 0, row = 0, column = -1;
		for (String key : this.shortcut.keySet()) {
			row = index % 5;
			if (row == 0) {
				column++;
			}
			String[] value = this.shortcut.get(key);
			String css = "position:absolute;left:" + (column * 90) + "px;top:" + (row * 85) + "px";
			desktopArea.appendChild(new Shortcut("images/desktop/shortcut.png", value[0], value[1], css));
			index++;
		}
		desktopArea.setDroppable("true");
		desktopArea.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				event.getTarget().getPage().getFellow("startPanel").setVisible(false);
			}
		});
		desktopArea.addEventListener(Events.ON_DROP, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				DropEvent dropEvent = (DropEvent) event;
				if (dropEvent.getDragged() instanceof Shortcut) {
					String css = "position:absolute;left:" + dropEvent.getPageX() + "px;top:" + dropEvent.getPageY() + "px";
					((Shortcut) dropEvent.getDragged()).setStyle(css);
				}
			}
		});
	}

	private void createTaskbar() {
		CustomDiv taskbar = new CustomDiv("taskbar", this);
		new CustomDiv("start_button", taskbar).addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				StartPanel startPanel = (StartPanel) event.getTarget().getPage().getFellow("startPanel");
				if (startPanel.isVisible()) {
					startPanel.setVisible(false);
				} else {
					startPanel.setZIndex(SessionUtil.getZindex());
					startPanel.setVisible(true);
				}
			}
		});
	}

	private void createStartPanel() {
		StartPanel startPanel = new StartPanel(this.menu, this.portrait);
		startPanel.setPage(this.getPage());
		startPanel.setId("startPanel");
		startPanel.setVisible(false);
		this.toolPanel = startPanel.getToolPanel();
	}

	private void appendToolbarbuttons(Toolbarbutton[] toolbarbuttons) {
		for (Toolbarbutton toolbarbutton : toolbarbuttons) {
			toolbarbutton.setParent(this.toolPanel);
			toolbarbutton.setStyle("font-size:12px;color:white;text-shadow:0 0 0");
		}
	}

	@Override
	public void initWindow() {
		SessionUtil.clearWindowSessions();
		this.initDesktop();
		this.createDesktopArea();
		this.createTaskbar();
		this.createStartPanel();
		this.appendToolbarbuttons(new Toolbarbutton[] { this.reset, this.quit });
	}

	public void onClick$reset() {
		ResetWindow window = (ResetWindow) ComponentCheck.createComponents(this, SYSTEM_RESET, this, null);
		window.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Messagebox.show("密码重置成功，请重新登录.");
				Sessions.getCurrent().invalidate();
				Executions.getCurrent().sendRedirect(BaseWindow.SYSTEM_LOGIN);
			}
		});
		window.setSysUser(SessionUtil.getUserInfoSession().getUser());
		window.doHighlighted();
	}

	public void onClick$quit() {
		Sessions.getCurrent().invalidate();
		Executions.getCurrent().sendRedirect(BaseWindow.SYSTEM_LOGIN);
	}
}
