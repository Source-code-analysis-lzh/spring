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

package org.springframework.context.support;

import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.lang.Nullable;

/**
 * 工厂可方便地访问配置有适用于大多数环境的转换器的ConversionService.
 * 设置{@link #setConverters "converters"}属性用自定义转换器补充默认转换器.
 *
 * <p>此实现创建一个{@link DefaultConversionService}.
 * 子类可以重写{@link #createConversionService()}以便返回其选择的{@link GenericConversionService}实例.
 *
 * <p>与所有{@code FactoryBean}实现一样，此类适用于在使用Spring {@code <beans>} XML配置Spring应用程序上下文时使用.
 * 使用{@link org.springframework.context.annotation.Configuration @Configuration}类配置容器时，
 * 只需实例化，配置并从{@link
 * org.springframework.context.annotation.Bean @Bean}注解方法返回适当的{@code ConversionService}对象.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0
 */
public class ConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean {

	@Nullable
	private Set<?> converters;

	@Nullable
	private GenericConversionService conversionService;


	/**
	 * Configure the set of custom converter objects that should be added:
	 * implementing {@link org.springframework.core.convert.converter.Converter},
	 * {@link org.springframework.core.convert.converter.ConverterFactory},
	 * or {@link org.springframework.core.convert.converter.GenericConverter}.
	 */
	public void setConverters(Set<?> converters) {
		this.converters = converters;
	}

	@Override
	public void afterPropertiesSet() {
		this.conversionService = createConversionService();
		ConversionServiceFactory.registerConverters(this.converters, this.conversionService);
	}

	/**
	 * Create the ConversionService instance returned by this factory bean.
	 * <p>Creates a simple {@link GenericConversionService} instance by default.
	 * Subclasses may override to customize the ConversionService instance that
	 * gets created.
	 */
	protected GenericConversionService createConversionService() {
		return new DefaultConversionService();
	}


	// implementing FactoryBean

	@Override
	@Nullable
	public ConversionService getObject() {
		return this.conversionService;
	}

	@Override
	public Class<? extends ConversionService> getObjectType() {
		return GenericConversionService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
