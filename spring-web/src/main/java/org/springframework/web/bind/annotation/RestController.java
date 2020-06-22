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

package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

/**
 * 一个方便注释，其本身由{@link Controller @Controller}和
 * {@link ResponseBody @ResponseBody}进行注释。
 * 
 * <p>带有此注释的类型被视为控制器，其中{@link RequestMapping @RequestMapping}
 * 方法默认情况下采用{@link ResponseBody @ResponseBody}语义。
 *
 * <p>注意：如果配置了适当的{@code HandlerMapping}-{@code HandlerAdapter}对，
 * 例如{@code RequestMappingHandlerMapping}-{@code RequestMappingHandlerAdapter}对
 * (这是MVC Java配置和MVC名称空间中的默认值)，则{@code @RestController}将被处理。
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody
public @interface RestController {

	/**
	 * 该值可能表明建议使用逻辑组件名称，以在自动检测到组件的情况下将其转换为Spring bean。
	 * @return the suggested component name, if any (or empty String otherwise)
	 * @since 4.0.1
	 */
	@AliasFor(annotation = Controller.class)
	String value() default "";

}
