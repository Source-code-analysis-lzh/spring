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

package org.springframework.web.bind;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.core.CollectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.MultipartFile;

/**
 * 特殊{@link DataBinder}，用于将Web请求参数绑定到JavaBean对象。 
 * 专为Web环境而设计，但不依赖Servlet API； 用作更特定的DataBinder变体
 * （例如{@link org.springframework.web.bind.ServletRequestDataBinder}）的基类。
 *
 * <p>包括对字段标记的支持，该字段标记解决了HTML checkboxes 和 select的options的常见问题：
 * 检测到字段是表单的一部分，但因为该字段为空未生成请求参数。 
 * 字段标记允许检测该状态并相应地重置相应的bean属性。 对于原本不存在的参数，默认值可以为字段指定其它值而不是空。
 *
 * @author Juergen Hoeller
 * @author Scott Andrews
 * @author Brian Clozel
 * @since 1.2
 * @see #registerCustomEditor
 * @see #setAllowedFields
 * @see #setRequiredFields
 * @see #setFieldMarkerPrefix
 * @see #setFieldDefaultPrefix
 * @see ServletRequestDataBinder
 */
public class WebDataBinder extends DataBinder {

	/**
	 * 字段标记参数以其开头的默认前缀，后跟字段名称：例如 字段"subscribeToNewsletter"
	 * 为"_subscribeToNewsletter"。
	 * <p>这样的标记参数表明该字段是可见的，也就是说，在表单提交时存在。 如果未找到相应的字段值参数，则将重置该字段。 
	 * 在这种情况下，字段标记参数的值无关紧要； 可以使用任意值。 这对于HTML复选框和选择选项特别有用。
	 * @see #setFieldMarkerPrefix
	 */
	public static final String DEFAULT_FIELD_MARKER_PREFIX = "_";

	/**
	 * 字段默认参数以其开头的默认前缀，后跟字段名称：例如 字段"subscribeToNewsletter"为"!subscribeToNewsletter"。
	 * <p>默认参数与字段标记的不同之处在于，它们提供默认值而不是空值。
	 * @see #setFieldDefaultPrefix
	 */
	public static final String DEFAULT_FIELD_DEFAULT_PREFIX = "!";

	@Nullable
	private String fieldMarkerPrefix = DEFAULT_FIELD_MARKER_PREFIX;

	@Nullable
	private String fieldDefaultPrefix = DEFAULT_FIELD_DEFAULT_PREFIX;

	private boolean bindEmptyMultipartFiles = true;


	/**
	 * Create a new WebDataBinder instance, with default object name.
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @see #DEFAULT_OBJECT_NAME
	 */
	public WebDataBinder(@Nullable Object target) {
		super(target);
	}

	/**
	 * Create a new WebDataBinder instance.
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @param objectName the name of the target object
	 */
	public WebDataBinder(@Nullable Object target, String objectName) {
		super(target, objectName);
	}


	/**
	 * 指定一个前缀，该前缀可用于以"prefix + field"作为名称的标记可能为空的字段的参数。 
	 * 这样的标记参数将通过存在性检查：您可以为其发送任何值，例如"visible"。 这对于HTML复选框和选择选项特别有用。
	 * <p>对于"_FIELD"参数，默认值为"_"（例如"_subscribeToNewsletter"）。 如果要完全关闭空白字段检查，请将其设置为null。
	 * <p>HTML复选框仅在被选中时才发送值，因此无法检测到以前选中后来未选中的复选框，至少没有使用标准HTML手段。
	 * <p>解决此问题的一种方法是，如果知道复选框已在表单中可见，则查找复选框参数值，如果未找到该值，则重置该复选框。 
	 * 在Spring Web MVC中，这通常发生在自定义{@code onBind}实现中。
	 * <p>如果为每个复选框字段发送了标记参数，例如"subscribeToNewsletter"字段的"_subscribeToNewsletter"，
	 * 则此自动重置机制将解决此缺陷。 无论如何发送标记参数，数据绑定程序都可以检测到空白字段并自动重置其值。
	 * @see #DEFAULT_FIELD_MARKER_PREFIX
	 */
	public void setFieldMarkerPrefix(@Nullable String fieldMarkerPrefix) {
		this.fieldMarkerPrefix = fieldMarkerPrefix;
	}

