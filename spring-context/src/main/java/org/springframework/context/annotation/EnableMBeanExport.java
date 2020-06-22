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

package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * 启用从Spring上下文以及所有{@code @ManagedResource}注释的bean的默认导出所有标准{@code MBean}。
 *
 * <p>生成的{@link org.springframework.jmx.export.MBeanExporter MBeanExporter} bean
 * 的名称为"mbeanExporter"。 或者，考虑显式定义自定义{@link AnnotationMBeanExporter} bean。
 *
 * <p>该注释的功能等效于Spring XML的{@code <context:mbean-export/>}元素。
 *
 * @author Phillip Webb
 * @since 3.2
 * @see MBeanExportConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MBeanExportConfiguration.class)
public @interface EnableMBeanExport {

	/**
	 * The default domain to use when generating JMX ObjectNames.
	 */
	String defaultDomain() default "";

	/**
	 * The bean name of the MBeanServer to which MBeans should be exported. Default is to
	 * use the platform's default MBeanServer.
	 */
	String server() default "";

	/**
	 * The policy to use when attempting to register an MBean under an
	 * {@link javax.management.ObjectName} that already exists. Defaults to
	 * {@link RegistrationPolicy#FAIL_ON_EXISTING}.
	 */
	RegistrationPolicy registration() default RegistrationPolicy.FAIL_ON_EXISTING;
}
