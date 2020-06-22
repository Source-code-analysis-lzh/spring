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

import org.springframework.lang.Nullable;

/**
 * 由{@link BeanFactory}中使用的对象实现的接口，这些对象本身就是单个对象的工厂.
 * 如果bean实现此接口，则它将用作对象公开的工厂，而不是直接用作将自身公开的bean实例.
 *
 * <p>注意：实现此接口的bean不能用作普通bean. FactoryBean以bean样式定义，
 * 但是为bean引用公开的对象({@link #getObject()})始终是它创建的对象.
 *
 * <p>FactoryBeans可以支持单例和原型，并且可以按需延迟创建对象，也可以在启动时立即创建对象.
 * {@link SmartFactoryBean}接口允许公开更细粒度的行为元数据.
 *
 * <p>此接口在框架本身中被大量使用，例如用于AOP {@link org.springframework.aop.framework.ProxyFactoryBean}
 * 或{@link org.springframework.jndi.JndiObjectFactoryBean}. 它也可以用于自定义组件. 但是，这仅在基础结构代码中很常见.
 *
 * <p>{@code FactoryBean}是程序性合同. 实现不应依赖于注释驱动的注入或其他反射功能.
 * {@link #getObjectType()} {@link #getObject()}调用可能会在启动过程的早期调用，
 * 甚至在任何后处理器设置之前也可能调用. 如果需要访问其他bean，请实现{@link BeanFactoryAware}并以编程方式获取它们.
 *
 * <p>容器仅负责管理FactoryBean实例的生命周期，而不负责管理FactoryBean创建的对象的生命周期.
 * 因此，不会自动调用暴露的bean对象上的destroy方法（例如{@link java.io.Closeable#close()}.
 * 相反，FactoryBean应该实现{@link DisposableBean}并将任何此类{@link java.io.Closeable#close()}调用委托给底层对象.
 *
 * <p>Finally, FactoryBean objects participate in the containing BeanFactory's
 * synchronization of bean creation. There is usually no need for internal
 * synchronization other than for purposes of lazy initialization within the
 * FactoryBean itself (or the like).
 * <p>最后，FactoryBean对象参与包含BeanFactory的Bean创建同步.
 * 除了出于FactoryBean自身（或类似方式）内部的延迟初始化的目的之外，通常不需要内部同步.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 08.03.2003
 * @param <T> the bean type
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @see org.springframework.jndi.JndiObjectFactoryBean
 */
public interface FactoryBean<T> {

	/**
	 * The name of an attribute that can be
	 * {@link org.springframework.core.AttributeAccessor#setAttribute set} on a
	 * {@link org.springframework.beans.factory.config.BeanDefinition} so that
	 * factory beans can signal their object type when it can't be deduced from
	 * the factory bean class.
	 * 可以在{@link org.springframework.beans.factory.config.BeanDefinition}上设置的属性的名称，
	 * 以便当无法从工厂bean类推导出该属性时，工厂bean可以发出信号通知其对象类型.
	 * @since 5.2
	 */
	String OBJECT_TYPE_ATTRIBUTE = "factoryBeanObjectType";


	/**
	 * 返回此工厂管理的对象的实例（可能是共享的或独立的）。
	 * <p>与{@link BeanFactory}一样，这允许同时支持Singleton和Prototype设计模式。
	 * <p>如果在调用时尚未完全初始化此FactoryBean（例如，因为它包含在循环引用中），
	 * 则抛出相应的{@link FactoryBeanNotInitializedException}。
	 * <p>从Spring 2.0开始，FactoryBeans可以返回{@code null}。 
	 * 工厂会将其视为正常值使用； 在这种情况下，它将不再抛出FactoryBeanNotInitializedException。 
	 * 鼓励FactoryBean实现现在酌情自行抛出FactoryBeanNotInitializedException。
	 * @return an instance of the bean (can be {@code null})
	 * @throws Exception in case of creation errors
	 * @see FactoryBeanNotInitializedException
	 */
	@Nullable
	T getObject() throws Exception;

	/**
	 * 返回此FactoryBean创建的对象的类型；如果事先未知，则返回{@code null}。
	 * <p>这样一来，无需实例化对象即可检查特定类型的bean，例如在自动装配时。
	 * <p>对于创建单例对象的实现，此方法应尽量避免创建单例。 它应该提前估计类型。 
	 * 对于原型，建议在此处返回有意义的类型。
	 * <p>可以在完全初始化此FactoryBean之前调用此方法。 它一定不能依赖初始化过程中创建的状态。 
	 * 当然，如果可用，它仍然可以使用这种状态。
	 * <p>注意：自动装配将仅忽略在此处返回{@code null}的FactoryBeans。 
	 * 因此，强烈建议使用FactoryBean的当前状态正确实现此方法。
	 * @return the type of object that this FactoryBean creates,
	 * or {@code null} if not known at the time of the call
	 * @see ListableBeanFactory#getBeansOfType
	 */
	@Nullable
	Class<?> getObjectType();

	/**
	 * 该工厂管理的对象是单例吗？ 也就是说，{@link #getObject()}总是返回相同的对象（可以缓存的引用）吗？
	 * <p>注意：如果FactoryBean指示保留单例对象，
	 * 则从其{@code getObject()}返回的对象可能会被拥有的BeanFactory缓存。 
	 * 因此，除非FactoryBean始终公开相同的引用，否则不要返回{@code true}。
	 * <p>FactoryBean本身的单例状态通常由拥有的BeanFactory提供； 通常，它必须在那里定义为单例。
	 * <p>注意：此方法返回{@code false}不一定表示返回的对象是独立的实例。 
	 * 扩展{@link SmartFactoryBean}接口的实现可以通过其{@link SmartFactoryBean#isPrototype()}
	 * 方法显式指示独立的实例。 如果{@code isSingleton()}实现返回{@code false}，
	 * 则仅假设未实现此扩展接口的Plain {@link FactoryBean}实现始终返回独立实例。
	 * <p>默认实现返回{@code true}，因为{@code FactoryBean}通常管理一个单例实例。
	 * @return whether the exposed object is a singleton
	 * @see #getObject()
	 * @see SmartFactoryBean#isPrototype()
	 */
	default boolean isSingleton() {
		return true;
	}

}
