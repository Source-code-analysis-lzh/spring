/*
 * Copyright 2002-2011 the original author or authors.
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

import org.quartz.SchedulerContext;

import org.springframework.beans.factory.Aware;

/**
 * 回调接口由需要访问SchedulerContext的Spring托管Quartz工件实现（没有自然访问权限）。
 *
 * <p>Currently only supported for custom JobFactory implementations
 * that are passed in via Spring's SchedulerFactoryBean.
 * <p>当前仅支持通过Spring的SchedulerFactoryBean传入的自定义JobFactory实现。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.0
 * @see org.quartz.spi.JobFactory
 * @see SchedulerFactoryBean#setJobFactory
 */
public interface SchedulerContextAware extends Aware {

	/**
	 * Set the SchedulerContext of the current Quartz Scheduler.
	 * @see org.quartz.Scheduler#getContext()
	 */
	void setSchedulerContext(SchedulerContext schedulerContext);

}
