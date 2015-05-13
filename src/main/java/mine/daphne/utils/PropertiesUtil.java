package mine.daphne.utils;

import java.util.Properties;

public class PropertiesUtil {

	private Properties properties;

	public PropertiesUtil() {
		load("/system.properties");
	}

	public PropertiesUtil(String fileName) {
		load(fileName);
	}

	public void load(String fileName) {
		try {
			properties = new Properties();
			properties.load(this.getClass().getResourceAsStream(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}
}
