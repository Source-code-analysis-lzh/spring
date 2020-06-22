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

package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * 由可以管理许多{@link ApplicationListener}对象并向其发布事件的对象实现的接口.
 *
 * <p>{@link org.springframework.context.ApplicationEventPublisher}
 * （通常是Spring {@link org.springframework.context.ApplicationContext}）
 * 可以将{@code ApplicationEventMulticaster}用作实际发布事件的委托.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @see ApplicationListener
 */
public interface ApplicationEventMulticaster {

	/**
	 * 添加一个侦听器以通知所有事件.
	 * @param listener the listener to add
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * 添加一个侦听器bean，以通知所有事件.
	 * @param listenerBeanName the name of the listener bean to add
	 */
	void addApplicationListenerBean(String listenerBeanName);

	/**
	 * 从通知列表中删除一个侦听器.
	 * @param listener the listener to remove
	 */
	void removeApplicationListener(ApplicationListener<?> listener);

	/**
	 * 从通知列表中删除一个侦听器bean.
	 * @param listenerBeanName the name of the listener bean to remove
	 */
	void removeApplicationListenerBean(String listenerBeanName);

	/**
	 * 删除在此多播器上注册的所有侦听器.
	 * <p>删除调用后，多播程序将不会对事件通知执行任何操作，直到注册了新的侦听器为止.
	 */
	void removeAllListeners();

	/**
	 * 将给定的应用程序事件多播到适当的侦听器.
	 * <p>如果可能，请考虑使用{@link #multicastEvent(ApplicationEvent, ResolvableType)}，
	 * 因为它可以为基于泛型的事件提供更好的支持.
	 * @param event the event to multicast
	 */
	void multicastEvent(ApplicationEvent event);

	/**
	 * 将给定的应用程序事件多播到适当的侦听器.
	 * <p>如果{@code eventType}为{@code null}，则基于事件实例构建默认类型.
	 * @param event the event to multicast
	 * @param eventType the type of event (can be {@code null})
	 * @since 4.2
	 */
	void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType);

}
