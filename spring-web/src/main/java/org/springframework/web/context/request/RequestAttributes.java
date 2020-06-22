/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

/**
 * 用于访问与请求关联的属性对象的抽象。 支持使用"global session"的可选概念
 * 访问请求范围的属性以及会话范围的属性。
 *
 * <p>可以为任何类型的请求/会话机制实现，尤其是对于Servlet请求。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see ServletRequestAttributes
 */
public interface RequestAttributes {

	/**
	 * Constant that indicates request scope.
	 */
	int SCOPE_REQUEST = 0;

	/**
	 * Constant that indicates session scope.
	 * <p>This preferably refers to a locally isolated session, if such
	 * a distinction is available.
	 * Else, it simply refers to the common session.
	 * <p>如果有这样的区别，则最好指的是本地隔离的会话。 否则，它仅指普通会议。
	 */
	int SCOPE_SESSION = 1;


	/**
	 * Name of the standard reference to the request object: "request".
	 * @see #resolveReference
	 */
	String REFERENCE_REQUEST = "request";

	/**
	 * Name of the standard reference to the session object: "session".
	 * @see #resolveReference
	 */
	String REFERENCE_SESSION = "session";


	/**
	 * Return the value for the scoped attribute of the given name, if any.
	 * @param name the name of the attribute
	 * @param scope the scope identifier
	 * @return the current attribute value, or {@code null} if not found
	 */
	@Nullable
	Object getAttribute(String name, int scope);

	/**
	 * Set the value for the scoped attribute of the given name,
	 * replacing an existing value (if any).
	 * @param name the name of the attribute
	 * @param scope the scope identifier
	 * @param value the value for the attribute
	 */
	void setAttribute(String name, Object value, int scope);

	/**
	 * Remove the scoped attribute of the given name, if it exists.
	 * <p>Note that an implementation should also remove a registered destruction
	 * callback for the specified attribute, if any. It does, however, <i>not</i>
	 * need to <i>execute</i> a registered destruction callback in this case,
	 * since the object will be destroyed by the caller (if appropriate).
	 * <p>请注意，实现还应删除指定属性的已注册销毁回调（如果有）。 
	 * 但是，在这种情况下，它不需要执行已注册的销毁回调，因为调用方将销毁该对象（如果适用）。
	 * @param name the name of the attribute
	 * @param scope the scope identifier
	 */
	void removeAttribute(String name, int scope);

	/**
	 * Retrieve the names of all attributes in the scope.
	 * @param scope the scope identifier
	 * @return the attribute names as String array
	 */
	String[] getAttributeNames(int scope);

	/**
	 * 注册一个回调，以在销毁给定范围内的指定属性时执行该回调。
	 * <p>实现应尽力在适当的时间执行回调：即分别在请求完成或会话终止时。 如果底层运行时环境不支持此类回调，
	 * 则必须忽略该回调并记录相应的警告。
	 * <p>Note that 'destruction' usually corresponds to destruction of the
	 * entire scope, not to the individual attribute having been explicitly
	 * removed by the application. If an attribute gets removed via this
	 * facade's {@link #removeAttribute(String, int)} method, any registered
	 * destruction callback should be disabled as well, assuming that the
	 * removed object will be reused or manually destroyed.
	 * <p>请注意，“销毁”通常对应于整个范围的销毁，而不是应用程序已明确删除的单个属性。 
	 * 如果通过{@link #removeAttribute(String, int)}方法删除了属性，任何已注册的销毁回调也应被禁用
	 * 则假定已删除的对象将被重用或手动销毁。
	 * <p><b>NOTE:</b> Callback objects should generally be serializable if
	 * they are being registered for a session scope. Otherwise the callback
	 * (or even the entire session) might not survive web app restarts.
	 * <p>注意：如果正在为会话作用域注册回调对象，则它们通常应可序列化。 
	 * 否则，回调（甚至整个会话）可能无法在Web应用重新启动后继续存在。
	 * @param name the name of the attribute to register the callback for
	 * @param callback the destruction callback to be executed
	 * @param scope the scope identifier
	 */
	void registerDestructionCallback(String name, Runnable callback, int scope);

	/**
	 * Resolve the contextual reference for the given key, if any.
	 * <p>At a minimum: the HttpServletRequest reference for key "request", and
	 * the HttpSession reference for key "session".
	 * @param key the contextual key
	 * @return the corresponding object, or {@code null} if none found
	 */
	@Nullable
	Object resolveReference(String key);

	/**
	 * Return an id for the current underlying session.
	 * @return the session id as String (never {@code null})
	 */
	String getSessionId();

	/**
	 * 公开底层会话的最佳可用互斥量：即，用于底层会话的同步对象。
	 * @return the session mutex to use (never {@code null})
	 */
	Object getSessionMutex();

}
