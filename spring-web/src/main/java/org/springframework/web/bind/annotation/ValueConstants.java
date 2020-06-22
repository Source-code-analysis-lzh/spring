/*
 * Copyright 2002-2012 the original author or authors.
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
 * 绑定注释之间共享的公共值常量。
 *
 * @author Juergen Hoeller
 * @since 3.0.1
 */
public interface ValueConstants {

	/**
	 * 常量，用于定义没有默认值的值-替代我们不能在注释属性中使用的{@code null}。
	 * <p>这是16个unicode字符的人工排列，其唯一目的是从不与用户声明的值匹配。
	 * @see RequestParam#defaultValue()
	 * @see RequestHeader#defaultValue()
	 * @see CookieValue#defaultValue()
	 */
	String DEFAULT_NONE = "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n";

}
