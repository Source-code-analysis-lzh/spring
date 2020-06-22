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
import java.beans.Introspector;
import java.lang.reflect.Method;

import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/**
 * {@link BeanInfoFactory}实现可评估bean类是否具有“非标准” JavaBeans setter方法，
 * 判断是否是Spring的（对程序包可见的）{@code ExtendedBeanInfo}实现进行自省的候选对象。
 *
 * <p>以{@link Ordered#LOWEST_PRECEDENCE}排序，以允许其它用户定义的{@link BeanInfoFactory}类型优先。
 *
 * @author Chris Beams
 * @since 3.2
 * @see BeanInfoFactory
 * @see CachedIntrospectionResults
 */
public class ExtendedBeanInfoFactory implements BeanInfoFactory, Ordered {

	/**
	 * Return an {@link ExtendedBeanInfo} for the given bean class, if applicable.
	 */
	@Override
	@Nullable
	public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
		return (supports(beanClass) ? new ExtendedBeanInfo(Introspector.getBeanInfo(beanClass)) : null);
	}

	/**
	 * Return whether the given bean class declares or inherits any non-void
	 * returning bean property or indexed property setter methods.
	 */
	private boolean supports(Class<?> beanClass) {
		for (Method method : beanClass.getMethods()) {
			if (ExtendedBeanInfo.isCandidateWriteMethod(method)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
