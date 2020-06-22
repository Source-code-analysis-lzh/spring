/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 独立XML应用程序上下文，从类路径中获取上下文定义文件，
 * 将纯路径解释为包含包路径的类路径资源名称(e.g. "mypackage/myresource.txt").
 * 对于测试工具以及嵌入在JAR中的应用程序上下文很有用.
 *
 * <p>可以通过{@link #getConfigLocations}覆盖配置位置的默认值，
 * 配置位置可以表示"/myfiles/context.xml"之类的具体文件，
 * 也可以表示"/myfiles/*-context.xml"之类的Ant样式模式
 * （请参阅 {@link org.springframework.util.AntPathMatcher} 有关模式详细信息的javadoc）.
 *
 * <p>注意：如果有多个配置位置，则较新的Bean定义将覆盖较早加载的文件中定义的定义.
 * 可以利用它来通过一个额外的XML文件有意覆盖某些bean定义.
 *
 * <p><b>这是一个简单的一站式便利ApplicationContext.
 * 考虑将{@link GenericApplicationContext}类与{@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}结合使用，以实现更灵活的上下文设置.</b>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getResource
 * @see #getResourceByPath
 * @see GenericApplicationContext
 */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {

	@Nullable
	private Resource[] configResources;


	/**
	 * 为bean样式的配置创建一个新的ClassPathXmlApplicationContext.
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public ClassPathXmlApplicationContext() {
	}

	/**
	 * 为bean样式的配置创建一个新的ClassPathXmlApplicationContext.
	 * @param parent the parent context
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public ClassPathXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}

	/**
	 * 创建一个新的，从给定的XML文件中加载定义，并自动刷新上下文的ClassPathXmlApplicationContext.
	 * @param configLocation resource location
	 * @throws BeansException if context creation failed
	 */
	public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
		this(new String[] {configLocation}, true, null);
	}

	/**
	 * 创建一个新的，从给定的XML文件中加载定义，并自动刷新上下文的ClassPathXmlApplicationContext.
	 * @param configLocations array of resource locations
	 * @throws BeansException if context creation failed
	 */
	public ClassPathXmlApplicationContext(String... configLocations) throws BeansException {
		this(configLocations, true, null);
	}

	/**
	 * 创建一个新的，从给定的XML文件中加载定义，并自动刷新上下文的ClassPathXmlApplicationContext.
	 * @param configLocations array of resource locations
	 * @param parent the parent context
	 * @throws BeansException if context creation failed
	 */
	public ClassPathXmlApplicationContext(String[] configLocations, @Nullable ApplicationContext parent)
			throws BeansException {

		this(configLocations, true, parent);
	}

	/**
	 * 创建一个新的，从给定的XML文件中加载定义的ClassPathXmlApplicationContext.
	 * @param configLocations array of resource locations
	 * @param refresh whether to automatically refresh the context,
	 * loading all bean definitions and creating all singletons.
	 * Alternatively, call refresh manually after further configuring the context.
	 * @throws BeansException if context creation failed
	 * @see #refresh()
	 */
	public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
		this(configLocations, refresh, null);
	}

	/**
	 * 使用给定的父级创建一个新的，从给定的XML文件中加载定义的ClassPathXmlApplicationContext.
	 * @param configLocations array of resource locations
	 * @param refresh whether to automatically refresh the context,
	 * loading all bean definitions and creating all singletons.
	 * Alternatively, call refresh manually after further configuring the context.
	 * @param parent the parent context
	 * @throws BeansException if context creation failed
	 * @see #refresh()
	 */
	public ClassPathXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
			throws BeansException {

		super(parent);
		setConfigLocations(configLocations);
		if (refresh) {
			refresh();
		}
	}


	/**
	 * 创建一个新的，从给定的XML文件中加载定义，并自动刷新上下文的ClassPathXmlApplicationContext.
	 * <p>这是一种相对于给定Class加载类路径资源的便捷方法.
	 * 为了获得充分的灵活性，请考虑将GenericApplicationContext
	 * 与XmlBeanDefinitionReader和ClassPathResource参数一起使用.
	 * @param path relative (or absolute) path within the class path
	 * @param clazz the class to load resources with (basis for the given paths)
	 * @throws BeansException if context creation failed
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public ClassPathXmlApplicationContext(String path, Class<?> clazz) throws BeansException {
		this(new String[] {path}, clazz);
	}

	/**
	 * 创建一个新的，从给定的XML文件中加载定义，并自动刷新上下文的ClassPathXmlApplicationContext.
	 * @param paths array of relative (or absolute) paths within the class path
	 * @param clazz the class to load resources with (basis for the given paths)
	 * @throws BeansException if context creation failed
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz) throws BeansException {
		this(paths, clazz, null);
	}

	/**
	 * 使用给定的父级创建一个新的，从给定的XML文件中加载定义，并自动刷新上下文的ClassPathXmlApplicationContext.
	 * @param paths array of relative (or absolute) paths within the class path
	 * @param clazz the class to load resources with (basis for the given paths)
	 * @param parent the parent context
	 * @throws BeansException if context creation failed
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz, @Nullable ApplicationContext parent)
			throws BeansException {

		super(parent);
		Assert.notNull(paths, "Path array must not be null");
		Assert.notNull(clazz, "Class argument must not be null");
		this.configResources = new Resource[paths.length];
		for (int i = 0; i < paths.length; i++) {
			this.configResources[i] = new ClassPathResource(paths[i], clazz);
		}
		refresh();
	}


	@Override
	@Nullable
	protected Resource[] getConfigResources() {
		return this.configResources;
	}

}
