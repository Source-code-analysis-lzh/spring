/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.jndi;

import javax.naming.NamingException;

import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

/**
 * 从底层Spring {@link JndiLocatorDelegate}读取属性的{@link PropertySource}实现。
 *
 * <p>默认情况下，底层{@code JndiLocatorDelegate}将配置其{@link JndiLocatorDelegate#setResourceRef(boolean) "resourceRef"}
 * 属性设置为{@code true}，这意味着所查找的名称将自动以"java:comp/env/"为前缀，以符合已发布的JNDI
 * <a href="https://download.oracle.com/javase/jndi/tutorial/beyond/misc/policy.html">JNDI
 * naming conventions</a>。 要覆盖此设置或更改前缀，请手动配置{@code JndiLocatorDelegate}并将其提供给此处接受它的构造函数之一。
 * 提供自定义JNDI属性时，也是如此。 在构造{@code JndiPropertySource}之前，
 * 应使用{@link JndiLocatorDelegate#setJndiEnvironment(java.util.Properties)}指定这些属性。
 *
 * <p>请注意，默认情况下{@link org.springframework.web.context.support.StandardServletEnvironment
 * StandardServletEnvironment}包括{@code JndiPropertySource}，并且可以在
 * {@link org.springframework.context.ApplicationContextInitializer
 * ApplicationContextInitializer}或{@link org.springframework.web.WebApplicationInitializer
 * WebApplicationInitializer}中执行底层{@link JndiLocatorDelegate}的任何自定义。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see JndiLocatorDelegate
 * @see org.springframework.context.ApplicationContextInitializer
 * @see org.springframework.web.WebApplicationInitializer
 * @see org.springframework.web.context.support.StandardServletEnvironment
 */
public class JndiPropertySource extends PropertySource<JndiLocatorDelegate> {

	/**
	 * Create a new {@code JndiPropertySource} with the given name
	 * and a {@link JndiLocatorDelegate} configured to prefix any names with
	 * "java:comp/env/".
	 */
	public JndiPropertySource(String name) {
		this(name, JndiLocatorDelegate.createDefaultResourceRefLocator());
	}

	/**
	 * Create a new {@code JndiPropertySource} with the given name and the given
	 * {@code JndiLocatorDelegate}.
	 */
	public JndiPropertySource(String name, JndiLocatorDelegate jndiLocator) {
		super(name, jndiLocator);
	}


	/**
	 * This implementation looks up and returns the value associated with the given
	 * name from the underlying {@link JndiLocatorDelegate}. If a {@link NamingException}
	 * is thrown during the call to {@link JndiLocatorDelegate#lookup(String)}, returns
	 * {@code null} and issues a DEBUG-level log statement with the exception message.
	 */
	@Override
	@Nullable
	public Object getProperty(String name) {
		if (getSource().isResourceRef() && name.indexOf(':') != -1) {
			// We're in resource-ref (prefixing with "java:comp/env") mode. Let's not bother
			// with property names with a colon it since they're probably just containing a
			// default value clause, very unlikely to match including the colon part even in
			// a textual property source, and effectively never meant to match that way in
			// JNDI where a colon indicates a separator between JNDI scheme and actual name.
			return null;
		}

		try {
			Object value = this.source.lookup(name);
			if (logger.isDebugEnabled()) {
				logger.debug("JNDI lookup for name [" + name + "] returned: [" + value + "]");
			}
			return value;
		}
		catch (NamingException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("JNDI lookup for name [" + name + "] threw NamingException " +
						"with message: " + ex.getMessage() + ". Returning null.");
			}
			return null;
		}
	}

}
