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

package org.springframework.context;

import java.io.Closeable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.lang.Nullable;

/**
 * SPI接口将由大多数（如果不是全部）应用程序上下文实现.
 * 除了{@link org.springframework.context.ApplicationContext}接口中的应用程序上下文客户端方法外，
 * 还提供了用于配置应用程序上下文的功能.
 *
 * <p>配置和生命周期方法都封装在这里，以避免对ApplicationContext客户端代码混乱.本方法仅应由启动和关闭代码使用.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @since 03.11.2003
 */
public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable {

	/**
	 * 在单个String值中，可以将任意数量的这些字符视为多个上下文配置路径之间的分隔符.
	 * @see org.springframework.context.support.AbstractXmlApplicationContext#setConfigLocation
	 * @see org.springframework.web.context.ContextLoader#CONFIG_LOCATION_PARAM
	 * @see org.springframework.web.servlet.FrameworkServlet#setContextConfigLocation
	 */
	String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

	/**
	 * 工厂中ConversionService bean的名称. 如果未提供任何内容，则适用默认转换规则.
	 * @since 3.0
	 * @see org.springframework.core.convert.ConversionService
	 */
	String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

	/**
	 * 工厂中LoadTimeWeaver Bean的名称. 如果提供了这样的bean，
	 * 则上下文将使用临时的ClassLoader进行类型匹配，以允许LoadTimeWeaver处理所有实际的bean类.
	 * @since 2.5
	 * @see org.springframework.instrument.classloading.LoadTimeWeaver
	 */
	String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";

	/**
	 * 工厂中{@link Environment}bean的名称.
	 * @since 3.1
	 */
	String ENVIRONMENT_BEAN_NAME = "environment";

	/**
	 * 工厂中系统属性Bean的名称.
	 * @see java.lang.System#getProperties()
	 */
	String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";

	/**
	 * 工厂中系统环境bean的名称.
	 * @see java.lang.System#getenv()
	 */
	String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";

	/**
	 * 关闭钩子线程的名称.
	 * @since 5.2
	 * @see #registerShutdownHook()
	 */
	String SHUTDOWN_HOOK_THREAD_NAME = "SpringContextShutdownHook";


	/**
	 * Set the unique id of this application context.
	 * @since 3.0
	 */
	void setId(String id);

	/**
	 * Set the parent of this application context.
	 * <p>Note that the parent shouldn't be changed: It should only be set outside
	 * a constructor if it isn't available when an object of this class is created,
	 * for example in case of WebApplicationContext setup.
	 * @param parent the parent context
	 * @see org.springframework.web.context.ConfigurableWebApplicationContext
	 */
	void setParent(@Nullable ApplicationContext parent);

	/**
	 * Set the {@code Environment} for this application context.
	 * @param environment the new environment
	 * @since 3.1
	 */
	void setEnvironment(ConfigurableEnvironment environment);

	/**
	 * Return the {@code Environment} for this application context in configurable
	 * form, allowing for further customization.
	 * @since 3.1
	 */
	@Override
	ConfigurableEnvironment getEnvironment();

	/**
	 * Add a new BeanFactoryPostProcessor that will get applied to the internal
	 * bean factory of this application context on refresh, before any of the
	 * bean definitions get evaluated. To be invoked during context configuration.
	 * @param postProcessor the factory processor to register
	 */
	void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

	/**
	 * Add a new ApplicationListener that will be notified on context events
	 * such as context refresh and context shutdown.
	 * <p>Note that any ApplicationListener registered here will be applied
	 * on refresh if the context is not active yet, or on the fly with the
	 * current event multicaster in case of a context that is already active.
	 * @param listener the ApplicationListener to register
	 * @see org.springframework.context.event.ContextRefreshedEvent
	 * @see org.springframework.context.event.ContextClosedEvent
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * 在此应用程序上下文中注册给定的协议解析器，从而允许处理其它资源协议.
	 * <p>任何此类解析程序都将在此上下文的标准解析规则之前调用. 因此，它也可以覆盖任何默认规则.
	 * @since 4.3
	 */
	void addProtocolResolver(ProtocolResolver resolver);

	/**
	 * 加载或刷新配置的持久表示形式，可能是XML文件，属性文件或关系数据库模式.
	 * <p>因为这是一种启动方法，所以如果失败，它应该销毁已创建的单例，以避免资源悬空.
	 * 换句话说，在调用该方法之后，应实例化所有单例或根本没有单例被实例化.
	 * @throws BeansException if the bean factory could not be initialized
	 * @throws IllegalStateException if already initialized and multiple refresh
	 * attempts are not supported
	 */
	void refresh() throws BeansException, IllegalStateException;

	/**
	 * 向JVM运行时注册一个shutdown钩子，除非JVM当时已经关闭，否则在JVM关闭时关闭该上下文.
	 * <p>可以多次调用此方法. 每个上下文实例仅注册一个关闭挂钩（最大数量）.
	 * <p>从Spring Framework 5.2开始，shutdown hook线程的{@linkplain Thread#getName() name}
	 * 应为{@link #SHUTDOWN_HOOK_THREAD_NAME}.
	 * @see java.lang.Runtime#addShutdownHook
	 * @see #close()
	 */
	void registerShutdownHook();

	/**
	 * 关闭此应用程序上下文，释放实现可能持有的所有资源和锁. 这包括销毁所有缓存的单例bean.
	 * <p>注意：不会在父上下文上调用{@code close}； 父级上下文具有自己的独立生命周期.
	 * <p>可以多次调用此方法，而不会产生副作用：在已关闭的上下文上的后续{@code close}调用将被忽略.
	 */
	@Override
	void close();

	/**
	 * 确定此应用程序上下文是否处于活动状态，即，是否至少刷新一次并且尚未关闭.
	 * @return whether the context is still active
	 * @see #refresh()
	 * @see #close()
	 * @see #getBeanFactory()
	 */
	boolean isActive();

	/**
	 * Return the internal bean factory of this application context.
	 * Can be used to access specific functionality of the underlying factory.
	 * <p>Note: Do not use this to post-process the bean factory; singletons
	 * will already have been instantiated before. Use a BeanFactoryPostProcessor
	 * to intercept the BeanFactory setup process before beans get touched.
	 * <p>Generally, this internal factory will only be accessible while the context
	 * is active, that is, in-between {@link #refresh()} and {@link #close()}.
	 * The {@link #isActive()} flag can be used to check whether the context
	 * is in an appropriate state.
	 * 返回此应用程序上下文的内部bean工厂. 可用于访问底层工厂的特定功能.
	 * <p>注意：请勿使用此方法对bean工厂进行后处理.
	 * 单例之前已经被实例化. 使用BeanFactoryPostProcessor来拦截Bean之前的BeanFactory设置过程.
	 * <p>通常，仅在上下文处于活动状态时（即在{@link #refresh()}和{@link #close()}之间）才能访问此内部工厂.
	 * {@link #isActive()}标志可用于检查上下文是否处于适当的状态.
	 * @return the underlying bean factory
	 * @throws IllegalStateException if the context does not hold an internal
	 * bean factory (usually if {@link #refresh()} hasn't been called yet or
	 * if {@link #close()} has already been called)
	 * @see #isActive()
	 * @see #refresh()
	 * @see #close()
	 * @see #addBeanFactoryPostProcessor
	 */
	ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

}
