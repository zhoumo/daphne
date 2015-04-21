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

import com.hebut.framework.entity.FwkUser;
import com.hebut.framework.service.ManageService;
import com.hebut.framework.ui.common.UrlManager;
import com.hebut.framework.util.JavaScriptUtil;
import com.hebut.framework.util.SessionUtil;
import com.hebut.framework.vo.UserInfo;
import com.hebut.rbac.core.Validator;

@SuppressWarnings("serial")
public class LoginWindow extends Window implements AfterCompose {

	private ManageService manageService;

	private Textbox username, password;

	private Image loginButton;

	@Override
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		JavaScriptUtil.setBrowserParameters();
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
		SessionUtil.createUserInfoSession(userInfo);
		SessionUtil.createLoginTimestampSession(new Date());
		Executions.sendRedirect(UrlManager.SYSTEM_DESKTOP);
	}
}
