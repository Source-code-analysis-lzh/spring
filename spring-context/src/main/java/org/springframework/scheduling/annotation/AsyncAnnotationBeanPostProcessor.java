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

package org.springframework.scheduling.annotation;

import java.lang.annotation.Annotation;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;

/**
 * Bean后处理器，通过扫描在类或方法级别上带有{@link Async}注释的任何Bean，
 * 将相应的{@link AsyncAnnotationAdvisor}添加到公开的代理（现有的AOP代理或实现目标的所有接口的新生成的代理）中，
 * 这样的Bean将具有异步调用行为。
 *
 * <p>可以提供负责异步执行的{@link TaskExecutor}和指示应异步调用方法的注释类型。 
 * 如果未指定注释类型，则此后处理器将检测Spring的{@link Async @Async}注释以及EJB 3.1 {@code javax.ejb.Asynchronous}注释。
 *
 * <p>对于具有{@code void}返回类型的方法，调用者无法访问在异步方法调用期间引发的任何异常。 
 * 可以指定{@link AsyncUncaughtExceptionHandler}来处理这些情况。
 *
 * <p>注意：默认情况下，底层异步advisor先于现有advisors应用，以便在调用链中尽早切换到异步执行。
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.0
 * @see Async
 * @see AsyncAnnotationAdvisor
 * @see #setBeforeExistingAdvisors
 * @see ScheduledAnnotationBeanPostProcessor
 */
@SuppressWarnings("serial")
public class AsyncAnnotationBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {

	/**
	 * The default name of the {@link TaskExecutor} bean to pick up: "taskExecutor".
	 * <p>Note that the initial lookup happens by type; this is just the fallback
	 * in case of multiple executor beans found in the context.
	 * @since 4.2
	 * @see AnnotationAsyncExecutionInterceptor#DEFAULT_TASK_EXECUTOR_BEAN_NAME
	 */
	public static final String DEFAULT_TASK_EXECUTOR_BEAN_NAME =
			AnnotationAsyncExecutionInterceptor.DEFAULT_TASK_EXECUTOR_BEAN_NAME;


	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private Supplier<Executor> executor;

	@Nullable
	private Supplier<AsyncUncaughtExceptionHandler> exceptionHandler;

	@Nullable
	private Class<? extends Annotation> asyncAnnotationType;



	public AsyncAnnotationBeanPostProcessor() {
		setBeforeExistingAdvisors(true);
	}


	/**
	 * Configure this post-processor with the given executor and exception handler suppliers,
	 * applying the corresponding default if a supplier is not resolvable.
	 * @since 5.1
	 */
	public void configure(
			@Nullable Supplier<Executor> executor, @Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {

		this.executor = executor;
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * Set the {@link Executor} to use when invoking methods asynchronously.
	 * <p>If not specified, default executor resolution will apply: searching for a
	 * unique {@link TaskExecutor} bean in the context, or for an {@link Executor}
	 * bean named "taskExecutor" otherwise. If neither of the two is resolvable,
	 * a local default executor will be created within the interceptor.
	 * @see AnnotationAsyncExecutionInterceptor#getDefaultExecutor(BeanFactory)
	 * @see #DEFAULT_TASK_EXECUTOR_BEAN_NAME
	 */
	public void setExecutor(Executor executor) {
		this.executor = SingletonSupplier.of(executor);
	}

	/**
	 * Set the {@link AsyncUncaughtExceptionHandler} to use to handle uncaught
	 * exceptions thrown by asynchronous method executions.
	 * @since 4.1
	 */
	public void setExceptionHandler(AsyncUncaughtExceptionHandler exceptionHandler) {
		this.exceptionHandler = SingletonSupplier.of(exceptionHandler);
	}

	/**
	 * Set the 'async' annotation type to be detected at either class or method
	 * level. By default, both the {@link Async} annotation and the EJB 3.1
	 * {@code javax.ejb.Asynchronous} annotation will be detected.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate that a method (or all
	 * methods of a given class) should be invoked asynchronously.
	 * @param asyncAnnotationType the desired annotation type
	 */
	public void setAsyncAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
		Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
		this.asyncAnnotationType = asyncAnnotationType;
	}


	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);

		AsyncAnnotationAdvisor advisor = new AsyncAnnotationAdvisor(this.executor, this.exceptionHandler);
		if (this.asyncAnnotationType != null) {
			advisor.setAsyncAnnotationType(this.asyncAnnotationType);
		}
		advisor.setBeanFactory(beanFactory);
		this.advisor = advisor;
	}

}
