/*
 * Copyright 2002-2012 the original author or authors.
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
 * 该注释用于初始化{@link org.springframework.web.bind.WebDataBinder}的方法，
 * 该方法将用于填充带注释的处理器方法的命令(command)和表单对象参数。
 *
 * <p>此类init-binder方法支持{@link RequestMapping}支持的所有参数，
 * 但命令/表单对象和相应的验证结果对象除外。 初始化绑定器方法不能具有返回值； 通常将它们声明为{@code void}。
 *
 * <p>典型的参数是将{@link org.springframework.web.bind.WebDataBinder}
 * 与{@link org.springframework.web.context.request.WebRequest}或{@link java.util.Locale}
 * 结合使用，从而允许注册特定于上下文的编辑器。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.web.bind.WebDataBinder
 * @see org.springframework.web.context.request.WebRequest
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InitBinder {

	/**
	 * 应该使用此init-binder方法的命令/表单属性和/或请求参数的名称。
	 * <p>默认值是应用于由注释的处理器类处理的所有命令/表单属性和所有请求参数。 
	 * 在此指定模型属性名称或请求参数名称会将init-binder方法限制为那些特定的属性/参数，
	 * 并且通常将不同的init-binder方法应用于不同的属性或参数组。
	 */
	String[] value() default {};

}
