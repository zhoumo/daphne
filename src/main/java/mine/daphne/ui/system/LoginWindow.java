package mine.daphne.ui.system;

import java.util.Date;

import mine.daphne.model.entity.SysUser;
import mine.daphne.model.vo.UserInfo;
import mine.daphne.security.core.Validator;
import mine.daphne.service.ManageService;
import mine.daphne.service.SessionService;
import mine.daphne.ui.common.BaseWindow;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Image;
import org.zkoss.zul.Textbox;

@SuppressWarnings("serial")
public class LoginWindow extends BaseWindow {

	private ManageService manageService;

	private Textbox username, password;

	private Image loginButton;

	private void login(String authority, SysUser user) {
		UserInfo userInfo = new UserInfo(authority);
		userInfo.setUser(user);
		SessionService.createUserInfoSession(userInfo);
		SessionService.createLoginTimestampSession(new Date());
		Executions.sendRedirect(BaseWindow.SYSTEM_DESKTOP);
	}

	@Override
	public void initWindow() {
		this.addForward(Events.ON_OK, this.loginButton, Events.ON_CLICK);
	}

	public void onClick$loginButton() {
		String admin = Validator.adminValidator(this.username.getValue().trim(), this.password.getValue().trim());
		if (!StringUtils.isEmpty(admin)) {
			this.login(admin, null);
		} else {
			SysUser user = this.manageService.findByLoginNameAndPassword(this.username.getValue().trim(), this.password.getValue().trim());
			if (user != null) {
				this.login(user.getAuthority(), user);
			} else {
				throw new WrongValueException(this.username, "用户名或密码错误！");
			}
		}
	}
}
