package com.alexalecu.dataBinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

public class JBus {
	
	// holds the properties which are watched, along with the subscribers to each of them
	private HashMap<String, HashSet<InstanceMethod>> subscriberMap =
		new HashMap<String, HashSet<InstanceMethod>>();
	
	// create a singleton instance
	private static JBus jbus = new JBus();
	
	/**
	 * @return the singleton instance of the JBus
	 */
	public static JBus getInstance() {
		return jbus;
	}

	/**
	 * register the object with the message bus; all its subscriber annotated methods will be called
	 * based on the property to which they subscribed
	 * @param o the object to register
	 */
	public void register(Object o) {
		// search all subscriber annotated methods
		Method[] methods = o.getClass().getMethods();
		for (Method method : methods) {
			Annotation[] annotations = method.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof Subscriber) {
					// add the object to the subscription list of the current property name
					String propertyName = ((Subscriber)annotation).property();
					InstanceMethod subscriber = new InstanceMethod(o, method);
					addSubscriber(propertyName, subscriber);
				}
			}
		}
	}
	
	/**
	 * post a property change notification to all registered subscribers
	 * @param propertyName the name of the property that changed
	 * @param value the new value of the property
	 */
	public void post(String propertyName, Object value) {
		// get the list of subscribers for this property
		HashSet<InstanceMethod> subscribers = subscriberMap.get(propertyName);
		
		if (subscribers == null)
			return;
		
		// and call the subscribed method for each subscribed object
		for (InstanceMethod subscriber : subscribers) {
			try {
				subscriber.getMethod().invoke(subscriber.getObject(), value);
			}
			catch (IllegalArgumentException e) {
				// don't throw the exception, continue with the next subscriber
			}
			catch (IllegalAccessException e) {
				// don't throw the exception, continue with the next subscriber
			}
			catch (InvocationTargetException e) {
				// don't throw the exception, continue with the next subscriber
			}
		}
	}
	
	/**
	 * add an object to the subscription list for the given property
	 * @param propertyName the name of the property to associate the subscriber to
	 * @param subscriber the subscriber to associate
	 */
	private void addSubscriber(String propertyName, InstanceMethod subscriber) {
		HashSet<InstanceMethod> subscribers = subscriberMap.get(propertyName);
		
		// if there's a subscriber list already, add the new one
		if (subscribers != null) {
			subscribers.add(subscriber);
			return;
		}
		
		// the property doesn't have a subscriber list yet; create one and add the subscriber to it
		subscribers = new HashSet<InstanceMethod>();
		subscribers.add(subscriber);
		subscriberMap.put(propertyName, subscribers);
	}
	
}
