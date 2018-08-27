package de.unistuttgart.iaas.amyassist.amy.core.natlang.userinteraction;

import java.lang.reflect.Method;

import de.unistuttgart.iaas.amyassist.amy.core.natlang.aim.XMLAIMIntent;

/**
 * Helper Template class
 * 
 * @author Felix Burk
 */
public class UserIntentTemplate {
	
	private final Method method;
	private final XMLAIMIntent xml;
	
	/**
	 * constructor
	 * @param method to use
	 * @param xml to use
	 */
	public UserIntentTemplate(Method method, XMLAIMIntent xml) {
		this.method = method;
		this.xml = xml;
	}

	/**
	 * Get's {@link #method method}
	 * @return  method
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * Get's {@link #xml xml}
	 * @return  xml
	 */
	public XMLAIMIntent getXml() {
		return this.xml;
	}
	
	
}
