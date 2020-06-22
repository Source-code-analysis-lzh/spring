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

package org.springframework.beans;

/**
 * 用于向{@link org.springframework.beans.PropertyEditorRegistry 属性编辑器注册表}
 * 注册自定义{@link java.beans.PropertyEditor 属性编辑器}的策略的接口.
 *
 * <p>当您需要在几种不同情况下使用同一组属性编辑器时，这特别有用：编写相应的注册器，并在每种情况下重用该注册器.
 *
 * @author Juergen Hoeller
 * @since 1.2.6
 * @see PropertyEditorRegistry
 * @see java.beans.PropertyEditor
 */
public interface PropertyEditorRegistrar {

	/**
	 * 使用给定的{@code PropertyEditorRegistry}注册自定义
	 * {@link java.beans.PropertyEditor PropertyEditors}。
	 * <p>传入的注册表通常是{@link BeanWrapper}或
	 * {@link org.springframework.validation.DataBinder DataBinder}。
	 * <p>期望实现将为此方法的每次调用创建全新的{@code PropertyEditors}实例
	 * （因为{@code PropertyEditors}不是线程安全的）。
	 * @param registry the {@code PropertyEditorRegistry} to register the
	 * custom {@code PropertyEditors} with
	 */
	void registerCustomEditors(PropertyEditorRegistry registry);

}
