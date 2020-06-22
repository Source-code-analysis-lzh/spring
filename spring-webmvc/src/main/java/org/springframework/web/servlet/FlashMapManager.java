/*
 * Copyright 2002-2014 the original author or authors.
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
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;

/**
 * 用于检索和保存{@link FlashMap}实例的策略接口。 有关Flash属性的一般概述，请参见{@link FlashMap}。
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see FlashMap
 */
public interface FlashMapManager {

	/**
	 * 查找由与当前请求匹配的先前请求保存的FlashMap，将其从底层存储中删除，还删除其它过期的FlashMap实例。
	 * <p>与即在重定向之前，调用要保存的属性{@link #saveOutputFlashMap}相比，
	 * 此方法在每个请求的开始处都被调用。
	 * @param request the current request
	 * @param response the current response
	 * @return a FlashMap matching the current request or {@code null}
	 */
	@Nullable
	FlashMap retrieveAndUpdate(HttpServletRequest request, HttpServletResponse response);

	/**
	 * 将给定的FlashMap保存在一些底层存储中，并设置其到期时间。
	 * <p>注意：在重定向之前调用此方法，以便允许在提交响应之前将FlashMap保存在HTTP会话或响应cookie中。
	 * @param flashMap the FlashMap to save
	 * @param request the current request
	 * @param response the current response
	 */
	void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest request, HttpServletResponse response);

}
