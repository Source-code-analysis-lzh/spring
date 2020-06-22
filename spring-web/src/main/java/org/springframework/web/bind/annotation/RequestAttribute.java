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
 * 将方法参数绑定到请求属性的注释。
 *
 * <p>主要动机是通过可选/必需检查以及转换为目标方法参数类型的方法，
 * 提供从控制器方法到请求属性的便捷访问。
 *
 * @author Rossen Stoyanchev
 * @since 4.3
 * @see RequestMapping
 * @see SessionAttribute
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestAttribute {

	/**
	 * Alias for {@link #name}.
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * 要绑定到的请求属性的名称。
	 * <p>从方法参数名称推断默认名称。
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * request属性是否为必需。
	 * <p>Defaults to {@code true}, leading to an exception being thrown if
	 * the attribute is missing. Switch this to {@code false} if you prefer
	 * a {@code null} or Java 8 {@code java.util.Optional} if the attribute
	 * doesn't exist.
	 * <p>默认为{@code true}，如果缺少属性，则引发异常。 如果您希望使用{@code null}或
	 * Java 8 {@code java.util.Optional}，请将其切换为{@code false}。
	 * 如果该属性不存在，则将其切换为可选。
	 */
	boolean required() default true;

}
