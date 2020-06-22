/*
 * Copyright 2002-2015 the original author or authors.
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

/**
 * JSR-303 {@link ConstraintValidatorFactory}实现，该实现委派给Spring BeanFactory
 * 以创建自动装配的{@link ConstraintValidator}实例.
 *
 * Spring提供了对Bean验证API的全面支持，包括将Bean验证提供程序作为Spring Bean进行启动.
 * 这使您可以在应用程序中需要验证的任何地方注入javax.validation.ValidatorFactory或
 * javax.validation.Validator.
 *
 * <p>请注意，此类仅供编程使用，而不用于标准{@code validation.xml}文件中的声明式使用.
 * 考虑将{@link org.springframework.web.bind.support.SpringWebConstraintValidatorFactory}
 * 用于Web应用程序中的声明式使用，例如 使用JAX-RS或JAX-WS.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory#createBean(Class)
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public class SpringConstraintValidatorFactory implements ConstraintValidatorFactory {

	private final AutowireCapableBeanFactory beanFactory;


	/**
	 * Create a new SpringConstraintValidatorFactory for the given BeanFactory.
	 * @param beanFactory the target BeanFactory
	 */
	public SpringConstraintValidatorFactory(AutowireCapableBeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
	}


	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
		return this.beanFactory.createBean(key);
	}

	// Bean Validation 1.1 releaseInstance method
	@Override
	public void releaseInstance(ConstraintValidator<?, ?> instance) {
		this.beanFactory.destroyBean(instance);
	}

}
