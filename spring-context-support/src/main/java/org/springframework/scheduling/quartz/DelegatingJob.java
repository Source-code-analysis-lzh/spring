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

package org.springframework.scheduling.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.springframework.util.Assert;

/**
 * 委托给定{@link java.lang.Runnable}实例的简单Quartz {@link org.quartz.Job}适配器。
 *
 * <p>通常与Runnable实例上的属性注入结合使用，
 * 而不是通过JobExecutionContext从Quartz JobDataMap接收参数。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see SpringBeanJobFactory
 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
 */
public class DelegatingJob implements Job {

	private final Runnable delegate;


	/**
	 * Create a new DelegatingJob.
	 * @param delegate the Runnable implementation to delegate to
	 */
	public DelegatingJob(Runnable delegate) {
		Assert.notNull(delegate, "Delegate must not be null");
		this.delegate = delegate;
	}

	/**
	 * Return the wrapped Runnable implementation.
	 */
	public final Runnable getDelegate() {
		return this.delegate;
	}


	/**
	 * Delegates execution to the underlying Runnable.
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		this.delegate.run();
	}

}
