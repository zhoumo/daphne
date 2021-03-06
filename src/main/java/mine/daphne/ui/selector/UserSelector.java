package mine.daphne.ui.selector;

import java.util.ArrayList;
import java.util.List;

import mine.daphne.model.entity.SysGroup;
import mine.daphne.model.entity.SysUser;
import mine.daphne.service.ManageService;
import mine.daphne.ui.common.PopWindow;
import mine.daphne.ui.manage.member.GroupTreeModel;
import mine.daphne.ui.manage.member.GroupTreeRenderer;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tree;

@SuppressWarnings("serial")
public class UserSelector extends PopWindow {

	private ManageService manageService;

	private Tree groupTree;

	private Listbox userList;

	private Paging userPaging;

	private List<SysUser> resultList = new ArrayList<SysUser>();

	private EventListener<Event> clickEventListener = new EventListener<Event>() {

		@Override
		public void onEvent(Event event) throws Exception {
			initUserListbox();
		}
	};

	private void initGroupTree() {
		GroupTreeModel model = new GroupTreeModel(this.manageService.findRootGroup(null));
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
		SysGroup group = (SysGroup) this.groupTree.getSelectedItem().getValue();
		this.userList.setModel(new ListModelList<Object>(this.manageService.findUsersByGroup(0, this.userPaging.getPageSize(), group.getId())));
	}

	@Override
	public void initPop() {
		this.groupTree.setItemRenderer(new GroupTreeRenderer(this.manageService, this.clickEventListener));
		this.userList.setItemRenderer(new UserListRenderer());
		this.initGroupTree();
	}

	public List<SysUser> getResultList() {
		return resultList;
	}

	public void onClick$submit() {
		for (Object object : this.userList.getSelectedItems()) {
			resultList.add((SysUser) ((Listitem) object).getValue());
		}
		this.setAttribute("groupId", ((SysGroup) this.groupTree.getSelectedItem().getValue()).getId());
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}
}
