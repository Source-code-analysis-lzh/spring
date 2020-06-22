/*
 * Copyright 2002-2014 the original author or authors.
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

/**
 * 容器注释聚合了几个{@link PropertySource}注释。
 *
 * <p>可以本地使用，声明多个嵌套的{@link PropertySource}注释。 也可以与Java 8对可重复注释的支持结合使用，
 * 其中{@link PropertySource}可以简单地在同一{@linkplain ElementType#TYPE type}上声明多次，从而隐式生成此容器注释。
 *
 * @author Phillip Webb
 * @since 4.0
 * @see PropertySource
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertySources {

	PropertySource[] value();

}
