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

import java.util.List;

import org.springframework.beans.PropertyAccessor;
import org.springframework.lang.Nullable;

/**
 * 存储并公开有关指定对象的数据绑定和验证错误的信息.
 *
 * <p>字段名称可以是目标对象的属性（例如，绑定到客户对象时的"name"），
 * 或者是子对象（例如"address.street"）时的嵌套字段.
 * 支持通过{@link #setNestedPath(String)}进行子树导航：
 * 例如，一个{@code AddressValidator}验证“地址”，而不用知道这是customer的子对象.
 *
 * <p>注意：{@code Errors}对象是单线程安全的.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setNestedPath
 * @see BindException
 * @see DataBinder
 * @see ValidationUtils
 */
public interface Errors {

	/**
	 * 嵌套路径中路径元素之间的分隔符，例如在"customer.name"或"customer.address.street"中.
	 * <p>"." = same as the
	 * {@link org.springframework.beans.PropertyAccessor#NESTED_PROPERTY_SEPARATOR nested property separator}
	 * in the beans package.
	 */
	String NESTED_PATH_SEPARATOR = PropertyAccessor.NESTED_PROPERTY_SEPARATOR;


	/**
	 * 返回绑定的根对象的名称.
	 */
	String getObjectName();

	/**
	 * 允许更改上下文，以便标准验证器可以验证子树. 将给定路径添加到字段名称之前将拒绝调用.
	 * <p>For example, an address validator could validate the subobject
	 * "address" of a customer object.
	 * @param nestedPath nested path within this object,
	 * e.g. "address" (defaults to "", {@code null} is also acceptable).
	 * Can end with a dot: both "address" and "address." are valid.
	 */
	void setNestedPath(String nestedPath);

	/**
	 * 返回此{@link Errors}对象的当前嵌套路径.
	 * <p>Returns a nested path with a dot, i.e. "address.", for easy
	 * building of concatenated paths. Default is an empty String.
	 */
	String getNestedPath();

	/**
	 * 将给定的子路径推入嵌套路径堆栈.
	 * <p>A {@link #popNestedPath()} call will reset the original
	 * nested path before the corresponding
	 * {@code pushNestedPath(String)} call.
	 * <p>Using the nested path stack allows to set temporary nested paths
	 * for subobjects without having to worry about a temporary path holder.
	 * <p>For example: current path "spouse.", pushNestedPath("child") ->
	 * result path "spouse.child."; popNestedPath() -> "spouse." again.
	 * @param subPath the sub path to push onto the nested path stack
	 * @see #popNestedPath
	 */
	void pushNestedPath(String subPath);

	/**
	 * Pop the former nested path from the nested path stack.
	 * @throws IllegalStateException if there is no former nested path on the stack
	 * @see #pushNestedPath
	 */
	void popNestedPath() throws IllegalStateException;

	/**
	 * 使用给定的错误描述为整个目标对象注册一个全局错误.
	 * @param errorCode error code, 可解释为消息键
	 */
	void reject(String errorCode);

	/**
	 * Register a global error for the entire target object,
	 * using the given error description.
	 * @param errorCode error code, interpretable as a message key
	 * @param defaultMessage fallback default message
	 */
	void reject(String errorCode, String defaultMessage);

