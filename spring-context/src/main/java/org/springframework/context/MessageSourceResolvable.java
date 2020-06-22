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

package org.springframework.context;

import org.springframework.lang.Nullable;

/**
 * 适用于{@link MessageSource}中消息解析的对象的接口.
 *
 * <p>Spring自己的验证错误类实现了此接口.
 *
 * @author Juergen Hoeller
 * @see MessageSource#getMessage(MessageSourceResolvable, java.util.Locale)
 * @see org.springframework.validation.ObjectError
 * @see org.springframework.validation.FieldError
 */
@FunctionalInterface
public interface MessageSourceResolvable {

	/**
	 * 按顺序尝试返回用于解析此消息的消息码. 因此，最后一个代码将是默认代码.
	 * @return a String array of codes which are associated with this message
	 */
	@Nullable
	String[] getCodes();

	/**
	 * 返回用于解析此消息的参数数组.
	 * <p>默认实现只是返回{@code null}.
	 * @return an array of objects to be used as parameters to replace
	 * placeholders within the message text
	 * @see java.text.MessageFormat
	 */
	@Nullable
	default Object[] getArguments() {
		return null;
	}

	/**
	 * 返回用于解析此消息的默认消息.
	 * <p>默认实现只是返回{@code null}. 请注意，默认消息可能与主要消息代码({@link #getCodes()})相同，
	 * 后者有效地对此特定消息强制执行{@link org.springframework.context.support.AbstractMessageSource#setUseCodeAsDefaultMessage}
	 * @return the default message, or {@code null} if no default
	 */
	@Nullable
	default String getDefaultMessage() {
		return null;
	}

}
