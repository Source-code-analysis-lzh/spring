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
 * 指示方法参数应绑定到路径段内的名称/值对的注释。 
 * 支持带注释的{@link RequestMapping}处理器方法。
 * 
 * <p>matrixVariable允许我们非常方便地进行多条件组合查询
 *
 * <p>如果方法参数类型为{@link java.util.Map}并指定了矩阵变量名称，
 * 则假定有适当的转换策略可用，则将矩阵变量值转换为{@link java.util.Map}。
 *
 * <p>如果方法参数为{@link java.util.Map Map&lt;String, String&gt;}或
 * {@link org.springframework.util.MultiValueMap MultiValueMap&lt;String, String&gt;}
 * 并且未指定变量名，则将使用所有矩阵变量名和值填充映射。
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 3.2
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MatrixVariable {

	/**
	 * Alias for {@link #name}.
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * 矩阵变量的名称。
	 * @since 4.2
	 * @see #value
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * 如果需要消除歧义，则表示矩阵变量所在的URI路径变量的名称（例如，多个路径段中存在具有相同名称的矩阵变量）。
	 */
	String pathVar() default ValueConstants.DEFAULT_NONE;

	/**
	 * 是否需要矩阵变量。
	 * <p>默认值为{@code true}，如果请求中缺少该变量，则会引发异常。 如果希望缺少变量，则将其切换为{@code false}。
	 * <p>或者，提供一个{@link #defaultValue}，它将该标志隐式设置为{@code false}。
	 */
	boolean required() default true;

	/**
	 * 用作后备的默认值。
	 * <p>Supplying a default value implicitly sets {@link #required} to
	 * {@code false}.
	 */
	String defaultValue() default ValueConstants.DEFAULT_NONE;

}
