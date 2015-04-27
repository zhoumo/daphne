package mine.daphne.ui.manage.member;

import java.util.ArrayList;
import java.util.List;

import mine.daphne.model.entity.Group;
import mine.daphne.model.entity.User;
import mine.daphne.service.ManageService;
import mine.daphne.ui.common.PopWindow;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;

@SuppressWarnings("serial")
public class UserWindow extends PopWindow {

	private Textbox userLoginName, userName, userPassword;

	private ManageService manageService;

	@Override
	public void initPop() {
		if (!Boolean.parseBoolean(this.getAttribute("isNew").toString())) {
			User user = (User) this.getAttribute("user");
			this.userLoginName.setValue(user.getLoginName());
			this.userName.setValue(user.getTrueName());
			this.userPassword.setValue(user.getPassword());
		}
	}

	public void onClick$submit() {
		User user = (User) this.getAttribute("user");
		Group group = (Group) this.getAttribute("group");
		user.setLoginName(this.userLoginName.getValue());
		user.setTrueName(this.userName.getValue());
		user.setPassword(this.userPassword.getValue());
		List<Group> groupList = new ArrayList<Group>();
		if (group != null) {
			groupList.add(group);
			user.setGroups(groupList);
		}
		if (user.getAuthority() == null) {
			user.setAuthority("");
		}
		this.manageService.saveOrUpdate(user);
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}

	public void onClick$cancel() {
		this.detach();
	}
}
