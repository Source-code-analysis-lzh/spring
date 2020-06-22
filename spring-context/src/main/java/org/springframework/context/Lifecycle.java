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

package org.springframework.context;

/**
 * 定义启动/停止生命周期控制方法的通用接口.
 * 典型的用例是控制异步处理.
 * <b>注意：此接口并不意味着特定的自动启动语义. 考虑为此目的实现{@link SmartLifecycle}.</b>
 *
 * <p>可以由组件（通常是在Spring上下文中定义的Spring bean）
 * 和容器（通常是Spring {@link ApplicationContext}本身）实现.
 * 容器会将开始/停止信号传播到每个容器中应用的所有组件，例如 在运行时停止/重新启动的情况.
 *
 * <p>可以用于直接调用或通过JMX进行管理操作.
 * 在后一种情况下，通常将使用{@link org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler}
 * 定义{@link org.springframework.jmx.export.MBeanExporter}，从而将活动控制的组件的可见性限制为Lifecycle接口.
 *
 * <p>请注意，当前的{@code Lifecycle}接口仅在顶级Singleton Bean上受支持.
 * 在任何其他组件上，{@code Lifecycle}接口将保持未被检测到并因此被忽略.
 * 另外，请注意，扩展的{@link SmartLifecycle}接口提供了与应用程序上下文的启动和关闭阶段的复杂集成.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see SmartLifecycle
 * @see ConfigurableApplicationContext
 * @see org.springframework.jms.listener.AbstractMessageListenerContainer
 * @see org.springframework.scheduling.quartz.SchedulerFactoryBean
 */
public interface Lifecycle {

	/**
	 * 启动此组件.
	 * <p>如果组件已经在运行，则不应抛出异常.
	 * <p>对于容器，这会将启动信号传播到所有适用的组件.
	 * @see SmartLifecycle#isAutoStartup()
	 */
	void start();

	/**
	 * 通常以同步方式停止此组件，以便在返回此方法后完全停止该组件.
	 * 当需要异步停止行为时，请考虑实现{@link SmartLifecycle}及其{@code stop(Runnable)}变体.
	 * <p>请注意，此停止通知不能保证在销毁之前出现：
	 * 在常规关闭时，{@code Lifecycle} Bean将在传播常规销毁回调之前首先收到一个停止通知；
	 * 否则，将不会收到该通知. 但是，在上下文生存期内的热刷新或中止的刷新尝试下，
	 * 将调用给定bean的destroy方法，而无需事先考虑停止信号.
	 * <p>如果组件未运行（尚未启动），则不应引发异常.
	 * <p>对于容器，这会将停止信号传播到所有适用的组件.
	 * @see SmartLifecycle#stop(Runnable)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	void stop();

	/**
	 * 检查此组件当前是否正在运行.
	 * <p>对于容器，仅当所有适用的组件当前正在运行时，它才会返回{@code true}.
	 * @return whether the component is currently running
	 */
	boolean isRunning();

}
