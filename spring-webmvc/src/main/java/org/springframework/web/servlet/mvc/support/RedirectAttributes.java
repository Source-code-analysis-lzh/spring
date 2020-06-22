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

package org.springframework.web.servlet.mvc.support;

import java.util.Collection;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.ui.Model;
import org.springframework.web.servlet.FlashMap;

/**
 * 控制器可以用来选择重定向的属性方案的{@link Model}接口的一种特殊化。 
 * 由于添加重定向属性的意图非常明确-即用于重定向URL，
 * 因此可以将属性值格式化为字符串并以这种方式存储，以使其有资格附加到查询字符串或
 * {@code org.springframework.web.servlet.view.RedirectView}中可扩展的URI变量。
 *
 * <p>该接口还提供了添加闪存属性的方法。 有关Flash属性的一般概述，请参见{@link FlashMap}。 
 * 您可以使用{@link RedirectAttributes}来存储Flash属性，它们将自动传播到当前请求的“输出” FlashMap。
 *
 * <p>Example usage in an {@code @Controller}:
 * <pre class="code">
 * &#064;RequestMapping(value = "/accounts", method = RequestMethod.POST)
 * public String handle(Account account, BindingResult result, RedirectAttributes redirectAttrs) {
 *   if (result.hasErrors()) {
 *     return "accounts/new";
 *   }
 *   // Save account ...
 *   redirectAttrs.addAttribute("id", account.getId()).addFlashAttribute("message", "Account created!");
 *   return "redirect:/accounts/{id}";
 * }
 * </pre>
 *
 * <p>调用该方法时，RedirectAttributes模型为空，除非该方法返回重定向视图名称或RedirectView，
 * 否则永远不要使用它。
 *
 * <p>重定向后，Flash属性会自动添加到服务目标URL的控制器模型中。
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public interface RedirectAttributes extends Model {

	@Override
	RedirectAttributes addAttribute(String attributeName, @Nullable Object attributeValue);

	@Override
	RedirectAttributes addAttribute(Object attributeValue);

	@Override
	RedirectAttributes addAllAttributes(Collection<?> attributeValues);

	@Override
	RedirectAttributes mergeAttributes(Map<String, ?> attributes);

	/**
	 * Add the given flash attribute.
	 * @param attributeName the attribute name; never {@code null}
	 * @param attributeValue the attribute value; may be {@code null}
	 */
	RedirectAttributes addFlashAttribute(String attributeName, @Nullable Object attributeValue);

	/**
	 * Add the given flash storage using a
	 * {@link org.springframework.core.Conventions#getVariableName generated name}.
	 * @param attributeValue the flash attribute value; never {@code null}
	 */
	RedirectAttributes addFlashAttribute(Object attributeValue);

	/**
	 * Return the attributes candidate for flash storage or an empty Map.
	 */
	Map<String, ?> getFlashAttributes();
}
