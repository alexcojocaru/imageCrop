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

public class Property<T> {
	private JBus bus;
	private String propertyName;
	private T value;
	
	/**
	 * create a bindable property
	 * @param bus the message bus responsible for transporting this type of property
	 * @param propertyName the name used to describe this property on the bus
	 */
	public Property(JBus bus, String propertyName) {
		this.bus = bus;
		this.propertyName = propertyName;
	}
	
	/**
	 * @return the value stored by this property
	 */
	public T get() {
		return value;
	}
	
	/**
	 * set the value stored by this property and post a property change notification on the bus
	 * @param value
	 */
	public void set(T value) {
		this.value = value;
		
		bus.post(propertyName, value);
	}
}
