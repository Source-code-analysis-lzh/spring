/**
 * 通过自动创建AOP代理而不需要使用ProxyFactoryBean，可在ApplicationContexts中使用的Bean后处理器可以简化AOP的使用。
 *
 * <p>仅需要将此包中的各种后处理器添加到ApplicationContext（通常在XML bean定义文档中），即可自动代理选定的bean。
 *
 * <p><b>NB</b>:BeanFactory实现不支持自动自动代理，因为仅在应用程序上下文中自动检测后处理器Bean。 
 * 可以将后处理器显式注册在ConfigurableBeanFactory上。
 */
@NonNullApi
@NonNullFields
package org.springframework.aop.framework.autoproxy;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
