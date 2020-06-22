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

package org.springframework.scheduling.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.enterprise.concurrent.ManagedExecutors;
import javax.enterprise.concurrent.ManagedTask;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.util.ClassUtils;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * 适配器，它接受{@code java.util.concurrent.Executor}并暴露Spring {@link org.springframework.core.task.TaskExecutor}。 
 * 还检测扩展的{@code java.util.concurrent.ExecutorService}，
 * 从而适配{@link org.springframework.core.task.AsyncTaskExecutor}接口。
 *
 * <p>自动检测JSR-236 {@link javax.enterprise.concurrent.ManagedExecutorService}以便为其公开ManagedTask适配器，
 * 从而公开基于{@link SchedulingAwareRunnable}的长时间运行提示和基于给定Runnable / Callable的{@code toString()}的标识名。 
 * 对于Java EE 7环境中的JSR-236样式查找，请考虑使用{@link DefaultManagedTaskExecutor}。
 *
 * <p>注意，有一个预先构建的{@link ThreadPoolTaskExecutor}，
 * 它允许以bean样式定义{@link java.util.concurrent.ThreadPoolExecutor}，
 * 将其直接作为Spring {@link org.springframework.core.task.TaskExecutor}公开。 
 * 这是对原始ThreadPoolExecutor定义的方便替代方法，它具有当前适配器类的单独定义。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see java.util.concurrent.Executor
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.ThreadPoolExecutor
 * @see java.util.concurrent.Executors
 * @see DefaultManagedTaskExecutor
 * @see ThreadPoolTaskExecutor
 */
public class ConcurrentTaskExecutor implements AsyncListenableTaskExecutor, SchedulingTaskExecutor {

	@Nullable
	private static Class<?> managedExecutorServiceClass;

	static {
		try {
			managedExecutorServiceClass = ClassUtils.forName(
					"javax.enterprise.concurrent.ManagedExecutorService",
					ConcurrentTaskScheduler.class.getClassLoader());
		}
		catch (ClassNotFoundException ex) {
			// JSR-236 API not available...
			managedExecutorServiceClass = null;
		}
	}

	private Executor concurrentExecutor;

	private TaskExecutorAdapter adaptedExecutor;


	/**
	 * Create a new ConcurrentTaskExecutor, using a single thread executor as default.
	 * @see java.util.concurrent.Executors#newSingleThreadExecutor()
	 */
	public ConcurrentTaskExecutor() {
		this.concurrentExecutor = Executors.newSingleThreadExecutor();
		this.adaptedExecutor = new TaskExecutorAdapter(this.concurrentExecutor);
	}

	/**
	 * Create a new ConcurrentTaskExecutor, using the given {@link java.util.concurrent.Executor}.
	 * <p>Autodetects a JSR-236 {@link javax.enterprise.concurrent.ManagedExecutorService}
	 * in order to expose {@link javax.enterprise.concurrent.ManagedTask} adapters for it.
	 * @param executor the {@link java.util.concurrent.Executor} to delegate to
	 */
	public ConcurrentTaskExecutor(@Nullable Executor executor) {
		this.concurrentExecutor = (executor != null ? executor : Executors.newSingleThreadExecutor());
		this.adaptedExecutor = getAdaptedExecutor(this.concurrentExecutor);
	}


	/**
	 * Specify the {@link java.util.concurrent.Executor} to delegate to.
	 * <p>Autodetects a JSR-236 {@link javax.enterprise.concurrent.ManagedExecutorService}
	 * in order to expose {@link javax.enterprise.concurrent.ManagedTask} adapters for it.
	 */
	public final void setConcurrentExecutor(@Nullable Executor executor) {
		this.concurrentExecutor = (executor != null ? executor : Executors.newSingleThreadExecutor());
		this.adaptedExecutor = getAdaptedExecutor(this.concurrentExecutor);
	}

	/**
	 * Return the {@link java.util.concurrent.Executor} that this adapter delegates to.
	 */
	public final Executor getConcurrentExecutor() {
		return this.concurrentExecutor;
	}

	/**
	 * Specify a custom {@link TaskDecorator} to be applied to any {@link Runnable}
	 * about to be executed.
	 * <p>Note that such a decorator is not necessarily being applied to the
	 * user-supplied {@code Runnable}/{@code Callable} but rather to the actual
	 * execution callback (which may be a wrapper around the user-supplied task).
	 * <p>The primary use case is to set some execution context around the task's
	 * invocation, or to provide some monitoring/statistics for task execution.
	 * @since 4.3
	 */
	public final void setTaskDecorator(TaskDecorator taskDecorator) {
		this.adaptedExecutor.setTaskDecorator(taskDecorator);
	}


	@Override
	public void execute(Runnable task) {
		this.adaptedExecutor.execute(task);
	}

	@Override
	public void execute(Runnable task, long startTimeout) {
		this.adaptedExecutor.execute(task, startTimeout);
	}

	@Override
	public Future<?> submit(Runnable task) {
		return this.adaptedExecutor.submit(task);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return this.adaptedExecutor.submit(task);
	}

	@Override
	public ListenableFuture<?> submitListenable(Runnable task) {
		return this.adaptedExecutor.submitListenable(task);
	}

	@Override
	public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
		return this.adaptedExecutor.submitListenable(task);
	}


	private static TaskExecutorAdapter getAdaptedExecutor(Executor concurrentExecutor) {
		if (managedExecutorServiceClass != null && managedExecutorServiceClass.isInstance(concurrentExecutor)) {
			return new ManagedTaskExecutorAdapter(concurrentExecutor);
		}
		return new TaskExecutorAdapter(concurrentExecutor);
	}


	/**
	 * TaskExecutorAdapter subclass that wraps all provided Runnables and Callables
	 * with a JSR-236 ManagedTask, exposing a long-running hint based on
	 * {@link SchedulingAwareRunnable} and an identity name based on the task's
	 * {@code toString()} representation.
	 */
	private static class ManagedTaskExecutorAdapter extends TaskExecutorAdapter {

		public ManagedTaskExecutorAdapter(Executor concurrentExecutor) {
			super(concurrentExecutor);
		}

		@Override
		public void execute(Runnable task) {
			super.execute(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
		}

		@Override
		public Future<?> submit(Runnable task) {
			return super.submit(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
		}

		@Override
		public <T> Future<T> submit(Callable<T> task) {
			return super.submit(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
		}

		@Override
		public ListenableFuture<?> submitListenable(Runnable task) {
			return super.submitListenable(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
		}

		@Override
		public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
			return super.submitListenable(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
		}
	}


	/**
	 * Delegate that wraps a given Runnable/Callable  with a JSR-236 ManagedTask,
	 * exposing a long-running hint based on {@link SchedulingAwareRunnable}
	 * and a given identity name.
	 */
	protected static class ManagedTaskBuilder {

		public static Runnable buildManagedTask(Runnable task, String identityName) {
			Map<String, String> properties;
			if (task instanceof SchedulingAwareRunnable) {
				properties = new HashMap<>(4);
				properties.put(ManagedTask.LONGRUNNING_HINT,
						Boolean.toString(((SchedulingAwareRunnable) task).isLongLived()));
			}
			else {
				properties = new HashMap<>(2);
			}
			properties.put(ManagedTask.IDENTITY_NAME, identityName);
			return ManagedExecutors.managedTask(task, properties, null);
		}

		public static <T> Callable<T> buildManagedTask(Callable<T> task, String identityName) {
			Map<String, String> properties = new HashMap<>(2);
			properties.put(ManagedTask.IDENTITY_NAME, identityName);
			return ManagedExecutors.managedTask(task, properties, null);
		}
	}

}
