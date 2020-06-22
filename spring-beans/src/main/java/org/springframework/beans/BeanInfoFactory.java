/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import org.springframework.lang.Nullable;

/**
 * 用于为Spring bean创建{@link BeanInfo}实例的策略接口。 
 * 可用于插入自定义bean属性解析策略（例如，用于JVM上的其他语言）或更有效的{@link BeanInfo}检索算法。
 *
 * <p>BeanInfoFactories通过使用{@link org.springframework.core.io.support.SpringFactoriesLoader}
 * 工具类由{@link CachedIntrospectionResults}实例化。
 *
 * 当创建{@link BeanInfo}时，{@code CachedIntrospectionResults}将遍历发现的工厂，并在每个工厂上调用{@link #getBeanInfo(Class)}。 
 * 如果返回{@code null}，将查询下一个工厂。 如果所有工厂都不支持该类，则将默认创建一个标准{@link BeanInfo}。
 *
 * <p>请注意，{@link org.springframework.core.io.support.SpringFactoriesLoader}
 * 通过{@link org.springframework.core.annotation.Order @Order}
 * 对{@code BeanInfoFactory}实例进行排序，因此优先级更高的优先。
 *
 * @author Arjen Poutsma
 * @since 3.2
 * @see CachedIntrospectionResults
 * @see org.springframework.core.io.support.SpringFactoriesLoader
 */
public interface BeanInfoFactory {

	/**
	 * 如果支持返回给定类的bean信息。
	 * @param beanClass the bean class
	 * @return the BeanInfo, or {@code null} if the given class is not supported
	 * @throws IntrospectionException in case of exceptions
	 */
	@Nullable
	BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException;

}
