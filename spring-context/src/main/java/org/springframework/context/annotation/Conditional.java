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

package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示仅当所有{@linkplain #value 指定条件}都匹配时，组件才有资格注册。
 *
 * <p>条件是可以在要注册Bean定义之前以编程方式确定的任何状态（有关详细信息，请参见{@link Condition}）。
 *
 * <p>{@code @Conditional}注释可以通过以下任何一种方式使用：
 * <ul>
 * <li>as a type-level annotation on any class directly or indirectly annotated with
 * {@code @Component}, including {@link Configuration @Configuration} classes</li>
 * <li>as a meta-annotation, for the purpose of composing custom stereotype
 * annotations</li>
 * <li>as a method-level annotation on any {@link Bean @Bean} method</li>
 * </ul>
 *
 * <p>如果{@code @Configuration}类标记有{@code @Conditional}，
 * 则与该类关联的所有{@code @Bean}方法，{@link Import @Import}注释和
 * {@link ComponentScan @ComponentScan}注释将受条件限制。
 *
 * <p>注意：不支持{@code @Conditional}注释的继承。 不会考虑超类或重写方法中的任何条件。 
 * 为了强制执行这些语义，{@code @Conditional}本身未声明为{@link java.lang.annotation.Inherited @Inherited}；
 * 此外，任何使用{@code @Conditional}进行元注释的自定义组合注释都不得声明为{@code @Inherited}。
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 4.0
 * @see Condition
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {

	/**
	 * All {@link Condition Conditions} that must {@linkplain Condition#matches match}
	 * in order for the component to be registered.
	 * 必须匹配所有条件才能注册组件。
	 */
	Class<? extends Condition>[] value();

}
