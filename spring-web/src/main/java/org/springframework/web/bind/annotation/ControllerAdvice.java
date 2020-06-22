/*
 * Copyright 2002-2019 the original author or authors.
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

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * {@link Component @Component}专门用于声明{@link ExceptionHandler @ExceptionHandler}，
 * {@link InitBinder @InitBinder}或{@link ModelAttribute @ModelAttribute}方法且
 * 在多个{@code @Controller}类之间共享的类。
 *
 * <p>可以将{@code @ControllerAdvice}注释的类显式声明为Spring Bean，或通过类路径扫描自动检测。 
 * 所有此类bean均基于{@link org.springframework.core.Ordered Ordered}语义或
 * {@link org.springframework.core.annotation.Order @Order} / {@link javax.annotation.Priority @Priority}
 * 声明进行排序，其中{@code Ordered}语义优先于{@code @Order} / {@code @Priority}声明。 
 * 然后在运行时按该顺序应用{@code @ControllerAdvice} bean。 但是请注意，实现
 * {@link org.springframework.core.PriorityOrdered PriorityOrdered}的{@code @ControllerAdvice} Bean
 * 的优先级不高于实现{@code Ordered}的{@code @ControllerAdvice} Bean。 另外，
 * 有范围的{@code @ControllerAdvice} Bean不推荐使用{@code Ordered} －例如，如果已将此类Bean配置为请求范围或会话范围的Bean。 
 * 为了处理异常，将在第一个匹配的异常处理程序方法的advice中选择{@code @ExceptionHandler}。 
 * 对于模型属性和数据绑定初始化，{@code @ModelAttribute}和{@code @InitBinder}方法将遵循{@code @ControllerAdvice}的顺序。
 *
 * <p>Note: For {@code @ExceptionHandler} methods, a root exception match will be
 * preferred to just matching a cause of the current exception, among the handler
 * methods of a particular advice bean. However, a cause match on a higher-priority
 * advice will still be preferred over any match (whether root or cause level)
 * on a lower-priority advice bean. As a consequence, please declare your primary
 * root exception mappings on a prioritized advice bean with a corresponding order.
 * <p>注意：对于{@code @ExceptionHandler}方法，在特定 advice bean的处理器方法中，与仅匹配导致当前异常的原因相比，
 * 将首选与根异常匹配。 但是，与优先级较低的建议Bean上的任何匹配项（根源或原因级别）相比，优先级较高的建议上的原因匹配仍然是首选。 
 * 因此，请在具有相应顺序的优先通知bean上声明您的主根异常映射。
 *
 * <p>默认情况下，{@code @ControllerAdvice}中的方法全局应用于所有控制器。 
 * 使用选择器（例如{@link #annotations}，{@link #basePackageClasses}和
 * {@link #basePackages}（或其别名{@link #value}））来定义目标控制器的更窄子集。
 * 如果声明了多个选择器，则将使用布尔OR逻辑，这意味着选定的控制器应至少匹配一个选择器。 
 * 请注意，选择器检查是在运行时执行的，因此添加许多选择器可能会对性能产生负面影响并增加复杂性。
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Sam Brannen
 * @since 3.2
 * @see org.springframework.stereotype.Controller
 * @see RestControllerAdvice
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ControllerAdvice {

	/**
	 * {@link #basePackages}属性的别名。
	 * <p>允许使用更简洁的注释声明 &mdash; 例如，{@code @ControllerAdvice("org.my.pkg")}
	 * 等同于{@code @ControllerAdvice(basePackages = "org.my.pkg")}。
	 * @since 4.0
	 * @see #basePackages
	 */
	@AliasFor("basePackages")
	String[] value() default {};

	/**
	 * 基软件包数组。
	 * <p>将包括属于那些基软件包或其子软件包的控制器，例如 
	 * {@code @ControllerAdvice(basePackages = "org.my.pkg")}或
	 * {@code @ControllerAdvice(basePackages = {"org.my.pkg", "org.my.other.pkg"})}。
	 * <p>{@link #value}是此属性的别名，允许更简洁地使用注释。
	 * <p>还可以考虑将{@link #basePackageClasses}用作基于字符串的包名称的类型安全替代方法。
	 * @since 4.0
	 */
	@AliasFor("value")
	String[] basePackages() default {};

	/**
	 * 类型安全的替代{@link #basePackages}的方法，用于指定要在其中选择控制器的包，
	 * 该包由{@code @ControllerAdvice}带注释的类 advised。
	 * <p>考虑在每个包中创建一个特殊的无操作标记类或接口，该类或接口除了被该属性引用外没有其他用途。
	 * @since 4.0
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * 类数组。
	 * <p>{@code @ControllerAdvice}注释类将advised 分配给至少一种给定类型的控制器。
	 * @since 4.0
	 */
	Class<?>[] assignableTypes() default {};

	/**
	 * 注释类型数组。
	 * <p>{@code @ControllerAdvice}带注释的类将advised 分配给使用至少一种提供的注释类型进行注释的控制器。
	 * <p>考虑创建一个自定义的组合注释，或使用预定义的注释，例如{@link RestController @RestController}。
	 * @since 4.0
	 */
	Class<? extends Annotation>[] annotations() default {};

}
