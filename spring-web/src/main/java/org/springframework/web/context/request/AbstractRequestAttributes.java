/*
 * Copyright 2002-2018 the original author or authors.
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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * RequestAttributes实现的抽象支持类，为特定于请求的销毁回调和更新访问的会话属性提供请求完成机制。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #requestCompleted()
 */
public abstract class AbstractRequestAttributes implements RequestAttributes {

	/** Map from attribute name String to destruction callback Runnable. */
	protected final Map<String, Runnable> requestDestructionCallbacks = new LinkedHashMap<>(8);

	private volatile boolean requestActive = true;


	/**
	 * 发出请求已完成的信号。
	 * <p>执行所有请求销毁回调，并更新在请求处理期间已访问的会话属性。
	 */
	public void requestCompleted() {
		executeRequestDestructionCallbacks();
		updateAccessedSessionAttributes();
		this.requestActive = false;
	}

	/**
	 * 确定原始请求是否仍处于活动状态。
	 * @see #requestCompleted()
	 */
	protected final boolean isRequestActive() {
		return this.requestActive;
	}

	/**
	 * 注册给定的回调以在请求完成后执行。
	 * @param name the name of the attribute to register the callback for
	 * @param callback the callback to be executed for destruction
	 */
	protected final void registerRequestDestructionCallback(String name, Runnable callback) {
		Assert.notNull(name, "Name must not be null");
		Assert.notNull(callback, "Callback must not be null");
		synchronized (this.requestDestructionCallbacks) {
			this.requestDestructionCallbacks.put(name, callback);
		}
	}

	/**
	 * 删除指定属性的请求销毁回调（如果有）。
	 * @param name the name of the attribute to remove the callback for
	 */
	protected final void removeRequestDestructionCallback(String name) {
		Assert.notNull(name, "Name must not be null");
		synchronized (this.requestDestructionCallbacks) {
			this.requestDestructionCallbacks.remove(name);
		}
	}

	/**
	 * 请求完成后，执行所有已注册执行的回调。
	 */
	private void executeRequestDestructionCallbacks() {
		synchronized (this.requestDestructionCallbacks) {
			for (Runnable runnable : this.requestDestructionCallbacks.values()) {
				runnable.run();
			}
			this.requestDestructionCallbacks.clear();
		}
	}

	/**
	 * 更新在请求处理期间已访问的所有会话属性，以将其可能更新的状态暴露给底层会话管理器。
	 */
	protected abstract void updateAccessedSessionAttributes();

}
