/*
 * Copyright 2002-2016 the original author or authors.
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

/**
 * 一个执行回调方法的装饰器，该接口应用于装饰将要执行的任何{@link Runnable}。
 *
 * <p>注意，这样的装饰器不一定要应用于用户提供的{@code Runnable}/{@code Callable}，
 * 而是应用于实际的执行回调（可能是用户提供的任务的包装）。
 *
 * <p>主要应用于传递上下文，或者提供任务的监控/统计信息
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @see TaskExecutor#execute(Runnable)
 * @see SimpleAsyncTaskExecutor#setTaskDecorator
 */
@FunctionalInterface
public interface TaskDecorator {

	/**
	 * 装饰给定的{@code Runnable}，返回可能包装的{@code Runnable}以便实际执行。
	 * @param runnable the original {@code Runnable}
	 * @return the decorated {@code Runnable}
	 */
	Runnable decorate(Runnable runnable);

}
