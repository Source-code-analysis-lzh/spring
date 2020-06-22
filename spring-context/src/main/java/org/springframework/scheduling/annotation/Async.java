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

package org.springframework.scheduling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将方法标记为异步执行候选方法的注释。 也可以在类型级别使用，在这种情况下，类型的所有方法都被视为异步方法。 
 * 但是请注意，{@link org.springframework.context.annotation.Configuration @Configuration}
 * 类中声明的方法不支持{@code @Async}。
 *
 * <p>就目标方法签名而言，支持任何参数类型。 但是，返回类型被限制为{@code void}或{@link java.util.concurrent.Future}。 
 * 在后一种情况下，您可以声明更具体的{@link org.springframework.util.concurrent.ListenableFuture}
 * 或{@link java.util.concurrent.CompletableFuture}类型，这些类型允许与异步任务进行更丰富的交互，
 * 并通过进一步的处理步骤立即进行合成。
 *
 * <p>从代理返回的{@code Future}句柄将是实际的异步{@code Future}，可用于跟踪异步方法执行的结果。 
 * 但是，由于目标方法需要实现相同的签名，因此它必须返回一个临时的{@code Future}句柄，该句柄仅将以下对象传递返回： 
 * Spring的{@link AsyncResult}，EJB 3.1的{@link javax.ejb.AsyncResult}
 * 或{@link java.util.concurrent.CompletableFuture#completedFuture(Object)}。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0
 * @see AnnotationAsyncExecutionInterceptor
 * @see AsyncAnnotationAdvisor
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {

	/**
	 * A qualifier value for the specified asynchronous operation(s).
	 * <p>May be used to determine the target executor to be used when executing
	 * the asynchronous operation(s), matching the qualifier value (or the bean
	 * name) of a specific {@link java.util.concurrent.Executor Executor} or
	 * {@link org.springframework.core.task.TaskExecutor TaskExecutor}
	 * bean definition.
	 * <p>When specified on a class-level {@code @Async} annotation, indicates that the
	 * given executor should be used for all methods within the class. Method-level use
	 * of {@code Async#value} always overrides any value set at the class level.
	 * @since 3.1.2
	 */
	String value() default "";

}
