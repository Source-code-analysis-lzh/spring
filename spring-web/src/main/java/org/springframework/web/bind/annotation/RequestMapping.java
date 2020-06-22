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

package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * 用于将Web请求映射到具有灵活方法签名的请求处理类中的方法的注释。
 *
 * <p>Spring MVC和Spring WebFlux都通过各自模块和包结构中的
 * {@code RequestMappingHandlerMapping} 和 {@code RequestMappingHandlerAdapter}来支持此注释。
 * 有关每个中支持的处理器方法参数和返回类型的确切列表，请使用下面的参考文档链接：
 * <ul>
 * <li>Spring MVC
 * <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-arguments">Method Arguments</a>
 * and
 * <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-return-types">Return Values</a>
 * </li>
 * <li>Spring WebFlux
 * <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-ann-arguments">Method Arguments</a>
 * and
 * <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-ann-return-types">Return Values</a>
 * </li>
 * </ul>
 *
 * <p><strong>Note:</strong> This annotation can be used both at the class and
 * at the method level. In most cases, at the method level applications will
 * prefer to use one of the HTTP method specific variants
 * {@link GetMapping @GetMapping}, {@link PostMapping @PostMapping},
 * {@link PutMapping @PutMapping}, {@link DeleteMapping @DeleteMapping}, or
 * {@link PatchMapping @PatchMapping}.</p>
 *
 * <p><b>NOTE:</b> When using controller interfaces (e.g. for AOP proxying),
 * make sure to consistently put <i>all</i> your mapping annotations - such as
 * {@code @RequestMapping} and {@code @SessionAttributes} - on
 * the controller <i>interface</i> rather than on the implementation class.
 * 
 *
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @since 2.5
 * @see GetMapping
 * @see PostMapping
 * @see PutMapping
 * @see DeleteMapping
 * @see PatchMapping
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 * @see org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMapping {

	/**
	 * 为该映射分配名称。
	 * <p>在类级别和方法级别都受支持！ 当在两个级别上使用时，组合名称是通过以“＃”作为分隔符的串联而派生的。
	 * @see org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
	 * @see org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy
	 */
	String name() default "";

	/**
	 * 此注释表示的主要映射。
	 * <p>This is an alias for {@link #path}. For example,
	 * <p>这是{@link #path}的别名。 例如，{@code @RequestMapping("/foo")}等同于
	 * {@code @RequestMapping(path="/foo")}。
	 * <p>在类级别和方法级别都受支持！ 当在类级别使用时，所有方法级别的映射都继承此主映射，
	 * 从而将其缩小为特定的处理器方法。
	 * <p>注意：未显式映射到任何路径的处理器方法将有效地映射到空路径。
	 */
	@AliasFor("path")
	String[] value() default {};

	/**
	 * 路径映射URI（例如{@code "/profile"}）。
	 * <p>还支持ant风格的路径模式（例如{@code "/profile/**"}）。 在方法级别（例如{@code "edit"}），
	 * 表示映射支持相对类型级别的路径。 路径映射URI可能包含占位符（例如<code>"/${profile_path}"</code>）。
	 * <p>在类级别和方法级别都受支持！ 当在类级别使用时，所有方法级别的映射都继承此主映射，
	 * 从而将其缩小为特定的处理器方法。
	 * <p><strong>NOTE</strong>: A handler method that is not mapped to any path
	 * explicitly is effectively mapped to an empty path.
	 * @since 4.2
	 */
	@AliasFor("value")
	String[] path() default {};

	/**
	 * 要映射到的HTTP请求方法，缩小了主要映射的范围：
	 * GET，POST，HEAD，OPTIONS，PUT，PATCH，DELETE，TRACE。
	 * <p>在类级别和方法级别都受支持！ 当在类级别使用时，所有方法级别的映射都继承此HTTP方法限制
	 * （即，甚至在解析处理器方法之前都会检查类级别的限制）。
	 */
	RequestMethod[] method() default {};

	/**
	 * 映射请求的参数，从而缩小了主映射的范围。
	 * <p>任何环境的格式均相同："myParam=myValue"样式表达式的序列，
	 * 仅当发现每个这样的参数都具有给定值时才映射请求。 可以使用"!="运算符来取反表达式，
	 * 如"myParam!=myValue"。 还支持"myParam"样式表达式，此类参数必须存在于请求中（允许具有任何值）。
	 * 最后，"!myParam"样式表达式指示指定的参数不应该出现在请求中。
	 * <p>在类级别和方法级别都受支持！ 当在类级别使用时，所有方法级别的映射都继承此参数限制
	 * （即，甚至在解析处理器方法之前都会检查类级别的限制）。
	 * <p>Parameter mappings are considered as restrictions that are enforced at
	 * the type level. The primary path mapping (i.e. the specified URI value)
	 * still has to uniquely identify the target handler, with parameter mappings
	 * simply expressing preconditions for invoking the handler.
	 * <p>参数映射被认为是在类级别上强制执行的限制。 主路径映射（即指定的URI值）
	 * 仍然必须唯一地标识目标处理器，而参数映射仅表示调用处理器的前提条件。
	 */
	String[] params() default {};

	/**
	 * 映射请求的标头，缩小了主映射的范围。
	 * <p>任何环境的格式均相同："My-Header=myValue"样式表达式序列，仅当发现每个此类标头具有给定值时，
	 * 才映射请求。 可以使用"!="运算符来否定表达式，如"My-Header!=myValue"中所示。 
	 * 还支持"My-Header"样式表达式，此类标头必须存在于请求中（允许具有任何值）。 
	 * 最后，"!My-Header"样式表达式指示请求中不应存在指定的标头。
	 * <p>还支持媒体类型通配符（*），用于诸如Accept和Content-Type的标头。 例如，
	 * <pre class="code">
	 * &#064;RequestMapping(value = "/something", headers = "content-type=text/*")
	 * </pre>
	 * 将匹配Content-Type为"text/html", "text/plain"等的请求。
	 * <p>在类级别和方法级别都受支持！ 在类级别使用时，所有方法级别的映射都继承此标头限制
	 * （即，甚至在解析处理器方法之前都会检查类级别的限制）。
	 * @see org.springframework.http.MediaType
	 */
	String[] headers() default {};

	/**
	 * 通过可以由映射的处理器使用的媒体类型缩小主映射。 由一种或多种媒体类型组成，
	 * 其中一种必须与请求的{@code Content-Type}标头匹配。 例子：
	 * <pre class="code">
	 * consumes = "text/plain"
	 * consumes = {"text/plain", "application/*"}
	 * consumes = MediaType.TEXT_PLAIN_VALUE
	 * </pre>
	 * 可以使用"!"运算符取反表达式。 如"!text/plain"中所示，该操作符匹配除"text/plain"
	 * 以外的所有{@code Content-Type}请求。
	 * <p>在类级别和方法级别都受支持！ 如果在两个级别都指定，则方法级别的consumes条件将覆盖类级别的条件。
	 * @see org.springframework.http.MediaType
	 * @see javax.servlet.http.HttpServletRequest#getContentType()
	 */
	String[] consumes() default {};

	/**
	 * 通过可以由映射的处理器生成的媒体类型来缩小主映射。 由一种或多种媒体类型组成，
	 * 其中一种必须通过针对请求的"acceptable"媒体类型的内容协商来选择。 
	 * 通常，这些是从{@code "Accept"}标头中提取的，但也可以从查询参数或其它参数派生。 例子：
	 * <pre class="code">
	 * produces = "text/plain"
	 * produces = {"text/plain", "application/*"}
	 * produces = MediaType.TEXT_PLAIN_VALUE
	 * produces = "text/plain;charset=UTF-8"
	 * </pre>
	 * <p>如果声明的媒体类型包含参数（例如"charset=UTF-8", "type=feed", type="entry"），
	 * 并且请求中的兼容媒体类型也具有该参数，则参数值必须匹配 。 否则，如果请求中的媒体类型不包含参数，
	 * 则假定客户端接受任何值。
	 * <p>可以使用"!"运算符取反表达式。如"!text/plain"中所示，该操作符将所有请求与"text/plain"
	 * 以外的其它{@code Accept}进行匹配。
	 * <p>在类级别和方法级别都受支持！ 如果在两个级别都指定，则方法级别的产生条件将覆盖类级别的条件。
	 * @see org.springframework.http.MediaType
	 * @see org.springframework.http.MediaType
	 */
	String[] produces() default {};

}
