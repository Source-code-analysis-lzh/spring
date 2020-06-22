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
 * {@link Lifecycle}接口的扩展，用于那些需要按特定顺序刷新和/或关闭{@code ApplicationContext}时启动的对象.
 *
 * <p>{@link #isAutoStartup()}返回值指示是否应在刷新上下文时启动此对象.
 * 接受回调的{@link #stop(Runnable)}方法对于具有异步关闭过程的对象很有用.
 * 此接口的任何实现都必须在关闭完成时调用回调的{@code run()}方法，
 * 以避免在整个{@code ApplicationContext}关闭中不必要的延迟.
 *
 * <p>此接口扩展了{@link Phased}，并且{@link #getPhase()}方法的返回值
 * 指示此Lifecycle组件应在其中启动和停止的阶段. 启动过程以最低的阶段值开始，
 * 以最高的阶段值结束（{@code Integer.MIN_VALUE}是可能的最低值，
 * 而{@code Integer.MAX_VALUE}是可能的最高值）. 关闭过程将应用相反的顺序.
 * 具有相同值的任何组件将在同一阶段内任意顺序.
 *
 * <p>例如：如果组件B依赖于已经启动的组件A，则组件A的相位值应低于组件B.在关闭过程中，组件B将在组件A之前停止.
 *
 * <p>任何显式的"depends-on"关系都将优先于阶段顺序，
 * 以便从属bean始终在其依赖之后开始，并始终在其依赖之前停止.
 *
 * <p>如果上下文中没有实现{@code SmartLifecycle}的所有{@code Lifecycle}组件都将被视为具有相位值0.
 * 这将允许{@code SmartLifecycle}组件在这些{@code Lifecycle}组件之前启动
 * （如果{@code SmartLifecycle}组件的相位值为负）或{@code SmartLifecycle}组件
 * 如果{@code SmartLifecycle}组件的相位值为正，则可能会在这些{@code Lifecycle}组件之后启动.
 *
 * <p>请注意，由于{@code SmartLifecycle}支持自动启动，
 * 因此无论如何在启动应用程序上下文时通常都会初始化{@code SmartLifecycle} bean实例.
 * 结果，bean定义lazy-init标志对{@code SmartLifecycle} bean的实际影响非常有限.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see LifecycleProcessor
 * @see ConfigurableApplicationContext
 */
public interface SmartLifecycle extends Lifecycle, Phased {

	/**
	 * {@code SmartLifecycle}的默认阶段：{@code Integer.MAX_VALUE}.
	 * <p>这不同于与常规{@link Lifecycle}实施相关联的公共阶段{@code 0}，
	 * 该阶段将通常自动启动的{@code SmartLifecycle} bean置于稍后的启动阶段和更早的关闭阶段.
	 * @since 5.1
	 * @see #getPhase()
	 * @see org.springframework.context.support.DefaultLifecycleProcessor#getPhase(Lifecycle)
	 */
	int DEFAULT_PHASE = Integer.MAX_VALUE;


	/**
	 * 如果此{@code Lifecycle}组件在刷新包含它的{@link ApplicationContext}时应由容器自动启动，
	 * 则返回{@code true}.
	 * <p>值为{@code false}表示该组件旨在通过显式{@link #start()}调用来启动，类似于普通的{@link Lifecycle}实现.
	 * <p>默认实现返回true.
	 * @see #start()
	 * @see #getPhase()
	 * @see LifecycleProcessor#onRefresh()
	 * @see ConfigurableApplicationContext#refresh()
	 */
	default boolean isAutoStartup() {
		return true;
	}

	/**
	 * Indicates that a Lifecycle component must stop if it is currently running.
	 * <p>The provided callback is used by the {@link LifecycleProcessor} to support
	 * an ordered, and potentially concurrent, shutdown of all components having a
	 * common shutdown order value. The callback <b>must</b> be executed after
	 * the {@code SmartLifecycle} component does indeed stop.
	 * <p>The {@link LifecycleProcessor} will call <i>only</i> this variant of the
	 * {@code stop} method; i.e. {@link Lifecycle#stop()} will not be called for
	 * {@code SmartLifecycle} implementations unless explicitly delegated to within
	 * the implementation of this method.
	 * <p>The default implementation delegates to {@link #stop()} and immediately
	 * triggers the given callback in the calling thread. Note that there is no
	 * synchronization between the two, so custom implementations may at least
	 * want to put the same steps within their common lifecycle monitor (if any).
	 * 指示生命周期组件如果正在运行，则必须停止.
	 * <p>{@link LifecycleProcessor}使用提供的回调来支持所有具有共同关闭顺序值的组件的有序（可能同时）关闭. 必须在SmartLifecycle组件确实停止后执行回调.
	 *
	 * <p>{@link LifecycleProcessor}将仅调用stop方法的此变体.
	 * 即，除非在此方法的实现中明确委托给{@link Lifecycle#stop()}，
	 * 否则不会为SmartLifecycle实现调用Lifecycle.stop（）.
	 *
	 * <p>默认实现委托给{@link Lifecycle#stop()}并立即在调用线程中触发给定的回调.
	 * 请注意，两者之间没有同步，因此自定义实现可能至少希望将相同的步骤放入其公共生命周期监视器（如果有）中.
	 * @see #stop()
	 * @see #getPhase()
	 */
	default void stop(Runnable callback) {
		stop();
		callback.run();
	}

	/**
	 * 返回此生命周期对象应该在其中运行的阶段.
	 * <p>默认实现返回{@link #DEFAULT_PHASE}，以便在常规的{@code Lifecycle}实现之后执行{@code stop()}回调.
	 * @see #isAutoStartup()
	 * @see #start()
	 * @see #stop(Runnable)
	 * @see org.springframework.context.support.DefaultLifecycleProcessor#getPhase(Lifecycle)
	 */
	@Override
	default int getPhase() {
		return DEFAULT_PHASE;
	}

}
