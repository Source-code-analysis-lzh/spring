/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * {@link BeanFactory}接口的扩展将由可以枚举其所有bean实例的bean工厂来实现，
 * 而不是按客户的要求按名称一一尝试进行bean查找. 预加载其所有bean定义的BeanFactory实现（例如，基于XML的工厂）可以实现此接口.
 *
 * <p>如果这是{@link HierarchicalBeanFactory}，则返回值将不考虑任何BeanFactory层次结构，而仅与当前工厂中定义的bean有关.
 * 也可以使用{@link BeanFactoryUtils}帮助器类来考虑祖先工厂中的bean.
 *
 * <p>该接口中的方法将仅遵守该工厂的bean定义. 他们将忽略通过其他方式（例如{@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * 的{@code registerSingleton}方法）注册的任何单例bean，但{@code getBeanNamesForType}和{@code getBeansOfType}除外，
 * 它们也将检查此类手动注册的单例. 当然，BeanFactory的{@code getBean}确实也允许透明访问此类特殊bean.
 * 但是，在典型情况下，无论如何，所有bean都将由外部bean定义来定义，因此大多数应用程序不必担心这种区别.
 *
 * <p>注意：除了{@code getBeanDefinitionCount}和{@code containsBeanDefinition}之外，
 * 此接口中的方法不适用于频繁调用. 实现执行可能很慢.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16 April 2001
 * @see HierarchicalBeanFactory
 * @see BeanFactoryUtils
 */
public interface ListableBeanFactory extends BeanFactory {

	/**
	 * 检查此bean工厂是否包含具有给定名称的bean定义.
	 * <p>不考虑该工厂可能参与的任何层次结构，并且忽略通过bean定义以外的其他方式注册的任何单例bean.
	 * @param beanName the name of the bean to look for
	 * @return if this bean factory contains a bean definition with the given name
	 * @see #containsBean
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * 返回工厂定义的bean个数.
	 * <p>不考虑该工厂可能参与的任何层次结构，并且忽略通过bean定义以外的其他方式注册的任何单例bean.
	 * @return the number of beans defined in the factory
	 */
	int getBeanDefinitionCount();

	/**
	 * 返回此工厂中定义的所有bean的名称.
	 * <p>不考虑该工厂可能参与的任何层次结构，并且忽略通过bean定义以外的其他方式注册的任何单例bean.
	 * @return the names of all beans defined in this factory,
	 * or an empty array if none defined
	 */
	String[] getBeanDefinitionNames();

	/**
	 * 根据Factory Beans的bean定义或{@code getObjectType}的值判断，返回与给定类型（包括子类）匹配的bean的名称.
	 * <p>注意：此方法仅自检顶级bean.它不检查可能也与指定类型匹配的嵌套bean.
	 * <p>是否考虑由FactoryBeans创建的对象，这意味着将初始化FactoryBeans.如果由FactoryBean创建的对象不匹配，则原始FactoryBean本身将与该类型匹配.
	 * <p>不考虑该工厂可能参与的任何层次结构.也可以使用BeanFactoryUtils的{@code beanNamesForTypeIncludingAncestors}在祖先工厂中包含bean.
	 * <p>注意：不要忽略通过bean定义以外的其它方式注册的单例bean.
	 * <p>此版本的{@code getBeanNamesForType}匹配所有类型的bean，无论是单例，原型还是FactoryBeans.
	 * 在大多数实现中，结果将与{@code getBeanNamesForType(type, true, true)}相同.
	 * <p>通过此方法返回的Bean名称应始终尽可能按后端配置中定义的顺序返回Bean名称.
	 * @param type the generically typed class or interface to match
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @since 4.2
	 * @see #isTypeMatch(String, ResolvableType)
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType)
	 */
	String[] getBeanNamesForType(ResolvableType type);

	/**
	 * 根据Factory Beans的bean定义或{@code getObjectType}的值判断，返回与给定类型（包括子类）匹配的bean的名称.
	 * <p>注意：此方法仅自检顶级bean.它不检查可能也与指定类型匹配的嵌套bean.
	 * <p>如果设置了"allowEagerInit"标志，则考虑了FactoryBeans创建的对象，这意味着将初始化FactoryBeans.
	 * 如果由FactoryBean创建的对象不匹配，则原始FactoryBean本身将与该类型匹配.如果未设置"allowEagerInit"，
	 * 则仅检查原始FactoryBean（不需要初始化每个FactoryBean）.
	 * <p>不考虑该工厂可能参与的任何层次结构.也可以使用BeanFactoryUtils的{@code beanNamesForTypeIncludingAncestors}在祖先工厂中包含bean.
	 * <p>注意：不要忽略通过bean定义以外的其他方式注册的单例bean.
	 * <p>通过此方法返回的Bean名称应始终尽可能按后端配置中定义的顺序返回Bean名称.
	 * @param type the generically typed class or interface to match
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @since 5.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType, boolean, boolean)
	 */
	String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * 根据Factory Beans的bean定义或{@code getObjectType}的值判断，返回与给定类型（包括子类）匹配的bean的名称.
	 * <p>注意：此方法仅自检顶级bean.它不检查可能也与指定类型匹配的嵌套bean.
	 * <p>是否考虑由FactoryBeans创建的对象，这意味着将初始化FactoryBeans.如果由FactoryBean创建的对象不匹配，则原始FactoryBean本身将与该类型匹配.
	 * <p>不考虑该工厂可能参与的任何层次结构.也可以使用BeanFactoryUtils的{@code beanNamesForTypeIncludingAncestors}在祖先工厂中包含bean.
	 * <p>注意：不要忽略通过bean定义以外的其他方式注册的单例bean.
	 * <p>此版本的{@code getBeanNamesForType}匹配所有类型的bean，无论是单例，原型还是FactoryBeans.
	 * 在大多数实现中，结果将与{@code getBeanNamesForType(type, true, true)}相同.
	 * <p>通过此方法返回的Bean名称应始终尽可能按后端配置中定义的顺序返回Bean名称.
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type);

	/**
	 * 根据Factory Beans的bean定义或{@code getObjectType}的值判断，返回与给定类型（包括子类）匹配的bean的名称.
	 * <p>注意：此方法仅自检顶级bean.它不检查可能也与指定类型匹配的嵌套bean.
	 * <p>如果设置了"allowEagerInit"标志，是否考虑了FactoryBeans创建的对象，这意味着将初始化FactoryBeans.
	 * 如果由FactoryBean创建的对象不匹配，则原始FactoryBean本身将与该类型匹配.
	 * 如果未设置"allowEagerInit"，则仅检查原始FactoryBean（不需要初始化每个FactoryBean）.
	 * <p>不考虑该工厂可能参与的任何层次结构.也可以使用BeanFactoryUtils的{@code beanNamesForTypeIncludingAncestors}在祖先工厂中包含bean.
	 * <p>注意：不要忽略通过bean定义以外的其他方式注册的单例bean.
	 * <p>通过此方法返回的Bean名称应始终尽可能按后端配置中定义的顺序返回Bean名称.
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * 根据Factory Beans的bean定义或{@code getObjectType}的值判断，返回与给定对象类型（包括子类）匹配的bean实例.
	 * <p>注意：此方法仅自检顶级bean.它不检查可能也与指定类型匹配的嵌套bean.
	 * <p>是否考虑由FactoryBeans创建的对象，这意味着将初始化FactoryBeans.如果由FactoryBean创建的对象不匹配，则原始FactoryBean本身将与该类型匹配.
	 * <p>不考虑该工厂可能参与的任何层次结构.也可以使用BeanFactoryUtils的{@code beansOfTypeIncludingAncestors}将Bean包括在祖先工厂中.
	 * <p>注意：不要忽略通过bean定义以外的其他方式注册的单例bean.
	 * <p>此版本的getBeansOfType匹配所有类型的bean，无论是单例，原型还是FactoryBeans.
	 * 在大多数实现中，结果将与{@code getBeansOfType(type, true, true)}相同.
	 * <p>此方法返回的Map应该始终尽可能在后端配置中按定义顺序返回bean名称和相应的bean实例.
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 1.1.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * {@code getObjectType} in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;

	/**
	 * 查找所有使用提供的{@link Annotation}类型进行注释的bean名称，但尚未创建相应的bean实例.
	 * <p>请注意，此方法考虑由FactoryBeans创建的对象，这意味着将初始化FactoryBeans以确定其对象类型.
	 * @param annotationType the type of annotation to look for
	 * (at class, interface or factory method level of the specified bean)
	 * @return the names of all matching beans
	 * @since 4.0
	 * @see #findAnnotationOnBean
	 */
	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

	/**
	 * 查找所有使用提供的{@link Annotation}类型进行注释的bean，返回包含相应bean实例的bean名称的Map.
	 * <p>请注意，此方法考虑由FactoryBeans创建的对象，这意味着将初始化FactoryBeans以确定其对象类型.
	 * @param annotationType the type of annotation to look for
	 * (at class, interface or factory method level of the specified bean)
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 3.0
	 * @see #findAnnotationOnBean
	 */
	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

	/**
	 * 在指定的bean上找到一个{@code annotationType}的{@link Annotation}，
	 * 遍历其接口和超类（如果在给定的类本身上找不到注释），并检查bean的factory方法（如果有）.
	 * @param beanName the name of the bean to look for annotations on
	 * @param annotationType the type of annotation to look for
	 * (at class, interface or factory method level of the specified bean)
	 * @return the annotation of the given type if found, or {@code null} otherwise
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 3.0
	 * @see #getBeanNamesForAnnotation
	 * @see #getBeansWithAnnotation
	 */
	@Nullable
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException;

}
