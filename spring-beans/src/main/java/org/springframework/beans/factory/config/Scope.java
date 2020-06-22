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

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;

/**
 * {@link ConfigurableBeanFactory}使用的策略接口，代表用于容纳Bean实例的目标范围。
 * 这允许使用自定义的其它范围扩展BeanFactory的标准范围
 * {@link ConfigurableBeanFactory#SCOPE_SINGLETON "singleton"}
 * 和{@link ConfigurableBeanFactory#SCOPE_PROTOTYPE "prototype"}，
 * 并使用{@link ConfigurableBeanFactory#registerScope(String, Scope) specific key}注册。
 *
 * <p>诸如{@link org.springframework.web.context.WebApplicationContext}
 * 之类的{@link org.springframework.context.ApplicationContext}
 * 实现可以注册特定于其环境的其它标准范围，例如 基于此Scope SPI的
 * {@link org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST "request"}
 * 和{@link org.springframework.web.context.WebApplicationContext#SCOPE_SESSION "session"}。
 *
 * <p>即使其主要用途是用于Web环境中的扩展范围，此SPI也是完全通用的：
 * 它提供了从任何底层存储机制（例如HTTP会话或自定义对话机制）获取和放置对象的能力。 
 * 传递给此类的{@code get}和{@code remove}方法的名称将标识当前作用域中的目标对象。
 *
 * <p>{@code Scope}实现应该是线程安全的。 
 * 如果需要，一个Scope实例可以同时与多个Bean工厂一起使用（除非它明确希望知道包含的BeanFactory），
 * 并且任何数量的线程可以从任意数量的工厂同时访问{@code Scope}。
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 * @see ConfigurableBeanFactory#registerScope
 * @see CustomScopeConfigurer
 * @see org.springframework.aop.scope.ScopedProxyFactoryBean
 * @see org.springframework.web.context.request.RequestScope
 * @see org.springframework.web.context.request.SessionScope
 */
public interface Scope {

	/**
	 * 从底层范围返回具有给定名称的对象，如果在底层存储机制中找不到该对象，则
	 * {@link org.springframework.beans.factory.ObjectFactory#getObject() 创建该对象}。
	 * <p>这是Scope的中心操作，并且是绝对必需的唯一操作。
	 * @param name the name of the object to retrieve
	 * @param objectFactory the {@link ObjectFactory} to use to create the scoped
	 * object if it is not present in the underlying storage mechanism
	 * @return the desired object (never {@code null})
	 * @throws IllegalStateException if the underlying scope is not currently active
	 */
	Object get(String name, ObjectFactory<?> objectFactory);

	/**
	 * 从底层范围中删除具有给定{@code name}的对象。
	 * <p>如果未找到对象，则返回{@code null}；否则返回移除的{@code Object}。
	 * <p>请注意，实现还应删除指定对象的已注册销毁回调（如果有）。 但是，在这种情况下，
	 * 它不需要执行已注册的销毁回调，因为调用方将主动销毁该对象（如果适用）。
	 * <p>注意：这是可选操作。 如果实现不支持显式删除对象，则它们可能引发
	 * {@link UnsupportedOperationException}。
	 * @param name the name of the object to remove
	 * @return the removed object, or {@code null} if no object was present
	 * @throws IllegalStateException if the underlying scope is not currently active
	 * @see #registerDestructionCallback
	 */
	@Nullable
	Object remove(String name);

	/**
	 * 注册一个回调，以在销毁范围内的指定对象时执行（或销毁整个范围，如果该不是销毁单个对象，
	 * 而只是终止整个范围对象）。
	 * <p>注意：这是可选操作。 仅对具有实际销毁配置的范围内的bean
	 * （DisposableBean，destroy-method，DestructionAwareBeanPostProcessor）调用此方法。 
	 * 实现应尽力在适当的时间执行给定的回调。 如果底层运行时环境完全不支持此类回调，则必须忽略该回调，
	 * 并记录相应的警告。
	 * <p>请注意，“销毁”是指将对象自动销毁为作用域自身生命周期的一部分，
	 * 而不是指已被应用程序明确删除的单个作用域对象。 如果通过此门面的{@link #remove(String)}
	 * 方法删除了一个范围对象，则假定已删除的对象将被重用或手动销毁，所有注册的销毁回调也应被删除。
	 * @param name the name of the object to execute the destruction callback for
	 * @param callback the destruction callback to be executed.
	 * Note that the passed-in Runnable will never throw an exception,
	 * so it can safely be executed without an enclosing try-catch block.
	 * Furthermore, the Runnable will usually be serializable, provided
	 * that its target object is serializable as well.
	 * @throws IllegalStateException if the underlying scope is not currently active
	 * @see org.springframework.beans.factory.DisposableBean
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getDestroyMethodName()
	 * @see DestructionAwareBeanPostProcessor
	 */
	void registerDestructionCallback(String name, Runnable callback);

	/**
	 * 解析给定键的上下文对象（如果有）。 例如。 键值为"request"的HttpServletRequest对象。
	 * @param key the contextual key
	 * @return the corresponding object, or {@code null} if none found
	 * @throws IllegalStateException if the underlying scope is not currently active
	 */
	@Nullable
	Object resolveContextualObject(String key);

	/**
	 * 返回当前底层范围的对话ID（如果有）。
	 * <p>The exact meaning of the conversation ID depends on the underlying
	 * storage mechanism. In the case of session-scoped objects, the
	 * conversation ID would typically be equal to (or derived from) the
	 * {@link javax.servlet.http.HttpSession#getId() 会话ID}; in the
	 * case of a custom conversation that sits within the overall session,
	 * the specific ID for the current conversation would be appropriate.
	 * <p>对话ID的确切含义取决于底层存储机制。 对于会话范围的对象，会话ID通常等于
	 * {@link javax.servlet.http.HttpSession#getId() 会话ID}（或从会话ID派生）。
	 * 如果自定义对话位于整个会话中，则当前对话的特定ID是合适的。
	 * <p><b>Note: This is an optional operation.</b> It is perfectly valid to
	 * return {@code null} in an implementation of this method if the
	 * underlying storage mechanism has no obvious candidate for such an ID.
	 * @return the conversation ID, or {@code null} if there is no
	 * conversation ID for the current scope
	 * @throws IllegalStateException if the underlying scope is not currently active
	 */
	@Nullable
	String getConversationId();

}
