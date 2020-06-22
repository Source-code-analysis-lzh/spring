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

package org.springframework.beans.factory.config;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * Extension of the {@link InstantiationAwareBeanPostProcessor} interface,
 * adding a callback for predicting the eventual type of a processed bean.
 * 扩展{@link InstantiationAwareBeanPostProcessor}接口，添加了用于预测已处理bean的最终类型的回调.
 *
 * <p>注意：此接口是专用接口，主要供框架内部使用.
 * 通常，应用程序提供的后处理器应仅实现纯{@link BeanPostProcessor}接口，
 * 或从{@link InstantiationAwareBeanPostProcessorAdapter}类派生. 在点发行版中，新方法也可能会添加到此接口.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see InstantiationAwareBeanPostProcessorAdapter
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

	/**
	 * 预测从此处理器的{@link #postProcessBeforeInstantiation}回调中最终返回的bean的类型.
	 * <p>默认实现返回{@code null}.
	 * @param beanClass the raw class of the bean
	 * @param beanName the name of the bean
	 * @return the type of the bean, or {@code null} if not predictable
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	@Nullable
	default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	/**
	 * 确定要用于给定bean的候选构造函数.
	 * <p>默认实现返回{@code null}.
	 * @param beanClass the raw class of the bean (never {@code null})
	 * @param beanName the name of the bean
	 * @return the candidate constructors, or {@code null} if none specified
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	@Nullable
	default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
			throws BeansException {

		return null;
	}

	/**
	 * 获取引用，以便尽早访问指定的bean，通常是为了解决循环引用.
	 * <p>此回调使后处理器有机会尽早公开包装器，也就是在目标Bean实例完全初始化之前.
	 * 暴露的对象应该等效于{@link #postProcessBeforeInitialization} / {@link #postProcessAfterInitialization}
	 * 将暴露的对象.注意，除非后处理器返回与所述后处理回调不同的包装，否则此方法返回的对象将用作Bean引用.
	 * 换句话说：这些后期处理回调可能最终会公开相同的引用，或者从这些后续回调中返回原始bean实例
	 * （如果已经为该方法的调用构建了受影响的bean的包装，它将被公开.作为默认的最终bean引用）.
	 * <p>默认实现按原样返回给定的{@code bean}.利用SmartInstantiationAwareBeanPostProcessor可以改变一下提前暴露的对象。
	 * @param bean the raw bean instance
	 * @param beanName the name of the bean
	 * @return the object to expose as bean reference
	 * (typically with the passed-in bean instance as default)
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
