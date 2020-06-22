/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.aop.support;

import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;

/**
 * 基于具体BeanFactory的PointcutAdvisor，它允许将任何Advice配置为对BeanFactory中的Advice Bean的引用，
 * 以及允许通过bean属性配置的Pointcut。
 *
 * <p>指定advice bean的名称而不是advice对象本身（如果在BeanFactory中运行）将在初始化时增加松散耦合，
 * 以便在切入点实际匹配之前不初始化advice对象。
 *
 * @author Juergen Hoeller
 * @since 2.0.2
 * @see #setPointcut
 * @see #setAdviceBeanName
 */
@SuppressWarnings("serial")
public class DefaultBeanFactoryPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

	private Pointcut pointcut = Pointcut.TRUE;


	/**
	 * Specify the pointcut targeting the advice.
	 * <p>Default is {@code Pointcut.TRUE}.
	 * @see #setAdviceBeanName
	 */
	public void setPointcut(@Nullable Pointcut pointcut) {
		this.pointcut = (pointcut != null ? pointcut : Pointcut.TRUE);
	}

	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}


	@Override
	public String toString() {
		return getClass().getName() + ": pointcut [" + getPointcut() + "]; advice bean '" + getAdviceBeanName() + "'";
	}

}
