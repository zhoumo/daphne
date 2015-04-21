package com.hebut.framework.ui.system;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Toolbarbutton;

import com.hebut.framework.ui.common.BaseWindow;
import com.hebut.framework.ui.desktop.Desktop;
import com.hebut.framework.ui.desktop.MenuModel;
import com.hebut.framework.util.SessionUtil;
import com.hebut.rbac.core.AuthorityParser;
import com.hebut.rbac.core.CommonUtil;
import com.hebut.rbac.model.application.Function;
import com.hebut.rbac.model.application.Module;

@SuppressWarnings("serial")
public class DesktopWindow extends Desktop {

	private Toolbarbutton setting;

	private Toolbarbutton quit;

	@Override
	protected void initModel() {
		for (Object roleKey : AuthorityParser.parseGetRoleKeys(SessionUtil.getUserInfoSession().getAuthority())) {
			for (Function function : CommonUtil.getFunctionList(roleKey.toString())) {
				this.shortcutModel.put(function.getName(), new String[] { function.getShow(), CommonUtil.getIndexByFunction(function) });
			}
		}
		for (Module module : AuthorityParser.parseGetModules(SessionUtil.getUserInfoSession().getAuthority())) {
			MenuModel menuModel = new MenuModel(module.getShow());
			this.modelConvert(menuModel, module.getFunctions());
			this.menuModel.add(menuModel);
		}
		String userName = "系统管理员";
		if (SessionUtil.getUserInfoSession().getUser() != null) {
			userName = SessionUtil.getUserInfoSession().getUser().getFuTrueName();
		}
		this.portraitModel = new String[] { "/images/desktop/portrait.png", userName };
	}

	private void modelConvert(MenuModel menuModel, List<Function> functionList) {
		List<MenuModel> menuList = new ArrayList<MenuModel>();
		for (Function function : functionList) {
			MenuModel newMenu = new MenuModel(function.getShow());
			if (function.getConstraints().size() != 0) {
				newMenu.setValue(CommonUtil.getIndexByFunction(function));
			}
			menuList.add(newMenu);
			modelConvert(newMenu, function.getFunctions());
		}
		menuModel.setChildren(menuList);
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
