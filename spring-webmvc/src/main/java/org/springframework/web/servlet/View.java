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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;

/**
 * MVC视图用于Web交互。 实现负责渲染内容并公开模型。 单个视图公开了多个模型属性。
 *
 * <p>This class and the MVC approach associated with it is discussed in Chapter 12 of
 * <a href="https://www.amazon.com/exec/obidos/tg/detail/-/0764543857/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 *
 * <p>视图实现可能差异很大。 一个明显的实现是基于JSP的。 其它实现可能基于XSLT，或者使用HTML生成库。 
 * 该接口旨在避免限制可能的实现范围。
 *
 * <p>视图应该是bean。 它们很可能被ViewResolver实例化为bean。 由于此接口是无状态的，
 * 因此视图实现应该是线程安全的。
 *
 * @author Rod Johnson
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @see org.springframework.web.servlet.view.AbstractView
 * @see org.springframework.web.servlet.view.InternalResourceView
 */
public interface View {

	/**
	 * 包含响应状态代码的{@link HttpServletRequest}中属性的名称。
	 * <p>注意：并非所有View实现都支持此属性。
	 * @since 3.0
	 */
	String RESPONSE_STATUS_ATTRIBUTE = View.class.getName() + ".responseStatus";

	/**
	 * 包含带有路径变量的Map的{@link HttpServletRequest}的属性的名称。 
	 * 该映射由基于字符串的URI模板变量名称（作为键）及其对应的基于对象的值组成-从URL的片段中提取并进行类型转换。
	 * <p>Note: This attribute is not required to be supported by all View implementations.
	 * @since 3.1
	 */
	String PATH_VARIABLES = View.class.getName() + ".pathVariables";

	/**
	 * 在内容协商期间选择的{@link org.springframework.http.MediaType}，它可能比配置View的MediaType更具体。 
	 * 例如："application/vnd.example-v1+xml" vs "application/*+xml"。
	 * @since 3.2
	 */
	String SELECTED_CONTENT_TYPE = View.class.getName() + ".selectedContentType";


	/**
	 * 返回视图的内容类型（如果已预定）。
	 * <p>可用于预先检查视图的内容类型，即在实际渲染尝试之前。
	 * @return the content type String (optionally including a character set),
	 * or {@code null} if not predetermined
	 */
	@Nullable
	default String getContentType() {
		return null;
	}

	/**
	 * 根据指定的模型渲染视图。
	 * <p>第一步将是准备请求：在JSP情况下，这意味着将模型对象设置为请求属性。 
	 * 第二步将是视图的实际渲染，例如，通过RequestDispatcher包含JSP。
	 * @param model a Map with name Strings as keys and corresponding model
	 * objects as values (Map can also be {@code null} in case of empty model)
	 * @param request current HTTP request
	 * @param response he HTTP response we are building
	 * @throws Exception if rendering failed
	 */
	void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

}
