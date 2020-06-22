/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.core.io;

/**
 * 从一个封闭的'context'中加载的资源的扩展接口，例如 来自{@link javax.servlet.ServletContext}，
 * 也可来自普通的类路径路径或相对的文件系统路径（在没有显式前缀的情况下指定，因此相对于本地
 * {@link ResourceLoader}的上下文应用）.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.web.context.support.ServletContextResource
 */
public interface ContextResource extends Resource {

	/**
	 * 返回封闭的'context'中的路径.
	 * <p>这通常是相对于特定于上下文的根目录的路径，例如 ServletContext根目录或PortletContext根目录.
	 */
	String getPathWithinContext();

}
