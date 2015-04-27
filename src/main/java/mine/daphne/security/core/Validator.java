package mine.daphne.security.core;

import java.util.ArrayList;
import java.util.List;

import mine.daphne.security.model.application.Constraint;
import mine.daphne.security.model.application.Function;
import mine.daphne.security.model.application.Module;
import mine.daphne.security.model.system.Admin;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.util.AntUrlPathMatcher;

public class Validator {

	public static String adminValidator(String name, String password) {
		for (Admin admin : XmlParser.getRbacConfig().getSystem().getAdmin()) {
			if (name.equals(admin.getName()) && password.equals(admin.getPassword())) {
				return admin.getGroup() + "*" + admin.getRoles();
			}
		}
		return null;
	}

	public static boolean urlValidator(String authority, String requestUrl) {
		for (String authoritySever : authority.split(",")) {
			for (String roleKey : authoritySever.substring(authoritySever.indexOf("*") + 1, authoritySever.length()).split("\\+")) {
				for (Module module : RoleFactory.getApplication(roleKey).getModule()) {
					if (searchUrl(module.getFunctions(), requestUrl)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean searchUrl(List<Function> functionList, String requestUrl) {
		for (Function function : functionList) {
			for (Constraint constraint : function.getConstraints()) {
				if (new AntUrlPathMatcher().pathMatchesUrl(constraint.getUrl(), requestUrl)) {
					return true;
				}
			}
			if (searchUrl(function.getFunctions(), requestUrl)) {
				return true;
			}
		}
		return false;
	}

	public static String authorityValidate(String authority, Object[] roles, String[] groups) {
		String newAuthority = "";
		if (!"".equals(authority)) {
			for (String authoritySever : authority.split(",")) {
				String[] groupSever = authoritySever.split("\\*");
				if (groupValidator(groups, groupSever[0])) {
					List<String> roleKeys = new ArrayList<String>();
					for (String roleKey : groupSever[1].split("\\+")) {
						if (roleValidator(roles, roleKey)) {
							roleKeys.add(roleKey);
						}
					}
					newAuthority += groupSever[0] + "*" + StringUtils.join(roleKeys.toArray(), "+") + ",";
				}
			}
		}
		return newAuthority;
	}

	private static boolean groupValidator(String[] groups, String tester) {
		for (String group : groups) {
			if (group.equals(tester)) {
				return true;
			}
		}
		return false;
	}

	private static boolean roleValidator(Object[] roles, String tester) {
		for (Object roleKey : roles) {
			if (roleKey.toString().equals(tester)) {
				return true;
			}
		}
		return false;
	}
}
