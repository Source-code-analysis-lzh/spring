/*
 * Copyright 2002-2014 the original author or authors.
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
import org.quartz.SchedulerException;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * Quartz Job接口的简单实现，将传入的JobDataMap以及SchedulerContext应用为bean属性值。 
 * 这是合适的，因为将为每次执行创建一个新的Job实例。 
 * JobDataMap条目将覆盖用相同的键的SchedulerContext条目。
 *
 * <p>例如，假设JobDataMap包含键"myParam"，其值为"5"：
 * 然后Job实现可以公开类型为int的bean属性"myParam"以接收该值，即方法"setMyParam(int)"。
 * 这也适用于复杂类型，例如业务对象等。
 *
 * <p>请注意，将依赖项注入应用于Job实例的首选方法是通过JobFactory：
 * 即，将{@link SpringBeanJobFactory}指定为Quartz 的JobFactory
 * (通常通过{@link SchedulerFactoryBean#setJobFactory} SchedulerFactoryBean的"jobFactory"属性})。 
 * 这允许实现注入依赖项Quartz Job，而无需依赖Spring基类。
 *
 * @author Juergen Hoeller
 * @since 18.02.2004
 * @see org.quartz.JobExecutionContext#getMergedJobDataMap()
 * @see org.quartz.Scheduler#getContext()
 * @see SchedulerFactoryBean#setSchedulerContextAsMap
 * @see SpringBeanJobFactory
 * @see SchedulerFactoryBean#setJobFactory
 */
public abstract class QuartzJobBean implements Job {

	/**
	 * 此实现将传入的job数据映射为bean属性值，然后将其委托给{@code executeInternal}执行。
	 * @see #executeInternal
	 */
	@Override
	public final void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValues(context.getScheduler().getContext());
			pvs.addPropertyValues(context.getMergedJobDataMap());
			bw.setPropertyValues(pvs, true);
		}
		catch (SchedulerException ex) {
			throw new JobExecutionException(ex);
		}
		executeInternal(context);
	}

	/**
	 * 执行实际job。 job数据映射将已被execute作为bean属性值应用。 该合同与标准Quartz执行方法完全相同。
	 * @see #execute
	 */
	protected abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;

}
