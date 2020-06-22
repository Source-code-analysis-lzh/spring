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

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * 用于访问Spring bean容器的根接口.
 *
 * <p>这是bean容器的最基本客户端视图. 诸如{@link ListableBeanFactory}
 * 和{@link org.springframework.beans.factory.config.ConfigurableBeanFactory}之类的其他接口可用于特定目的.
 *
 * <p>该接口由包含多个bean定义的对象实现，每个定义均由字符串名称唯一标识.
 * 根据bean的定义，工厂将返回所包含对象的独立实例（Prototype设计模式），
 * 或者返回单个共享实例（Singleton设计模式的替代方案，其中实例是作用域中的单例）.
 * 将返回哪种类型的实例取决于bean工厂的配置：API是相同的.
 * 从Spring 2.0开始，根据具体的应用程序上下文（例如，Web环境中的"request"和"session"作用域），可以使用更多作用域.
 *
 * <p>这种方式的重点是BeanFactory是应用程序组件的中央注册表，
 * 并集中了应用程序组件的配置（例如，不再需要单个对象读取属性文件）.
 * 有关此方式的好处的讨论，请参见“一对一J2EE专家设计和开发”的第4章和第11章.
 *
 * <p>请注意，通常最好依靠依赖注入（“推送”配置）通过设置器或构造函数配置应用程序对象，
 * 而不是使用任何形式的“拉”配置（例如BeanFactory查找）.
 * Spring的Dependency Injection功能是使用此BeanFactory接口及其子接口实现的.
 *
 * <p>通常，BeanFactory将加载存储在配置源（例如XML文档）中的bean定义，并使用{@code org.springframework.beans}包来配置bean.
 * 但是，实现可以根据需要直接在Java代码中直接返回它创建的Java对象.
 * 定义的存储方式没有任何限制：LDAP，RDBMS，XML，属性文件等.鼓励实现以支持Bean之间的引用（Dependency Injection）.
 *
 * <p>与{@link ListableBeanFactory}中的方法相比，此接口中的所有操作还将检查父工厂
 * （如果这是{@link HierarchicalBeanFactory}）. 如果在此工厂实例中未找到bean，则将询问直接的父工厂.
 * 该工厂实例中的Bean应该覆盖任何父工厂中的同名bean.
 *
 * <p>Bean工厂实现应尽可能支持标准Bean生命周期接口. 整个初始化方法及其标准顺序为：
 * <ol>
 * <li>BeanNameAware's {@code setBeanName}
 * <li>BeanClassLoaderAware's {@code setBeanClassLoader}
 * <li>BeanFactoryAware's {@code setBeanFactory}
 * <li>EnvironmentAware's {@code setEnvironment}
 * <li>EmbeddedValueResolverAware's {@code setEmbeddedValueResolver}
 * <li>ResourceLoaderAware's {@code setResourceLoader}
 * (仅在应用程序上下文中运行时适用)
 * <li>ApplicationEventPublisherAware's {@code setApplicationEventPublisher}
 * (only applicable when running in an application context)
 * <li>MessageSourceAware's {@code setMessageSource}
 * (only applicable when running in an application context)
 * <li>ApplicationContextAware's {@code setApplicationContext}
 * (only applicable when running in an application context)
 * <li>ServletContextAware's {@code setServletContext}
 * (only applicable when running in a web application context)
 * <li>{@code postProcessBeforeInitialization} methods of BeanPostProcessors
 * <li>InitializingBean's {@code afterPropertiesSet}
 * <li>a custom init-method definition
 * <li>{@code postProcessAfterInitialization} methods of BeanPostProcessors
 * </ol>
 *
 * <p>在关闭bean工厂时，以下生命周期方法适用：
 * <ol>
 * <li>{@code postProcessBeforeDestruction} methods of DestructionAwareBeanPostProcessors
 * <li>DisposableBean's {@code destroy}
 * <li>a custom destroy-method definition
 * </ol>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

	/**
	 * 用于引用{@link FactoryBean}实例，并将其与FactoryBean创建的bean区分开.
	 * 例如，如果名为{@code myJndiObject}的bean是FactoryBean，
	 * 则获取{@code &myJndiObject}将返回工厂，而不是工厂返回的实例.
	 */
	String FACTORY_BEAN_PREFIX = "&";


	/**
	 * 返回一个实例，该实例可以是指定bean的共享或独立的。
	 * <p>该方法允许使用Spring BeanFactory替代Singleton或Prototype设计模式。 
	 * 对于Singleton bean，调用者可以保留对返回对象的引用。
	 * <p>将别名转换回相应的规范bean名称。
	 * <p>在该工厂实例中找不到该bean将询问父工厂是否存在。
	 * @param name the name of the bean to retrieve
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no bean with the specified name
	 * @throws BeansException if the bean could not be obtained
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * 返回一个实例，该实例可以是指定bean的共享或独立的。
	 * <p>行为与{@link #getBean(String)}相同，但是如果bean不是需要的类型，
	 * 则通过抛出BeanNotOfRequiredTypeException来提供类型安全性的度量。 
	 * 这意味着如{@link #getBean(String)}那样，不能正确地强制转换结果时抛出ClassCastException。
	 * <p>将别名转换回相应的规范bean名称。
	 * <p>在该工厂实例中找不到该bean将询问父工厂是否存在。
	 * @param name the name of the bean to retrieve
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * 返回一个实例，该实例可以是指定bean的共享或独立的。
	 * <p>允许指定显式构造函数参数/工厂方法参数，覆盖Bean定义中指定的默认参数（如果有）。
	 * @param name the name of the bean to retrieve
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 2.5
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * 返回与给定对象类型唯一匹配的bean实例（如果有）。
	 * <p>此方法进入{@link ListableBeanFactory}按类型查找范围，
	 * 但也可以根据给定类型的名称转换为常规的按名称查找。 
	 * 对于跨bean集的更广泛的检索操作，请使用{@link ListableBeanFactory}和/或{@link BeanFactoryUtils}。
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/**
	 * 返回一个实例，该实例可以是指定bean的共享或独立的。
	 * <p>允许指定显式构造函数参数/工厂方法参数，覆盖Bean定义中指定的默认参数（如果有）。
	 * <p>此方法进入{@link ListableBeanFactory}按类型查找范围，
	 * 但也可以根据给定类型的名称转换为常规的按名称查找。 
	 * 对于跨bean集的更广泛的检索操作，请使用{@link ListableBeanFactory}和/或{@link BeanFactoryUtils}。
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 4.1
	 */
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	/**
	 * 返回指定bean的提供程序，以允许按需延迟检索的实例，包括可用性和唯一性选项。
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see #getBeanProvider(ResolvableType)
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	/**
	 * 返回指定bean的提供程序，以允许按需延迟检索实例，包括可用性和唯一性选项。
	 * @param requiredType type the bean must match; can be a generic type declaration.
	 * Note that collection types are not supported here, in contrast to reflective
	 * injection points. For programmatically retrieving a list of beans matching a
	 * specific type, specify the actual bean type as an argument here and subsequently
	 * use {@link ObjectProvider#orderedStream()} or its lazy streaming/iteration options.
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see ObjectProvider#iterator()
	 * @see ObjectProvider#stream()
	 * @see ObjectProvider#orderedStream()
	 */
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

	/**
	 * 该bean工厂是否包含具有给定名称的bean定义或外部注册的单例实例？
	 * <p>如果给定名称是别名，它将被转换回相应的规范bean名称。
	 * <p>如果该工厂是分层工厂，如果在该工厂实例中找不到该bean，则将询问任何父工厂。
	 * <p>如果找到与给定名称匹配的bean定义或单例实例，则无论命名的bean定义在范围上是具体的还是抽象的，
	 * 懒惰的或渴望的，此方法都将返回{@code true}。 
	 * 因此，请注意，此方法的真实返回值不一定表示{@link #getBean}将能够获取具有相同名称的实例。
	 * @param name the name of the bean to query
	 * @return whether a bean with the given name is present
	 */
	boolean containsBean(String name);

	/**
	 * 该bean是共享单例吗？ 也就是说，{@link #getBean}总是返回相同的实例吗？
	 * <p>注意：此方法返回{@code false}时不能清楚地表明独立实例。 
	 * 它指示非单实例，也可以对应于作用域Bean。 使用{@link #isPrototype}操作显式检查独立实例。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return whether this bean corresponds to a singleton instance
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @see #getBean
	 * @see #isPrototype
	 */
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 这个bean是原型吗？ 也就是说，{@link #getBean}总是返回独立的实例吗？
	 * <p>注意：此方法返回{@code false}不能清楚地指明为单例对象。 
	 * 它指示非独立实例，该实例也可能对应于作用域Bean。 
	 * 使用{@link #isSingleton}操作显式检查共享的单例实例。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return whether this bean will always deliver independent instances
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.3
	 * @see #getBean
	 * @see #isSingleton
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 检查具有给定名称的Bean是否与指定的类型匹配。 
	 * 更具体地说，检查对给定名称的{@link #getBean}调用是否将返回指定目标类型的对象。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code ResolvableType})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 4.2
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 检查具有给定名称的Bean是否与指定的类型匹配。 
	 * 更具体地说，检查对给定名称的{@link #getBean}调用是否将返回指定目标类型的对象。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code Class})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.1
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 确定具有给定名称的bean的类型。 更具体地说，确定给定名称的{@link #getBean}返回的对象的类型。
	 * <p>对于{@link FactoryBean}，返回由{@link FactoryBean#getObjectType()}公开的FactoryBean创建的对象的类型。 
	 * 这可能导致先前未初始化的FactoryBean的初始化（请参见{@link #getType(String, boolean)}）。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return the type of the bean, or {@code null} if not determinable
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 1.1.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 确定具有给定名称的bean的类型。 更具体地说，确定给定名称的{@link #getBean}返回的对象的类型。
	 * <p>对于{@link FactoryBean}，返回由{@link FactoryBean#getObjectType()}公开的FactoryBean创建的对象的类型。 
	 * 如果没有早期类型信息可用，则取决于{@code allowFactoryBeanInit}标志，这可能导致先前未初始化的FactoryBean的初始化。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param allowFactoryBeanInit whether a {@code FactoryBean} may get initialized
	 * just for the purpose of determining its object type
	 * @return the type of the bean, or {@code null} if not determinable
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 5.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	@Nullable
	Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException;

	/**
	 * 返回给定bean名称的别名（如果有）。
	 * <p>All of those aliases point to the same bean when used in a {@link #getBean} call.
	 * <p>If the given name is an alias, the corresponding original bean name
	 * and other aliases (if any) will be returned, with the original bean name
	 * being the first element in the array.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the bean name to check for aliases
	 * @return the aliases, or an empty array if none
	 * @see #getBean
	 */
	String[] getAliases(String name);

}
