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
