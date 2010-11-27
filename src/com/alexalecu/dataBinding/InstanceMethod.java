package com.alexalecu.dataBinding;

import java.lang.reflect.Method;

/**
 * stores a method along with the class instance it occurs in
 */
public class InstanceMethod {

	private Object object;
	private Method method;
	
	public InstanceMethod(Object object, Method method) {
		this.object = object;
		this.method = method;
	}
	
	/**
	 * @return the class instance which holds the method
	 */
	public Object getObject() {
		return object;
	}
	
	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}
	
}
