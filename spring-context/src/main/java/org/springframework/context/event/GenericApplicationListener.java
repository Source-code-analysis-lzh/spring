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

package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * 标准{@link ApplicationListener}接口的扩展变体，进一步公开了元数据，例如受支持的事件和源类型.
 *
 * <p>从Spring Framework 4.2开始，此接口取代了基于类的{@link SmartApplicationListener}
 * 并全面处理了泛型事件类型.
 *
 * @author Stephane Nicoll
 * @since 4.2
 * @see SmartApplicationListener
 * @see GenericApplicationListenerAdapter
 */
public interface GenericApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {

	/**
	 * 确定此侦听器是否实际上支持给定的事件类型.
	 * @param eventType the event type (never {@code null})
	 */
	boolean supportsEventType(ResolvableType eventType);

	/**
	 * 确定此侦听器是否实际上支持给定的源类型.
	 * <p>默认实现始终返回{@code true}.
	 * @param sourceType the source type, or {@code null} if no source
	 */
	default boolean supportsSourceType(@Nullable Class<?> sourceType) {
		return true;
	}

	/**
	 * 在同一事件的一组侦听器中确定此侦听器的顺序.
	 * <p>默认实现返回{@link #LOWEST_PRECEDENCE}.
	 */
	@Override
	default int getOrder() {
		return LOWEST_PRECEDENCE;
	}

}
