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

package org.springframework.context.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/**
 * 一个简单的支持线程{@link Scope}实现。
 *
 * <p>注意：在普通上下文中，默认情况下不注册此线程范围。 相反，您需要通过
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory#registerScope}或通过
 * {@link org.springframework.beans.factory.config.CustomScopeConfigurer} bean将其显式赋值给设置中的作用域键。
 *
 * <p>{@code SimpleThreadScope}不会清除与其关联的任何对象。 因此，通常最好在Web环境中使用请求绑定范围的实现，
 * 例如{@code org.springframework.web.context.request.RequestScope}，以实现整个范围属性的生命周期（包括可靠的销毁）。
 *
 * <p>有关支持销毁回调的基于线程的Scope的实现，请参阅
 * <a href="https://www.springbyexample.org/examples/custom-thread-scope-module.html">Spring by Example</a>。
 *
 * <p>Thanks to Eugene Kuleshov for submitting the original prototype for a thread scope!
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.web.context.request.RequestScope
 */
public class SimpleThreadScope implements Scope {

	private static final Log logger = LogFactory.getLog(SimpleThreadScope.class);

	private final ThreadLocal<Map<String, Object>> threadScope =
			new NamedThreadLocal<Map<String, Object>>("SimpleThreadScope") {
				@Override
				protected Map<String, Object> initialValue() {
					return new HashMap<>();
				}
			};


	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Map<String, Object> scope = this.threadScope.get();
		return scope.computeIfAbsent(name, k -> objectFactory.getObject());
	}

	@Override
	@Nullable
	public Object remove(String name) {
		Map<String, Object> scope = this.threadScope.get();
		return scope.remove(name);
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		logger.warn("SimpleThreadScope does not support destruction callbacks. " +
				"Consider using RequestScope in a web environment.");
	}

	@Override
	@Nullable
	public Object resolveContextualObject(String key) {
		return null;
	}

	@Override
	public String getConversationId() {
		return Thread.currentThread().getName();
	}

}
