package mine.daphne.ui.manage.member;

import java.util.List;

import mine.daphne.model.entity.Group;

import org.zkoss.zul.AbstractTreeModel;

@SuppressWarnings("serial")
public class GroupTreeModel extends AbstractTreeModel<Object> {

	public GroupTreeModel(Object root) {
		super(root);
	}

	public Object getChild(Object parent, int index) {
		if (parent instanceof Group) {
			Group group = (Group) parent;
			return group.getChildren().get(index);
		} else if (parent instanceof List) {
			@SuppressWarnings("unchecked")
			List<Group> groupList = (List<Group>) parent;
			return groupList.get(index);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (parent instanceof Group) {
			Group group = (Group) parent;
			return group.getChildren().size();
		} else if (parent instanceof List) {
			@SuppressWarnings("unchecked")
			List<Group> groupList = (List<Group>) parent;
			return groupList.size();
		}
		return 0;
	}

	public boolean isLeaf(Object node) {
		if (node instanceof Group) {
			Group group = (Group) node;
			return group.getChildren().size() > 0 ? false : true;
		} else {
			return false;
		}
	}
}
