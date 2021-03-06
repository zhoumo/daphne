package mine.daphne.ui.manage.member;

import java.util.ArrayList;
import java.util.List;

import mine.daphne.model.entity.SysGroup;
import mine.daphne.model.entity.SysUser;
import mine.daphne.security.ComponentCheck;
import mine.daphne.security.core.AuthorityParser;
import mine.daphne.service.ManageService;
import mine.daphne.ui.common.BaseWindow;
import mine.daphne.ui.selector.UserSelector;

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

@SuppressWarnings("serial")
public class MemberWindow extends BaseWindow {

	private ManageService manageService;

	private Tree groupTree;

	private Listbox userList;

	private Paging userPaging;

	private EventListener<Event> clickEventListener = new EventListener<Event>() {

		@Override
		public void onEvent(Event event) throws Exception {
			((SysGroup) ((Treeitem) event.getTarget()).getValue()).setSelected(true);
			initUserListbox();
		}
	};

	private void initGroupTree() {
		SysGroup group = null;
		if (super.hasItemSelected(this.groupTree)) {
			group = (SysGroup) this.groupTree.getSelectedItem().getValue();
		}
		GroupTreeModel model = new GroupTreeModel(this.manageService.findRootGroup(group));
		this.groupTree.setModel(model);
	}

	private void initUserListbox() {
		this.userPaging.setActivePage(0);
		this.userPaging.setTotalSize(Integer.parseInt(this.groupTree.getSelectedItem().getAttribute("number").toString()));
		this.userPaging.addEventListener("onPaging", new EventListener<Event>() {

			public void onEvent(Event event) throws Exception {
				SysGroup group = groupTree.getSelectedItem().getValue();
				userList.setModel(new ListModelList<Object>(manageService.findUsersByGroup(userPaging.getActivePage(), userPaging.getPageSize(), group.getId())));
			}
		});
		SysGroup group = this.groupTree.getSelectedItem().getValue();
		this.userList.setModel(new ListModelList<Object>(this.manageService.findUsersByGroup(0, this.userPaging.getPageSize(), group.getId())));
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
		window.initPop();
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
		window.initPop();
		window.doHighlighted();
	}

	@Override
	public void initWindow() {
		this.groupTree.setItemRenderer(new GroupTreeRenderer(this.manageService, this.clickEventListener));
		this.userList.setItemRenderer(new UserListRenderer());
		this.initGroupTree();
	}

	public void onClick$groupAdd() {
		this.createGroupWindow(true, new SysGroup(), this.groupTree.getSelectedCount() != 0 ? this.groupTree.getSelectedItem().getValue() : null);
	}

	public void onClick$groupDelete() {
		if (super.hasItemSelected(this.groupTree)) {
			if (Integer.parseInt(this.groupTree.getSelectedItem().getAttribute("number").toString()) == 0) {
				this.manageService.delete(this.groupTree.getSelectedItem().getValue());
				this.initGroupTree();
			} else {
				Messagebox.show("当前组中存在用户，不能进行删除！", "警告", Messagebox.OK, Messagebox.EXCLAMATION);
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
					SysGroup currentGroup = (SysGroup) groupTree.getSelectedItem().getValue();
					for (SysUser user : window.getResultList()) {
						List<SysGroup> groupList = new ArrayList<SysGroup>();
						for (SysGroup group : user.getGroups()) {
							if (group.getId().longValue() != currentGroup.getId().longValue()) {
								groupList.add(currentGroup);
							}
						}
						user.getGroups().addAll(groupList);
						manageService.saveOrUpdate(user);
					}
					initGroupTree();
					initUserListbox();
				}
			});
			window.initPop();
			window.doHighlighted();
		}
	}

	public void onClick$userAdd() {
		this.createUserWindow(true, new SysUser(), this.groupTree.getSelectedCount() != 0 ? this.groupTree.getSelectedItem().getValue() : null);
	}

	public void onClick$userDelete() {
		if (super.hasItemSelected(this.userList)) {
			for (Object item : this.userList.getSelectedItems()) {
				SysUser user = (SysUser) ((Listitem) item).getValue();
				if (user.getGroups().size() == 1) {
					this.manageService.delete(user);
				} else {
					SysGroup group = (SysGroup) this.groupTree.getSelectedItem().getValue();
					user.setAuthority(AuthorityParser.removeRoleKeyByGroup(user.getAuthority(), group.getId().toString()));
					user.removeGroup(group);
					this.manageService.saveOrUpdate(user);
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
				List<SysUser> userList = new ArrayList<SysUser>();
				for (Object object : this.userList.getSelectedItems()) {
					userList.add((SysUser) ((Listitem) object).getValue());
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
			window.initPop();
			window.doHighlighted();
		}
	}
}
