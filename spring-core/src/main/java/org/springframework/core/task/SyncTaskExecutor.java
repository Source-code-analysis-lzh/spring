/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.core.task;

import java.io.Serializable;

import org.springframework.util.Assert;

/**
 * {@link TaskExecutor}实现，可在调用线程中同步执行每个任务。
 *
 * <p>主要用于测试场景。
 *
 * <p>在调用线程中执行确实具有参与线程上下文的优势，例如，线程上下文类加载器或线程的当前事务关联。 
 * 但是，在许多情况下，异步执行将是更可取的：针对此类情况，请选择异步{@code TaskExecutor}。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see SimpleAsyncTaskExecutor
 */
@SuppressWarnings("serial")
public class SyncTaskExecutor implements TaskExecutor, Serializable {

	/**
	 * Executes the given {@code task} synchronously, through direct
	 * invocation of it's {@link Runnable#run() run()} method.
	 * @throws IllegalArgumentException if the given {@code task} is {@code null}
	 */
	@Override
	public void execute(Runnable task) {
		Assert.notNull(task, "Runnable must not be null");
		task.run();
	}

}
