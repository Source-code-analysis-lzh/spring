/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.web.context.support;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySource.StubPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiPropertySource;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ConfigurableWebEnvironment;

/**
 * 基于{@code Servlet}的Web应用程序将使用的{@link Environment}实现。 默认情况下，所有与Web相关的（基于Servlet的）
 * {@code ApplicationContext}类都将初始化这个实例。
 *
 * <p>借助于{@code ServletConfig}，{@code ServletContext}和基于JNDI的{@link PropertySource}实例。 
 * 有关详细信息，请参见{@link #customizePropertySources}方法文档。
 *
 * @author Chris Beams
 * @since 3.1
 * @see StandardEnvironment
 */
public class StandardServletEnvironment extends StandardEnvironment implements ConfigurableWebEnvironment {

	/** Servlet context init parameters property source name: {@value}. */
	public static final String SERVLET_CONTEXT_PROPERTY_SOURCE_NAME = "servletContextInitParams";

	/** Servlet config init parameters property source name: {@value}. */
	public static final String SERVLET_CONFIG_PROPERTY_SOURCE_NAME = "servletConfigInitParams";

	/** JNDI property source name: {@value}. */
	public static final String JNDI_PROPERTY_SOURCE_NAME = "jndiProperties";


	/**
	 * 使用超类提供的属性源以及适用于基于servlet的标准环境的属性源来定制属性源集：
	 * <ul>
	 * <li>{@value #SERVLET_CONFIG_PROPERTY_SOURCE_NAME}
	 * <li>{@value #SERVLET_CONTEXT_PROPERTY_SOURCE_NAME}
	 * <li>{@value #JNDI_PROPERTY_SOURCE_NAME}
	 * </ul>
	 * <p>{@value #SERVLET_CONFIG_PROPERTY_SOURCE_NAME}中存在的属性将优先于{@value #SERVLET_CONTEXT_PROPERTY_SOURCE_NAME}
	 * 中的属性，而在上述任一方法中发现的属性都将优先于{@value #JNDI_PROPERTY_SOURCE_NAME}中发现的属性。
	 * <p>以上任何一项中的属性都将优先于{@link StandardEnvironment}超类提供的系统属性和环境变量。
	 * <p>与{@code Servlet}相关的属性源在此阶段作为{@link StubPropertySource stubs}添加，
	 * 并且在实际的{@link ServletContext}对象可用时将
	 * {@linkplain #initPropertySources(ServletContext, ServletConfig) 完全初始化}。
	 * @see StandardEnvironment#customizePropertySources
	 * @see org.springframework.core.env.AbstractEnvironment#customizePropertySources
	 * @see ServletConfigPropertySource
	 * @see ServletContextPropertySource
	 * @see org.springframework.jndi.JndiPropertySource
	 * @see org.springframework.context.support.AbstractApplicationContext#initPropertySources
	 * @see #initPropertySources(ServletContext, ServletConfig)
	 */
	@Override
	protected void customizePropertySources(MutablePropertySources propertySources) {
		propertySources.addLast(new StubPropertySource(SERVLET_CONFIG_PROPERTY_SOURCE_NAME));
		propertySources.addLast(new StubPropertySource(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME));
		if (JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable()) {
			propertySources.addLast(new JndiPropertySource(JNDI_PROPERTY_SOURCE_NAME));
		}
		super.customizePropertySources(propertySources);
	}

	@Override
	public void initPropertySources(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
		WebApplicationContextUtils.initServletPropertySources(getPropertySources(), servletContext, servletConfig);
	}

}
