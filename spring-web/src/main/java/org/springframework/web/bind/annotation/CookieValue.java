/*
 * Copyright 2002-2016 the original author or authors.
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

/**
 * 指示方法参数应绑定到HTTP cookie的注释。
 *
 * <p>方法参数可以声明为{@link javax.servlet.http.Cookie}类型或Cookie值类型（字符串，整数等）。
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see RequestMapping
 * @see RequestParam
 * @see RequestHeader
 * @see org.springframework.web.bind.annotation.RequestMapping
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookieValue {

	/**
	 * Alias for {@link #name}.
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * 要绑定的cookie的名称。
	 * @since 4.2
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * cookie是否是必需的。
	 * <p>默认为{@code true}，如果请求中缺少该cookie值，则会引发异常。 
	 * 如果在请求中该cookie的值为{@code null}，则希望将其设置为{@code false}。
	 * <p>或者，提供一个{@link #defaultValue}，它将该标志隐式设置为{@code false}。
	 */
	boolean required() default true;

	/**
	 * 用作后备的默认值。
	 * <p>提供默认值会隐式将{@link #required}设置为{@code false}。
	 */
	String defaultValue() default ValueConstants.DEFAULT_NONE;

}
