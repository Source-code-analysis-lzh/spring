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

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.triggers.CronTriggerImpl;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Constants;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 一个用于创建Quartz {@link org.quartz.CronTrigger}实例的Spring {@link FactoryBean}，
 * 支持Bean风格的触发器配置使用。
 *
 * <p>{@code CronTrigger(Impl)}本身已经是JavaBean，但是缺少合理的默认值。 
 * 此类使用Spring bean名称作为作业名称，使用Quartz默认组("DEFAULT")作为作业组，
 * 将当前时间用作开始时间，并使用不确定的重复次数（如果未指定）。
 *
 * <p>该类还将使用给定{@link org.quartz.JobDetail}的作业名称和组来注册触发器。 
 * 这允许{@link SchedulerFactoryBean}自动为相应的JobDetail注册触发器，而不是分别注册JobDetail。
 *
 * @author Juergen Hoeller
 * @since 3.1
 * @see #setName
 * @see #setGroup
 * @see #setStartDelay
 * @see #setJobDetail
 * @see SchedulerFactoryBean#setTriggers
 * @see SchedulerFactoryBean#setJobDetails
 */
public class CronTriggerFactoryBean implements FactoryBean<CronTrigger>, BeanNameAware, InitializingBean {

	/** Constants for the CronTrigger class. */
	private static final Constants constants = new Constants(CronTrigger.class);


	@Nullable
	private String name;

	@Nullable
	private String group;

	@Nullable
	private JobDetail jobDetail;

	private JobDataMap jobDataMap = new JobDataMap();

	@Nullable
	private Date startTime;

	private long startDelay = 0;

	@Nullable
	private String cronExpression;

	@Nullable
	private TimeZone timeZone;

	@Nullable
	private String calendarName;

	private int priority;

	private int misfireInstruction;

	@Nullable
	private String description;

	@Nullable
	private String beanName;

	@Nullable
	private CronTrigger cronTrigger;


	/**
	 * Specify the trigger's name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Specify the trigger's group.
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * 设置此触发器应与之关联的JobDetail。
	 */
	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	/**
	 * Set the trigger's JobDataMap.
	 * @see #setJobDataAsMap
	 */
	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

	/**
	 * Return the trigger's JobDataMap.
	 */
	public JobDataMap getJobDataMap() {
		return this.jobDataMap;
	}

	/**
	 * Register objects in the JobDataMap via a given Map.
	 * <p>These objects will be available to this Trigger only,
	 * in contrast to objects in the JobDetail's data map.
	 * @param jobDataAsMap a Map with String keys and any objects as values
	 * (for example Spring-managed beans)
	 */
	public void setJobDataAsMap(Map<String, ?> jobDataAsMap) {
		this.jobDataMap.putAll(jobDataAsMap);
	}

	/**
	 * Set a specific start time for the trigger.
	 * <p>Note that a dynamically computed {@link #setStartDelay} specification
	 * overrides a static timestamp set here.
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * Set the start delay in milliseconds.
	 * <p>The start delay is added to the current system time (when the bean starts)
	 * to control the start time of the trigger.
	 */
	public void setStartDelay(long startDelay) {
		Assert.isTrue(startDelay >= 0, "Start delay cannot be negative");
		this.startDelay = startDelay;
	}

	/**
	 * Specify the cron expression for this trigger.
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * Specify the time zone for this trigger's cron expression.
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Associate a specific calendar with this cron trigger.
	 */
	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	/**
	 * Specify the priority of this trigger.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Specify a misfire instruction for this trigger.
	 */
	public void setMisfireInstruction(int misfireInstruction) {
		this.misfireInstruction = misfireInstruction;
	}

	/**
	 * Set the misfire instruction via the name of the corresponding
	 * constant in the {@link org.quartz.CronTrigger} class.
	 * Default is {@code MISFIRE_INSTRUCTION_SMART_POLICY}.
	 * @see org.quartz.CronTrigger#MISFIRE_INSTRUCTION_FIRE_ONCE_NOW
	 * @see org.quartz.CronTrigger#MISFIRE_INSTRUCTION_DO_NOTHING
	 * @see org.quartz.Trigger#MISFIRE_INSTRUCTION_SMART_POLICY
	 */
	public void setMisfireInstructionName(String constantName) {
		this.misfireInstruction = constants.asNumber(constantName).intValue();
	}

	/**
	 * Associate a textual description with this trigger.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}


	@Override
	public void afterPropertiesSet() throws ParseException {
		Assert.notNull(this.cronExpression, "Property 'cronExpression' is required");

		if (this.name == null) {
			this.name = this.beanName;
		}
		if (this.group == null) {
			this.group = Scheduler.DEFAULT_GROUP;
		}
		if (this.jobDetail != null) {
			this.jobDataMap.put("jobDetail", this.jobDetail);
		}
		if (this.startDelay > 0 || this.startTime == null) {
			this.startTime = new Date(System.currentTimeMillis() + this.startDelay);
		}
		if (this.timeZone == null) {
			this.timeZone = TimeZone.getDefault();
		}

		CronTriggerImpl cti = new CronTriggerImpl();
		cti.setName(this.name != null ? this.name : toString());
		cti.setGroup(this.group);
		if (this.jobDetail != null) {
			cti.setJobKey(this.jobDetail.getKey());
		}
		cti.setJobDataMap(this.jobDataMap);
		cti.setStartTime(this.startTime);
		cti.setCronExpression(this.cronExpression);
		cti.setTimeZone(this.timeZone);
		cti.setCalendarName(this.calendarName);
		cti.setPriority(this.priority);
		cti.setMisfireInstruction(this.misfireInstruction);
		cti.setDescription(this.description);
		this.cronTrigger = cti;
	}


	@Override
	@Nullable
	public CronTrigger getObject() {
		return this.cronTrigger;
	}

	@Override
	public Class<?> getObjectType() {
		return CronTrigger.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
