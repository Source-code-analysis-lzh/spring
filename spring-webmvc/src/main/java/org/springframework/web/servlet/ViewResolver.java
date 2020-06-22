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

import java.util.Locale;

import org.springframework.lang.Nullable;

/**
 * 由可以通过名称解析视图的对象实现的接口。
 *
 * <p>在应用程序运行期间，视图状态不会改变，因此实现可以自由地缓存视图。
 *
 * <p>鼓励实施以支持国际化，即本地化视图解析。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.web.servlet.view.InternalResourceViewResolver
 * @see org.springframework.web.servlet.view.ResourceBundleViewResolver
 * @see org.springframework.web.servlet.view.XmlViewResolver
 */
public interface ViewResolver {

	/**
	 * 通过名称解析给定的视图。
	 * <p>注意：为了允许ViewResolver链，如果未定义具有给定名称的视图，则ViewResolver应该返回{@code null}。
	 * 但是，这不是必需的：某些ViewResolvers将始终尝试使用给定名称构建View对象，而无法返回{@code null}
	 * （而是在View创建失败时引发异常）。
	 * @param viewName name of the view to resolve
	 * @param locale the Locale in which to resolve the view.
	 * ViewResolvers that support internationalization should respect this.
	 * @return the View object, or {@code null} if not found
	 * (optional, to allow for ViewResolver chaining)
	 * @throws Exception if the view cannot be resolved
	 * (typically in case of problems creating an actual View object)
	 */
	@Nullable
	View resolveViewName(String viewName, Locale locale) throws Exception;

}
