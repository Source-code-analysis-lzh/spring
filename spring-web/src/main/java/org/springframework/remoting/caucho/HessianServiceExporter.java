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

package org.springframework.remoting.caucho;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

/**
 * 基于Servlet-API的HTTP请求处理器，将指定的服务bean导出为Hessian服务终结点，可通过Hessian代理进行访问。
 *
 * <p>Hessian是一种苗条的二进制RPC协议。 有关Hessian的信息，请访问<a href="http://hessian.caucho.com">Hessian website</a>。
 * <b>注意：从Spring 4.0开始，此导出程序需要Hessian 4.0或更高版本。</b>
 *
 * <p>此类的导出的Hessian服务可由任何Hessian客户端访问，因为不涉及任何特殊处理。
 *
 * @author Juergen Hoeller
 * @since 13.05.2003
 * @see HessianClientInterceptor
 * @see HessianProxyFactoryBean
 * @see org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 */
public class HessianServiceExporter extends HessianExporter implements HttpRequestHandler {

	/**
	 * Processes the incoming Hessian request and creates a Hessian response.
	 */
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(),
					new String[] {"POST"}, "HessianServiceExporter only supports POST requests");
		}

		response.setContentType(CONTENT_TYPE_HESSIAN);
		try {
			invoke(request.getInputStream(), response.getOutputStream());
		}
		catch (Throwable ex) {
			throw new NestedServletException("Hessian skeleton invocation failed", ex);
		}
	}

}
