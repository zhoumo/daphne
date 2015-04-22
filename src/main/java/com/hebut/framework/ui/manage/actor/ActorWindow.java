package com.hebut.framework.ui.manage.actor;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import com.hebut.framework.entity.FwkUser;
import com.hebut.framework.security.ComponentCheck;
import com.hebut.framework.service.ManageService;
import com.hebut.framework.ui.common.BaseWindow;
import com.hebut.framework.ui.selector.UserSelector;
import com.hebut.rbac.core.AuthorityParser;
import com.hebut.rbac.core.CommonUtil;

@SuppressWarnings("serial")
public class ActorWindow extends BaseWindow {

	private ManageService manageService;

	private Tree roleTree;

	private Listbox userList;

	private Paging userPaging;

	private EventListener<Event> clickEventListener = new EventListener<Event>() {

		@Override
		public void onEvent(Event event) throws Exception {
			((String[]) ((Treeitem) event.getTarget()).getValue())[2] = "true";
			if (!userList.isVisible()) {
				userList.setVisible(true);
				userPaging.setVisible(true);
			}
			initUserListbox();
		}
	};

	@Override
	public void initWindow() {
		this.roleTree.setItemRenderer(new RoleTreeRenderer(this.manageService, this.clickEventListener));
		this.userList.setItemRenderer(new UserListRenderer());
		this.initGroupTree();
	}

	public void onClick$userIn() {
		if (super.hasItemSelected(this.roleTree)) {
			final UserSelector window = (UserSelector) ComponentCheck.createComponents(this, SELECTOR_USER, null, null);
			window.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					for (FwkUser user : window.getResultList()) {
						user.setFuAuthority(AuthorityParser.appendRoleKey(user.getFuAuthority(), window.getAttribute("groupId").toString(), ((String[]) (roleTree.getSelectedItem()).getValue())[1]));
						manageService.saveOrUpdateWithCache(user);
					}
					initGroupTree();
					initUserListbox();
				}
			});
			window.initWindow();
			window.doHighlighted();
		}
	}

	public void onClick$userOut() {
		if (super.hasItemSelected(this.userList)) {
			for (Object object : this.userList.getSelectedItems()) {
				FwkUser user = (FwkUser) ((Listitem) object).getValue();
				user.setFuAuthority(AuthorityParser.removeRoleKey(user.getFuAuthority(), ((String[]) (this.roleTree.getSelectedItem()).getValue())[1]));
				manageService.saveOrUpdateWithCache(user);
			}
			initGroupTree();
			initUserListbox();
		}
	}

	private void initGroupTree() {
		String selectedRoleKey = "";
		if (super.hasItemSelected(this.roleTree)) {
			selectedRoleKey = ((String[]) (this.roleTree.getSelectedItem()).getValue())[1];
		}
		RoleTreeModel model = new RoleTreeModel(CommonUtil.getRoleKeySet().toArray(), selectedRoleKey);
		this.roleTree.setModel(model);
	}

	private void initUserListbox() {
		this.userPaging.setActivePage(0);
		this.userPaging.setTotalSize(Integer.parseInt(this.roleTree.getSelectedItem().getAttribute("number").toString()));
		this.userPaging.addEventListener("onPaging", new EventListener<Event>() {

			public void onEvent(Event event) throws Exception {
				userList.setModel(new ListModelList<Object>(manageService.findUsersByRoleKey(userPaging.getActivePage(), userPaging.getPageSize(), ((String[]) (roleTree.getSelectedItem()).getValue())[1])));
			}
		});
		this.userList.setModel(new ListModelList<Object>(this.manageService.findUsersByRoleKey(0, this.userPaging.getPageSize(), ((String[]) (this.roleTree.getSelectedItem()).getValue())[1])));
	}
}
