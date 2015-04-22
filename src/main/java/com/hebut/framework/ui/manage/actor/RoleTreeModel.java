package com.hebut.framework.ui.manage.actor;

import org.zkoss.zul.AbstractTreeModel;

import com.hebut.rbac.core.CommonUtil;

@SuppressWarnings("serial")
public class RoleTreeModel extends AbstractTreeModel<Object> {

	private String selectedRoleKey;

	public RoleTreeModel(Object root, String selectedRoleKey) {
		super(root);
		this.selectedRoleKey = selectedRoleKey;
	}

	public Object getChild(Object parent, int index) {
		if (parent instanceof Object[]) {
			Object[] roles = (Object[]) parent;
			if (roles[index].toString().equals(this.selectedRoleKey)) {
				return new String[] { CommonUtil.getRoleShowByRoleKey(roles[index].toString()), roles[index].toString(), "true" };
			} else {
				return new String[] { CommonUtil.getRoleShowByRoleKey(roles[index].toString()), roles[index].toString(), "false" };
			}
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (parent instanceof Object[]) {
			Object[] roles = (Object[]) parent;
			return roles.length;
		}
		return 0;
	}

	public boolean isLeaf(Object node) {
		return true;
	}
}
