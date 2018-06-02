/*
 * Copyright 2015-2018 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.jupiter.api.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnabledIfSystemProperty {

	/**
	 * The name of the JVM system property to retrieve.
	 *
	 * @return the system property name; never <em>blank</em>
	 * @see System#getProperty(String)
	 */
	String named();

	/**
	 * A regular expression that will be used to match against the retrieved
	 * value of the {@link #named} JVM system property.
	 *
	 * @return the regular expression; never <em>blank</em>
	 * @see String#matches(String)
	 * @see java.util.regex.Pattern
	 */
	String matches();

}
