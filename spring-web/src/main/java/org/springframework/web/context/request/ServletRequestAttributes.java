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

package org.springframework.web.context.request;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * {@link RequestAttributes}接口的基于Servlet的实现。
 *
 * <p>从servlet请求和HTTP会话范围访问对象，而“会话”和“全局会话”之间没有区别。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see javax.servlet.ServletRequest#getAttribute
 * @see javax.servlet.http.HttpSession#getAttribute
 */
public class ServletRequestAttributes extends AbstractRequestAttributes {

	/**
	 * Constant identifying the {@link String} prefixed to the name of a
	 * destruction callback when it is stored in a {@link HttpSession}.
	 */
	public static final String DESTRUCTION_CALLBACK_NAME_PREFIX =
			ServletRequestAttributes.class.getName() + ".DESTRUCTION_CALLBACK.";

	protected static final Set<Class<?>> immutableValueTypes = new HashSet<>(16);

	static {
		immutableValueTypes.addAll(NumberUtils.STANDARD_NUMBER_TYPES);
		immutableValueTypes.add(Boolean.class);
		immutableValueTypes.add(Character.class);
		immutableValueTypes.add(String.class);
	}


	private final HttpServletRequest request;

	@Nullable
	private HttpServletResponse response;

	@Nullable
	private volatile HttpSession session;

	private final Map<String, Object> sessionAttributesToUpdate = new ConcurrentHashMap<>(1);


	/**
	 * 为给定请求创建一个新的ServletRequestAttributes实例。
	 * @param request current HTTP request
	 */
	public ServletRequestAttributes(HttpServletRequest request) {
		Assert.notNull(request, "Request must not be null");
		this.request = request;
	}

	/**
	 * Create a new ServletRequestAttributes instance for the given request.
	 * @param request current HTTP request
	 * @param response current HTTP response (for optional exposure)
	 */
	public ServletRequestAttributes(HttpServletRequest request, @Nullable HttpServletResponse response) {
		this(request);
		this.response = response;
	}


	/**
	 * 公开我们包装的本机{@link HttpServletRequest}。
	 */
	public final HttpServletRequest getRequest() {
		return this.request;
	}

	/**
	 * 公开包装的本机{@link HttpServletResponse}（如果有）。
	 */
	@Nullable
	public final HttpServletResponse getResponse() {
		return this.response;
	}

	/**
	 * 公开我们包装的{@link HttpSession}。
	 * @param allowCreate 如果尚不存在，是否允许创建新会话
	 */
	@Nullable
	protected final HttpSession getSession(boolean allowCreate) {
		if (isRequestActive()) {
			HttpSession session = this.request.getSession(allowCreate);
			this.session = session;
			return session;
		}
		else {
			// Access through stored session reference, if any...
			HttpSession session = this.session;
			if (session == null) {
				if (allowCreate) {
					throw new IllegalStateException(
							"No session found and request already completed - cannot create new session!");
				}
				else { // 通过存储的会话引用进行访问
					session = this.request.getSession(false);
					this.session = session;
				}
			}
			return session;
		}
	}

	private HttpSession obtainSession() {
		HttpSession session = getSession(true);
		Assert.state(session != null, "No HttpSession");
		return session;
	}


	@Override
	public Object getAttribute(String name, int scope) {
		if (scope == SCOPE_REQUEST) {
			if (!isRequestActive()) {
				throw new IllegalStateException(
						"Cannot ask for request attribute - request is not active anymore!");
			}
			return this.request.getAttribute(name);
		}
		else {
			HttpSession session = getSession(false);
			if (session != null) {
				try {
					Object value = session.getAttribute(name);
					if (value != null) {
						// 只要从会话中获取值，就表示该值可能被修改，则需要更新到会话存储中
						this.sessionAttributesToUpdate.put(name, value);
					}
					return value;
				}
				catch (IllegalStateException ex) {
					// Session invalidated - shouldn't usually happen.
				}
			}
			return null;
		}
	}

	@Override
	public void setAttribute(String name, Object value, int scope) {
		if (scope == SCOPE_REQUEST) {
			if (!isRequestActive()) {
				throw new IllegalStateException(
						"Cannot set request attribute - request is not active anymore!");
			}
			this.request.setAttribute(name, value);
		}
		else {
			HttpSession session = obtainSession();
			// 直接更新后就不需要自动更新了
			this.sessionAttributesToUpdate.remove(name);
			session.setAttribute(name, value);
		}
	}

