package com.alexalecu.dataBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * subscribe the method to notifications fired for multiple notify event types;
 * only public methods will receive notifications due to the limitations imposed by the JBus
 * during the registration process
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscriberList {

	public Subscriber[] value();
	
}
