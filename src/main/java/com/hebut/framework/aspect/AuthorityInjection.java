package com.hebut.framework.aspect;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.hebut.framework.model.entity.FwkGroup;
import com.hebut.framework.model.entity.FwkUser;
import com.hebut.rbac.core.CommonUtil;
import com.hebut.rbac.core.Validator;

@Aspect
@Component
public class AuthorityInjection {

	@Before("execution(public * com.hebut.framework.service..save*(..))")
	public void beforeSave(JoinPoint joinPoint) throws Throwable {
		Object[] object = joinPoint.getArgs();
		if (object[0] instanceof FwkUser) {
			FwkUser user = (FwkUser) object[0];
			user.setFuAuthority(Validator.authorityValidate(user.getFuAuthority(), CommonUtil.getRoleKeySet().toArray(), this.groupsCreator(user.getGroups())));
		}
	}

	private String[] groupsCreator(List<FwkGroup> groupList) {
		int index = 0;
		String[] groupIdList = new String[groupList.size()];
		for (FwkGroup group : groupList) {
			groupIdList[index++] = group.getFgId().toString();
		}
		return groupIdList;
	}
}