	@Override
	public void removeAttribute(String name, int scope) {
		if (scope == SCOPE_REQUEST) {
			if (isRequestActive()) {
				// 移除属性还需要移除对应销毁回调
				removeRequestDestructionCallback(name);
				this.request.removeAttribute(name);
			}
		}
		else {
			HttpSession session = getSession(false);
			if (session != null) {
				// 移除的属性就不需要自动更新了
				this.sessionAttributesToUpdate.remove(name);
				try {
					// 移除该属性的回调
					session.removeAttribute(DESTRUCTION_CALLBACK_NAME_PREFIX + name);
					// 移除该属性的值
					session.removeAttribute(name);
				}
				catch (IllegalStateException ex) {
					// Session invalidated - shouldn't usually happen.
				}
			}
		}
	}

	@Override
	public String[] getAttributeNames(int scope) {
		if (scope == SCOPE_REQUEST) {
			if (!isRequestActive()) {
				throw new IllegalStateException(
						"Cannot ask for request attributes - request is not active anymore!");
			}
			return StringUtils.toStringArray(this.request.getAttributeNames());
		}
		else {
			HttpSession session = getSession(false);
			if (session != null) {
				try {
					return StringUtils.toStringArray(session.getAttributeNames());
				}
				catch (IllegalStateException ex) {
					// Session invalidated - shouldn't usually happen.
				}
			}
			return new String[0];
		}
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback, int scope) {
		if (scope == SCOPE_REQUEST) {
			registerRequestDestructionCallback(name, callback);
		}
		else {
			registerSessionDestructionCallback(name, callback);
		}
	}

	@Override
	public Object resolveReference(String key) {
		if (REFERENCE_REQUEST.equals(key)) {
			return this.request;
		}
		else if (REFERENCE_SESSION.equals(key)) {
			return getSession(true);
		}
		else {
			return null;
		}
	}

	@Override
	public String getSessionId() {
		return obtainSession().getId();
	}

	@Override
	public Object getSessionMutex() {
		return WebUtils.getSessionMutex(obtainSession());
	}


	/**
	 * Update all accessed session attributes through {@code session.setAttribute}
	 * calls, explicitly indicating to the container that they might have been modified.
	 */
	@Override
	protected void updateAccessedSessionAttributes() {
		if (!this.sessionAttributesToUpdate.isEmpty()) {
			// Update all affected session attributes.
			HttpSession session = getSession(false);
			if (session != null) {
				try {
					for (Map.Entry<String, Object> entry : this.sessionAttributesToUpdate.entrySet()) {
						String name = entry.getKey();
						Object newValue = entry.getValue();
						Object oldValue = session.getAttribute(name);
						if (oldValue == newValue && !isImmutableSessionAttribute(name, newValue)) {
							session.setAttribute(name, newValue);
						}
					}
				}
				catch (IllegalStateException ex) {
					// Session invalidated - shouldn't usually happen.
				}
			}
			this.sessionAttributesToUpdate.clear();
		}
	}

	/**
	 * 确定是否将给定值视为不可变的会话属性，即不能通过{@code session.setAttribute}对其进行重新设置，
	 * 因为其值无法在内部有意义地更改。
	 * <p>默认实现对{@code String},{@code Character}, {@code Boolean}和{@code Number}
	 * 值返回{@code true}。
	 * @param name the name of the attribute
	 * @param value the corresponding value to check
	 * @return {@code true} if the value is to be considered as immutable for the
	 * purposes of session attribute management; {@code false} otherwise
	 * @see #updateAccessedSessionAttributes()
	 */
	protected boolean isImmutableSessionAttribute(String name, @Nullable Object value) {
		return (value == null || immutableValueTypes.contains(value.getClass()));
	}

	/**
	 * 将给定的回调注册为要在会话终止后执行。
	 * <p>注意：回调对象应可序列化，以便在Web应用程序重启后继续运行。
	 * @param name the name of the attribute to register the callback for
	 * @param callback the callback to be executed for destruction
	 */
	protected void registerSessionDestructionCallback(String name, Runnable callback) {
		HttpSession session = obtainSession();
		session.setAttribute(DESTRUCTION_CALLBACK_NAME_PREFIX + name,
				new DestructionCallbackBindingListener(callback));
	}


	@Override
	public String toString() {
		return this.request.toString();
	}

}
