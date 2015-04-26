package com.hebut.framework.ui.system;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Toolbarbutton;

import com.hebut.framework.model.vo.Menu;
import com.hebut.framework.service.SessionService;
import com.hebut.framework.ui.common.BaseWindow;
import com.hebut.framework.ui.desktop.Desktop;
import com.hebut.rbac.core.AuthorityParser;
import com.hebut.rbac.core.CommonUtil;
import com.hebut.rbac.model.application.Function;
import com.hebut.rbac.model.application.Module;

@SuppressWarnings("serial")
public class DesktopWindow extends Desktop {

	private Toolbarbutton setting;

	private Toolbarbutton quit;

	@Override
	protected void init() {
		for (Object roleKey : AuthorityParser.parseGetRoleKeys(SessionService.getUserInfoSession().getAuthority())) {
			for (Function function : CommonUtil.getFunctionList(roleKey.toString())) {
				this.shortcut.put(function.getName(), new String[] { function.getShow(), CommonUtil.getIndexByFunction(function) });
			}
		}
		for (Module module : AuthorityParser.parseGetModules(SessionService.getUserInfoSession().getAuthority())) {
			Menu menu = new Menu(module.getShow());
			this.modelConvert(menu, module.getFunctions());
			this.menu.add(menu);
		}
		String userName = "系统管理员";
		if (SessionService.getUserInfoSession().getUser() != null) {
			userName = SessionService.getUserInfoSession().getUser().getFuTrueName();
		}
		this.portrait = new String[] { "/images/desktop/portrait.png", userName };
	}

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

	@Override
	protected void initToolbarbuttons() {
		this.appendToolbarbuttons(new Toolbarbutton[] { this.setting, this.quit });
	}

	public void onClick$quit() {
		Sessions.getCurrent().invalidate();
		Executions.getCurrent().sendRedirect(BaseWindow.SYSTEM_LOGIN);
	}
}
