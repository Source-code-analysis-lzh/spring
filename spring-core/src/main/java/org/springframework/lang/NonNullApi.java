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

package org.springframework.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * 一个通用的Spring注释，用于声明对于给定的包，参数和返回值默认情况下被视为不可为空.
 *
 * <p>利用JSR-305元注释在支持JSR-305的通用工具上指示Java中的可空性，并由Kotlin用来推断Spring API的可空性.
 *
 * <p>应与参数和返回值级别的{@link Nullable}注释一起在程序包级别上使用.
 *
 * @author Sebastien Deleuze
 * @author Juergen Hoeller
 * @since 5.0
 * @see NonNullFields
 * @see Nullable
 * @see NonNull
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull
@TypeQualifierDefault({ElementType.METHOD, ElementType.PARAMETER})
public @interface NonNullApi {
}
