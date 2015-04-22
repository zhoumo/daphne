package com.hebut.framework.ui.manage.member;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import com.hebut.framework.entity.FwkGroup;
import com.hebut.framework.entity.FwkUser;
import com.hebut.framework.security.ComponentCheck;
import com.hebut.framework.service.ManageService;
import com.hebut.framework.ui.common.BaseWindow;
import com.hebut.framework.ui.selector.UserSelector;
import com.hebut.rbac.core.AuthorityParser;

@SuppressWarnings("serial")
public class MemberWindow extends BaseWindow {

	private ManageService manageService;

	private Tree groupTree;

	private Listbox userList;

	private Paging userPaging;

	private EventListener<Event> clickEventListener = new EventListener<Event>() {

		@Override
		public void onEvent(Event event) throws Exception {
			((FwkGroup) ((Treeitem) event.getTarget()).getValue()).setSelected(true);
			if (!userList.isVisible()) {
				userList.setVisible(true);
				userPaging.setVisible(true);
			}
			initUserListbox();
		}
	};

	private EventListener<Event> doubleClickEventListener = new EventListener<Event>() {

		@Override
		public void onEvent(Event event) throws Exception {
			if (event.getTarget() instanceof Treeitem) {
				onClick$groupUpdate();
			} else if (event.getTarget() instanceof Listitem) {
				onClick$userUpdate();
			}
		}
	};

	@Override
	public void initWindow() {
		this.groupTree.setItemRenderer(new GroupTreeRenderer(this.manageService, this.clickEventListener, this.doubleClickEventListener));
		this.userList.setItemRenderer(new UserListRenderer(this.doubleClickEventListener));
		this.initGroupTree();
	}

	public void onClick$groupAdd() {
		this.createGroupWindow(true, new FwkGroup(), this.groupTree.getSelectedCount() != 0 ? this.groupTree.getSelectedItem().getValue() : null);
	}

	public void onClick$groupDelete() throws InterruptedException {
		if (super.hasItemSelected(this.groupTree)) {
			if (Integer.parseInt(this.groupTree.getSelectedItem().getAttribute("number").toString()) == 0) {
				this.manageService.delete(this.groupTree.getSelectedItem().getValue());
				this.initGroupTree();
			} else {
				Messagebox.show("当前组中存在用户，不能进行删除！");
			}
		}
	}

	public void onClick$groupUpdate() {
		if (super.hasItemSelected(this.groupTree)) {
			this.createGroupWindow(false, this.groupTree.getSelectedItem().getValue(), null);
		}
	}

	public void onClick$groupTransfer() {
		if (super.hasItemSelected(this.groupTree)) {
			final UserSelector window = (UserSelector) ComponentCheck.createComponents(this, SELECTOR_USER, null, null);
			window.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					FwkGroup currentGroup = (FwkGroup) groupTree.getSelectedItem().getValue();
					for (FwkUser user : window.getResultList()) {
						List<FwkGroup> groupList = new ArrayList<FwkGroup>();
						for (FwkGroup group : user.getGroups()) {
							if (group.getFgId().longValue() != currentGroup.getFgId().longValue()) {
								groupList.add(currentGroup);
							}
						}
						user.getGroups().addAll(groupList);
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

	public void onClick$userAdd() {
		this.createUserWindow(true, new FwkUser(), this.groupTree.getSelectedCount() != 0 ? this.groupTree.getSelectedItem().getValue() : null);
	}

	public void onClick$userDelete() {
		if (super.hasItemSelected(this.userList)) {
			for (Object item : this.userList.getSelectedItems()) {
				FwkUser user = (FwkUser) ((Listitem) item).getValue();
				if (user.getGroups().size() == 1) {
					this.manageService.delete(user);
				} else {
					FwkGroup group = (FwkGroup) this.groupTree.getSelectedItem().getValue();
					user.setFuAuthority(AuthorityParser.removeRoleKeyByGroup(user.getFuAuthority(), group.getFgId().toString()));
					user.removeGroup(group);
					this.manageService.saveOrUpdateWithCache(user);
				}
			}
			this.initGroupTree();
			this.initUserListbox();
		}
	}

	public void onClick$userUpdate() {
		if (super.hasItemSelected(this.userList)) {
			this.createUserWindow(false, this.userList.getSelectedItem().getValue(), null);
		}
	}

	public void onClick$userRole() {
		if (super.hasItemSelected(this.userList)) {
			final RoleWindow window = (RoleWindow) ComponentCheck.createComponents(this, MANAGE_ROLE, null, null);
			if (this.userList.getSelectedCount() == 1) {
				window.setAttribute("single", true);
				window.setAttribute("group", this.groupTree.getSelectedItem().getValue());
				window.setAttribute("user", this.userList.getSelectedItem().getValue());
			} else {
				List<FwkUser> userList = new ArrayList<FwkUser>();
				for (Object object : this.userList.getSelectedItems()) {
					userList.add((FwkUser) ((Listitem) object).getValue());
				}
				window.setAttribute("single", false);
				window.setAttribute("group", this.groupTree.getSelectedItem().getValue());
				window.setAttribute("user", userList);
			}
			window.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					initUserListbox();
				}
			});
			window.initWindow();
			window.doHighlighted();
		}
	}

	private void initGroupTree() {
		FwkGroup group = null;
		if (super.hasItemSelected(this.groupTree)) {
			group = (FwkGroup) this.groupTree.getSelectedItem().getValue();
		}
		GroupTreeModel model = new GroupTreeModel(this.manageService.findRootGroup(group));
		this.groupTree.setModel(model);
	}

	private void initUserListbox() {
		this.userPaging.setActivePage(0);
		this.userPaging.setTotalSize(Integer.parseInt(this.groupTree.getSelectedItem().getAttribute("number").toString()));
		this.userPaging.addEventListener("onPaging", new EventListener<Event>() {

			public void onEvent(Event event) throws Exception {
				userList.setModel(new ListModelList<Object>(manageService.findUsersByGroup(userPaging.getActivePage(), userPaging.getPageSize(), (FwkGroup) ((Listitem) event.getTarget()).getValue())));
			}
		});
		this.userList.setModel(new ListModelList<Object>(this.manageService.findUsersByGroup(0, this.userPaging.getPageSize(), (FwkGroup) this.groupTree.getSelectedItem().getValue())));
	}

	private void createGroupWindow(boolean isNew, Object group, Object parent) {
		final GroupWindow window = (GroupWindow) ComponentCheck.createComponents(this, MANAGE_GROUP, null, null);
		window.setAttribute("isNew", isNew);
		window.setAttribute("group", group);
		window.setAttribute("parent", parent);
		window.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				initGroupTree();
			}
		});
		window.initWindow();
		window.doHighlighted();
	}

	private void createUserWindow(boolean isNew, Object user, final Object group) {
		final UserWindow window = (UserWindow) ComponentCheck.createComponents(this, MANAGE_USER, null, null);
		window.setAttribute("isNew", isNew);
		window.setAttribute("user", user);
		window.setAttribute("group", group);
		window.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				initGroupTree();
				initUserListbox();
			}
		});
		window.initWindow();
		window.doHighlighted();
	}
}