	/**
	 * Return the prefix for parameters that mark potentially empty fields.
	 */
	@Nullable
	public String getFieldMarkerPrefix() {
		return this.fieldMarkerPrefix;
	}

	/**
	 * Specify a prefix that can be used for parameters that indicate default
	 * value fields, having "prefix + field" as name. The value of the default
	 * field is used when the field is not provided.
	 * <p>Default is "!", for "!FIELD" parameters (e.g. "!subscribeToNewsletter").
	 * Set this to null if you want to turn off the field defaults completely.
	 * <p>HTML checkboxes only send a value when they're checked, so it is not
	 * possible to detect that a formerly checked box has just been unchecked,
	 * at least not with standard HTML means.  A default field is especially
	 * useful when a checkbox represents a non-boolean value.
	 * <p>The presence of a default parameter preempts the behavior of a field
	 * marker for the given field.
	 * @see #DEFAULT_FIELD_DEFAULT_PREFIX
	 */
	public void setFieldDefaultPrefix(@Nullable String fieldDefaultPrefix) {
		this.fieldDefaultPrefix = fieldDefaultPrefix;
	}

	/**
	 * Return the prefix for parameters that mark default fields.
	 */
	@Nullable
	public String getFieldDefaultPrefix() {
		return this.fieldDefaultPrefix;
	}

	/**
	 * Set whether to bind empty MultipartFile parameters. Default is "true".
	 * <p>Turn this off if you want to keep an already bound MultipartFile
	 * when the user resubmits the form without choosing a different file.
	 * Else, the already bound MultipartFile will be replaced by an empty
	 * MultipartFile holder.
	 * @see org.springframework.web.multipart.MultipartFile
	 */
	public void setBindEmptyMultipartFiles(boolean bindEmptyMultipartFiles) {
		this.bindEmptyMultipartFiles = bindEmptyMultipartFiles;
	}

	/**
	 * 返回是否绑定空的MultipartFile参数。
	 */
	public boolean isBindEmptyMultipartFiles() {
		return this.bindEmptyMultipartFiles;
	}


	/**
	 * 此实现在委派给超类绑定过程之前执行字段默认值和标记检查。
	 * @see #checkFieldDefaults
	 * @see #checkFieldMarkers
	 */
	@Override
	protected void doBind(MutablePropertyValues mpvs) {
		checkFieldDefaults(mpvs);
		checkFieldMarkers(mpvs);
		super.doBind(mpvs);
	}

	/**
	 * 检查给定的属性值以获取字段默认值，如以字段默认前缀开头的字段。
	 * <p>字段默认值的存在指示如果不存在该字段，则应使用指定的值。
	 * @param mpvs the property values to be bound (can be modified)
	 * @see #getFieldDefaultPrefix
	 */
	protected void checkFieldDefaults(MutablePropertyValues mpvs) {
		String fieldDefaultPrefix = getFieldDefaultPrefix();
		if (fieldDefaultPrefix != null) {
			PropertyValue[] pvArray = mpvs.getPropertyValues();
			for (PropertyValue pv : pvArray) {
				if (pv.getName().startsWith(fieldDefaultPrefix)) {
					String field = pv.getName().substring(fieldDefaultPrefix.length());
					// 判断是否存在该属性，如果不存在，则使用带前缀的属性默认值
					if (getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
						mpvs.add(field, pv.getValue());
					}
					mpvs.removePropertyValue(pv); // 最后，移除带前缀的属性
				}
			}
		}
	}

