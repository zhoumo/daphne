package com.hebut.framework.util;

import com.hebut.framework.factory.BeanFactory;
import com.hebut.framework.service.BaseService;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;

public class CacheUtil {

	public static final String defaultCache = "defaultCache";

	public static Cache getCache(String cacheName) {
		Cache cache = (Cache) BeanFactory.getBean(cacheName == null ? CacheUtil.defaultCache : cacheName);
		if (cache == null) {
			throw new CacheException("Can not find Cache:" + cacheName);
		}
		return cache;
	}

	public static String getCacheKey(Class<?> entity, String id) {
		return entity.getName() + "_" + id;
	}

	public static void initCache(Class<?> clazz) {
		BaseService baseService = (BaseService) BeanFactory.getBean("baseService");
		baseService.batchInsertCache(clazz, baseService.listAll(clazz), null);
	}
}
