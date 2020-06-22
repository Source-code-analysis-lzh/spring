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
import org.springframework.ui.Model;

/**
 * 将方法参数或方法返回值绑定到已命名模型属性的注释。以便暴露给web视图。 
 * 支持带有{@link RequestMapping @RequestMapping}方法的控制器类。
 *
 * <p>通过注释{@link RequestMapping @RequestMapping}方法的相应参数，
 * 使用特定的属性名称将命令对象(请求传入的对象)公开到Web视图。
 *
 * <p>也可以用于通过使用{@link RequestMapping @RequestMapping}方法
 * 注释控制器类中的访问器方法(就是普通的方法)来将引用数据公开到Web视图。 
 * 此类访问器方法可以具有{@link RequestMapping @RequestMapping}方法支持的任何参数，
 * 从而返回要公开的模型属性值。
 *
 * <p>但是请注意，当请求处理导致{@code Exception}时，引用数据和所有其它模型内容对Web视图不可用，
 * 因为可以随时引发异常，从而使模型的内容不可靠。 因此，{@link ExceptionHandler @ExceptionHandler}
 * 方法不提供对{@link Model}参数的访问。
 *
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 2.5
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelAttribute {

	/**
	 * Alias for {@link #name}.
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * 要绑定到的模型属性的名称。
	 * <p>默认模型属性名称是根据非限定类名称从声明的属性类型（即方法参数类型或方法返回类型）推断出来的。
	 * 类"mypackage.OrderAddress"为"orderAddress"，或"List&lt;mypackage.OrderAddress&gt;"
	 * 为"orderAddressList"。
	 * @since 4.3
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * 允许声明直接在{@code @ModelAttribute}方法参数或{@code @ModelAttribute}方法返回的属性上禁用的数据绑定，
	 * 这两种方法都将阻止该属性的数据绑定。
	 * <p>默认情况下，此选项设置为{@code true}，在这种情况下将应用数据绑定。 
	 * 将此设置为{@code false}可禁用数据绑定。
	 * @since 4.3
	 */
	boolean binding() default true;

}
