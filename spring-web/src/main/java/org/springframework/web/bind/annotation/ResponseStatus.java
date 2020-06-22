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

package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;

/**
 * 用应返回的状态{@link #code}和{@link #reason}标记方法或异常类。
 *
 * <p>调用处理器方法时，状态代码将应用于HTTP响应，并覆盖通过其它方式(如：
 * {@code ResponseEntity} or {@code "redirect:"})。
 *
 * <p>警告：在异常类上使用此注释时，或在设置此注释的{@code reason}属性时，
 * 将使用{@code HttpServletResponse.sendError}方法。
 *
 * <p>使用{@code HttpServletResponse.sendError}，响应被认为是完整的，不应再进一步写入。
 * 此外，Servlet容器通常将编写HTML错误页面，因此使用{@code reason}不适合REST API。
 * 在这种情况下，最好使用{@link org.springframework.http.ResponseEntity}作为返回类型，
 * 并完全避免使用{@code @ResponseStatus}。
 *
 * <p>请注意，控制器类也可以使用{@code @ResponseStatus}进行注释，然后由所有{@code @RequestMapping}方法继承。
 *
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @since 3.0
 * @see org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
 * @see javax.servlet.http.HttpServletResponse#sendError(int, String)
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseStatus {

	/**
	 * Alias for {@link #code}.
	 */
	@AliasFor("code")
	HttpStatus value() default HttpStatus.INTERNAL_SERVER_ERROR;

	/**
	 * 用于响应的状态码。
	 * <p>默认值为{@link HttpStatus#INTERNAL_SERVER_ERROR}，通常应将其更改为更合适的值。
	 * @since 4.2
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int)
	 * @see javax.servlet.http.HttpServletResponse#sendError(int)
	 */
	@AliasFor("value")
	HttpStatus code() default HttpStatus.INTERNAL_SERVER_ERROR;

	/**
	 * The <em>reason</em> to be used for the response.
	 * @see javax.servlet.http.HttpServletResponse#sendError(int, String)
	 */
	String reason() default "";

}
