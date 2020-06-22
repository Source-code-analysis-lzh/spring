/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;

/**
 * 基本控制器接口，代表一个组件，该组件像{@code HttpServlet}一样接收{@code HttpServletRequest}
 * 和{@code HttpServletResponse}实例，但能够参与MVC工作流程。 控制器与{@code Action}动作的概念相当。
 *
 * <p>Controller接口的任何实现都应该是可重用的，线程安全的类，能够在应用程序的整个生命周期中处理多个HTTP请求。 
 * 为了能够轻松配置Controller，鼓励Controller实现为（通常是JavaBean）。
 *
 * <h3><a name="workflow">工作流</a></h3>
 *
 * <p>{@code DispatcherServlet}收到请求并完成解析语言环境，主题等的工作后，
 * 便尝试使用{@link org.springframework.web.servlet.HandlerMapping HandlerMapping}
 * 尝试解析Controller。 当找到一个Controller处理请求时，将调用所定位Controller的
 * {@link #handleRequest(HttpServletRequest, HttpServletResponse) handleRequest}方法。 
 * 然后，所定位的Controller负责处理实际请求，并在适用时返回适当的
 * {@link org.springframework.web.servlet.ModelAndView ModelAndView}。 
 * 因此，实际上，此方法是{@link org.springframework.web.servlet.DispatcherServlet DispatcherServlet}
 * 的主要入口点，它将请求委托给控制器。
 *
 * <p>因此，基本上任何{@code Controller}接口的直接实现都只处理HttpServletRequests并应返回ModelAndView，
 * 由DispatcherServlet进一步解释。 任何其它功能，例如可选验证，表单处理等，都应通过扩展
 * {@link org.springframework.web.servlet.mvc.AbstractController AbstractController}或其子类之一来获得。
 *
 * <h3>设计和测试注意事项</h3>
 *
 * <p>Controller接口经过明确设计，可以像HttpServlet一样对HttpServletRequest和HttpServletResponse对象进行操作。 
 * 与例如WebWork，JSF或Tapestry相比，它并不旨在将自己与Servlet API解耦。相反的是，Servlet API的全部功能都可用，
 * 从而使Controllers成为通用用途：Controller不仅能够处理Web用户界面请求，而且能够处理远程协议或按需生成报告。
 *
 * <p>通过将HttpServletRequest和HttpServletResponse对象的模拟对象作为
 * {@link #handleRequest(HttpServletRequest, HttpServletResponse) handleRequest}方法的参数传入，
 * 可以轻松测试控制器。 为了方便起见，Spring附带了一组Servlet API模拟，这些模拟适用于测试任何种类的Web组件，
 * 但是特别适合于测试Spring Web控制器。 与Struts Action相比，不需要模拟ActionServlet或任何其他基础结构。 
 * 模拟HttpServletRequest和HttpServletResponse就足够了。
 * 
 *
 * <p>如果控制器需要了解特定的环境引用，则可以选择实现特定的感知接口，就像Spring（web）应用程序上下文中的任何其他bean一样，例如：
 * <ul>
 * <li>{@code org.springframework.context.ApplicationContextAware}</li>
 * <li>{@code org.springframework.context.ResourceLoaderAware}</li>
 * <li>{@code org.springframework.web.context.ServletContextAware}</li>
 * </ul>
 *
 * <p>通过在相应的感知接口中定义的相应设置器，可以轻松地在测试环境中传递此类环境参考。 
 * 通常，建议使依赖关系尽可能地小：例如，如果您只需要资源加载，则仅实现ResourceLoaderAware。 
 * 或者，从WebApplicationObjectSupport基类派生，该基类通过便捷的访问器为您提供所有这些引用，
 * 但在初始化时需要ApplicationContext引用。
 *
 * <p>控制器可以选择实现{@link LastModified}接口。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see LastModified
 * @see SimpleControllerHandlerAdapter
 * @see AbstractController
 * @see org.springframework.mock.web.MockHttpServletRequest
 * @see org.springframework.mock.web.MockHttpServletResponse
 * @see org.springframework.context.ApplicationContextAware
 * @see org.springframework.context.ResourceLoaderAware
 * @see org.springframework.web.context.ServletContextAware
 * @see org.springframework.web.context.support.WebApplicationObjectSupport
 */
@FunctionalInterface
public interface Controller {

	/**
	 * 处理请求并返回DispatcherServlet将呈现的ModelAndView对象。 返回值为{@code null}并非错误：
	 * 它表示此对象已完成请求处理本身（就是已经响应请求），因此没有要渲染的ModelAndView。
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return a ModelAndView to render, or {@code null} if handled directly
	 * @throws Exception in case of errors
	 */
	@Nullable
	ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
