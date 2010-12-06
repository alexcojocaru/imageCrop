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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

public class JBus {
	
	// holds the event types watched, along with the subscribers to each of them
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
					// add the object to the subscription list for the given event type
					String eventType = ((Subscriber)annotation).eventType();
					InstanceMethod subscriber = new InstanceMethod(o, method);
					addSubscriber(eventType, subscriber);
				}
			}
		}
	}
	
	/**
	 * add an object to the subscription list for the given event type
	 * @param eventType the type of the event to associate the subscriber to
	 * @param subscriber the subscriber to associate
	 */
	private void addSubscriber(String eventType, InstanceMethod subscriber) {
		HashSet<InstanceMethod> subscribers = subscriberMap.get(eventType);
		
		// if there's a subscriber list already, add the new one
		if (subscribers != null) {
			subscribers.add(subscriber);
			return;
		}
		
		// the event type doesn't have a subscriber list yet; create one and add the subscriber
		subscribers = new HashSet<InstanceMethod>();
		subscribers.add(subscriber);
		subscriberMap.put(eventType, subscribers);
	}
	
	/**
	 * post a property change notification to all registered subscribers
	 * @param eventType the event type corresponding to the property that changed
	 * @param value the new value of the property
	 */
	public void post(String eventType, Object value) {
		// get the list of subscribers for this property
		HashSet<InstanceMethod> subscribers = subscriberMap.get(eventType);
		
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
	
}
