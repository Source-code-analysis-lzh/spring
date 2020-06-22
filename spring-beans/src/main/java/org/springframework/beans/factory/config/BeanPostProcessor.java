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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * 工厂钩子允许自定义修改新的bean实例 &mdash;例如，检查标记接口或使用代理包装bean.
 *
 * <p>通常，通过标记接口等填充bean或者类似的后处理器将实现{@link #postProcessBeforeInitialization}，
 * 而使用代理包装bean的后处理器通常将实现{@link #postProcessAfterInitialization}.
 *
 * <h3>注册</h3>
 * <p>{@code ApplicationContext}可以在其bean定义中自动检测{@code BeanPostProcessor} bean，
 * 并将那些后处理器应用于随后创建的任何bean.
 * 一个普通的{@code BeanFactory}允许以编程方式注册后处理器，并将其应用于通过Bean工厂创建的所有Bean.
 *
 * <h3>排序</h3>
 * 在{@code ApplicationContext}中自动检测到的{@code BeanPostProcessor} bean
 * 将根据{@link org.springframework.core.PriorityOrdered}和{@link org.springframework.core.Ordered}语义进行排序.
 * 相反，通过{@code BeanFactory}编程注册的{@code BeanPostProcessor} Bean将按注册顺序应用；
 * 以编程方式注册的后处理器将忽略通过实现{@code PriorityOrdered}或{@code Ordered}接口表示的任何排序语义.
 * 此外，{@code BeanPostProcessor} Bean不考虑{@link org.springframework.core.annotation.Order @Order}注解.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor
 * @see DestructionAwareBeanPostProcessor
 * @see ConfigurableBeanFactory#addBeanPostProcessor
 * @see BeanFactoryPostProcessor
 */
public interface BeanPostProcessor {

	/**
	 * 在任何bean初始化回调（例如InitializingBean的{@code afterPropertiesSet}或自定义init-method）之前，
	 * 将此{@code BeanPostProcessor}应用于给定的新bean实例. 该bean将用属性值填充. 返回的bean实例可能是原始实例的包装.
	 * <p>默认实现按原样返回给定的{@code bean}.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * 在任何bean初始化回调（例如InitializingBean的{@code afterPropertiesSet}或自定义的init-method）之后，
	 * 将此{@code BeanPostProcessor}应用于给定的新bean实例. 该bean将用属性值填充. 返回的bean实例可能是原始实例的包装.
	 * <p>对于FactoryBean，将为FactoryBean实例和由FactoryBean创建的对象（从Spring 2.0开始）调用此回调.
	 * 后处理器可以通过FactoryBean检查的相应bean实例来决定是应用到{@code bean instanceof FactoryBean}还是创建的对象，还是两者都应用.
	 * <p>与所有其他{@code BeanPostProcessor}回调不同，
	 * 在由{@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation}方法触发短路之后，也会调用此回调.
	 * <p>默认实现按原样返回给定的{@code bean}.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.FactoryBean
	 */
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
