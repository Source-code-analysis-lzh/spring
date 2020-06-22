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

package org.springframework.core.task.support;

import java.util.concurrent.Executor;

import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

/**
 * 公开任何Spring {@link org.springframework.core.task.TaskExecutor}
 * 的{@link java.util.concurrent.Executor}接口的适配器。
 *
 * <p>从Spring 3.0开始，此功能不再有用，因为TaskExecutor本身扩展了Executor接口。 
 * 适配器仅与现在隐藏给定对象的TaskExecutor本质有关，仅将标准Executor接口公开给客户端。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see java.util.concurrent.Executor
 * @see org.springframework.core.task.TaskExecutor
 */
public class ConcurrentExecutorAdapter implements Executor {

	private final TaskExecutor taskExecutor;


	/**
	 * Create a new ConcurrentExecutorAdapter for the given Spring TaskExecutor.
	 * @param taskExecutor the Spring TaskExecutor to wrap
	 */
	public ConcurrentExecutorAdapter(TaskExecutor taskExecutor) {
		Assert.notNull(taskExecutor, "TaskExecutor must not be null");
		this.taskExecutor = taskExecutor;
	}


	@Override
	public void execute(Runnable command) {
		this.taskExecutor.execute(command);
	}

}
