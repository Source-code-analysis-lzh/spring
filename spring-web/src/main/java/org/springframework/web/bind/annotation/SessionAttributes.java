/*
 * Copyright 2002-2015 the original author or authors.
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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * 指示特定处理器使用的会话属性的注释。
 *
 * <p>这通常将列出应透明地存储在会话或某些会话存储中的模型属性名称，以用作跨请求使用。 
 * 在类型级别声明，应用于带注释的处理器类所基于的模型属性。
 *
 * <p>注意：使用此注解指示的会话属性对应于特定处理器的模型属性，并透明地存储在会话中。 
 * 一旦处理器指示其会话完成，这些属性将被删除。 因此，将此功能用于这样的会话属性，
 * 这些属性应该在特定处理器的会话过程中临时存储在会话中。
 *
 * <p>对于永久会话属性，例如 用户身份验证对象，请改用传统的{@code session.setAttribute}方法。 
 * 或者，考虑使用通用{@link org.springframework.web.context.request.WebRequest}接口的属性管理功能。
 *
 * <p>注意：使用控制器接口时（例如，用于AOP代理），请确保将所有映射注释
 * （例如{@code @RequestMapping}和{@code @SessionAttributes}）一致地放在控制器接口上，
 * 而不是在实现类上。
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 2.5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SessionAttributes {

	/**
	 * Alias for {@link #names}.
	 */
	@AliasFor("names")
	String[] value() default {};

	/**
	 * 模型中应存储在会话或某些会话存储中的会话属性的名称。
	 * <p>注意：这表示模型属性名称。 会话属性名称可能与模型属性名称匹配，也可能不匹配。 
	 * 因此，应用程序不应依赖于会话属性名称，而应仅对模型进行操作。
	 * @since 4.2
	 */
	@AliasFor("value")
	String[] names() default {};

	/**
	 * 模型中应存储在会话或某些会话存储中的会话属性的类型。
	 * <p>不管属性名称如何，这些类型的所有模型属性都将存储在会话中。
	 */
	Class<?>[] types() default {};

}
