package mine.daphne.security.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mine.daphne.security.model.RbacConfig;
import mine.daphne.security.model.application.Application;
import mine.daphne.security.model.application.Function;
import mine.daphne.security.model.application.Module;

import org.apache.commons.lang.StringUtils;

public class RoleFactory {

	private static Map<String, Application> authorityMap;

	public static Application getApplication(String roleKey) {
		if (StringUtils.isEmpty(roleKey)) {
			return new Application();
		}
		return authorityMap.get(roleKey);
	}

	public static void createFactory(RbacConfig config) throws CloneNotSupportedException {
		if (authorityMap == null) {
			authorityMap = new HashMap<String, Application>();
			for (Module module : config.getApplication().getModule()) {
				for (Function function : module.getFunctions()) {
					formatFunction(function, module.getRoles());
				}
			}
			Iterator<String> it = CommonUtil.getRoleKeySet().iterator();
			while (it.hasNext()) {
				Application application = (Application) config.getApplication().clone();
				String roleKey = it.next();
				List<Module> removeList = new ArrayList<Module>();
				for (int i = 0; i < application.getModule().size(); i++) {
					Module module = application.getModule().get(i);
					if (!module.getRoles().contains(roleKey)) {
						removeList.add(module);
					} else {
						for (Function function : module.getFunctions()) {
							removeFunction(function, module.getFunctions(), roleKey);
						}
					}
				}
				application.getModule().removeAll(removeList);
				authorityMap.put(roleKey, application);
			}
		}
	}

	private static void formatFunction(Function function, String roles) {
		if ("".equals(function.getRoles())) {
			function.setRoles(roles);
		}
		for (Function childFunction : function.getFunctions()) {
			formatFunction(childFunction, function.getRoles());
		}
	}

	private static <T> void removeFunction(Function function, List<T> functionList, String roleKey) {
		if (!function.getRoles().equals(roleKey)) {
			functionList.remove(function);
			return;
		}
		for (Function childFunction : function.getFunctions()) {
			removeFunction(childFunction, function.getFunctions(), roleKey);
		}
	}
}
