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

/**
 * 封装事件发布功能的接口.
 *
 * <p>用作{@link ApplicationContext}的超级接口.
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 1.1.1
 * @see ApplicationContext
 * @see ApplicationEventPublisherAware
 * @see org.springframework.context.ApplicationEvent
 * @see org.springframework.context.event.ApplicationEventMulticaster
 * @see org.springframework.context.event.EventPublicationInterceptor
 */
@FunctionalInterface
public interface ApplicationEventPublisher {

	/**
	 * 通知所有与此应用程序注册的匹配侦听器一个应用程序事件.
	 * 事件可以是框架事件（例如ContextRefreshedEvent）或特定于应用程序的事件.
	 * <p>这样的事件发布步骤实际上是到多播器的切换，并且根本不意味着同步/异步执行或立即执行.
	 * 鼓励事件侦听器尽可能地高效，它们单独使用异步执行来进行较长时间的运行并可能阻塞的操作.
	 * @param event the event to publish
	 * @see #publishEvent(Object)
	 * @see org.springframework.context.event.ContextRefreshedEvent
	 * @see org.springframework.context.event.ContextClosedEvent
	 */
	default void publishEvent(ApplicationEvent event) {
		publishEvent((Object) event);
	}

	/**
	 * 通知事件所有与此应用程序注册的匹配侦听器.
	 * <p>如果指定的{@code event}不是{@link ApplicationEvent}，则将其包装在{@link PayloadApplicationEvent}中.
	 * <p>这样的事件发布步骤实际上是到多播器的切换，并且根本不意味着同步/异步执行或什至立即执行.
	 * 鼓励事件侦听器尽可能地高效，它们单独使用异步执行来进行较长时间的运行并可能阻塞的操作.
	 * @param event the event to publish
	 * @since 4.2
	 * @see #publishEvent(ApplicationEvent)
	 * @see PayloadApplicationEvent
	 */
	void publishEvent(Object event);

}
