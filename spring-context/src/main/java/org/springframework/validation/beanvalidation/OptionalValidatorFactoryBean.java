/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.validation.beanvalidation;

import javax.validation.ValidationException;

import org.apache.commons.logging.LogFactory;

/**
 * {@link LocalValidatorFactoryBean}子类
 * 可以在没有Bean验证提供程序的情况下将{@link org.springframework.validation.Validator}调用简单地转换为no-ops.
 *
 * <p>如果存在{@code javax.validation} API，但未配置任何显式的Validator，
 * 则这是Spring的MVC配置名称空间使用的实际类.
 *
 * @author Juergen Hoeller
 * @since 4.0.1
 */
public class OptionalValidatorFactoryBean extends LocalValidatorFactoryBean {

	@Override
	public void afterPropertiesSet() {
		try {
			super.afterPropertiesSet();
		}
		catch (ValidationException ex) {
			LogFactory.getLog(getClass()).debug("Failed to set up a Bean Validation provider", ex);
		}
	}

}
