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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * 实用程序类提供了用于调用{@link Validator}和拒绝空字段的便捷方法.
 *
 * <p>当使用{@link #rejectIfEmpty}或{@link #rejectIfEmptyOrWhitespace}时，在Validator实现中检查空字段可能会变成一句话.
 *
 * @author Juergen Hoeller
 * @author Dmitriy Kopylenko
 * @since 06.05.2003
 * @see Validator
 * @see Errors
 */
public abstract class ValidationUtils {

	private static final Log logger = LogFactory.getLog(ValidationUtils.class);


	/**
	 * 为给定的对象调用指定的{@link Validator}，并提供{@link Errors}实例.
	 * @param validator the {@code Validator} to be invoked
	 * @param target the object to bind the parameters to
	 * @param errors the {@link Errors} instance that should store the errors
	 * @throws IllegalArgumentException if either of the {@code Validator} or {@code Errors}
	 * arguments is {@code null}, or if the supplied {@code Validator} does not
	 * {@link Validator#supports(Class) support} the validation of the supplied object's type
	 */
	public static void invokeValidator(Validator validator, Object target, Errors errors) {
		invokeValidator(validator, target, errors, (Object[]) null);
	}

	/**
	 * 为给定的对象调用指定的{@link Validator}/{@link SmartValidator}.
	 * @param validator the {@code Validator} to be invoked
	 * @param target the object to bind the parameters to
	 * @param errors the {@link Errors} instance that should store the errors
	 * @param validationHints one or more hint objects to be passed to the validation engine
	 * @throws IllegalArgumentException if either of the {@code Validator} or {@code Errors}
	 * arguments is {@code null}, or if the supplied {@code Validator} does not
	 * {@link Validator#supports(Class) support} the validation of the supplied object's type
	 */
	public static void invokeValidator(
			Validator validator, Object target, Errors errors, @Nullable Object... validationHints) {

		Assert.notNull(validator, "Validator must not be null");
		Assert.notNull(target, "Target object must not be null");
		Assert.notNull(errors, "Errors object must not be null");

		if (logger.isDebugEnabled()) {
			logger.debug("Invoking validator [" + validator + "]");
		}
		if (!validator.supports(target.getClass())) {
			throw new IllegalArgumentException(
					"Validator [" + validator.getClass() + "] does not support [" + target.getClass() + "]");
		}

		if (!ObjectUtils.isEmpty(validationHints) && validator instanceof SmartValidator) {
			((SmartValidator) validator).validate(target, errors, validationHints);
		}
		else {
			validator.validate(target, errors);
		}

		if (logger.isDebugEnabled()) {
			if (errors.hasErrors()) {
				logger.debug("Validator found " + errors.getErrorCount() + " errors");
			}
			else {
				logger.debug("Validator found no errors");
			}
		}
	}


	/**
	 * 如果值为空，则使用给定的错误代码拒绝给定的字段.
	 * <p>在这种情况下，“空”值表示{@code null}或空字符串“”.
	 * <p>不需要传入需要验证字段的对象，因为{@link Errors}实例可以自行解析字段值（通常将保留对目标对象的内部引用）.
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 */
	public static void rejectIfEmpty(Errors errors, String field, String errorCode) {
		rejectIfEmpty(errors, field, errorCode, null, null);
	}

	/**
	 * 如果值为空，则使用给定的错误代码和默认消息拒绝给定的字段.
	 * <p>An 'empty' value in this context means either {@code null} or
	 * the empty string "".
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode error code, interpretable as message key
	 * @param defaultMessage fallback default message
	 */
	public static void rejectIfEmpty(Errors errors, String field, String errorCode, String defaultMessage) {
		rejectIfEmpty(errors, field, errorCode, null, defaultMessage);
	}

	/**
	 * 如果值为空，则使用给定的错误代码和错误参数拒绝给定的字段.
	 * <p>An 'empty' value in this context means either {@code null} or
	 * the empty string "".
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 * @param errorArgs the error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 */
	public static void rejectIfEmpty(Errors errors, String field, String errorCode, Object[] errorArgs) {
		rejectIfEmpty(errors, field, errorCode, errorArgs, null);
	}

	/**
	 * 如果值为空，则使用给定的错误代码，错误参数和默认消息拒绝给定的字段.
	 * <p>An 'empty' value in this context means either {@code null} or
	 * the empty string "".
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 * @param errorArgs the error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 * @param defaultMessage fallback default message
	 */
	public static void rejectIfEmpty(Errors errors, String field, String errorCode,
			@Nullable Object[] errorArgs, @Nullable String defaultMessage) {

		Assert.notNull(errors, "Errors object must not be null");
		Object value = errors.getFieldValue(field);
		if (value == null || !StringUtils.hasLength(value.toString())) {
			errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
		}
	}

	/**
	 * 如果该值为空或仅包含空格，则拒绝具有给定错误代码的给定字段.
	 * <p>An 'empty' value in this context means either {@code null},
	 * the empty string "", or consisting wholly of whitespace.
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 */
	public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode) {
		rejectIfEmptyOrWhitespace(errors, field, errorCode, null, null);
	}

	/**
	 * Reject the given field with the given error code and default message
	 * if the value is empty or just contains whitespace.
	 * <p>An 'empty' value in this context means either {@code null},
	 * the empty string "", or consisting wholly of whitespace.
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 * @param defaultMessage fallback default message
	 */
	public static void rejectIfEmptyOrWhitespace(
			Errors errors, String field, String errorCode, String defaultMessage) {

		rejectIfEmptyOrWhitespace(errors, field, errorCode, null, defaultMessage);
	}

	/**
	 * Reject the given field with the given error code and error arguments
	 * if the value is empty or just contains whitespace.
	 * <p>An 'empty' value in this context means either {@code null},
	 * the empty string "", or consisting wholly of whitespace.
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 * @param errorArgs the error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 */
	public static void rejectIfEmptyOrWhitespace(
			Errors errors, String field, String errorCode, @Nullable Object[] errorArgs) {

		rejectIfEmptyOrWhitespace(errors, field, errorCode, errorArgs, null);
	}

	/**
	 * Reject the given field with the given error code, error arguments
	 * and default message if the value is empty or just contains whitespace.
	 * <p>An 'empty' value in this context means either {@code null},
	 * the empty string "", or consisting wholly of whitespace.
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 * @param errorArgs the error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 * @param defaultMessage fallback default message
	 */
	public static void rejectIfEmptyOrWhitespace(
			Errors errors, String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {

		Assert.notNull(errors, "Errors object must not be null");
		Object value = errors.getFieldValue(field);
		if (value == null ||!StringUtils.hasText(value.toString())) {
			errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
		}
	}

}
