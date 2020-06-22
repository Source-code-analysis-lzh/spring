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

package org.springframework.web.filter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.util.WebUtils;

/**
 * 过滤器基类，旨在确保在任何servlet容器上每个请求单次执行。 
 * 它提供了带有HttpServletRequest和HttpServletResponse参数的
 * {@link #doFilterInternal}方法。
 *
 * <p>从Servlet 3.0开始，过滤器可以作为在单独线程中发生的
 * {@link javax.servlet.DispatcherType#REQUEST REQUEST}或
 * {@link javax.servlet.DispatcherType#ASYNC ASYNC}调度的一部分来调用。 
 * 可以在{@code web.xml}中配置是否应将其包含在异步调度中的过滤器。 但是，在某些情况下，
 * servlet容器采用不同的默认配置。 因此，子类可以重写方法{@link #shouldNotFilterAsyncDispatch()}
 * 来静态声明是否确实应在两种类型的调度期间调用它们一次，以提供线程初始化，日志记录，安全性等。 
 * 这种机制补充了并没有取代在{@code web.xml}中使用dispatcher类型配置过滤器的需求。
 *
 * <p>子类可以使用{@link #isAsyncDispatch(HttpServletRequest)}
 * 确定何时将过滤器作为异步调度的一部分来调用，并使用{@link #isAsyncStarted(HttpServletRequest)}
 * 确定何时将请求置于异步模式，因此，对于给定的请求当前调度将不是最后一个。
 *
 * <p>然而，在其自己的线程中也发生的另一种{@link javax.servlet.DispatcherType#ERROR ERROR}。 
 * 如果子类希望在错误分配期间被调用一次，则可以静态声明，则子类可以重写{@link #shouldNotFilterErrorDispatch()}。
 *
 * <p>{@link #getAlreadyFilteredAttributeName}方法确定如何识别请求已被过滤。
 * 默认实现基于具体过滤器实例的配置名称。
 *
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 06.12.2003
 */
public abstract class OncePerRequestFilter extends GenericFilterBean {

	/**
	 * Suffix that gets appended to the filter name for the
	 * "already filtered" request attribute.
	 * @see #getAlreadyFilteredAttributeName
	 */
	public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";


	/**
	 * This {@code doFilter} implementation stores a request attribute for
	 * "already filtered", proceeding without filtering again if the
	 * attribute is already there.
	 * @see #getAlreadyFilteredAttributeName
	 * @see #shouldNotFilter
	 * @see #doFilterInternal
	 */
	@Override
	public final void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			throw new ServletException("OncePerRequestFilter just supports HTTP requests");
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
		boolean hasAlreadyFilteredAttribute = request.getAttribute(alreadyFilteredAttributeName) != null;

