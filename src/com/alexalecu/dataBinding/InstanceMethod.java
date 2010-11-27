/*
 * Copyright (C) 2010 Alex Cojocaru
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
