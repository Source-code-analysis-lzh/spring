/*
 * Copyright 2002-2017 the original author or authors.
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

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.lang.Nullable;

/**
 * 由@{@link org.springframework.context.annotation.Configuration}和@EnableAsync注释的实现类接口，
 * 该接口实现类希望自定义处理异步方法调用时使用的{@link Executor}实例，
 * 或用于处理从具有{@code void}返回类型的异步方法引发的异常的{@link AsyncUncaughtExceptionHandler}实例。
 *
 * <p>如果只需要自定义一个元素，请考虑使用{@link AsyncConfigurerSupport}为这两种方法提供默认实现。 
 * 此外，如果将来引入新的自定义选项，将确保该接口的向后兼容性。
 *
 * <p>See @{@link EnableAsync} for usage examples.
 *
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 3.1
 * @see AbstractAsyncConfiguration
 * @see EnableAsync
 * @see AsyncConfigurerSupport
 */
public interface AsyncConfigurer {

	/**
	 * The {@link Executor} instance to be used when processing async
	 * method invocations.
	 */
	@Nullable
	default Executor getAsyncExecutor() {
		return null;
	}

	/**
	 * The {@link AsyncUncaughtExceptionHandler} instance to be used
	 * when an exception is thrown during an asynchronous method execution
	 * with {@code void} return type.
	 */
	@Nullable
	default AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return null;
	}

}
