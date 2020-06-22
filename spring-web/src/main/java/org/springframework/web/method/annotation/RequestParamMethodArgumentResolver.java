/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.web.method.annotation;

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 解析使用@{@link RequestParam}注释的方法参数，{@link MultipartFile}类型的参数需结合
 * Spring的{@link MultipartResolver}抽象以及结合Servlet 3.0 multipart请求的
 * {@code javax.servlet.http.Part}类型的参数。 也可以在默认解析模式下创建此解析器，在默认解析模式下，
 * 未使用{@link RequestParam @RequestParam}注释的简单类型（int，long等）也被视为具有从参数名称派生为请求参数名称。
 *
 * <p>如果方法参数类型为{@link Map}，则注释中指定的名称用于解析请求参数String值。 
 * 然后，假设已注册了适当的{@link Converter}或{@link PropertyEditor}，则通过类型转换将该值转换为{@link Map}。 
 * 或者，如果未指定请求参数名称，则使用{@link RequestParamMapMethodArgumentResolver}来以map的形式提供对所有请求参数的访问。
 *
 * <p>调用{@link WebDataBinder}将类型转换应用于尚未与方法参数类型匹配的已解析请求标头值。
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @since 3.1
 * @see RequestParamMapMethodArgumentResolver
 */
public class RequestParamMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver
		implements UriComponentsContributor {

	private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

	private final boolean useDefaultResolution;


	/**
	 * Create a new {@link RequestParamMethodArgumentResolver} instance.
	 * @param useDefaultResolution 在默认解析模式下，即使未注释，也可以将{@link BeanUtils#isSimpleProperty}
	 * 中定义的简单类型的方法参数视为请求参数(它的名称就是方法参数名称)，即使该请求参数未注释也是如此。   
	 */
	public RequestParamMethodArgumentResolver(boolean useDefaultResolution) {
		this.useDefaultResolution = useDefaultResolution;
	}

	/**
	 * Create a new {@link RequestParamMethodArgumentResolver} instance.
	 * @param beanFactory 一个用于以解析默认值中包含${...}占位符和#{...} SpEL表达式的bean工厂，
	 * 如果默认值包含不希望的表达式，则返回{@code null}  
	 * @param useDefaultResolution in default resolution mode a method argument
	 * that is a simple type, as defined in {@link BeanUtils#isSimpleProperty},
	 * is treated as a request parameter even if it isn't annotated, the
	 * request parameter name is derived from the method parameter name.
	 */
	public RequestParamMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory,
			boolean useDefaultResolution) {

		super(beanFactory);
		this.useDefaultResolution = useDefaultResolution;
	}


	/**
	 * 支持以下内容：
	 * <ul>
	 * <li>@RequestParam 注释的方法参数。 这不包括注释未指定名称的{@link Map}参数。
	 * 关于它请参阅{@link RequestParamMapMethodArgumentResolver}以获得此类参数。
	 * <li>没有使用@{@link RequestPart}注释的MultipartFile类型的参数。
	 * <li>没有使用@{@link RequestPart}注释的{@code Part}类型的参数。
	 * <li>在默认解析模式下，即使不使用@{@link RequestParam}，也要解析简单类型参数。
	 * </ul>
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (parameter.hasParameterAnnotation(RequestParam.class)) {
			if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
				RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
				// 注释需要指定请求参数名称
				return (requestParam != null && StringUtils.hasText(requestParam.name()));
			}
			else {
				return true;
			}
		}
		else {
			if (parameter.hasParameterAnnotation(RequestPart.class)) {
				return false;
			}
			parameter = parameter.nestedIfOptional();
			// 默认解析MultipartFile或Part类型的参数
			if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
				return true;
			}
			else if (this.useDefaultResolution) { // 默认模式，则解析简单类型参数
				return BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
			}
			else {
				return false;
			}
		}
	}

	// 从@RequestParam获取配置值
	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		RequestParam ann = parameter.getParameterAnnotation(RequestParam.class);
		return (ann != null ? new RequestParamNamedValueInfo(ann) : new RequestParamNamedValueInfo());
	}

	// 根据请求参数名称获取参数值
	@Override
	@Nullable
	protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
		HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);

		if (servletRequest != null) {
			// 解析Multipart类型参数
			Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
			if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
				return mpArg; // 直接返回解析后Multipart类型值
			}
		}

		Object arg = null;
		MultipartRequest multipartRequest = request.getNativeRequest(MultipartRequest.class);
		if (multipartRequest != null) {
			List<MultipartFile> files = multipartRequest.getFiles(name);
			if (!files.isEmpty()) {
				arg = (files.size() == 1 ? files.get(0) : files);
			}
		}
		if (arg == null) {
			String[] paramValues = request.getParameterValues(name);
			if (paramValues != null) {
				arg = (paramValues.length == 1 ? paramValues[0] : paramValues);
			}
		}
		return arg;
	}

	// 请求中找不到对应值，则抛出相应的异常
	@Override
	protected void handleMissingValue(String name, MethodParameter parameter, NativeWebRequest request)
			throws Exception {

		HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
		if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
			if (servletRequest == null || !MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
				throw new MultipartException("Current request is not a multipart request");
			}
			else {
				throw new MissingServletRequestPartException(name);
			}
		}
		else {
			throw new MissingServletRequestParameterException(name,
					parameter.getNestedParameterType().getSimpleName());
		}
	}

	@Override
	public void contributeMethodArgument(MethodParameter parameter, @Nullable Object value,
			UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {

		Class<?> paramType = parameter.getNestedParameterType();
		if (Map.class.isAssignableFrom(paramType) || MultipartFile.class == paramType || Part.class == paramType) {
			return;
		}

		RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
		String name = (requestParam != null && StringUtils.hasLength(requestParam.name()) ?
				requestParam.name() : parameter.getParameterName());
		Assert.state(name != null, "Unresolvable parameter name");

		parameter = parameter.nestedIfOptional();
		if (value instanceof Optional) {
			value = ((Optional<?>) value).orElse(null);
		}

		if (value == null) {
			if (requestParam != null &&
					(!requestParam.required() || !requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE))) {
				return;
			}
			builder.queryParam(name);
		}
		else if (value instanceof Collection) {
			for (Object element : (Collection<?>) value) {
				element = formatUriValue(conversionService, TypeDescriptor.nested(parameter, 1), element);
				builder.queryParam(name, element);
			}
		}
		else {
			builder.queryParam(name, formatUriValue(conversionService, new TypeDescriptor(parameter), value));
		}
	}

	@Nullable
	protected String formatUriValue(
			@Nullable ConversionService cs, @Nullable TypeDescriptor sourceType, @Nullable Object value) {

		if (value == null) {
			return null;
		}
		else if (value instanceof String) {
			return (String) value;
		}
		else if (cs != null) {
			return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
		}
		else {
			return value.toString();
		}
	}


	private static class RequestParamNamedValueInfo extends NamedValueInfo {

		public RequestParamNamedValueInfo() {
			super("", false, ValueConstants.DEFAULT_NONE);
		}

		public RequestParamNamedValueInfo(RequestParam annotation) {
			super(annotation.name(), annotation.required(), annotation.defaultValue());
		}
	}

}
