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

import org.springframework.core.annotation.AliasFor;

/**
 * 指示方法参数应绑定到URI模板变量的注释。 支持带注释的{@link RequestMapping}处理器方法。
 *
 * <p>如果方法参数为{@link java.util.Map Map&lt;String, String&gt;}，
 * 则将使用所有路径变量名称和值填充map。
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see RequestMapping
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {

	/**
	 * Alias for {@link #name}.
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * 要绑定的路径变量的名称。
	 * @since 4.3.3
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * 是否需要path变量。
	 * <p>默认为{@code true}，如果传入请求中缺少path变量，则会引发异常。 
	 * 如果您希望使用{@code null}或Java 8 {@code java.util.Optional}，请将其切换为{@code false}。
	 * 例如 在用于不同请求的{@code ModelAttribute}方法上。
	 * @since 4.3.3
	 */
	boolean required() default true;

}
