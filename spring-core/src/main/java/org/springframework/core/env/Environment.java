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

/**
 * 表示当前应用程序正在其中运行的环境的接口.
 * 应用程序环境模型的两个关键方面：<em>profiles</em>和<em>properties</em>.
 * 与属性访问有关的方法通过{@link PropertyResolver}超级接口公开.
 *
 * <p><em>profile</em>文件是仅在给定<em>profile</em>文件处于活动状态时才向容器注册的Bean定义的命名逻辑组.
 * 无论是用XML定义还是通过注释定义，可以将Bean分配给<em>profile</em>文件.
 * 有关语法的详细信息，请参见spring-beans 3.1模式或{@link org.springframework.context.annotation.Profile @Profile}
 * 批注. 与配置文件相关的环境对象的作用是确定当前哪些配置文件（如果有）处于活动状态，
 * 以及默认情况下哪些配置文件（如果有）应处于活动状态.
 *
 * <p><em>Properties</em>在几乎所有应用程序中都起着重要作用，
 * 并且可能源自各种来源：属性文件，JVM系统属性，系统环境变量，JNDI，Servlet上下文参数，临时属性对象，Map等.
 * 环境对象与属性的关系是为用户提供方便的服务接口，以配置属性源并从中解析属性.
 *
 * <p>在{@code ApplicationContext}中管理的Bean可以注册为
 * {@link org.springframework.context.EnvironmentAware EnvironmentAware}或{@code @Inject}
 * {@code Environment}以便直接查询profile文件状态或解析属性.
 *
 * <p>但是，在大多数情况下，应用程序级Bean不必直接与{@code Environment}交互，
 * 而必须将{@code ${...}}属性值替换为诸如{@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer}
 * 之类的属性占位符配置程序，后者本身就是{@code EnvironmentAware}，
 * 并且自Spring 3.1开始使用{@code <context:property-placeholder/>}时，默认情况下会被注册.
 *
 * <p>必须通过从所有{@code AbstractApplicationContext}子类{@code getEnvironment()}
 * 方法返回的{@code ConfigurableEnvironment}接口完成环境对象的配置.
 * 请参阅{@link ConfigurableEnvironment} Javadoc以获取使用示例，
 * 这些示例演示在应用程序上下文{@code refresh()}之前对属性源进行的操作.
 *
 * @author Chris Beams
 * @since 3.1
 * @see PropertyResolver
 * @see EnvironmentCapable
 * @see ConfigurableEnvironment
 * @see AbstractEnvironment
 * @see StandardEnvironment
 * @see org.springframework.context.EnvironmentAware
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#setEnvironment
 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
 */
public interface Environment extends PropertyResolver {

	/**
	 * 返回为此环境显式激活的profiles文件集. profile文件用于创建Bean定义的逻辑分组，
	 * 以便有条件地进行注册，例如基于部署环境. 可以通过将
	 * {@linkplain AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME "spring.profiles.active"}
	 * 设置为系统属性或调用{@link ConfigurableEnvironment#setActiveProfiles(String...)}来激活配置文件.
	 * 如果未将任何配置文件明确指定为活动配置文件，则将自动激活任何默认配置文件.
	 * @see #getDefaultProfiles
	 * @see ConfigurableEnvironment#setActiveProfiles
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 */
	String[] getActiveProfiles();

	/**
	 * 当未显式设置活动配置文件时，将返回默认情况下一组配置文件.
	 * @see #getActiveProfiles
	 * @see ConfigurableEnvironment#setDefaultProfiles
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 */
	String[] getDefaultProfiles();

	/**
	 * 返回一个或多个给定的配置文件是否处于活动状态，或者在没有显式活动配置文件的情况下，
	 * 返回一个或多个给定的配置文件是否包含在默认配置文件集中.
	 * 如果profile以'!'开头 逻辑取反，即如果给定的配置文件未激活，则该方法将返回{@code true}.
	 * 例如，如果配置文件'p1'处于活动状态或'p2'处于非活动状态，
	 * 则{@code env.acceptsProfiles("p1", "!p2")}将返回{@code true}.
	 * @throws IllegalArgumentException if called with zero arguments
	 * or if any profile is {@code null}, empty, or whitespace only
	 * @see #getActiveProfiles
	 * @see #getDefaultProfiles
	 * @see #acceptsProfiles(Profiles)
	 * @deprecated 不推荐使用. 从5.1开始，支持{@link #acceptsProfiles(Profiles)}
	 */
	@Deprecated
	boolean acceptsProfiles(String... profiles);

	/**
	 * 返回{@linkplain #getActiveProfiles() 活动 profiles}文件是否与给定的{@link Profiles}文件谓词匹配.
	 */
	boolean acceptsProfiles(Profiles profiles);

}
