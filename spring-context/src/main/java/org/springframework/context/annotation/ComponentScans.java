/*
 * Copyright 2002-2015 the original author or authors.
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
 * 容器注释聚合了几个{@link ComponentScan}注释。
 *
 * <p>可以本地使用，声明几个嵌套的{@link ComponentScan}注解。 也可以与Java 8对可重复注释的支持结合使用，
 * 在Java 8中，可以简单地在同一方法上多次声明{@link ComponentScan}，从而隐式生成此容器注释。
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @see ComponentScan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScans {

	ComponentScan[] value();

}
