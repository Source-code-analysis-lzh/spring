/*
 * Copyright 2002-2015 the original author or authors.
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

/**
 * Java 5 HTTP请求方法的枚举。 旨在与{@link RequestMapping}
 * 注解的{@link RequestMapping#method()}属性一起使用。
 *
 * <p>请注意，默认情况下，{@link org.springframework.web.servlet.DispatcherServlet}
 * 仅支持GET，HEAD，POST，PUT，PATCH和DELETE。 DispatcherServlet将使用默认的HttpServlet
 * 行为处理TRACE和OPTIONS，除非也明确告知也要调度那些请求类型：检查"dispatchOptionsRequest"
 * 和"dispatchTraceRequest"属性，并在必要时将其切换为"true"。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see RequestMapping
 * @see org.springframework.web.servlet.DispatcherServlet#setDispatchOptionsRequest
 * @see org.springframework.web.servlet.DispatcherServlet#setDispatchTraceRequest
 */
public enum RequestMethod {

	GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE

}
