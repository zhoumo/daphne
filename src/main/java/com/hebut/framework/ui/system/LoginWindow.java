package com.hebut.framework.ui.system;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Image;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.hebut.framework.model.entity.FwkUser;
import com.hebut.framework.model.vo.UserInfo;
import com.hebut.framework.service.ManageService;
import com.hebut.framework.service.SessionService;
import com.hebut.framework.ui.common.BaseWindow;
import com.hebut.rbac.core.Validator;

@SuppressWarnings("serial")
public class LoginWindow extends Window implements AfterCompose {

	private ManageService manageService;

	private Textbox username, password;

	private Image loginButton;

	@Override
	@SuppressWarnings("deprecation")
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		this.addForward(Events.ON_OK, this.loginButton, Events.ON_CLICK);
	}

	public void onClick$loginButton() {
		String admin = Validator.adminValidator(this.username.getValue().trim(), this.password.getValue().trim());
		if (!StringUtils.isEmpty(admin)) {
			this.login(admin, null);
		} else {
			FwkUser user = this.manageService.findByLoginNameAndPassword(this.username.getValue().trim(), this.password.getValue().trim());
			if (user != null) {
				this.login(user.getFuAuthority(), user);
			} else {
				throw new WrongValueException(this.username, "用户名或密码错误！");
			}
		}
	}

	private void login(String authority, FwkUser user) {
		UserInfo userInfo = new UserInfo(authority);
		userInfo.setUser(user);
		SessionService.createUserInfoSession(userInfo);
		SessionService.createLoginTimestampSession(new Date());
		Executions.sendRedirect(BaseWindow.SYSTEM_DESKTOP);
	}
}
