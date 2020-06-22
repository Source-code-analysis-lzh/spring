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

package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

/**
 * 一种能够根据上下文对象求值的表达式.
 * 封装先前解析的表达式字符串的详细信息. 提供用于表达式计算的通用抽象.
 *
 * @author Keith Donald
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface Expression {

	/**
	 * 返回用于创建此表达式的原始字符串（未修改）.
	 * @return the original expression string
	 */
	String getExpressionString();

	/**
	 * 在默认标准上下文中计算此表达式.
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	@Nullable
	Object getValue() throws EvaluationException;

	/**
	 * 在默认上下文中计算表达式. 如果计算结果与预期结果类型不匹配（并且无法转换为预期结果类型），则将返回异常.
	 * @param desiredResultType the class the caller would like the result to be
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	@Nullable
	<T> T getValue(@Nullable Class<T> desiredResultType) throws EvaluationException;

	/**
	 * 针对指定的根对象计算此表达式.
	 * @param rootObject the root object against which to evaluate the expression
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	@Nullable
	Object getValue(@Nullable Object rootObject) throws EvaluationException;

	/**
	 * 在默认上下文中针对指定的根对象计算表达式.
	 * 如果计算结果与预期结果类型不匹配（并且无法转换为预期结果类型），则将返回异常.
	 * @param rootObject the root object against which to evaluate the expression
	 * @param desiredResultType the class the caller would like the result to be
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	@Nullable
	<T> T getValue(@Nullable Object rootObject, @Nullable Class<T> desiredResultType) throws EvaluationException;

	/**
	 * 在提供的上下文中计算此表达式并返回计算结果.
	 * @param context the context in which to evaluate the expression
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	@Nullable
	Object getValue(EvaluationContext context) throws EvaluationException;

	/**
	 * 在提供的上下文中计算此表达式并返回计算结果，但是将提供的根上下文用作该上下文中指定的任何默认根对象的替代.
	 * @param context the context in which to evaluate the expression
	 * @param rootObject the root object against which to evaluate the expression
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	@Nullable
	Object getValue(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException;

	/**
	 * 在指定的上下文中计算表达式，该表达式可以解析对属性，方法，类型等的引用.
	 * 计算结果的类型应为指定类，否则将引发异常，并且不能将其转换为该类型则抛出异常.
	 * @param context the context in which to evaluate the expression
	 * @param desiredResultType the class the caller would like the result to be
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	@Nullable
	<T> T getValue(EvaluationContext context, @Nullable Class<T> desiredResultType) throws EvaluationException;

	/**
	 * 在指定的上下文中计算表达式，该表达式可以解析对属性，方法，类型等的引用.
	 * 计算结果的类型应为指定类，否则将引发异常.提供的根对象将覆盖提供的上下文上指定的任何默认值.
	 * @param context the context in which to evaluate the expression
	 * @param rootObject the root object against which to evaluate the expression
	 * @param desiredResultType the class the caller would like the result to be
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	@Nullable
	<T> T getValue(EvaluationContext context, @Nullable Object rootObject, @Nullable Class<T> desiredResultType)
			throws EvaluationException;

	/**
	 * 返回使用默认上下文且可以传递给{@link #setValue}方法的最通用类型.
	 * @return the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	@Nullable
	Class<?> getValueType() throws EvaluationException;

	/**
	 * 返回使用默认上下文且可以传递给{@link #setValue(Object, Object)}方法的最通用类型.
	 * @param rootObject the root object against which to evaluate the expression
	 * @return the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	@Nullable
	Class<?> getValueType(@Nullable Object rootObject) throws EvaluationException;

	/**
	 * 返回使用给定上下文且可以传递给{@link #setValue(EvaluationContext, Object)}方法的最通用类型.
	 * @param context the context in which to evaluate the expression
	 * @return the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	@Nullable
	Class<?> getValueType(EvaluationContext context) throws EvaluationException;

	/**
	 * 返回使用给定上下文且可以传递给{@link #setValue(EvaluationContext, Object, Object)}方法的最通用类型.
	 * 提供的根对象将覆盖上下文中指定的任何对象.
	 * @param context the context in which to evaluate the expression
	 * @param rootObject the root object against which to evaluate the expression
	 * @return the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	@Nullable
	Class<?> getValueType(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException;

	/**
	 * 返回使用默认上下文且可以传递给{@link #setValue}方法的最通用类型.
	 * @return a type descriptor for values that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	@Nullable
	TypeDescriptor getValueTypeDescriptor() throws EvaluationException;

	/**
	 * Return the most general type that can be passed to the
	 * {@link #setValue(Object, Object)} method using the default context.
	 * @param rootObject the root object against which to evaluate the expression
	 * @return a type descriptor for values that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	@Nullable
	TypeDescriptor getValueTypeDescriptor(@Nullable Object rootObject) throws EvaluationException;

	/**
	 * Return the most general type that can be passed to the
	 * {@link #setValue(EvaluationContext, Object)} method for the given context.
	 * @param context the context in which to evaluate the expression
	 * @return a type descriptor for values that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	@Nullable
	TypeDescriptor getValueTypeDescriptor(EvaluationContext context) throws EvaluationException;

	/**
	 * Return the most general type that can be passed to the
	 * {@link #setValue(EvaluationContext, Object, Object)} method for the given
	 * context. The supplied root object overrides any specified in the context.
	 * @param context the context in which to evaluate the expression
	 * @param rootObject the root object against which to evaluate the expression
	 * @return a type descriptor for values that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	@Nullable
	TypeDescriptor getValueTypeDescriptor(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException;

	/**
	 * 确定是否可以写入表达式，即可以调用setValue().
	 * @param rootObject the root object against which to evaluate the expression
	 * @return {@code true} if the expression is writable; {@code false} otherwise
	 * @throws EvaluationException if there is a problem determining if it is writable
	 */
	boolean isWritable(@Nullable Object rootObject) throws EvaluationException;

	/**
	 * 确定是否可以写入表达式，即可以调用setValue().
	 * @param context the context in which the expression should be checked
	 * @return {@code true} if the expression is writable; {@code false} otherwise
	 * @throws EvaluationException if there is a problem determining if it is writable
	 */
	boolean isWritable(EvaluationContext context) throws EvaluationException;

	/**
	 * 确定是否可以写入表达式，即可以调用setValue(). 提供的根对象将覆盖上下文中指定的任何对象.
	 * @param context the context in which the expression should be checked
	 * @param rootObject the root object against which to evaluate the expression
	 * @return {@code true} if the expression is writable; {@code false} otherwise
	 * @throws EvaluationException if there is a problem determining if it is writable
	 */
	boolean isWritable(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException;

	/**
	 * 在提供的上下文中将此表达式设置为提供的值.
	 * @param rootObject the root object against which to evaluate the expression
	 * @param value the new value
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	void setValue(@Nullable Object rootObject, @Nullable Object value) throws EvaluationException;

	/**
	 * 在提供的上下文中将此表达式设置为提供的值.
	 * @param context the context in which to set the value of the expression
	 * @param value the new value
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	void setValue(EvaluationContext context, @Nullable Object value) throws EvaluationException;

	/**
	 * 在提供的上下文中将此表达式设置为提供的值. 提供的根对象将覆盖上下文中指定的任何对象.
	 * @param context the context in which to set the value of the expression
	 * @param rootObject the root object against which to evaluate the expression
	 * @param value the new value
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	void setValue(EvaluationContext context, @Nullable Object rootObject, @Nullable Object value) throws EvaluationException;

}
