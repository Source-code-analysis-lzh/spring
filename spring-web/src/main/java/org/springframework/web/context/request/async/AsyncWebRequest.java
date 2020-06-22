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

package org.springframework.web.context.request.async;

import java.util.function.Consumer;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * 用异步请求处理方法扩展{@link NativeWebRequest}的接口。
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public interface AsyncWebRequest extends NativeWebRequest {

	/**
	 * 设置完成并发处理所需的时间。 当并发处理正在进行时，即{@link #isAsyncStarted()}为{@code true}时，
	 * 不应设置此属性。
	 * @param timeout amount of time in milliseconds; {@code null} means no
	 * 	timeout, i.e. rely on the default timeout of the container.
	 */
	void setTimeout(@Nullable Long timeout);

	/**
	 * 添加一个处理器以在并发处理超时时调用。
	 */
	void addTimeoutHandler(Runnable runnable);

	/**
	 * 添加处理器以在并发处理请求发生错误时调用。
	 * @since 5.0
	 */
	void addErrorHandler(Consumer<Throwable> exceptionHandler);

	/**
	 * 添加处理器以在请求处理完成时调用。
	 */
	void addCompletionHandler(Runnable runnable);

	/**
	 * 标记异步请求处理的开始，以便在主处理线程退出时，响应保持打开状态，以便在另一个线程中进行进一步处理。
	 * @throws IllegalStateException if async processing has completed or is not supported
	 */
	void startAsync();

	/**
	 * 调用{@link #startAsync()}之后，请求是否处于异步模式。 
	 * 如果异步处理从未开始，已经完成，或者调度了请求以进行进一步处理，则返回"false"。
	 */
	boolean isAsyncStarted();

	/**
	 * 将请求分派到容器，以便在应用程序线程中并发执行后恢复处理。
	 */
	void dispatch();

	/**
	 * 异步处理是否已完成。
	 */
	boolean isAsyncComplete();

}
