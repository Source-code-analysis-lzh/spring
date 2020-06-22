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

package org.springframework.aop.framework;

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/**
 * 包含用于获取有关当前AOP调用信息的静态方法的类。
 *
 * <p>如果将AOP框架配置为暴露当前代理（不是默认代理），则{@code currentProxy()}方法可用。 
 * 它返回正在使用的AOP代理对象。 目标对象或advice可以使用它进行advised的调用，
 * 就像在EJB中可以使用{@code getEJBObject()}一样。 他们还可以使用它来查找advice配置。
 *
 * <p>Spring的AOP框架默认情况下不公开代理，因为这样做会降低性能。
 *
 * <p>该类中的功能可能由需要在调用时访问资源的目标对象使用。 
 * 但是，如果有合理的选择，则不应使用此方法，因为它会使应用程序代码取决于AOP尤其是Spring AOP框架下的用法。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13.03.2003
 */
public final class AopContext {

	/**
	 * ThreadLocal holder for AOP proxy associated with this thread.
	 * Will contain {@code null} unless the "exposeProxy" property on
	 * the controlling proxy configuration has been set to "true".
	 * @see ProxyConfig#setExposeProxy
	 */
	private static final ThreadLocal<Object> currentProxy = new NamedThreadLocal<>("Current AOP proxy");


	private AopContext() {
	}


	/**
	 * Try to return the current AOP proxy. This method is usable only if the
	 * calling method has been invoked via AOP, and the AOP framework has been set
	 * to expose proxies. Otherwise, this method will throw an IllegalStateException.
	 * @return the current AOP proxy (never returns {@code null})
	 * @throws IllegalStateException if the proxy cannot be found, because the
	 * method was invoked outside an AOP invocation context, or because the
	 * AOP framework has not been configured to expose the proxy
	 */
	public static Object currentProxy() throws IllegalStateException {
		Object proxy = currentProxy.get();
		if (proxy == null) {
			throw new IllegalStateException(
					"Cannot find current proxy: Set 'exposeProxy' property on Advised to 'true' to make it available, and " +
							"ensure that AopContext.currentProxy() is invoked in the same thread as the AOP invocation context.");
		}
		return proxy;
	}

	/**
	 * Make the given proxy available via the {@code currentProxy()} method.
	 * <p>Note that the caller should be careful to keep the old value as appropriate.
	 * @param proxy the proxy to expose (or {@code null} to reset it)
	 * @return the old proxy, which may be {@code null} if none was bound
	 * @see #currentProxy()
	 */
	@Nullable
	static Object setCurrentProxy(@Nullable Object proxy) {
		Object old = currentProxy.get();
		if (proxy != null) {
			currentProxy.set(proxy);
		}
		else {
			currentProxy.remove();
		}
		return old;
	}

}
