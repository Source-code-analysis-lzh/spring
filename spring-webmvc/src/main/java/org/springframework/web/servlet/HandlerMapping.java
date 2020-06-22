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

package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.Nullable;

/**
 * 由定义请求和处理器对象之间的映射关系的对象实现的接口。
 *
 * <p>此类可以由应用程序开发人员实现，尽管这不是必需的，
 * 因为{@link org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping}
 * 和{@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}
 * 包含在框架中。 如果未在应用程序上下文中注册HandlerMapping bean，则前者是默认值。
 *
 * <p>HandlerMapping实现可以支持映射的拦截器，但不是必须的。 
 * 处理器将始终包装在{@link HandlerExecutionChain}实例中，
 * 并可选地伴随一些{@link HandlerInterceptor}实例。 
 * DispatcherServlet将首先以给定的顺序调用每个HandlerInterceptor的{@code preHandle}方法，
 * 如果所有{@code preHandle}方法都返回{@code true}，则最终调用处理器本身。
 *
 * <p>参数化此映射的功能是此MVC框架的强大而独特的功能。 例如，可以根据会话状态，
 * cookie状态或许多其它变量来编写自定义映射。 没有其他MVC框架似乎同样灵活。
 *
 * <p>注意：实现可以实现{@link org.springframework.core.Ordered}接口，以便能够指定排序顺序，
 * 从而可以让DispatcherServlet指定应用的优先级。 非排序实例被视为最低优先级。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.core.Ordered
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping
 * @see org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 */
public interface HandlerMapping {

	/**
	 * 包含最佳匹配模式的映射处理器在{@link HttpServletRequest}属性中的名称。
	 * @since 4.3.21
	 */
	String BEST_MATCHING_HANDLER_ATTRIBUTE = HandlerMapping.class.getName() + ".bestMatchingHandler";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the path
	 * used to look up the matching handler, which depending on the configured
	 * {@link org.springframework.web.util.UrlPathHelper} could be the full path
	 * or without the context path, decoded or not, etc.
	 * @since 5.2
	 */
	String LOOKUP_PATH = HandlerMapping.class.getName() + ".lookupPath";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the path
	 * within the handler mapping, in case of a pattern match, or the full
	 * relevant URI (typically within the DispatcherServlet's mapping) else.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * typically support it, but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 */
	String PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE = HandlerMapping.class.getName() + ".pathWithinHandlerMapping";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the
	 * best matching pattern within the handler mapping.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * typically support it, but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 */
	String BEST_MATCHING_PATTERN_ATTRIBUTE = HandlerMapping.class.getName() + ".bestMatchingPattern";

	/**
	 * {@link HttpServletRequest}中布尔属性的名称，该属性指示是否应检查类型级别的映射。
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations.
	 */
	String INTROSPECT_TYPE_LEVEL_MAPPING = HandlerMapping.class.getName() + ".introspectTypeLevelMapping";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the URI
	 * templates map, mapping variable names to values.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * typically support it, but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 */
	String URI_TEMPLATE_VARIABLES_ATTRIBUTE = HandlerMapping.class.getName() + ".uriTemplateVariables";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains a map with
	 * URI variable names and a corresponding MultiValueMap of URI matrix
	 * variables for each.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations and may also not be present depending on
	 * whether the HandlerMapping is configured to keep matrix variable content
	 */
	String MATRIX_VARIABLES_ATTRIBUTE = HandlerMapping.class.getName() + ".matrixVariables";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the set of
	 * producible MediaTypes applicable to the mapped handler.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. Handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 */
	String PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE = HandlerMapping.class.getName() + ".producibleMediaTypes";

	/**
	 * 返回此请求的处理器和所有拦截器。 该选择可以根据请求URL，会话状态或实现类选择的任何因素进行。
	 * <p>返回的HandlerExecutionChain包含一个处理器对象，甚至没有一个标签接口，
	 * 因此不会以任何方式限制处理器。 例如，可以编写HandlerAdapter来允许使用另一个框架的处理器对象。
	 * <p>如果未找到匹配项，则返回{@code null}。 这不是错误。 
	 * DispatcherServlet将查询所有已注册的HandlerMapping Bean以查找匹配项，
	 * 并且仅在没有人可以找到处理器的情况下才确定存在错误。
	 * @param request current HTTP request
	 * @return a HandlerExecutionChain instance containing handler object and
	 * any interceptors, or {@code null} if no mapping found
	 * @throws Exception if there is an internal error
	 */
	@Nullable
	HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

}
