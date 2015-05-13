package mine.daphne.ui.system;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;

import mine.daphne.model.entity.SysUser;
import mine.daphne.service.ManageService;
import mine.daphne.ui.common.PopWindow;

@SuppressWarnings("serial")
public class ResetWindow extends PopWindow {

	private Textbox password;

	private ManageService manageService;

	private SysUser sysUser;

	@Override
	public void initPop() {
	}

	public void onClick$submit() throws Exception {
		if (StringUtils.isEmpty(password.getValue())) {
			return;
		}
		sysUser.setPassword(password.getValue());
		manageService.saveOrUpdate(sysUser);
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}
}
