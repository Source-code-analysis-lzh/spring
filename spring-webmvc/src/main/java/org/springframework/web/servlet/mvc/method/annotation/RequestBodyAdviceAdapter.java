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
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;

/**
 * 使用默认方法实现来实现{@link org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
 * ResponseBodyAdvice}的便捷起点。
 *
 * <p>需要子类来实现{@link #supports}，当advice的应用时返回true。
 *
 * @author Rossen Stoyanchev
 * @since 4.2
 */
public abstract class RequestBodyAdviceAdapter implements RequestBodyAdvice {

	/**
	 * The default implementation returns the InputMessage that was passed in.
	 */
	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
			throws IOException {

		return inputMessage;
	}

	/**
	 * The default implementation returns the body that was passed in.
	 */
	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

		return body;
	}

	/**
	 * The default implementation returns the body that was passed in.
	 */
	@Override
	@Nullable
	public Object handleEmptyBody(@Nullable Object body, HttpInputMessage inputMessage,
			MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {

		return body;
	}

}
