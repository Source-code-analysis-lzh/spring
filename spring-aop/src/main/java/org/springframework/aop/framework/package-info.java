/**
 * 包含Spring的基本AOP基础结构的软件包，
 * 遵循<a href="http://aopalliance.sourceforge.net">AOP Alliance</a>接口。
 *
 * <p>Spring AOP支持代理接口或类，引入，并提供静态和动态切入点。
 *
 * <p>任何Spring AOP代理都可以转换为此包中的ProxyConfig AOP配置接口，以添加或删除拦截器。
 *
 * <p>ProxyFactoryBean是在BeanFactory或ApplicationContext中创建AOP代理的便捷方法。 
 * 但是，可以使用ProxyFactory类以编程方式创建代理。
 */
@NonNullApi
@NonNullFields
package org.springframework.aop.framework;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
