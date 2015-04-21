package com.hebut.rbac.core;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.hebut.rbac.model.RbacConfig;

public class XmlParser {

	private static String defualt = "configuration.xml";

	private static RbacConfig config;

	public static RbacConfig getRbacConfig() {
		if (config == null) {
			getRbacConfig(defualt);
		}
		return config;
	}

	public static RbacConfig getRbacConfig(String file) {
		if (config == null) {
			createRbacConfig(XmlParser.class.getResource("/").getPath() + file);
		}
		return config;
	}

	private static void createRbacConfig(String file) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(RbacConfig.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			config = (RbacConfig) jaxbUnmarshaller.unmarshal(new File(file));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
