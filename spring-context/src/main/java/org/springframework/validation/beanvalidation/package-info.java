/**
 * 支持类，用于将JSR-303 Bean验证提供程序（例如Hibernate Validator）
 * 集成到Spring ApplicationContext中，尤其是通过Spring的数据绑定和验证API.
 *
 * <p>中心类是{@link org.springframework.validation.beanvalidation.LocalValidatorFactoryBean}，
 * 它定义了一个共享的 ValidatorFactory/Validator 设置，以供其它Spring组件使用.
 */
@NonNullApi
@NonNullFields
package org.springframework.validation.beanvalidation;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
