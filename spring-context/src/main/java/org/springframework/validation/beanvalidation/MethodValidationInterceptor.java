/*
 * Copyright 2002-2020 the original author or authors.
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

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.validation.annotation.Validated;

/**
 * 一个AOP Alliance {@link MethodInterceptor}实现，委派给JSR-303提供程序以对带注释的方法执行方法级验证.
 *
 * <p>适用于方法在其参数和/或返回值上具有JSR-303约束注释（在后一种情况下在方法级别指定，通常作为内联注释）.
 *
 * <p>E.g.: {@code public @NotNull Object myValidMethod(@NotNull String arg1, @Max(10) int arg2)}
 *
 * <p>可以通过Spring的{@link Validated}注释在包含的目标类的类型级别上指定验证组，
 * 并将其应用于该类的所有公共服务方法. 默认情况下，JSR-303将仅针对其默认组进行验证.
 *
 * <p>As of Spring 5.0, this functionality requires a Bean Validation 1.1+ provider.
 *
 * @author Juergen Hoeller
 * @since 3.1
 * @see MethodValidationPostProcessor
 * @see javax.validation.executable.ExecutableValidator
 */
public class MethodValidationInterceptor implements MethodInterceptor {

	private final Validator validator;


	/**
	 * Create a new MethodValidationInterceptor using a default JSR-303 validator underneath.
	 */
	public MethodValidationInterceptor() {
		this(Validation.buildDefaultValidatorFactory());
	}

	/**
	 * Create a new MethodValidationInterceptor using the given JSR-303 ValidatorFactory.
	 * @param validatorFactory the JSR-303 ValidatorFactory to use
	 */
	public MethodValidationInterceptor(ValidatorFactory validatorFactory) {
		this(validatorFactory.getValidator());
	}

	/**
	 * Create a new MethodValidationInterceptor using the given JSR-303 Validator.
	 * @param validator the JSR-303 Validator to use
	 */
	public MethodValidationInterceptor(Validator validator) {
		this.validator = validator;
	}


	@Override
	@Nullable
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// Avoid Validator invocation on FactoryBean.getObjectType/isSingleton
		if (isFactoryBeanMetadataMethod(invocation.getMethod())) {
			return invocation.proceed();
		}

		Class<?>[] groups = determineValidationGroups(invocation);

		// Standard Bean Validation 1.1 API
		ExecutableValidator execVal = this.validator.forExecutables();
		Method methodToValidate = invocation.getMethod();
		Set<ConstraintViolation<Object>> result;

		Object target = invocation.getThis();
		Assert.state(target != null, "Target must not be null");

		try {
			result = execVal.validateParameters(target, methodToValidate, invocation.getArguments(), groups);
		}
		catch (IllegalArgumentException ex) {
			// Probably a generic type mismatch between interface and impl as reported in SPR-12237 / HV-1011
			// Let's try to find the bridged method on the implementation class...
			methodToValidate = BridgeMethodResolver.findBridgedMethod(
					ClassUtils.getMostSpecificMethod(invocation.getMethod(), target.getClass()));
			result = execVal.validateParameters(target, methodToValidate, invocation.getArguments(), groups);
		}
		if (!result.isEmpty()) {
			throw new ConstraintViolationException(result);
		}

		Object returnValue = invocation.proceed();

		result = execVal.validateReturnValue(target, methodToValidate, returnValue, groups);
		if (!result.isEmpty()) {
			throw new ConstraintViolationException(result);
		}

		return returnValue;
	}

	private boolean isFactoryBeanMetadataMethod(Method method) {
		Class<?> clazz = method.getDeclaringClass();

		// Call from interface-based proxy handle, allowing for an efficient check?
		if (clazz.isInterface()) {
			return ((clazz == FactoryBean.class || clazz == SmartFactoryBean.class) &&
					!method.getName().equals("getObject"));
		}

		// Call from CGLIB proxy handle, potentially implementing a FactoryBean method?
		Class<?> factoryBeanType = null;
		if (SmartFactoryBean.class.isAssignableFrom(clazz)) {
			factoryBeanType = SmartFactoryBean.class;
		}
		else if (FactoryBean.class.isAssignableFrom(clazz)) {
			factoryBeanType = FactoryBean.class;
		}
		return (factoryBeanType != null && !method.getName().equals("getObject") &&
				ClassUtils.hasMethod(factoryBeanType, method));
	}

	/**
	 * Determine the validation groups to validate against for the given method invocation.
	 * <p>Default are the validation groups as specified in the {@link Validated} annotation
	 * on the containing target class of the method.
	 * @param invocation the current MethodInvocation
	 * @return the applicable validation groups as a Class array
	 */
	protected Class<?>[] determineValidationGroups(MethodInvocation invocation) {
		Validated validatedAnn = AnnotationUtils.findAnnotation(invocation.getMethod(), Validated.class);
		if (validatedAnn == null) {
			Object target = invocation.getThis();
			Assert.state(target != null, "Target must not be null");
			validatedAnn = AnnotationUtils.findAnnotation(target.getClass(), Validated.class);
		}
		return (validatedAnn != null ? validatedAnn.value() : new Class<?>[0]);
	}

}
