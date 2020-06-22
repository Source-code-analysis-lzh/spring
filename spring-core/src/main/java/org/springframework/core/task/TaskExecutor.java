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

import java.util.concurrent.Executor;

/**
 * 简单的任务执行程序接口，抽象了{@link Runnable}的执行.
 *
 * <p>实现可以使用各种不同的执行策略，如：同步，异步，使用线程池等。
 *
 * <p>等效于JDK 1.5的{@link java.util.concurrent.Executor}接口； 
 * 现在在Spring 3.0中对其进行了扩展，以便客户端可以声明对Executor的依赖关系
 * 并接收任何TaskExecutor实现。 该接口与标准Executor接口保持独立，
 * 主要是为了与Spring 2.x中的JDK 1.4向后兼容。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see java.util.concurrent.Executor
 */
@FunctionalInterface
public interface TaskExecutor extends Executor {

	/**
	 * 执行给定的{@code task}。
	 * <p>如果实现使用异步执行策略，则该调用可能立即返回，或者在同步执行的情况下，调用可能会阻塞。
	 * @param task the {@code Runnable} to execute (never {@code null})
	 * @throws TaskRejectedException if the given task was not accepted
	 */
	@Override
	void execute(Runnable task);

}
