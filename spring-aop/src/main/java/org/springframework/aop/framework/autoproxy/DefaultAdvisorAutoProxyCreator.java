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

package org.springframework.aop.framework.autoproxy;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;

/**
 * {@code BeanPostProcessor}实现可基于当前{@code BeanFactory}中的所有候选{@code Advisor}来创建AOP代理。 
 * 这个类是完全通用的。 它不包含用于处理任何特定方面（例如池化方面）的特殊代码。
 *
 * <p>可以通过将{@code usePrefix}属性设置为true来过滤advisors，
 * 例如，在同一工厂中使用此类型的多个后处理器，在这种情况下，
 * 仅以DefaultAdvisorAutoProxyCreator的Bean名称开头，后跟一个点（例如"aapc."） 的advisors将会被使用。 
 * 通过设置{@code advisorBeanNamePrefix}属性，可以从Bean名称更改此默认前缀。 在这种情况下，也将使用分隔符(.)。
 *
 * @author Rod Johnson
 * @author Rob Harrop
 */
@SuppressWarnings("serial")
public class DefaultAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator implements BeanNameAware {

	/** Separator between prefix and remainder of bean name. */
	public static final String SEPARATOR = ".";


	private boolean usePrefix = false;

	@Nullable
	private String advisorBeanNamePrefix;


	/**
	 * Set whether to only include advisors with a certain prefix in the bean name.
	 * <p>Default is {@code false}, including all beans of type {@code Advisor}.
	 * @see #setAdvisorBeanNamePrefix
	 */
	public void setUsePrefix(boolean usePrefix) {
		this.usePrefix = usePrefix;
	}

	/**
	 * Return whether to only include advisors with a certain prefix in the bean name.
	 */
	public boolean isUsePrefix() {
		return this.usePrefix;
	}

	/**
	 * Set the prefix for bean names that will cause them to be included for
	 * auto-proxying by this object. This prefix should be set to avoid circular
	 * references. Default value is the bean name of this object + a dot.
	 * @param advisorBeanNamePrefix the exclusion prefix
	 */
	public void setAdvisorBeanNamePrefix(@Nullable String advisorBeanNamePrefix) {
		this.advisorBeanNamePrefix = advisorBeanNamePrefix;
	}

	/**
	 * Return the prefix for bean names that will cause them to be included
	 * for auto-proxying by this object.
	 */
	@Nullable
	public String getAdvisorBeanNamePrefix() {
		return this.advisorBeanNamePrefix;
	}

	@Override
	public void setBeanName(String name) {
		// If no infrastructure bean name prefix has been set, override it.
		if (this.advisorBeanNamePrefix == null) {
			this.advisorBeanNamePrefix = name + SEPARATOR;
		}
	}


	/**
	 * Consider {@code Advisor} beans with the specified prefix as eligible, if activated.
	 * @see #setUsePrefix
	 * @see #setAdvisorBeanNamePrefix
	 */
	@Override
	protected boolean isEligibleAdvisorBean(String beanName) {
		if (!isUsePrefix()) {
			return true;
		}
		String prefix = getAdvisorBeanNamePrefix();
		return (prefix != null && beanName.startsWith(prefix));
	}

}
