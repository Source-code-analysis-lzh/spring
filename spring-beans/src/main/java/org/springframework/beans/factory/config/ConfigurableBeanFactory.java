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

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * 大多数bean工厂都将实现配置接口. 除了{@link org.springframework.beans.factory.BeanFactory}
 * 接口中的bean工厂客户端方法之外，还提供了配置bean工厂的功能.
 *
 * <p>此bean工厂接口并不打算在常规应用程序代码中使用：坚持{@link org.springframework.beans.factory.BeanFactory}
 * 或{@link org.springframework.beans.factory.ListableBeanFactory}以满足典型需求.
 * 此扩展接口仅用于允许在框架内部进行即插即用，并允许对bean工厂配置方法的特殊访问.
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.ListableBeanFactory
 * @see ConfigurableListableBeanFactory
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

	/**
	 * Scope identifier for the standard singleton scope: {@value}.
	 * <p>Custom scopes can be added via {@code registerScope}.
	 * @see #registerScope
	 */
	String SCOPE_SINGLETON = "singleton";

	/**
	 * Scope identifier for the standard prototype scope: {@value}.
	 * <p>Custom scopes can be added via {@code registerScope}.
	 * @see #registerScope
	 */
	String SCOPE_PROTOTYPE = "prototype";


	/**
	 * Set the parent of this bean factory.
	 * <p>Note that the parent cannot be changed: It should only be set outside
	 * a constructor if it isn't available at the time of factory instantiation.
	 * @param parentBeanFactory the parent BeanFactory
	 * @throws IllegalStateException if this factory is already associated with
	 * a parent BeanFactory
	 * @see #getParentBeanFactory()
	 */
	void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

	/**
	 * Set the class loader to use for loading bean classes.
	 * Default is the thread context class loader.
	 * <p>Note that this class loader will only apply to bean definitions
	 * that do not carry a resolved bean class yet. This is the case as of
	 * Spring 2.0 by default: Bean definitions only carry bean class names,
	 * to be resolved once the factory processes the bean definition.
	 * @param beanClassLoader the class loader to use,
	 * or {@code null} to suggest the default class loader
	 */
	void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);

	/**
	 * Return this factory's class loader for loading bean classes
	 * (only {@code null} if even the system ClassLoader isn't accessible).
	 * @see org.springframework.util.ClassUtils#forName(String, ClassLoader)
	 */
	@Nullable
	ClassLoader getBeanClassLoader();

	/**
	 * Specify a temporary ClassLoader to use for type matching purposes.
	 * Default is none, simply using the standard bean ClassLoader.
	 * <p>A temporary ClassLoader is usually just specified if
	 * <i>load-time weaving</i> is involved, to make sure that actual bean
	 * classes are loaded as lazily as possible. The temporary loader is
	 * then removed once the BeanFactory completes its bootstrap phase.
	 * @since 2.5
	 */
	void setTempClassLoader(@Nullable ClassLoader tempClassLoader);

	/**
	 * Return the temporary ClassLoader to use for type matching purposes,
	 * if any.
	 * @since 2.5
	 */
	@Nullable
	ClassLoader getTempClassLoader();

	/**
	 * Set whether to cache bean metadata such as given bean definitions
	 * (in merged fashion) and resolved bean classes. Default is on.
	 * <p>Turn this flag off to enable hot-refreshing of bean definition objects
	 * and in particular bean classes. If this flag is off, any creation of a bean
	 * instance will re-query the bean class loader for newly resolved classes.
	 */
	void setCacheBeanMetadata(boolean cacheBeanMetadata);

	/**
	 * Return whether to cache bean metadata such as given bean definitions
	 * (in merged fashion) and resolved bean classes.
	 */
	boolean isCacheBeanMetadata();

	/**
	 * Specify the resolution strategy for expressions in bean definition values.
	 * <p>There is no expression support active in a BeanFactory by default.
	 * An ApplicationContext will typically set a standard expression strategy
	 * here, supporting "#{...}" expressions in a Unified EL compatible style.
	 * @since 3.0
	 */
	void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver);

	/**
	 * 返回bean定义值中表达式的解析策略。
	 * @since 3.0
	 */
	@Nullable
	BeanExpressionResolver getBeanExpressionResolver();

	/**
	 * 指定一个Spring 3.0 ConversionService来转换属性值，以替代JavaBeans PropertyEditor.
	 * @since 3.0
	 */
	void setConversionService(@Nullable ConversionService conversionService);

	/**
	 * Return the associated ConversionService, if any.
	 * @since 3.0
	 */
	@Nullable
	ConversionService getConversionService();

	/**
	 * 添加一个PropertyEditorRegistrar以应用于所有bean创建过程.
	 * <p>这样的注册商创建新的PropertyEditor实例，并在给定的注册表中注册它们，这样对于每次创建bean的尝试都是新的.
	 * 这样可以避免在自定义编辑器上进行同步；
	 * 因此，通常最好使用此方法来代替{@link #registerCustomEditor}.
	 * @param registrar the PropertyEditorRegistrar to register
	 */
	void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

	/**
	 * 为给定类型的所有属性注册给定的定制属性编辑器. 在工厂配置期间调用.
	 * <p>注意，此方法将注册一个共享的自定义编辑器实例. 为了线程安全，将同步对该实例的访问.
	 * 通常最好使用{@link #addPropertyEditorRegistrar}代替此方法，以避免需要在定制编辑器上进行同步.
	 * @param requiredType type of the property
	 * @param propertyEditorClass the {@link PropertyEditor} class to register
	 */
	void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

	/**
	 * Initialize the given PropertyEditorRegistry with the custom editors
	 * that have been registered with this BeanFactory.
	 * @param registry the PropertyEditorRegistry to initialize
	 */
	void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

	/**
	 * 设置一个自定义类型转换器，此BeanFactory应使用该转换器来转换Bean属性值，构造函数参数值等.
	 * <p>这将覆盖默认的PropertyEditor机制，因此使任何自定义编辑器或自定义编辑器注册器都不相关.
	 * @since 2.5
	 * @see #addPropertyEditorRegistrar
	 * @see #registerCustomEditor
	 */
	void setTypeConverter(TypeConverter typeConverter);

	/**
	 * 获取此BeanFactory使用的类型转换器. 每个调用可能是一个新实例，因为TypeConverters通常不是线程安全的.
	 * <p>如果默认的PropertyEditor机制处于活动状态，则返回的TypeConverter将知道所有已注册的自定义编辑器.
	 * @since 2.5
	 */
	TypeConverter getTypeConverter();

	/**
	 * Add a String resolver for embedded values such as annotation attributes.
	 * @param valueResolver the String resolver to apply to embedded values
	 * @since 3.0
	 */
	void addEmbeddedValueResolver(StringValueResolver valueResolver);

	/**
	 * Determine whether an embedded value resolver has been registered with this
	 * bean factory, to be applied through {@link #resolveEmbeddedValue(String)}.
	 * @since 4.3
	 */
	boolean hasEmbeddedValueResolver();

	/**
	 * Resolve the given embedded value, e.g. an annotation attribute.
	 * @param value the value to resolve
	 * @return the resolved value (may be the original value as-is)
	 * @since 3.0
	 */
	@Nullable
	String resolveEmbeddedValue(String value);

	/**
	 * Add a new BeanPostProcessor that will get applied to beans created
	 * by this factory. To be invoked during factory configuration.
	 * <p>Note: Post-processors submitted here will be applied in the order of
	 * registration; any ordering semantics expressed through implementing the
	 * {@link org.springframework.core.Ordered} interface will be ignored. Note
	 * that autodetected post-processors (e.g. as beans in an ApplicationContext)
	 * will always be applied after programmatically registered ones.
	 * @param beanPostProcessor the post-processor to register
	 */
	void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

	/**
	 * Return the current number of registered BeanPostProcessors, if any.
	 */
	int getBeanPostProcessorCount();

	/**
	 * Register the given scope, backed by the given Scope implementation.
	 * @param scopeName the scope identifier
	 * @param scope the backing Scope implementation
	 */
	void registerScope(String scopeName, Scope scope);

	/**
	 * Return the names of all currently registered scopes.
	 * <p>This will only return the names of explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * @return the array of scope names, or an empty array if none
	 * @see #registerScope
	 */
	String[] getRegisteredScopeNames();

	/**
	 * Return the Scope implementation for the given scope name, if any.
	 * <p>This will only return explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * @param scopeName the name of the scope
	 * @return the registered Scope implementation, or {@code null} if none
	 * @see #registerScope
	 */
	@Nullable
	Scope getRegisteredScope(String scopeName);

	/**
	 * Provides a security access control context relevant to this factory.
	 * @return the applicable AccessControlContext (never {@code null})
	 * @since 3.0
	 */
	AccessControlContext getAccessControlContext();

	/**
	 * Copy all relevant configuration from the given other factory.
	 * <p>Should include all standard configuration settings as well as
	 * BeanPostProcessors, Scopes, and factory-specific internal settings.
	 * Should not include any metadata of actual bean definitions,
	 * such as BeanDefinition objects and bean name aliases.
	 * @param otherFactory the other BeanFactory to copy from
	 */
	void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

	/**
	 * Given a bean name, create an alias. We typically use this method to
	 * support names that are illegal within XML ids (used for bean names).
	 * <p>Typically invoked during factory configuration, but can also be
	 * used for runtime registration of aliases. Therefore, a factory
	 * implementation should synchronize alias access.
	 * @param beanName the canonical name of the target bean
	 * @param alias the alias to be registered for the bean
	 * @throws BeanDefinitionStoreException if the alias is already in use
	 */
	void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

	/**
	 * Resolve all alias target names and aliases registered in this
	 * factory, applying the given StringValueResolver to them.
	 * <p>The value resolver may for example resolve placeholders
	 * in target bean names and even in alias names.
	 * @param valueResolver the StringValueResolver to apply
	 * @since 2.5
	 */
	void resolveAliases(StringValueResolver valueResolver);

	/**
	 * Return a merged BeanDefinition for the given bean name,
	 * merging a child bean definition with its parent if necessary.
	 * Considers bean definitions in ancestor factories as well.
	 * @param beanName the name of the bean to retrieve the merged definition for
	 * @return a (potentially merged) BeanDefinition for the given bean
	 * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
	 * @since 2.5
	 */
	BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Determine whether the bean with the given name is a FactoryBean.
	 * @param name the name of the bean to check
	 * @return whether the bean is a FactoryBean
	 * ({@code false} means the bean exists but is not a FactoryBean)
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.5
	 */
	boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Explicitly control the current in-creation status of the specified bean.
	 * For container-internal use only.
	 * @param beanName the name of the bean
	 * @param inCreation whether the bean is currently in creation
	 * @since 3.1
	 */
	void setCurrentlyInCreation(String beanName, boolean inCreation);

	/**
	 * Determine whether the specified bean is currently in creation.
	 * @param beanName the name of the bean
	 * @return whether the bean is currently in creation
	 * @since 2.5
	 */
	boolean isCurrentlyInCreation(String beanName);

	/**
	 * Register a dependent bean for the given bean,
	 * to be destroyed before the given bean is destroyed.
	 * @param beanName the name of the bean
	 * @param dependentBeanName the name of the dependent bean
	 * @since 2.5
	 */
	void registerDependentBean(String beanName, String dependentBeanName);

	/**
	 * Return the names of all beans which depend on the specified bean, if any.
	 * @param beanName the name of the bean
	 * @return the array of dependent bean names, or an empty array if none
	 * @since 2.5
	 */
	String[] getDependentBeans(String beanName);

	/**
	 * Return the names of all beans that the specified bean depends on, if any.
	 * @param beanName the name of the bean
	 * @return the array of names of beans which the bean depends on,
	 * or an empty array if none
	 * @since 2.5
	 */
	String[] getDependenciesForBean(String beanName);

	/**
	 * Destroy the given bean instance (usually a prototype instance
	 * obtained from this factory) according to its bean definition.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * @param beanName the name of the bean definition
	 * @param beanInstance the bean instance to destroy
	 */
	void destroyBean(String beanName, Object beanInstance);

	/**
	 * Destroy the specified scoped bean in the current target scope, if any.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * @param beanName the name of the scoped bean
	 */
	void destroyScopedBean(String beanName);

	/**
	 * Destroy all singleton beans in this factory, including inner beans that have
	 * been registered as disposable. To be called on shutdown of a factory.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 */
	void destroySingletons();

}
