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

import java.util.EventListener;

/**
 * 由应用程序事件侦听器实现的接口.
 *
 * <p>基于Observer设计模式的标准{@code java.util.EventListener}接口.
 *
 * <p>从Spring 3.0开始，{@code ApplicationListener}可以泛型地声明其感兴趣的事件类型.
 * 向Spring {@code ApplicationContext}注册后，将相应地过滤事件，并且仅针对匹配事件对象调用侦听器.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @param <E> the specific {@code ApplicationEvent} subclass to listen to
 * @see org.springframework.context.ApplicationEvent
 * @see org.springframework.context.event.ApplicationEventMulticaster
 * @see org.springframework.context.event.EventListener
 */
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

	/**
	 * 处理应用程序事件.
	 * @param event the event to respond to
	 */
	void onApplicationEvent(E event);

}
