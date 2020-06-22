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

package org.springframework.beans;

/**
 * {@link TypeConverter}接口的简单实现，该接口不对特定目标对象进行操作。 
 * 这是使用成熟的BeanWrapperImpl实例满足任意类型转换需求的一种替代方法，
 * 同时在下面使用了完全相同的转换算法（包括委派给{@link java.beans.PropertyEditor}
 * 和{@link org.springframework.core.convert.ConversionService})）。
 *
 * <p>注意：由于依赖于{@link java.beans.PropertyEditor PropertyEditors}，
 * 因此SimpleTypeConverter不是线程安全的。 为每个线程使用一个单独的实例。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see BeanWrapperImpl
 */
public class SimpleTypeConverter extends TypeConverterSupport {

	public SimpleTypeConverter() {
		this.typeConverterDelegate = new TypeConverterDelegate(this);
		registerDefaultEditors();
	}

}
