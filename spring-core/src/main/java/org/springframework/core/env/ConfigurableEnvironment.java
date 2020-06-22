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

package org.springframework.core.env;

import java.util.Map;

/**
 * 大多数（如果不是全部）{@link Environment}类型都将实现配置接口.
 * 提供用于设置活动和默认配置文件以及操纵底层属性源的工具.
 * 允许客户端通过{@link ConfigurablePropertyResolver}超级接口设置和验证所需的属性，自定义转换服务以及其它功能.
 *
 * <h2>Manipulating property sources</h2>
 * <p>Property sources may be removed, reordered, or replaced; and additional
 * property sources may be added using the {@link MutablePropertySources}
 * instance returned from {@link #getPropertySources()}. The following examples
 * are against the {@link StandardEnvironment} implementation of
 * {@code ConfigurableEnvironment}, but are generally applicable to any implementation,
 * though particular default property sources may differ.
 * <p>Property sources可能会被删除，重新排序或替换；
 * 并且可以使用从{@link #getPropertySources()}返回的{@link MutablePropertySources}实例添加其他属性源.
 * 以下示例与{@code ConfigurableEnvironment}的{@link StandardEnvironment}实现相反，
 * 但通常适用于任何实现，尽管特定的默认属性来源可能有所不同.
 *
 * <h4>Example: adding a new property source with highest search priority</h4>
 * <pre class="code">
 * ConfigurableEnvironment environment = new StandardEnvironment();
 * MutablePropertySources propertySources = environment.getPropertySources();
 * Map&lt;String, String&gt; myMap = new HashMap&lt;&gt;();
 * myMap.put("xyz", "myValue");
 * propertySources.addFirst(new MapPropertySource("MY_MAP", myMap));
 * </pre>
 *
 * <h4>Example: removing the default system properties property source</h4>
 * <pre class="code">
 * MutablePropertySources propertySources = environment.getPropertySources();
 * propertySources.remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)
 * </pre>
 *
 * <h4>Example: mocking the system environment for testing purposes</h4>
 * <pre class="code">
 * MutablePropertySources propertySources = environment.getPropertySources();
 * MockPropertySource mockEnvVars = new MockPropertySource().withProperty("xyz", "myValue");
 * propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars);
 * </pre>
 *
 * 当{@code ApplicationContext}使用{@link Environment}时，在调用上下文的
 * {@link org.springframework.context.support.AbstractApplicationContext#refresh() refresh()}
 * 方法之前执行任何此类{@code PropertySource}操作非常重要.
 * 这样可以确保在容器启动过程中所有属性源均可用，包括{@linkplain org.springframework.context.support.PropertySourcesPlaceholderConfigurer 属性占位符配置器}使用.
 *
 * @author Chris Beams
 * @since 3.1
 * @see StandardEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 */
public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {

	/**
	 * 指定为此{@code Environment}激活的配置文件集。 在容器引导期间计算Profiles文件，以确定是否应在容器中注册bean定义。
	 * <p>任何现有的活动配置文件都将替换为给定的参数； 使用零参数进行调用以清除当前的活动配置文件集。 
	 * 使用{@link #addActiveProfile}在保留现有集合的同时添加配置文件。
	 * @throws IllegalArgumentException if any profile is null, empty or whitespace-only
	 * @see #addActiveProfile
	 * @see #setDefaultProfiles
	 * @see org.springframework.context.annotation.Profile
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 */
	void setActiveProfiles(String... profiles);

	/**
	 * 将配置文件添加到当前的活动配置文件集中。
	 * @throws IllegalArgumentException if the profile is null, empty or whitespace-only
	 * @see #setActiveProfiles
	 */
	void addActiveProfile(String profile);

	/**
	 * 如果没有通过{@link #setActiveProfiles}显式激活其他配置文件，则指定默认情况下将其激活的配置文件集。
	 * @throws IllegalArgumentException if any profile is null, empty or whitespace-only
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 */
	void setDefaultProfiles(String... profiles);

	/**
	 * 以可变形式返回此{@code Environment}的{@link PropertySources}，
	 * 从而允许处理在针对该{@code Environment}对象解析属性时应搜索的{@link PropertySource}对象集。 
	 * 各种{@link MutablePropertySources}方法（例如{@link MutablePropertySources#addFirst addFirst}
	 * {@link MutablePropertySources#addLast addLast},{@link MutablePropertySources#addBefore addBefore}
	 * 和{@link MutablePropertySources#addAfter addAfter}）允许对属性源顺序进行细粒度控制。 
	 * 例如，这在确保某些用户定义的属性源具有优先于默认属性源（例如系统属性集或系统环境变量集）的搜索优先级时很有用。
	 * @see AbstractEnvironment#customizePropertySources
	 */
	MutablePropertySources getPropertySources();

	/**
	 * Return the value of {@link System#getProperties()} if allowed by the current
	 * {@link SecurityManager}, otherwise return a map implementation that will attempt
	 * to access individual keys using calls to {@link System#getProperty(String)}.
	 * <p>Note that most {@code Environment} implementations will include this system
	 * properties map as a default {@link PropertySource} to be searched. Therefore, it is
	 * recommended that this method not be used directly unless bypassing other property
	 * sources is expressly intended.
	 * <p>Calls to {@link Map#get(Object)} on the Map returned will never throw
	 * {@link IllegalAccessException}; in cases where the SecurityManager forbids access
	 * to a property, {@code null} will be returned and an INFO-level log message will be
	 * issued noting the exception.
	 */
	Map<String, Object> getSystemProperties();

	/**
	 * 如果当前{@link SecurityManager}允许，则返回{@link System#getenv()}的值，否则返回一个map实现，
	 * 该实现将尝试使用对{@link System#getenv(String)}的调用来访问各个键。
	 * <p>请注意，大多数{@link Environment}实现都将包含此系统属性map，作为要搜索的默认{@link PropertySource}。 
	 * 因此，建议不要直接使用此方法，除非明确打算绕过其它属性源。
	 * <p>在返回的Map上调用{@link Map#get(Object)}永远不会引发{@link IllegalAccessException}； 
	 * 在SecurityManager禁止访问属性的情况下，将返回{@code null}并发出INFO级别的日志消息，指出该异常。
	 */
	Map<String, Object> getSystemEnvironment();

	/**
	 * 将给定的父环境的活动配置文件，默认配置文件和属性源追加到此（子）环境各自的集合中。
	 * <p>对于父代和子代中都存在的任何名称相同的{@code PropertySource}实例，将保留子代实例，并丢弃父代实例。
	 * 这样的效果是允许子级覆盖属性源，并避免对常见属性源类型冗余搜索例如， 系统环境和系统属性。
	 * <p>活动和默认配置文件名称也会被过滤，以防重复，以避免混淆和冗余存储。
	 * <p>在任何情况下，父环境都保持不变。 请注意，在调用{@code merge}之后对父环境所做的任何更改都不会反映在子项中。 
	 * 因此，在调用{@code merge}之前，应注意配置父属性源和配置文件信息。
	 * @param parent the environment to merge with
	 * @since 3.1.2
	 * @see org.springframework.context.support.AbstractApplicationContext#setParent
	 */
	void merge(ConfigurableEnvironment parent);

}
