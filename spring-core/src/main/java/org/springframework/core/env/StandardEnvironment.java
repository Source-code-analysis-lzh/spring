/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.core.env;

/**
 * 适用于“标准”（即非Web）应用程序的{@link Environment}实现。
 *
 * <p>除了诸如属性解析和与配置(profile)文件相关的操作之类的{@link ConfigurableEnvironment}常用功能之外，
 * 此实现还配置了两个默认属性源，将按以下顺序搜索：
 * <ul>
 * <li>{@linkplain AbstractEnvironment#getSystemProperties() system properties}
 * <li>{@linkplain AbstractEnvironment#getSystemEnvironment() system environment variables}
 * </ul>
 *
 * 也就是说，如果键"xyz"同时存在于JVM系统属性以及当前进程的环境变量集中，则调用{@code environment.getProperty("xyz")}
 * 将从系统属性中返回该"xyz"的值。
 * 默认情况下选择此顺序，是因为系统属性是针对每个JVM的，而环境变量在给定系统上的许多JVM中可能是相同的。 
 * 通过赋予系统属性优先级，可以基于每个JVM覆盖环境变量。
 *
 * <p>这些默认属性来源可能会被删除，重新排序或替换。 并且可以使用{@link #getPropertySources()}中的
 * {@link MutablePropertySources}实例添加其它属性源。 有关用法示例，请参见{@link ConfigurableEnvironment} Javadoc。
 *
 * <p>请参阅{@link SystemEnvironmentPropertySource} Javadoc，以获取有关shell环境（例如Bash）中属性名称的特殊处理的详细信息，
 * 这些属性不允许变量名称中使用句点字符。
 *
 * @author Chris Beams
 * @since 3.1
 * @see ConfigurableEnvironment
 * @see SystemEnvironmentPropertySource
 * @see org.springframework.web.context.support.StandardServletEnvironment
 */
public class StandardEnvironment extends AbstractEnvironment {

	/** System environment property source name: {@value}. */
	public static final String SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME = "systemEnvironment";

	/** JVM system properties property source name: {@value}. */
	public static final String SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME = "systemProperties";


	/**
	 * 使用适用于任何标准Java环境的属性定制属性源集：
	 * <ul>
	 * <li>{@value #SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME}
	 * <li>{@value #SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME}
	 * </ul>
	 * <p>{@value #SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME}中存在的属性将优先于
	 * {@value #SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME}中的属性。
	 * @see AbstractEnvironment#customizePropertySources(MutablePropertySources)
	 * @see #getSystemProperties()
	 * @see #getSystemEnvironment()
	 */
	@Override
	protected void customizePropertySources(MutablePropertySources propertySources) {
		propertySources.addLast(
				new PropertiesPropertySource(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, getSystemProperties()));
		propertySources.addLast(
				new SystemEnvironmentPropertySource(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, getSystemEnvironment()));
	}

}