	/**
	 * 检查字段标记的给定属性值，如以字段标记前缀开头的字段。
	 * <p>字段标记的存在指示指定的字段以表格形式存在。 如果属性值不包含相应的字段值，则该字段将被视为空字段并将被适当地重置。
	 * @param mpvs the property values to be bound (can be modified)
	 * @see #getFieldMarkerPrefix
	 * @see #getEmptyValue(String, Class)
	 */
	protected void checkFieldMarkers(MutablePropertyValues mpvs) {
		String fieldMarkerPrefix = getFieldMarkerPrefix();
		if (fieldMarkerPrefix != null) {
			PropertyValue[] pvArray = mpvs.getPropertyValues();
			for (PropertyValue pv : pvArray) {
				if (pv.getName().startsWith(fieldMarkerPrefix)) {
					String field = pv.getName().substring(fieldMarkerPrefix.length());
					// 判断是否存在该属性，如果不存在，则使用带前缀的属性默认值
					if (getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
						Class<?> fieldType = getPropertyAccessor().getPropertyType(field);
						// 设置该字段属性值为空值
						mpvs.add(field, getEmptyValue(field, fieldType));
					}
					mpvs.removePropertyValue(pv); // 最后，移除带前缀的属性
				}
			}
		}
	}

	/**
	 * Determine an empty value for the specified field.
	 * <p>The default implementation delegates to {@link #getEmptyValue(Class)}
	 * if the field type is known, otherwise falls back to {@code null}.
	 * @param field the name of the field
	 * @param fieldType the type of the field
	 * @return the empty value (for most fields: {@code null})
	 */
	@Nullable
	protected Object getEmptyValue(String field, @Nullable Class<?> fieldType) {
		return (fieldType != null ? getEmptyValue(fieldType) : null);
	}

	/**
	 * 确定指定字段的空值。
	 * <p>The default implementation returns:
	 * <ul>
	 * <li>{@code Boolean.FALSE} for boolean fields
	 * <li>an empty array for array types
	 * <li>Collection implementations for Collection types
	 * <li>Map implementations for Map types
	 * <li>else, {@code null} is used as default
	 * </ul>
	 * @param fieldType the type of the field
	 * @return the empty value (for most fields: {@code null})
	 * @since 5.0
	 */
	@Nullable
	public Object getEmptyValue(Class<?> fieldType) {
		try {
			if (boolean.class == fieldType || Boolean.class == fieldType) {
				// Special handling of boolean property.
				return Boolean.FALSE;
			}
			else if (fieldType.isArray()) {
				// Special handling of array property.
				return Array.newInstance(fieldType.getComponentType(), 0);
			}
			else if (Collection.class.isAssignableFrom(fieldType)) {
				return CollectionFactory.createCollection(fieldType, 0);
			}
			else if (Map.class.isAssignableFrom(fieldType)) {
				return CollectionFactory.createMap(fieldType, 0);
			}
		}
		catch (IllegalArgumentException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to create default value - falling back to null: " + ex.getMessage());
			}
		}
		// Default value: null.
		return null;
	}


	/**
	 * 绑定给定请求中包含的所有multipart文件（如果有的话）（对于multipart请求）。 由子类调用。
	 * <p>仅当Multipart文件不为空或我们也配置为绑定空的multipart文件时，它们才会添加到属性值中。
	 * @param multipartFiles a Map of field name String to MultipartFile object
	 * @param mpvs the property values to be bound (can be modified)
	 * @see org.springframework.web.multipart.MultipartFile
	 * @see #setBindEmptyMultipartFiles
	 */
	protected void bindMultipart(Map<String, List<MultipartFile>> multipartFiles, MutablePropertyValues mpvs) {
		multipartFiles.forEach((key, values) -> {
			if (values.size() == 1) {
				MultipartFile value = values.get(0);
				if (isBindEmptyMultipartFiles() || !value.isEmpty()) {
					mpvs.add(key, value);
				}
			}
			else {
				mpvs.add(key, values);
			}
		});
	}

}
