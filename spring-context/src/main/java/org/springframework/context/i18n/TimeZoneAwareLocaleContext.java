/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.context.i18n;

import java.util.TimeZone;

import org.springframework.lang.Nullable;

/**
 * 扩展{@link LocaleContext}，增加对当前时区的了解.
 *
 * <p>将LocaleContext的此变量设置为{@link LocaleContextHolder}
 * 意味着已配置了一些支持TimeZone的基础结构，即使该基础结构当前可能无法生成非null的TimeZone.
 *
 * @author Juergen Hoeller
 * @author Nicholas Williams
 * @since 4.0
 * @see LocaleContextHolder#getTimeZone()
 */
public interface TimeZoneAwareLocaleContext extends LocaleContext {

	/**
	 * 返回当前的TimeZone，可以根据实现策略对其进行固定或动态确定.
	 * @return the current TimeZone, or {@code null} if no specific TimeZone associated
	 */
	@Nullable
	TimeZone getTimeZone();

}
