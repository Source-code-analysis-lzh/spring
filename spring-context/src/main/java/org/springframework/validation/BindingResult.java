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

package org.springframework.validation;

import java.beans.PropertyEditor;
import java.util.Map;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.lang.Nullable;

/**
 * 表示绑定结果的常规接口. 扩展了{@link Errors}注册功能的接口，允许应用{@link Validator}，
 * 并添加了特定于绑定的分析和模型构建.
 *
 * <p>用作通过{@link DataBinder#getBindingResult()}方法获得的{@link DataBinder}的结果持有者.
 * BindingResult实现也可以直接使用，例如在其上调用{@link Validator}（例如，作为单元测试的一部分）.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see DataBinder
 * @see Errors
 * @see Validator
 * @see BeanPropertyBindingResult
 * @see DirectFieldBindingResult
 * @see MapBindingResult
 */
public interface BindingResult extends Errors {

	/**
	 * 模型中BindingResult实例名称的前缀，后跟对象名称.
	 */
	String MODEL_KEY_PREFIX = BindingResult.class.getName() + ".";


	/**
	 * 返回包装的目标对象，该对象可能是Bean，具有公共字段的对象，一个Map-取决于具体的绑定策略.
	 */
	@Nullable
	Object getTarget();

	/**
	 * 返回获得状态的模型Map，将BindingResult实例公开为'MODEL_KEY_PREFIX + objectName'，
	 * 将对象本身公开为'objectName'.
	 * <p>请注意，每次调用此方法时都会构造Map. 将内容添加到map，然后重新调用此方法将不起作用.
	 * <p>通过此方法返回的模型Map中的属性通常包含在{@link org.springframework.web.servlet.ModelAndView}中，
	 * 用于在JSP中使用Spring的{@code bind}标记的表单视图，该视图需要访问BindingResult实例.
	 * Spring的预构建表单控制器将在渲染表单视图时为您完成此操作. 当自己构建ModelAndView实例时，
	 * 您需要包括此方法返回的模型Map中的属性.
	 * @see #getObjectName()
	 * @see #MODEL_KEY_PREFIX
	 * @see org.springframework.web.servlet.ModelAndView
	 * @see org.springframework.web.servlet.tags.BindTag
	 */
	Map<String, Object> getModel();

	/**
	 * 提取给定字段的原始字段值.通常用于比较目的.
	 * @param field the field to check
	 * @return the current value of the field in its raw form, or {@code null} if not known
	 */
	@Nullable
	Object getRawFieldValue(String field);

	/**
	 * 查找给定类型和属性的自定义属性编辑器.
	 * @param field the path of the property (name or nested path), or
	 * {@code null} if looking for an editor for all properties of the given type
	 * @param valueType the type of the property (can be {@code null} if a property
	 * is given but should be specified in any case for consistency checking)
	 * @return the registered editor, or {@code null} if none
	 */
	@Nullable
	PropertyEditor findEditor(@Nullable String field, @Nullable Class<?> valueType);

	/**
	 * 返回底层的PropertyEditorRegistry.
	 * @return the PropertyEditorRegistry, or {@code null} if none
	 * available for this BindingResult
	 */
	@Nullable
	PropertyEditorRegistry getPropertyEditorRegistry();

	/**
	 * 将给定的错误代码解析为消息代码.
	 * <p>Calls the configured {@link MessageCodesResolver} with appropriate parameters.
	 * @param errorCode the error code to resolve into message codes
	 * @return the resolved message codes
	 */
	String[] resolveMessageCodes(String errorCode);

	/**
	 * 将给定的错误代码解析为给定字段的消息代码.
	 * <p>Calls the configured {@link MessageCodesResolver} with appropriate parameters.
	 * @param errorCode the error code to resolve into message codes
	 * @param field the field to resolve message codes for
	 * @return the resolved message codes
	 */
	String[] resolveMessageCodes(String errorCode, String field);

	/**
	 * 将自定义{@link ObjectError}或{@link FieldError}添加到错误列表.
	 * <p>目的在于给如{@link BindingErrorProcessor}之类的合作策略使用.
	 * @see ObjectError
	 * @see FieldError
	 * @see BindingErrorProcessor
	 */
	void addError(ObjectError error);

	/**
	 * 记录指定字段的给定值.
	 * <p>To be used when a target object cannot be constructed, making
	 * the original field values available through {@link #getFieldValue}.
	 * In case of a registered error, the rejected value will be exposed
	 * for each affected field.
	 * <p>在无法构造目标对象时使用，可通过{@link #getFieldValue}使原始字段值可用.
	 * 如果出现注册错误，将为每个受影响的字段显示拒绝值.
	 * @param field the field to record the value for
	 * @param type the type of the field
	 * @param value the original value
	 * @since 5.0.4
	 */
	default void recordFieldValue(String field, Class<?> type, @Nullable Object value) {
	}

	/**
	 * 将指定的禁止字段标记为已禁止.
	 * <p>The data binder invokes this for each field value that was
	 * detected to target a disallowed field.
	 * @see DataBinder#setAllowedFields
	 */
	default void recordSuppressedField(String field) {
	}

	/**
	 * 返回绑定过程中禁止显示的字段列表.
	 * <p>Can be used to determine whether any field values were targeting
	 * disallowed fields.
	 * @see DataBinder#setAllowedFields
	 */
	default String[] getSuppressedFields() {
		return new String[0];
	}

}
