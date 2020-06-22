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

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.lang.Nullable;

/**
 * {@link BeanPostProcessor}的子接口，它添加实例化之前的回调，以及在实例化之后但在设置显式属性或发生自动装配之前的回调.
 *
 * <p>通常用于抑制特定目标Bean的默认实例化，例如创建具有特殊TargetSource的代理（池目标，延迟初始化目标等），
 * 或实现其它注入策略，例如字段注入.
 *
 * <p>注意：此接口是专用接口，主要供框架内部使用.
 * 建议尽可能实现普通的{@link BeanPostProcessor}接口，
 * 或从{@link InstantiationAwareBeanPostProcessorAdapter}派生，以免对该接口直接进行扩展.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 1.2
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#setCustomTargetSourceCreators
 * @see org.springframework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

	/**
	 * 在实例化目标bean之前应用此BeanPostProcessor. 返回的bean对象可以是代替目标bean使用的代理，从而有效地抑制了目标bean的默认实例化.
	 * <p>如果此方法返回一个非null对象，则Bean创建过程将被短路.
	 * 唯一还需要执行的是自已配置的{@link BeanPostProcessor BeanPostProcessors}的{@link #postProcessAfterInitialization}回调.
	 * <p>此回调将应用于具有其bean类的bean定义以及工厂方法定义，在这种情况下，返回的bean类型将在此处传递.
	 * <p>后处理器可以实现扩展的{@link SmartInstantiationAwareBeanPostProcessor}接口，以便预测它们将在此处返回的Bean对象的类型.
	 * <p>默认实现返回{@code null}.
	 * @param beanClass the class of the bean to be instantiated
	 * @param beanName the name of the bean
	 * @return the bean object to expose instead of a default instance of the target bean,
	 * or {@code null} to proceed with default instantiation
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see #postProcessAfterInstantiation
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getBeanClass()
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getFactoryMethodName()
	 */
	@Nullable
	default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	/**
	 * 通过构造函数或工厂方法在实例化bean之后但在发生Spring属性填充（通过显式属性或自动装配）之前执行操作.
	 * <p>这是在Spring的自动装配开始之前对给定的bean实例执行自定义字段注入的理想回调.
	 * <p>默认实现返回{@code true}.
	 * @param bean the bean instance created, with properties not having been set yet
	 * @param beanName the name of the bean
	 * @return {@code true} if properties should be set on the bean; {@code false}
	 * if property population should be skipped. Normal implementations should return {@code true}.
	 * Returning {@code false} will also prevent any subsequent InstantiationAwareBeanPostProcessor
	 * instances being invoked on this bean instance.
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see #postProcessBeforeInstantiation
	 */
	default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}

	/**
	 * 在工厂将它们应用于给定bean之前，对给定的属性值进行后处理，而无需使用属性描述符.
	 * 如果实现提供自定义的{@link #postProcessPropertyValues}实现，则本方法实现应返回{@code null}（默认值），否则提供{@code pvs}.
	 * 在此接口的将来版本中（将删除{@link #postProcessPropertyValues}），默认实现将直接按原样返回给定的{@code pvs}.
	 * @param pvs the property values that the factory is about to apply (never {@code null})
	 * @param bean the bean instance created, but whose properties have not yet been set
	 * @param beanName the name of the bean
	 * @return the actual property values to apply to the given bean (can be the passed-in
	 * PropertyValues instance), or {@code null} which proceeds with the existing properties
	 * but specifically continues with a call to {@link #postProcessPropertyValues}
	 * (requiring initialized {@code PropertyDescriptor}s for the current bean class)
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @since 5.1
	 * @see #postProcessPropertyValues
	 */
	@Nullable
	default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
			throws BeansException {

		return null;
	}

	/**
	 * 在工厂将给定属性值应用于给定bean之前，对它们进行后处理. 允许检查是否满足所有依赖关系，例如基于bean属性设置器上的"Required"注释.
	 * <p>还允许替换要应用的属性值，通常是通过基于原始PropertyValues创建新的MutablePropertyValues实例，添加或删除特定值来实现.
	 * <p>默认实现按原样返回给定的{@code pvs}.
	 * @param pvs the property values that the factory is about to apply (never {@code null})
	 * @param pds the relevant property descriptors for the target bean (with ignored
	 * dependency types - which the factory handles specifically - already filtered out)
	 * @param bean the bean instance created, but whose properties have not yet been set
	 * @param beanName the name of the bean
	 * @return the actual property values to apply to the given bean (can be the passed-in
	 * PropertyValues instance), or {@code null} to skip property population
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see #postProcessProperties
	 * @see org.springframework.beans.MutablePropertyValues
	 * @deprecated as of 5.1, in favor of {@link #postProcessProperties(PropertyValues, Object, String)}
	 */
	@Deprecated
	@Nullable
	default PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

		return pvs;
	}

}
