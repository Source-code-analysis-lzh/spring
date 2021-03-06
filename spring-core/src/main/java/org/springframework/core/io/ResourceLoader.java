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

package org.springframework.core.io;

import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

/**
 * 用于加载资源（例如类路径或文件系统资源）的策略接口. 提供此功能
 * 需要{@link org.springframework.context.ApplicationContext}，
 * 再加上扩展的{@link org.springframework.core.io.support.ResourcePatternResolver}支持.
 *
 * <p>{@link DefaultResourceLoader}是一个独立的实现，可在ApplicationContext外部使用，也由{@link ResourceEditor}使用.
 *
 * <p>Bean properties of type Resource and Resource array can be populated
 * from Strings when running in an ApplicationContext, using the particular
 * context's resource loading strategy.
 * <p>当在ApplicationContext中运行时，可以使用特定上下文的资源加载策略从Strings填充Resource和Resource array类型的Bean属性.
 *
 * @author Juergen Hoeller
 * @since 10.03.2004
 * @see Resource
 * @see org.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourceLoader {

	/** 从类路径"classpath:"加载的伪URL前缀. */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;


	/**
	 * 返回指定资源位置的资源句柄.
	 * <p>该资源句柄应该始终是可重用的资源描述符，并允许{@link Resource#getInputStream()}多次调用.
	 * <p><ul>
	 * <li>Must support fully qualified URLs, e.g. "file:C:/test.dat".
	 * <li>Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
	 * <li>Should support relative file paths, e.g. "WEB-INF/test.dat".
	 * (这将是特定于实现的，通常由ApplicationContext实现提供.)
	 * </ul>
	 * <p>请注意，资源句柄并不意味着存在的资源； 您需要调用{@link Resource#exists}来检查是否存在.
	 * @param location the resource location
	 * @return a corresponding Resource handle (never {@code null})
	 * @see #CLASSPATH_URL_PREFIX
	 * @see Resource#exists()
	 * @see Resource#getInputStream()
	 */
	Resource getResource(String location);

	/**
	 * 公开此ResourceLoader使用的ClassLoader.
	 * <p>需要直接访问ClassLoader的客户端可以使用ResourceLoader以统一的方式进行操作，而不是依赖于线程上下文ClassLoader.
	 * @return the ClassLoader
	 * (only {@code null} if even the system ClassLoader isn't accessible)
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 * @see org.springframework.util.ClassUtils#forName(String, ClassLoader)
	 */
	@Nullable
	ClassLoader getClassLoader();

}
