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

package org.springframework.web.method.support;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.SimpleSessionStatus;

/**
 * 记录模型和视图在调用控制器方法的过程中{@link HandlerMethodArgumentResolver HandlerMethodArgumentResolvers}
 * 和{@link HandlerMethodReturnValueHandler HandlerMethodReturnValueHandlers}做出的相关决策。
 *
 * <p>{@link #setRequestHandled}标志可用于指示请求已直接处理，并且不需要视图解析。
 *
 * <p>A default {@link Model} is automatically created at instantiation.
 * An alternate model instance may be provided via {@link #setRedirectModel}
 * for use in a redirect scenario. When {@link #setRedirectModelScenario} is set
 * to {@code true} signalling a redirect scenario, the {@link #getModel()}
 * returns the redirect model instead of the default model.
 * <p>实例化时会自动创建一个默认模型{@link Model}。 可以通过{@link #setRedirectModel}提供替代模型实例，
 * 以用于重定向场景。 当{@link #setRedirectModelScenario}设置为{@code true}表示重定向场景时，
 * {@link #getModel()}返回重定向模型，而不是默认模型。
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ModelAndViewContainer {

	private boolean ignoreDefaultModelOnRedirect = false;

	@Nullable
	private Object view;

	private final ModelMap defaultModel = new BindingAwareModelMap();

	@Nullable
	private ModelMap redirectModel;

	private boolean redirectModelScenario = false;

	@Nullable
	private HttpStatus status;

	private final Set<String> noBinding = new HashSet<>(4);

	private final Set<String> bindingDisabled = new HashSet<>(4);

	private final SessionStatus sessionStatus = new SimpleSessionStatus();

	private boolean requestHandled = false;


	/**
	 * By default the content of the "default" model is used both during
	 * rendering and redirect scenarios. Alternatively controller methods
	 * can declare an argument of type {@code RedirectAttributes} and use
	 * it to provide attributes to prepare the redirect URL.
	 * <p>Setting this flag to {@code true} guarantees the "default" model is
	 * never used in a redirect scenario even if a RedirectAttributes argument
	 * is not declared. Setting it to {@code false} means the "default" model
	 * may be used in a redirect if the controller method doesn't declare a
	 * RedirectAttributes argument.
	 * <p>The default setting is {@code false}.
	 */
	public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
		this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
	}

	/**
	 * Set a view name to be resolved by the DispatcherServlet via a ViewResolver.
	 * Will override any pre-existing view name or View.
	 */
	public void setViewName(@Nullable String viewName) {
		this.view = viewName;
	}

	/**
	 * Return the view name to be resolved by the DispatcherServlet via a
	 * ViewResolver, or {@code null} if a View object is set.
	 */
	@Nullable
	public String getViewName() {
		return (this.view instanceof String ? (String) this.view : null);
	}

	/**
	 * Set a View object to be used by the DispatcherServlet.
	 * Will override any pre-existing view name or View.
	 */
	public void setView(@Nullable Object view) {
		this.view = view;
	}

	/**
	 * Return the View object, or {@code null} if we using a view name
	 * to be resolved by the DispatcherServlet via a ViewResolver.
	 */
	@Nullable
	public Object getView() {
		return this.view;
	}

	/**
	 * 该视图是否是通过名称指定的视图引用，该名称由DispatcherServlet通过ViewResolver解析。
	 */
	public boolean isViewReference() {
		return (this.view instanceof String);
	}

	/**
	 * Return the model to use -- either the "default" or the "redirect" model.
	 * The default model is used if {@code redirectModelScenario=false} or
	 * there is no redirect model (i.e. RedirectAttributes was not declared as
	 * a method argument) and {@code ignoreDefaultModelOnRedirect=false}.
	 */
	public ModelMap getModel() {
		if (useDefaultModel()) {
			return this.defaultModel;
		}
		else {
			if (this.redirectModel == null) {
				this.redirectModel = new ModelMap();
			}
			return this.redirectModel;
		}
	}

	/**
	 * Whether to use the default model or the redirect model.
	 */
	private boolean useDefaultModel() {
		return (!this.redirectModelScenario || (this.redirectModel == null && !this.ignoreDefaultModelOnRedirect));
	}

	/**
	 * 返回实例化时创建的“默认”模型。
	 * <p>In general it is recommended to use {@link #getModel()} instead which
	 * returns either the "default" model (template rendering) or the "redirect"
	 * model (redirect URL preparation). Use of this method may be needed for
	 * advanced cases when access to the "default" model is needed regardless,
	 * e.g. to save model attributes specified via {@code @SessionAttributes}.
	 * <p>通常，建议使用{@link #getModel()}代替它返回“默认”模型（模板渲染）
	 * 或“重定向”模型（重定向URL准备）。 在高级情况下，无论是否需要访问“默认”模型，都可能需要使用此方法。 
	 * 如，保存通过{@code @SessionAttributes}指定的模型属性。
	 * @return the default model (never {@code null})
	 * @since 4.1.4
	 */
	public ModelMap getDefaultModel() {
		return this.defaultModel;
	}

	/**
	 * Provide a separate model instance to use in a redirect scenario.
	 * <p>The provided additional model however is not used unless
	 * {@link #setRedirectModelScenario} gets set to {@code true}
	 * to signal an actual redirect scenario.
	 */
	public void setRedirectModel(ModelMap redirectModel) {
		this.redirectModel = redirectModel;
	}

	/**
	 * Whether the controller has returned a redirect instruction, e.g. a
	 * "redirect:" prefixed view name, a RedirectView instance, etc.
	 */
	public void setRedirectModelScenario(boolean redirectModelScenario) {
		this.redirectModelScenario = redirectModelScenario;
	}

	/**
	 * Provide an HTTP status that will be passed on to with the
	 * {@code ModelAndView} used for view rendering purposes.
	 * @since 4.3
	 */
	public void setStatus(@Nullable HttpStatus status) {
		this.status = status;
	}

	/**
	 * Return the configured HTTP status, if any.
	 * @since 4.3
	 */
	@Nullable
	public HttpStatus getStatus() {
		return this.status;
	}

	/**
	 * Programmatically register an attribute for which data binding should not occur,
	 * not even for a subsequent {@code @ModelAttribute} declaration.
	 * @param attributeName the name of the attribute
	 * @since 4.3
	 */
	public void setBindingDisabled(String attributeName) {
		this.bindingDisabled.add(attributeName);
	}

	/**
	 * Whether binding is disabled for the given model attribute.
	 * @since 4.3
	 */
	public boolean isBindingDisabled(String name) {
		return (this.bindingDisabled.contains(name) || this.noBinding.contains(name));
	}

	/**
	 * Register whether data binding should occur for a corresponding model attribute,
	 * corresponding to an {@code @ModelAttribute(binding=true/false)} declaration.
	 * <p>Note: While this flag will be taken into account by {@link #isBindingDisabled},
	 * a hard {@link #setBindingDisabled} declaration will always override it.
	 * @param attributeName the name of the attribute
	 * @since 4.3.13
	 */
	public void setBinding(String attributeName, boolean enabled) {
		if (!enabled) {
			this.noBinding.add(attributeName);
		}
		else {
			this.noBinding.remove(attributeName);
		}
	}

	/**
	 * Return the {@link SessionStatus} instance to use that can be used to
	 * signal that session processing is complete.
	 */
	public SessionStatus getSessionStatus() {
		return this.sessionStatus;
	}

	/**
	 * Whether the request has been handled fully within the handler, e.g.
	 * {@code @ResponseBody} method, and therefore view resolution is not
	 * necessary. This flag can also be set when controller methods declare an
	 * argument of type {@code ServletResponse} or {@code OutputStream}).
	 * <p>The default value is {@code false}.
	 */
	public void setRequestHandled(boolean requestHandled) {
		this.requestHandled = requestHandled;
	}

	/**
	 * Whether the request has been handled fully within the handler.
	 */
	public boolean isRequestHandled() {
		return this.requestHandled;
	}

	/**
	 * Add the supplied attribute to the underlying model.
	 * A shortcut for {@code getModel().addAttribute(String, Object)}.
	 */
	public ModelAndViewContainer addAttribute(String name, @Nullable Object value) {
		getModel().addAttribute(name, value);
		return this;
	}

	/**
	 * Add the supplied attribute to the underlying model.
	 * A shortcut for {@code getModel().addAttribute(Object)}.
	 */
	public ModelAndViewContainer addAttribute(Object value) {
		getModel().addAttribute(value);
		return this;
	}

	/**
	 * 将所有属性复制到底层模型。
	 * A shortcut for {@code getModel().addAllAttributes(Map)}.
	 */
	public ModelAndViewContainer addAllAttributes(@Nullable Map<String, ?> attributes) {
		getModel().addAllAttributes(attributes);
		return this;
	}

	/**
	 * Copy attributes in the supplied {@code Map} with existing objects of
	 * the same name taking precedence (i.e. not getting replaced).
	 * A shortcut for {@code getModel().mergeAttributes(Map<String, ?>)}.
	 */
	public ModelAndViewContainer mergeAttributes(@Nullable Map<String, ?> attributes) {
		getModel().mergeAttributes(attributes);
		return this;
	}

	/**
	 * Remove the given attributes from the model.
	 */
	public ModelAndViewContainer removeAttributes(@Nullable Map<String, ?> attributes) {
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				getModel().remove(key);
			}
		}
		return this;
	}

	/**
	 * Whether the underlying model contains the given attribute name.
	 * A shortcut for {@code getModel().containsAttribute(String)}.
	 */
	public boolean containsAttribute(String name) {
		return getModel().containsAttribute(name);
	}


	/**
	 * Return diagnostic information.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ModelAndViewContainer: ");
		if (!isRequestHandled()) {
			if (isViewReference()) {
				sb.append("reference to view with name '").append(this.view).append("'");
			}
			else {
				sb.append("View is [").append(this.view).append(']');
			}
			if (useDefaultModel()) {
				sb.append("; default model ");
			}
			else {
				sb.append("; redirect model ");
			}
			sb.append(getModel());
		}
		else {
			sb.append("Request handled directly");
		}
		return sb.toString();
	}

}
