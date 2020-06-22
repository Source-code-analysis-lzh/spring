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

package org.springframework.core.task.support;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

/**
 * 适配器，它使用Spring {@link org.springframework.core.task.TaskExecutor}
 * 并提供对{@code java.util.concurrent.ExecutorService}的适配。
 *
 * <p>这主要是为了适应通过{@code java.util.concurrent.ExecutorService} API进行通信的客户端组件。
 * 它也可以用作Java EE 7环境中本地Spring {@code TaskExecutor}
 * 后端与JNDI所在的{@code ManagedExecutorService}之间的公共基础。
 *
 * 注意：此ExecutorService适配器不支持{@code java.util.concurrent.ExecutorService} API
 * （("shutdown()" etc)中的生命周期方法，类似于Java EE 7环境中的服务器级{@code ManagedExecutorService}。
 * 生命周期始终取决于后端池，此适配器充当该目标池的仅访问代理。
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see java.util.concurrent.ExecutorService
 */
public class ExecutorServiceAdapter extends AbstractExecutorService {

	private final TaskExecutor taskExecutor;


	/**
	 * Create a new ExecutorServiceAdapter, using the given target executor.
	 * @param taskExecutor the target executor to delegate to
	 */
	public ExecutorServiceAdapter(TaskExecutor taskExecutor) {
		Assert.notNull(taskExecutor, "TaskExecutor must not be null");
		this.taskExecutor = taskExecutor;
	}


	@Override
	public void execute(Runnable task) {
		this.taskExecutor.execute(task);
	}

	@Override
	public void shutdown() {
		throw new IllegalStateException(
				"Manual shutdown not supported - ExecutorServiceAdapter is dependent on an external lifecycle");
	}

	@Override
	public List<Runnable> shutdownNow() {
		throw new IllegalStateException(
				"Manual shutdown not supported - ExecutorServiceAdapter is dependent on an external lifecycle");
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		throw new IllegalStateException(
				"Manual shutdown not supported - ExecutorServiceAdapter is dependent on an external lifecycle");
	}

	@Override
	public boolean isShutdown() {
		return false;
	}

	@Override
	public boolean isTerminated() {
		return false;
	}

}
