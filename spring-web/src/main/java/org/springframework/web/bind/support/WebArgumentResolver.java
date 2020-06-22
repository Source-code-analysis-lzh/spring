/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.web.bind.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * SPI，用于解析特定处理器方法参数的自定义参数。 通常用于检测特殊参数类型，以解析它们的参数值。
 *
 * <p>一个典型的实现可能如下所示：
 *
 * <pre class="code">
 * public class MySpecialArgumentResolver implements WebArgumentResolver {
 *
 *   public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) {
 *     if (methodParameter.getParameterType().equals(MySpecialArg.class)) {
 *       return new MySpecialArg("myValue");
 *     }
 *     return UNRESOLVED;
 *   }
 * }</pre>
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#setCustomArgumentResolvers
 */
@FunctionalInterface
public interface WebArgumentResolver {

	/**
	 * 解析器不知道如何处理给定方法参数时要返回的标记。
	 */
	Object UNRESOLVED = new Object();


	/**
	 * 在给定的Web请求中为给定的处理器方法参数解析参数。
	 * @param methodParameter the handler method parameter to resolve
	 * @param webRequest the current web request, allowing access to the native request as well
	 * @return the argument value, or {@code UNRESOLVED} if not resolvable
	 * @throws Exception in case of resolution failure
	 */
	@Nullable
	Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception;

}