		if (skipDispatch(httpRequest) || shouldNotFilter(httpRequest)) {

			// Proceed without invoking this filter...
			filterChain.doFilter(request, response);
		}
		else if (hasAlreadyFilteredAttribute) {

			if (DispatcherType.ERROR.equals(request.getDispatcherType())) {
				doFilterNestedErrorDispatch(httpRequest, httpResponse, filterChain);
				return;
			}

			// Proceed without invoking this filter...
			filterChain.doFilter(request, response);
		}
		else {
			// Do invoke this filter...
			request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);
			try {
				doFilterInternal(httpRequest, httpResponse, filterChain);
			}
			finally {
				// Remove the "already filtered" request attribute for this request.
				request.removeAttribute(alreadyFilteredAttributeName);
			}
		}
	}

	private boolean skipDispatch(HttpServletRequest request) {
		if (isAsyncDispatch(request) && shouldNotFilterAsyncDispatch()) {
			return true;
		}
		if (request.getAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE) != null && shouldNotFilterErrorDispatch()) {
			return true;
		}
		return false;
	}

	/**
	 * The dispatcher type {@code javax.servlet.DispatcherType.ASYNC} introduced
	 * in Servlet 3.0 means a filter can be invoked in more than one thread over
	 * the course of a single request. This method returns {@code true} if the
	 * filter is currently executing within an asynchronous dispatch.
	 * @param request the current request
	 * @since 3.2
	 * @see WebAsyncManager#hasConcurrentResult()
	 */
	protected boolean isAsyncDispatch(HttpServletRequest request) {
		return WebAsyncUtils.getAsyncManager(request).hasConcurrentResult();
	}

	/**
	 * Whether request processing is in asynchronous mode meaning that the
	 * response will not be committed after the current thread is exited.
	 * @param request the current request
	 * @since 3.2
	 * @see WebAsyncManager#isConcurrentHandlingStarted()
	 */
	protected boolean isAsyncStarted(HttpServletRequest request) {
		return WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted();
	}

	/**
	 * Return the name of the request attribute that identifies that a request
	 * is already filtered.
	 * <p>The default implementation takes the configured name of the concrete filter
	 * instance and appends ".FILTERED". If the filter is not fully initialized,
	 * it falls back to its class name.
	 * @see #getFilterName
	 * @see #ALREADY_FILTERED_SUFFIX
	 */
	protected String getAlreadyFilteredAttributeName() {
		String name = getFilterName();
		if (name == null) {
			name = getClass().getName();
		}
		return name + ALREADY_FILTERED_SUFFIX;
	}

	/**
	 * Can be overridden in subclasses for custom filtering control,
	 * returning {@code true} to avoid filtering of the given request.
	 * <p>The default implementation always returns {@code false}.
	 * @param request current HTTP request
	 * @return whether the given request should <i>not</i> be filtered
	 * @throws ServletException in case of errors
	 */
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return false;
	}

	/**
	 * The dispatcher type {@code javax.servlet.DispatcherType.ASYNC} introduced
	 * in Servlet 3.0 means a filter can be invoked in more than one thread
	 * over the course of a single request. Some filters only need to filter
	 * the initial thread (e.g. request wrapping) while others may need
	 * to be invoked at least once in each additional thread for example for
	 * setting up thread locals or to perform final processing at the very end.
	 * <p>Note that although a filter can be mapped to handle specific dispatcher
	 * types via {@code web.xml} or in Java through the {@code ServletContext},
	 * servlet containers may enforce different defaults with regards to
	 * dispatcher types. This flag enforces the design intent of the filter.
	 * <p>The default return value is "true", which means the filter will not be
	 * invoked during subsequent async dispatches. If "false", the filter will
	 * be invoked during async dispatches with the same guarantees of being
	 * invoked only once during a request within a single thread.
	 * @since 3.2
	 */
	protected boolean shouldNotFilterAsyncDispatch() {
		return true;
	}

	/**
	 * Whether to filter error dispatches such as when the servlet container
	 * processes and error mapped in {@code web.xml}. The default return value
	 * is "true", which means the filter will not be invoked in case of an error
	 * dispatch.
	 * @since 3.2
	 */
	protected boolean shouldNotFilterErrorDispatch() {
		return true;
	}


	/**
	 * Same contract as for {@code doFilter}, but guaranteed to be
	 * just invoked once per request within a single request thread.
	 * See {@link #shouldNotFilterAsyncDispatch()} for details.
	 * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
	 * default ServletRequest and ServletResponse ones.
	 */
	protected abstract void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException;

	/**
	 * Typically an ERROR dispatch happens after the REQUEST dispatch completes,
	 * and the filter chain starts anew. On some servers however the ERROR
	 * dispatch may be nested within the REQUEST dispatch, e.g. as a result of
	 * calling {@code sendError} on the response. In that case we are still in
	 * the filter chain, on the same thread, but the request and response have
	 * been switched to the original, unwrapped ones.
	 * <p>Sub-classes may use this method to filter such nested ERROR dispatches
	 * and re-apply wrapping on the request or response. {@code ThreadLocal}
	 * context, if any, should still be active as we are still nested within
	 * the filter chain.
	 * @since 5.1.9
	 */
	protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		filterChain.doFilter(request, response);
	}

}
