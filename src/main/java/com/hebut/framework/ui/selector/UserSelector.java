package com.hebut.framework.ui.selector;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tree;

import com.hebut.framework.entity.FwkGroup;
import com.hebut.framework.entity.FwkUser;
import com.hebut.framework.service.ManageService;
import com.hebut.framework.ui.common.PopWindow;
import com.hebut.framework.ui.manage.member.GroupTreeModel;
import com.hebut.framework.ui.manage.member.GroupTreeRenderer;

@SuppressWarnings("serial")
public class UserSelector extends PopWindow {

	private ManageService manageService;

	private Tree groupTree;

	private Listbox userList;

	private Paging userPaging;

	private List<FwkUser> resultList = new ArrayList<FwkUser>();

	private EventListener<Event> clickEventListener = new EventListener<Event>() {

		@Override
		public void onEvent(Event event) throws Exception {
			if (!userList.isVisible()) {
				userList.setVisible(true);
				userPaging.setVisible(true);
			}
			initUserListbox();
		}
	};

	public List<FwkUser> getResultList() {
		return resultList;
	}

	public void initWindow() {
		this.groupTree.setItemRenderer(new GroupTreeRenderer(this.manageService, this.clickEventListener, null));
		this.userList.setItemRenderer(new UserListRenderer());
		this.initGroupTree();
	}

	public void onClick$submit() {
		for (Object object : this.userList.getSelectedItems()) {
			resultList.add((FwkUser) ((Listitem) object).getValue());
		}
		this.setAttribute("groupId", ((FwkGroup) this.groupTree.getSelectedItem().getValue()).getFgId());
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}

	public void onClick$cancel() {
		this.detach();
	}

	private void initGroupTree() {
		GroupTreeModel model = new GroupTreeModel(this.manageService.findRootGroup(null));
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
}
