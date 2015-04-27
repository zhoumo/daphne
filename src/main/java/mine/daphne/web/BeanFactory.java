package mine.daphne.web;

import org.springframework.web.context.WebApplicationContext;

public class BeanFactory {

	private static WebApplicationContext webApplicationContext = null;

	public static void setWebApplicationContext(WebApplicationContext webApplicationContext) {
		BeanFactory.webApplicationContext = webApplicationContext;
	}

	public static Object getBean(String beanName) {
		if (webApplicationContext == null) {
			throw new RuntimeException("应用环境错误");
		}
		return webApplicationContext.getBean(beanName);
	}
}
