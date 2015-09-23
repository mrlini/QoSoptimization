package org.utcluj.io;

import org.utcluj.model.MacroActivity;

public class MacroActivitiesClassLoader {

	public static MacroActivity loadByCategory(String category) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		return (MacroActivity) Class.forName("org.utcluj.model.service." + category).newInstance();
		
	}
}
