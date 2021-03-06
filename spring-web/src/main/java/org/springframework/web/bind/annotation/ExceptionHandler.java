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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于在特定处理器类和/或处理器方法中处理异常的注释。
 *
 * <p>使用此注释的处理器方法具有非常灵活的签名。 它们可以具有任意类型的以下类型的参数：
 * <ul>
 * <li>An exception argument: declared as a general Exception or as a more
 * specific exception. This also serves as a mapping hint if the annotation
 * itself does not narrow the exception types through its {@link #value()}.
 * <li>Request and/or response objects (typically from the Servlet API).
 * You may choose any specific request/response type, e.g.
 * {@link javax.servlet.ServletRequest} / {@link javax.servlet.http.HttpServletRequest}.
 * <li>Session object: typically {@link javax.servlet.http.HttpSession}.
 * An argument of this type will enforce the presence of a corresponding session.
 * As a consequence, such an argument will never be {@code null}.
 * <i>Note that session access may not be thread-safe, in particular in a
 * Servlet environment: Consider switching the
 * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#setSynchronizeOnSession
 * "synchronizeOnSession"} flag to "true" if multiple requests are allowed to
 * access a session concurrently.</i>
 * <li>{@link org.springframework.web.context.request.WebRequest} or
 * {@link org.springframework.web.context.request.NativeWebRequest}.
 * Allows for generic request parameter access as well as request/session
 * attribute access, without ties to the native Servlet API.
 * <li>{@link java.util.Locale} for the current request locale
 * (determined by the most specific locale resolver available,
 * i.e. the configured {@link org.springframework.web.servlet.LocaleResolver}
 * in a Servlet environment).
 * <li>{@link java.io.InputStream} / {@link java.io.Reader} for access
 * to the request's content. This will be the raw InputStream/Reader as
 * exposed by the Servlet API.
 * <li>{@link java.io.OutputStream} / {@link java.io.Writer} for generating
 * the response's content. This will be the raw OutputStream/Writer as
 * exposed by the Servlet API.
 * <li>{@link org.springframework.ui.Model} as an alternative to returning
 * a model map from the handler method. Note that the provided model is not
 * pre-populated with regular model attributes and therefore always empty,
 * as a convenience for preparing the model for an exception-specific view.
 * </ul>
 *
 * <p>处理器方法支持以下返回类型：
 * <ul>
 * <li>A {@code ModelAndView} object (from Servlet MVC).
 * <li>A {@link org.springframework.ui.Model} object, with the view name implicitly
 * determined through a {@link org.springframework.web.servlet.RequestToViewNameTranslator}.
 * <li>A {@link java.util.Map} object for exposing a model,
 * with the view name implicitly determined through a
 * {@link org.springframework.web.servlet.RequestToViewNameTranslator}.
 * <li>A {@link org.springframework.web.servlet.View} object.
 * <li>A {@link String} value which is interpreted as view name.
 * <li>{@link ResponseBody @ResponseBody} annotated methods (Servlet-only)
 * to set the response content. The return value will be converted to the
 * response stream using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message converters}.
 * <li>An {@link org.springframework.http.HttpEntity HttpEntity&lt;?&gt;} or
 * {@link org.springframework.http.ResponseEntity ResponseEntity&lt;?&gt;} object
 * (Servlet-only) to set response headers and content. The ResponseEntity body
 * will be converted and written to the response stream using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message converters}.
 * <li>{@code void} if the method handles the response itself (by
 * writing the response content directly, declaring an argument of type
 * {@link javax.servlet.ServletResponse} / {@link javax.servlet.http.HttpServletResponse}
 * for that purpose) or if the view name is supposed to be implicitly determined
 * through a {@link org.springframework.web.servlet.RequestToViewNameTranslator}
 * (not declaring a response argument in the handler method signature).
 * </ul>
 *
 * <p>您可以将{@code ExceptionHandler}注释与{@link ResponseStatus @ResponseStatus}
 * 结合使用以获得特定的HTTP错误状态。
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.web.context.request.WebRequest
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionHandler {

	/**
	 * 带注释的方法处理的异常。 如果为空，则默认为方法参数列表中列出的任何异常。
	 */
	Class<? extends Throwable>[] value() default {};

}
