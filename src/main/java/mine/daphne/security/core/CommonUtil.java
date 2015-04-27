package mine.daphne.security.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mine.daphne.security.model.actor.Group;
import mine.daphne.security.model.actor.Role;
import mine.daphne.security.model.application.Constraint;
import mine.daphne.security.model.application.Function;
import mine.daphne.security.model.application.Module;

public class CommonUtil {

	public static String getRoleShowByRoleKey(String roleKey) {
		for (Group group : XmlParser.getRbacConfig().getActor().getGroup()) {
			for (Role role : group.getRole()) {
				if (roleKey.equals(group.getName() + "-" + role.getName())) {
					return role.getShow();
				}
			}
		}
		return "";
	}

	public static Set<String> getRoleKeySet() {
		Set<String> roleKeySet = new HashSet<String>();
		for (Group group : XmlParser.getRbacConfig().getActor().getGroup()) {
			for (Role role : group.getRole()) {
				roleKeySet.add(group.getName() + "-" + role.getName());
			}
		}
		return roleKeySet;
	}

	public static List<Function> getFunctionList(String roleKey) {
		List<Function> resultList = new ArrayList<Function>();
		for (Module module : RoleFactory.getApplication(roleKey).getModule()) {
			searchFunctionList(module.getFunctions(), resultList);
		}
		return resultList;
	}

	private static void searchFunctionList(List<Function> functionList, List<Function> resultList) {
		for (Function function : functionList) {
			if (function.getConstraints().size() != 0) {
				resultList.add(function);
			}
			searchFunctionList(function.getFunctions(), resultList);
		}
	}

	public static String getIndexByFunction(Function function) {
		String index = "";
		for (Constraint constraint : function.getConstraints()) {
			if (!"".equals(constraint.getIndex())) {
				if (constraint.getUrl().endsWith("**")) {
					index = constraint.getUrl().substring(0, constraint.getUrl().length() - 2) + constraint.getIndex();
				} else if (!constraint.getUrl().endsWith("/")) {
					index = constraint.getUrl() + "/" + constraint.getIndex();
				} else {
					index = constraint.getUrl() + constraint.getIndex();
				}
			}
		}
		return index;
	}
}
