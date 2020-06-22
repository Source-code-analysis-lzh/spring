/*
 * Copyright 2002-2012 the original author or authors.
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

import org.springframework.beans.PropertyAccessException;

/**
 * 处理{@code DataBinder}缺少的字段错误，以及将{@code PropertyAccessException}转换为{@code FieldError}的策略.
 *
 * <p>错误处理器是可插入的，因此您可以根据需要不同地对待错误. 针对典型需求提供了默认实现.
 *
 * <p>注意：从Spring 2.0开始，此接口在给定的BindingResult上运行，
 * 以与任何绑定策略（bean属性，直接字段访问等）兼容.
 * 它仍然可以接收BindException作为参数（因为BindException也实现了BindingResult接口），但是不再直接对其进行操作.
 *
 * @author Alef Arendsen
 * @author Juergen Hoeller
 * @since 1.2
 * @see DataBinder#setBindingErrorProcessor
 * @see DefaultBindingErrorProcessor
 * @see BindingResult
 * @see BindException
 */
public interface BindingErrorProcessor {

	/**
	 * 将缺少的字段错误应用于给定的BindException.
	 * <p>通常，会为缺少的必填字段创建字段错误.
	 * @param missingField the field that was missing during binding
	 * @param bindingResult the errors object to add the error(s) to.
	 * You can add more than just one error or maybe even ignore it.
	 * The {@code BindingResult} object features convenience utils such as
	 * a {@code resolveMessageCodes} method to resolve an error code.
	 * @see BeanPropertyBindingResult#addError
	 * @see BeanPropertyBindingResult#resolveMessageCodes
	 */
	void processMissingFieldError(String missingField, BindingResult bindingResult);

	/**
	 * 将给定的{@code PropertyAccessException}转换为在给定的Errors实例上注册的适当错误.
	 * <p>请注意，有两种错误类型可用：{@code FieldError}和{@code ObjectError}.
	 * 通常，将创建字段错误，但是在某些情况下，可能需要创建一个全局{@code ObjectError}.
	 * @param ex the {@code PropertyAccessException} to translate
	 * @param bindingResult the errors object to add the error(s) to.
	 * You can add more than just one error or maybe even ignore it.
	 * The {@code BindingResult} object features convenience utils such as
	 * a {@code resolveMessageCodes} method to resolve an error code.
	 * @see Errors
	 * @see FieldError
	 * @see ObjectError
	 * @see MessageCodesResolver
	 * @see BeanPropertyBindingResult#addError
	 * @see BeanPropertyBindingResult#resolveMessageCodes
	 */
	void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult);

}
