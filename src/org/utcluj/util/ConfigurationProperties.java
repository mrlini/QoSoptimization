package org.utcluj.util;

import java.util.Properties;

public class ConfigurationProperties {

	private final static String PDDL_PATH = "pddl.path";

	// private final static String BPEL_PATH = "bpel.path";

	private final static String CURRENT_MACRO_DEFINITIONS = "macro.def.current";

	private final static String OUT_FILE = "out.txt";

	private static Properties config = new Properties();

	// defaults
	static {

		config.setProperty(PDDL_PATH, "plan.xml");
		config.setProperty(CURRENT_MACRO_DEFINITIONS,
				"MacroActivities/MacroActivities50.xml");
	}

	public static Properties getProperties() {

		return config;
	}

	public static String getPddlPath() {

		return config.getProperty(PDDL_PATH);
	}

	public static void setPddlPath(String path) {

		config.setProperty(PDDL_PATH, path);
	}

	public static String getCurrentMacroDefinitions() {

		return config.getProperty(CURRENT_MACRO_DEFINITIONS);
	}

	public static void setCurrentMacroDefinitions(String path) {

		config.setProperty(CURRENT_MACRO_DEFINITIONS, path);
	}

	public static String getOutFile() {

		return config.getProperty(OUT_FILE);
	}

	public static void setOutFile(String file) {

		config.setProperty(OUT_FILE, file);
	}
}
