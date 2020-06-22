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

package org.springframework.web.bind.annotation;

import java.beans.PropertyEditor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

/**
 * 可用于将"multipart/form-data"请求的part与方法参数关联的注释。
 *
 * <p>支持的方法参数类型包括和Spring的MultipartResolver结合的{@link MultipartFile}，
 * 以及和Servlet 3.0 multipart请求结合的{@code javax.servlet.http.Part}，
 * 或者通过HttpMessageConverter参考请求部分的'Content-Type'标头转换为的其它任何方法参数。
 * 这类似于@RequestBody基于非分段常规请求的内容来解析参数的操作。
 *
 * <p>请注意，@{@link RequestParam}注释也可用于将"multipart/form-data"请求的一部分
 * 与支持相同方法参数类型的方法参数相关联。 主要区别在于，当方法参数不是字符串或原始
 * {@code MultipartFile} / {@code Part}时，{@code @RequestParam}依赖于通过注册的
 * {@link Converter} or {@link PropertyEditor}进行的类型转换，而{@link RequestPart}
 * 则依赖于{@link HttpMessageConverter HttpMessageConverters}并参考请求部分(part)的'Content-Type'标头 。 
 * {@link RequestParam}可能与名称-值表单字段一起使用，而{@link RequestPart}可能与包含更复杂内容的部分(part)
 * 一起使用，例如 JSON，XML。
 *
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @since 3.1
 * @see RequestParam
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestPart {

	/**
	 * Alias for {@link #name}.
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * The name of the part in the {@code "multipart/form-data"} request to bind to.
	 * @since 4.2
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * Whether the part is required.
	 * <p>Defaults to {@code true}, leading to an exception being thrown
	 * if the part is missing in the request. Switch this to
	 * {@code false} if you prefer a {@code null} value if the part is
	 * not present in the request.
	 */
	boolean required() default true;

}
