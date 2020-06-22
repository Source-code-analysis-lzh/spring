/*
 * Copyright 2002-2020 the original author or authors.
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

import java.util.function.Predicate;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/**
 * Interface to be implemented by types that determine which @{@link Configuration}
 * class(es) should be imported based on a given selection criteria, usually one or
 * more annotation attributes.
 * 由类型决定的接口，这些类型根据给定的选择条件（通常是一个或多个注释属性）来确定应导入哪个@{@link Configuration}类。
 *
 * <p>该接口就是用来根据给定的条件，选择导入哪些配置类。
 * 
 * <p>An {@link ImportSelector} may implement any of the following
 * {@link org.springframework.beans.factory.Aware Aware} interfaces,
 * and their respective methods will be called prior to {@link #selectImports}:
 * <ul>
 * <li>{@link org.springframework.context.EnvironmentAware EnvironmentAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}</li>
 * <li>{@link org.springframework.context.ResourceLoaderAware ResourceLoaderAware}</li>
 * </ul>
 *
 * <p>Alternatively, the class may provide a single constructor with one or more of
 * the following supported parameter types:
 * <ul>
 * <li>{@link org.springframework.core.env.Environment Environment}</li>
 * <li>{@link org.springframework.beans.factory.BeanFactory BeanFactory}</li>
 * <li>{@link java.lang.ClassLoader ClassLoader}</li>
 * <li>{@link org.springframework.core.io.ResourceLoader ResourceLoader}</li>
 * </ul>
 *
 * <p>{@code ImportSelector}实现通常以与常规{@code @Import}注释相同的方式处理，但是，
 * 也可以将导入的选择推迟到所有{@code @Configuration}类都已处理完之后（有关详细信息，请参见{@link DeferredImportSelector}）。
 *
 * 该接口文档上说的明明白白，其主要作用是收集需要导入的配置类，
 * 如果该接口的实现类同时实现EnvironmentAware， BeanFactoryAware ，
 * BeanClassLoaderAware或者ResourceLoaderAware，
 * 那么在调用其selectImports方法之前先调用上述接口中对应的方法，
 * 如果需要在所有的@Configuration处理完再导入时可以实现DeferredImportSelector接口。
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see DeferredImportSelector
 * @see ImportClassPathBeanDefinitionScanner
 * @see ImportBeanDefinitionRegistrar
 * @see Configuration
 */
public interface ImportSelector {

	/**
	 * 根据导入{@link Configuration}类的{@link AnnotationMetadata}选择并返回要导入的类的名称。
	 * @return the class names, or an empty array if none
	 */
	String[] selectImports(AnnotationMetadata importingClassMetadata);

	/**
	 * Return a predicate for excluding classes from the import candidates, to be
	 * transitively applied to all classes found through this selector's imports.
	 * 返回一个谓词，从导入候选中排除类，该谓词将可传递地应用于通过此选择器的导入找到的所有类。
	 * <p>If this predicate returns {@code true} for a given fully-qualified
	 * class name, said class will not be considered as an imported configuration
	 * class, bypassing class file loading as well as metadata introspection.
	 * <p>如果该谓词对于给定的完全限定的类名返回{@code true}，则将不认为该类是导入的配置类，
	 * 从而绕过了类文件的加载以及元数据的内省。
	 * @return the filter predicate for fully-qualified candidate class names
	 * of transitively imported configuration classes, or {@code null} if none
	 * @since 5.2.4
	 */
	@Nullable
	default Predicate<String> getExclusionFilter() {
		return null;
	}

}
