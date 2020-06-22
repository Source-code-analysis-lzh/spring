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

package org.springframework.validation;

import org.springframework.lang.Nullable;

/**
 * {@link Validator}接口的扩展变体，增加了对验证“提示”的支持.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.1
 */
public interface SmartValidator extends Validator {

	/**
	 * 验证提供的{@code target}对象，该目标对象必须是{@link Class}类型，
	 * 即{@link #supports(Class)}方法返回{@code true} 的类型.
	 * <p>提供的{@link Errors errors}实例可用于报告任何导致的验证错误.
	 * <p>{@code validate()}的此变体支持验证提示，例如针对JSR-303提供程序的验证组
	 * （在这种情况下，提供的提示对象必须是{@code Class}类型的注释参数）.
	 * <p>Note: Validation hints may get ignored by the actual target {@code Validator},
	 * in which case this method should behave just like its regular
	 * {@link #validate(Object, Errors)} sibling.
	 * @param target the object that is to be validated
	 * @param errors contextual state about the validation process
	 * @param validationHints one or more hint objects to be passed to the validation engine
	 * @see javax.validation.Validator#validate(Object, Class[])
	 */
	void validate(Object target, Errors errors, Object... validationHints);

	/**
	 * Validate the supplied value for the specified field on the target type,
	 * reporting the same validation errors as if the value would be bound to
	 * the field on an instance of the target class.
	 * @param targetType the target type
	 * @param fieldName the name of the field
	 * @param value the candidate value
	 * @param errors contextual state about the validation process
	 * @param validationHints one or more hint objects to be passed to the validation engine
	 * @since 5.1
	 * @see javax.validation.Validator#validateValue(Class, String, Object, Class[])
	 */
	default void validateValue(
			Class<?> targetType, String fieldName, @Nullable Object value, Errors errors, Object... validationHints) {

		throw new IllegalArgumentException("Cannot validate individual value for " + targetType);
	}

}
