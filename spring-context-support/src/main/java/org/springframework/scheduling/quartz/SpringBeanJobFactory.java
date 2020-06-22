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

package org.springframework.scheduling.quartz;

import org.quartz.SchedulerContext;
import org.quartz.spi.TriggerFiredBundle;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

/**
 * {@link AdaptableJobFactory}的子类，还支持对bean属性的Spring样式依赖项注入。 
 * 从本质上讲，Spring的{@link QuartzJobBean}的直接等效形式为Quartz {@link org.quartz.spi.JobFactory}。
 *
 * <p>将调度上下文，作业数据映射和触发器数据映射条目应用为bean属性值。 
 * 如果找不到匹配的bean属性，则默认情况下将忽略该条目。 这类似于QuartzJobBean的行为。
 *
 * <p>Compatible with Quartz 2.1.4 and higher, as of Spring 4.1.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see SchedulerFactoryBean#setJobFactory
 * @see QuartzJobBean
 */
public class SpringBeanJobFactory extends AdaptableJobFactory
		implements ApplicationContextAware, SchedulerContextAware {

	@Nullable
	private String[] ignoredUnknownProperties;

	@Nullable
	private ApplicationContext applicationContext;

	@Nullable
	private SchedulerContext schedulerContext;


	/**
	 * Specify the unknown properties (not found in the bean) that should be ignored.
	 * <p>Default is {@code null}, indicating that all unknown properties
	 * should be ignored. Specify an empty array to throw an exception in case
	 * of any unknown properties, or a list of property names that should be
	 * ignored if there is no corresponding property found on the particular
	 * job class (all other unknown properties will still trigger an exception).
	 */
	public void setIgnoredUnknownProperties(String... ignoredUnknownProperties) {
		this.ignoredUnknownProperties = ignoredUnknownProperties;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setSchedulerContext(SchedulerContext schedulerContext) {
		this.schedulerContext = schedulerContext;
	}


	/**
	 * 创建job实例，并使用从调度程序上下文，job数据映射和触发器数据映射中获取的属性值填充该作业实例。
	 */
	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		Object job = (this.applicationContext != null ?
				this.applicationContext.getAutowireCapableBeanFactory().createBean(
						bundle.getJobDetail().getJobClass(), AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false) :
				super.createJobInstance(bundle));

		if (isEligibleForPropertyPopulation(job)) {
			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(job);
			MutablePropertyValues pvs = new MutablePropertyValues();
			if (this.schedulerContext != null) {
				pvs.addPropertyValues(this.schedulerContext);
			}
			pvs.addPropertyValues(bundle.getJobDetail().getJobDataMap());
			pvs.addPropertyValues(bundle.getTrigger().getJobDataMap());
			if (this.ignoredUnknownProperties != null) {
				for (String propName : this.ignoredUnknownProperties) {
					if (pvs.contains(propName) && !bw.isWritableProperty(propName)) {
						pvs.removePropertyValue(propName);
					}
				}
				bw.setPropertyValues(pvs);
			}
			else {
				bw.setPropertyValues(pvs, true);
			}
		}

		return job;
	}

	/**
	 * Return whether the given job object is eligible for having
	 * its bean properties populated.
	 * <p>The default implementation ignores {@link QuartzJobBean} instances,
	 * which will inject bean properties themselves.
	 * @param jobObject the job object to introspect
	 * @see QuartzJobBean
	 */
	protected boolean isEligibleForPropertyPopulation(Object jobObject) {
		return (!(jobObject instanceof QuartzJobBean));
	}

}
