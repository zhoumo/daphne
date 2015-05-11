package mine.daphne.ui.manage.member;

import java.util.List;

import mine.daphne.model.entity.SysGroup;

import org.zkoss.zul.AbstractTreeModel;

@SuppressWarnings("serial")
public class GroupTreeModel extends AbstractTreeModel<Object> {

	public GroupTreeModel(Object root) {
		super(root);
	}

	public Object getChild(Object parent, int index) {
		if (parent instanceof SysGroup) {
			SysGroup group = (SysGroup) parent;
			return group.getChildren().get(index);
		} else if (parent instanceof List) {
			@SuppressWarnings("unchecked")
			List<SysGroup> groupList = (List<SysGroup>) parent;
			return groupList.get(index);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (parent instanceof SysGroup) {
			SysGroup group = (SysGroup) parent;
			return group.getChildren().size();
		} else if (parent instanceof List) {
			@SuppressWarnings("unchecked")
			List<SysGroup> groupList = (List<SysGroup>) parent;
			return groupList.size();
		}
		return 0;
	}

	public boolean isLeaf(Object node) {
		if (node instanceof SysGroup) {
			SysGroup group = (SysGroup) node;
			return group.getChildren().size() > 0 ? false : true;
		} else {
			return false;
		}
	}
}