	/**
	 * Register a global error for the entire target object,
	 * using the given error description.
	 * @param errorCode error code, interpretable as a message key
	 * @param errorArgs error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 * @param defaultMessage fallback default message
	 */
	void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);

	/**
	 * 使用给定的错误描述为当前对象的指定字段注册一个字段错误（遵循当前嵌套路径，如果有的话）.
	 * <p>The field name may be {@code null} or empty String to indicate
	 * the current object itself rather than a field of it. This may result
	 * in a corresponding field error within the nested object graph or a
	 * global error if the current object is the top object.
	 * @param field the field name (may be {@code null} or empty String)
	 * @param errorCode error code, interpretable as a message key
	 * @see #getNestedPath()
	 */
	void rejectValue(@Nullable String field, String errorCode);

	/**
	 * Register a field error for the specified field of the current object
	 * (respecting the current nested path, if any), using the given error
	 * description.
	 * <p>The field name may be {@code null} or empty String to indicate
	 * the current object itself rather than a field of it. This may result
	 * in a corresponding field error within the nested object graph or a
	 * global error if the current object is the top object.
	 * @param field the field name (may be {@code null} or empty String)
	 * @param errorCode error code, interpretable as a message key
	 * @param defaultMessage fallback default message
	 * @see #getNestedPath()
	 */
	void rejectValue(@Nullable String field, String errorCode, String defaultMessage);

	/**
	 * Register a field error for the specified field of the current object
	 * (respecting the current nested path, if any), using the given error
	 * description.
	 * <p>The field name may be {@code null} or empty String to indicate
	 * the current object itself rather than a field of it. This may result
	 * in a corresponding field error within the nested object graph or a
	 * global error if the current object is the top object.
	 * @param field the field name (may be {@code null} or empty String)
	 * @param errorCode error code, interpretable as a message key
	 * @param errorArgs error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 * @param defaultMessage fallback default message
	 * @see #getNestedPath()
	 */
	void rejectValue(@Nullable String field, String errorCode,
			@Nullable Object[] errorArgs, @Nullable String defaultMessage);

	/**
	 * 将所有错误从给定的{@code Errors}实例添加到此{@code Errors}实例.
	 * <p>This is a convenience method to avoid repeated {@code reject(..)}
	 * calls for merging an {@code Errors} instance into another
	 * {@code Errors} instance.
	 * <p>Note that the passed-in {@code Errors} instance is supposed
	 * to refer to the same target object, or at least contain compatible errors
	 * that apply to the target object of this {@code Errors} instance.
	 * @param errors the {@code Errors} instance to merge in
	 */
	void addAllErrors(Errors errors);

	/**
	 * Return if there were any errors.
	 */
	boolean hasErrors();

	/**
	 * Return the total number of errors.
	 */
	int getErrorCount();

	/**
	 * 获取所有错误，包括全局错误和字段错误.
	 * @return a list of {@link ObjectError} instances
	 */
	List<ObjectError> getAllErrors();

	/**
	 * Are there any global errors?
	 * @return {@code true} if there are any global errors
	 * @see #hasFieldErrors()
	 */
	boolean hasGlobalErrors();

	/**
	 * Return the number of global errors.
	 * @return the number of global errors
	 * @see #getFieldErrorCount()
	 */
	int getGlobalErrorCount();

	/**
	 * Get all global errors.
	 * @return a list of {@link ObjectError} instances
	 */
	List<ObjectError> getGlobalErrors();

	/**
	 * Get the <i>first</i> global error, if any.
	 * @return the global error, or {@code null}
	 */
	@Nullable
	ObjectError getGlobalError();

	/**
	 * Are there any field errors?
	 * @return {@code true} if there are any errors associated with a field
	 * @see #hasGlobalErrors()
	 */
	boolean hasFieldErrors();

	/**
	 * Return the number of errors associated with a field.
	 * @return the number of errors associated with a field
	 * @see #getGlobalErrorCount()
	 */
	int getFieldErrorCount();

	/**
	 * Get all errors associated with a field.
	 * @return a List of {@link FieldError} instances
	 */
	List<FieldError> getFieldErrors();

	/**
	 * Get the <i>first</i> error associated with a field, if any.
	 * @return the field-specific error, or {@code null}
	 */
	@Nullable
	FieldError getFieldError();

	/**
	 * Are there any errors associated with the given field?
	 * @param field the field name
	 * @return {@code true} if there were any errors associated with the given field
	 */
	boolean hasFieldErrors(String field);

	/**
	 * Return the number of errors associated with the given field.
	 * @param field the field name
	 * @return the number of errors associated with the given field
	 */
	int getFieldErrorCount(String field);

	/**
	 * Get all errors associated with the given field.
	 * <p>Implementations should support not only full field names like
	 * "name" but also pattern matches like "na*" or "address.*".
	 * @param field the field name
	 * @return a List of {@link FieldError} instances
	 */
	List<FieldError> getFieldErrors(String field);

	/**
	 * Get the first error associated with the given field, if any.
	 * @param field the field name
	 * @return the field-specific error, or {@code null}
	 */
	@Nullable
	FieldError getFieldError(String field);

	/**
	 * 返回给定字段的当前值，或者是当前bean属性值，或者是来自最后一个绑定的拒绝更新.
	 * <p>即使类型不匹配，也可以方便地访问用户指定的字段值.
	 * @param field the field name
	 * @return the current value of the given field
	 */
	@Nullable
	Object getFieldValue(String field);

	/**
	 * 返回给定字段的类型.
	 * <p>Implementations should be able to determine the type even
	 * when the field value is {@code null}, for example from some
	 * associated descriptor.
	 * @param field the field name
	 * @return the type of the field, or {@code null} if not determinable
	 */
	@Nullable
	Class<?> getFieldType(String field);

}
