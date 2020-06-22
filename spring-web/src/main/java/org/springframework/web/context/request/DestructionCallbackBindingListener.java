/*
 * Copyright 2002-2015 the original author or authors.
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

import java.io.Serializable;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * 实现Servlet HttpSessionBindingListener接口的适配器，包装了会话销毁回调。
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see RequestAttributes#registerDestructionCallback
 * @see ServletRequestAttributes#registerSessionDestructionCallback
 */
@SuppressWarnings("serial")
public class DestructionCallbackBindingListener implements HttpSessionBindingListener, Serializable {

	private final Runnable destructionCallback;


	/**
	 * Create a new DestructionCallbackBindingListener for the given callback.
	 * @param destructionCallback the Runnable to execute when this listener
	 * object gets unbound from the session
	 */
	public DestructionCallbackBindingListener(Runnable destructionCallback) {
		this.destructionCallback = destructionCallback;
	}


	@Override
	public void valueBound(HttpSessionBindingEvent event) {
	}

	//valueUnbound的触发条件是以下三种情况：
	//1. 执行session.invalidate()时。
	//2. session超时，自动销毁时。
	//3. 执行session.setAttribute(“onlineUserListener”, “其他对象”);或session.removeAttribute(“onlineUserListener”);将listener从session中删除时。
	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		this.destructionCallback.run();
	}

}
