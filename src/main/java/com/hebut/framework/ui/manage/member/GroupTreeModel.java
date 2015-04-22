package com.hebut.framework.ui.manage.member;

import java.util.List;
import org.zkoss.zul.AbstractTreeModel;

import com.hebut.framework.model.entity.FwkGroup;

@SuppressWarnings("serial")
public class GroupTreeModel extends AbstractTreeModel<Object> {

	public GroupTreeModel(Object root) {
		super(root);
	}

	public Object getChild(Object parent, int index) {
		if (parent instanceof FwkGroup) {
			FwkGroup group = (FwkGroup) parent;
			return group.getChildren().get(index);
		} else if (parent instanceof List) {
			@SuppressWarnings("unchecked")
			List<FwkGroup> groupList = (List<FwkGroup>) parent;
			return groupList.get(index);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (parent instanceof FwkGroup) {
			FwkGroup group = (FwkGroup) parent;
			return group.getChildren().size();
		} else if (parent instanceof List) {
			@SuppressWarnings("unchecked")
			List<FwkGroup> groupList = (List<FwkGroup>) parent;
			return groupList.size();
		}
		return 0;
	}

	public boolean isLeaf(Object node) {
		if (node instanceof FwkGroup) {
			FwkGroup group = (FwkGroup) node;
			return group.getChildren().size() > 0 ? false : true;
		} else {
			return false;
		}
	}
}
