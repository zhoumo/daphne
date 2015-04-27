package mine.daphne.aspect;

import java.util.List;

import mine.daphne.model.entity.Group;
import mine.daphne.model.entity.User;
import mine.daphne.security.core.CommonUtil;
import mine.daphne.security.core.Validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorityInjection {

	@Before("execution(public * mine.daphne.service..save*(..))")
	public void beforeSave(JoinPoint joinPoint) throws Throwable {
		Object[] object = joinPoint.getArgs();
		if (object[0] instanceof User) {
			User user = (User) object[0];
			user.setAuthority(Validator.authorityValidate(user.getAuthority(), CommonUtil.getRoleKeySet().toArray(), this.groupsCreator(user.getGroups())));
		}
	}

	private String[] groupsCreator(List<Group> groupList) {
		int index = 0;
		String[] groupIdList = new String[groupList.size()];
		for (Group group : groupList) {
			groupIdList[index++] = group.getId().toString();
		}
		return groupIdList;
	}
}
