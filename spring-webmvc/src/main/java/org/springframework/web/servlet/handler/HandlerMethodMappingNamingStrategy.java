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

package org.springframework.web.servlet.handler;

import org.springframework.web.method.HandlerMethod;

/**
 * 为处理器方法的映射分配名称的策略。
 *
 * <p>The strategy can be configured on
 * <p>可以在{@link org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
 * AbstractHandlerMethodMapping}上配置该策略。 它用于为每个已注册处理器方法的映射分配名称。 
 * 然后可以通过{@link org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#getHandlerMethodsForMappingName(String)
 * AbstractHandlerMethodMapping#getHandlerMethodsForMappingName}查询名称。
 *
 * <p>应用程序可以借助静态方法{@link org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder#fromMappingName(String)
 * MvcUriComponentsBuilder#fromMappingName}或在JSP中通过Spring标签库注册的"mvcUrl"
 * 函数按名称为控制器方法建立URL。
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 * @param <T> the mapping type
 */
@FunctionalInterface
public interface HandlerMethodMappingNamingStrategy<T> {

	/**
	 * Determine the name for the given HandlerMethod and mapping.
	 * @param handlerMethod the handler method
	 * @param mapping the mapping
	 * @return the name
	 */
	String getName(HandlerMethod handlerMethod, T mapping);

}
