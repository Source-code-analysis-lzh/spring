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

package org.springframework.context.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.annotation.AliasFor;

/**
 * 将方法标记为应用程序事件的侦听器的注释.
 *
 * <p>如果带注释的方法支持单个事件类型，则该方法可以声明单个参数以反映要侦听的事件类型.
 * 如果带注释的方法支持多种事件类型，则此注释可以使用{@code classes}属性引用一个或多个受支持的事件类型.
 * 有关更多详细信息，请参见{@link #classes} javadoc.
 *
 * <p>事件可以是{@link ApplicationEvent}实例，也可以是任意对象.
 *
 * <p>{@code @EventListener}批注的处理是通过内部{@link EventListenerMethodProcessor} Bean进行的，
 * 该bean在使用Java config时自动注册，或者在使用XML config时通过{@code <context:annotation-config/>}
 * 或{@code <context:component-scan/>}元素手动注册.
 *
 * <p>带注释的方法可能具有非{@code void}返回类型. 完成后，方法调用的结果将作为新事件发送.
 * 如果返回类型是数组或集合，则每个元素将作为一个新的单独事件发送.
 *
 * <p>此注释可用作创建自定义组成的注释的元注释.
 * <em>composed annotations</em>.
 *
 * <h3>异常处理</h3>
 * <p>尽管事件侦听器可以声明它引发了任意异常类型，
 * 但从事件侦听器引发的所有检查的异常都将包装在
 * {@link java.lang.reflect.UndeclaredThrowableException UndeclaredThrowableException}中，
 * 因为事件发布者只能处理运行时异常.
 *
 * <h3>异步监听器</h3>
 * <p>如果希望特定的侦听器异步处理事件，则可以使用Spring的
 * {@link org.springframework.scheduling.annotation.Async @Async}支持，
 * 但是在使用异步事件时请注意以下限制.
 *
 * <ul>
 * <li>如果异步事件侦听器引发异常，则该异常不会传播到调用方.
 * 有关更多详细信息，请参见{@link org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
 * AsyncUncaughtExceptionHandler}.</li>
 * <li>异步事件侦听器方法无法通过返回值来发布后续事件. 如果您需要发布另一个事件作为处理的结果，
 * 请注入{@link org.springframework.context.ApplicationEventPublisher ApplicationEventPublisher}以手动发布事件.</li>
 * </ul>
 *
 * <h3>监听器顺序</h3>
 * <p>也可以定义特定事件的侦听器的调用顺序.
 * 为此，请在该事件侦听器注释旁边添加Spring的公共{@link org.springframework.core.annotation.Order @Order}注释.
 *
 * @author Stephane Nicoll
 * @author Sam Brannen
 * @since 4.2
 * @see EventListenerMethodProcessor
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventListener {

	/**
	 * {@link #classes}的别名.
	 */
	@AliasFor("classes")
	Class<?>[] value() default {};

	/**
	 * 该侦听器处理的事件类.
	 * <p>如果使用单个值指定此属性，则带注释的方法可以选择接受单个参数.
	 * 但是，如果此属性指定了多个值，则带注释的方法不得声明任何参数.
	 */
	@AliasFor("value")
	Class<?>[] classes() default {};

	/**
	 * Spring表达式语言（SpEL）表达式，用于使事件处理按条件处理.
	 * <p>如果表达式的计算结果为布尔值{@code true}或以下字符串之一：{@code "true"}, {@code "on"}，
	 * {@code "yes"}, or {@code "1"}，则将处理该事件.
	 * <p>默认表达式为{@code ""}，表示始终处理事件.
	 * <p>将根据提供以下元数据的专用上下文计算SpEL表达式：
	 * <ul>
	 * <li>{@code #root.event} or {@code event} for references to the
	 * {@link ApplicationEvent}</li>
	 * <li>{@code #root.args} or {@code args} for references to the method
	 * arguments array</li>
	 * <li>Method arguments can be accessed by index. For example, the first
	 * argument can be accessed via {@code #root.args[0]}, {@code args[0]},
	 * {@code #a0}, or {@code #p0}.</li>
	 * <li>Method arguments can be accessed by name (with a preceding hash tag)
	 * if parameter names are available in the compiled byte code.</li>
	 * </ul>
	 */
	String condition() default "";

}
