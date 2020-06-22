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

package org.springframework.web.servlet.mvc.method.annotation;

import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;

/**
 * 一个{@link ResponseBodyAdvice}实现，增加了对在Spring MVC {@code @RequestMapping}或
 * {@code @ExceptionHandler}方法上声明的Jackson的{@code @JsonView}注释的支持。
 *
 * <p>注释中指定的序列化视图将传递到{@link org.springframework.http.converter.json.MappingJackson2HttpMessageConverter}
 * 中，然后使用它来序列化响应正文。
 *
 * <p>请注意，尽管{@code @JsonView}允许指定多个类，但是用作ResponseBodyAdvice的{@code @JsonView}仅支持一个类参数。 
 * 可以考虑使用复合接口。
 * 
 * @author Rossen Stoyanchev
 * @since 4.1
 * @see com.fasterxml.jackson.annotation.JsonView
 * @see com.fasterxml.jackson.databind.ObjectMapper#writerWithView(Class)
 */
public class JsonViewResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return super.supports(returnType, converterType) && returnType.hasMethodAnnotation(JsonView.class);
	}

	@Override
	protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
			MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {

		JsonView ann = returnType.getMethodAnnotation(JsonView.class);
		Assert.state(ann != null, "No JsonView annotation");

		Class<?>[] classes = ann.value();
		if (classes.length != 1) {
			throw new IllegalArgumentException(
					"@JsonView only supported for response body advice with exactly 1 class argument: " + returnType);
		}

		bodyContainer.setSerializationView(classes[0]);
	}

}
