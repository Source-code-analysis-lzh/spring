/**
 * Spring的Web MVC框架的主题支持类。 提供标准的ThemeResolver实现，
 * 以及用于主题更改的HandlerInterceptor。
 *
 * <p>
 * <ul>
 * <li>如果您不提供这些类之一的bean作为{@code themeResolver}，
 * 则将为{@code FixedThemeResolver}提供默认主题名称'theme'。</li>
 * <li>If you use a defined {@code FixedThemeResolver}, you will able to use another theme
 * name for default, but the users will stick on this theme.</li>
 * <li>With a {@code CookieThemeResolver} or {@code SessionThemeResolver}, you can allow
 * the user to change his current theme.</li>
 * <li>Generally, you will put in the themes resource bundles the paths of CSS files, images and HTML constructs.</li>
 * <li>For retrieving themes data, you can either use the spring:theme tag in JSP or access via the
 * {@code RequestContext} for other view technologies.</li>
 * <li>The {@code pagedlist} demo application uses themes</li>
 * </ul>
 */
@NonNullApi
@NonNullFields
package org.springframework.web.servlet.theme;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
