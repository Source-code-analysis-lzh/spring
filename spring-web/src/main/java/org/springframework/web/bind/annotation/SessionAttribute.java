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
 * 将方法参数绑定到会话属性的注释。
 *
 * <p>主要动机是通过可选/必需检查以及转换为目标方法参数类型的方法，
 * 提供对现有或永久会话属性（例如，用户身份验证对象）的便捷访问。
 *
 * <p>对于需要添加或删除会话属性的用例，请考虑将{@code org.springframework.web.context.request.WebRequest}
 * 或{@code javax.servlet.http.HttpSession}注入到控制器方法中。
 *
 * <p>要在会话中临时存储模型属性作为控制器工作流的一部分，请考虑改用{@link SessionAttributes}。
 *
 * @author Rossen Stoyanchev
 * @since 4.3
 * @see RequestMapping
 * @see SessionAttributes
 * @see RequestAttribute
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionAttribute {

	/**
	 * Alias for {@link #name}.
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * 要绑定到的会话属性的名称。
	 * <p>从方法参数名称推断默认名称。
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * 会话属性是否为必需。
	 * <p>默认值为{@code true}，如果会话中缺少属性或没有会话，则会引发异常。 
	 * 如果该属性不存在您希望使用{@code null}或Java 8 {@code java.util.Optional}，
	 * 请将其切换为{@code false}。
	 */
	boolean required() default true;

}
