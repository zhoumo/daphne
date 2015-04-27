package mine.daphne.security.core;

import java.util.ArrayList;
import java.util.List;

import mine.daphne.security.model.application.Module;

import org.apache.commons.lang.StringUtils;

public class AuthorityParser {

	public static List<Module> parseGetModules(String authority) {
		List<Module> moduleList = new ArrayList<Module>();
		for (Object roleKey : parseGetRoleKeys(authority)) {
			for (Module module : RoleFactory.getApplication(roleKey.toString()).getModule()) {
				if (!moduleList.contains(module)) {
					moduleList.add(module);
				}
			}
		}
		return moduleList;
	}

	public static Object[] parseGetRoleKeys(String authority) {
		List<String> roles = new ArrayList<String>();
		for (String authoritySever : authority.split(",")) {
			if (StringUtils.isEmpty(authoritySever)) {
				continue;
			}
			for (String role : authoritySever.split("\\+")) {
				if (StringUtils.isEmpty(role)) {
					continue;
				}
				roles.add(role.substring(role.indexOf("*") + 1, role.length()));
			}
		}
		return roles.toArray();
	}

	public static String parseGetRoleKeysByGroup(String authority, String group) {
		for (String authoritySever : authority.split(",")) {
			String[] groupSever = authoritySever.split("\\*");
			if (group.equals(groupSever[0])) {
				return groupSever[1];
			}
		}
		return "";
	}

	public static List<String> parseGetGroupByRoleKey(String authority, String roleKeys) {
		List<String> groupList = new ArrayList<String>();
		for (String authoritySever : authority.split(",")) {
			String[] groupSever = authoritySever.split("\\*");
			if (groupSever[1].contains(roleKeys)) {
				groupList.add(groupSever[0]);
			}
		}
		return groupList;
	}

	public static String appendRoleKeys(String authority, String group, String roleKeys) {
		String newAuthority = "";
		boolean flag = true;
		if ("".equals(authority)) {
			if (!"".equals(roleKeys)) {
				flag = false;
				newAuthority = group + "*" + roleKeys + ",";
			}
		} else {
			for (String authoritySever : authority.split(",")) {
				String[] groupSever = authoritySever.split("\\*");
				if (groupSever[0].equals(group)) {
					flag = false;
					if (!"".equals(roleKeys)) {
						newAuthority += group + "*" + roleKeys + ",";
					}
				} else {
					newAuthority += authoritySever + ",";
				}
			}
		}
		if (flag) {
			newAuthority += group + "*" + roleKeys + ",";
		}
		return newAuthority;
	}

	public static String appendRoleKey(String authority, String group, String roleKey) {
		String newAuthority = "";
		boolean flag = true;
		for (String authoritySever : authority.split(",")) {
			String[] groupSever = authoritySever.split("\\*");
			if (groupSever[0].equals(group)) {
				flag = false;
				if (!groupSever[1].contains(roleKey)) {
					newAuthority += group + "*" + groupSever[1] + "+" + roleKey + ",";
				} else {
					newAuthority += authoritySever + ",";
				}
			} else {
				newAuthority += authoritySever + ",";
			}
		}
		if (flag) {
			newAuthority += group + "*" + roleKey + ",";
		}
		return newAuthority;
	}

	public static String removeRoleKey(String authority, String roleKey) {
		String newAuthority = "";
		String temporary = "";
		for (String authoritySever : authority.split(",")) {
			String[] groupSever = authoritySever.split("\\*");
			if (groupSever[1].contains(roleKey)) {
				if (groupSever[1].length() != roleKey.length()) {
					temporary = groupSever[0] + "*" + groupSever[1].replace(roleKey, "");
					if (temporary.endsWith("+")) {
						temporary = temporary.substring(0, temporary.length() - 1);
					}
					newAuthority += temporary + ",";
				}
			} else {
				newAuthority += groupSever[0] + "*" + groupSever[1] + ",";
			}
		}
		if (!"".equals(newAuthority)) {
			newAuthority = newAuthority.replace("++", "+");
			newAuthority = newAuthority.replace("*+", "*");
		}
		return newAuthority;
	}

	public static String removeRoleKeyByGroup(String authority, String groupId) {
		String newAuthority = "";
		for (String authoritySever : authority.split(",")) {
			String[] groupSever = authoritySever.split("\\*");
			if (!groupSever[0].equals(groupId)) {
				newAuthority += groupSever[0] + "*" + groupSever[1] + ",";
			}
		}
		return newAuthority;
	}
}
