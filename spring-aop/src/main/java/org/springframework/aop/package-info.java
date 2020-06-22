/**
 * 建立在AOP Alliance AOP互操作性接口上的Core Spring AOP接口。
 *
 * <p>Spring中可以使用任何AOP Alliance MethodInterceptor。
 *
 * <br>Spring AOP还提供：
 * <ul>
 * <li>Introduction(引入)支持，是通过将需要添加的新的行为逻辑，以新的接口定义增加到目标对象上。
 *
 * <li>一个切入点抽象，支持“静态”切入点（基于类和方法）和“动态”切入点（还考虑方法参数）。 当前没有AOP Alliance接口的切入点。
 * <li>各种各样的advice类型，包括around, before, after returning 和 throws advice。
 * <li>可扩展性允许在不修改核心框架的情况下插入任意自定义advice类型。
 * </ul>
 *
 * <p>Spring AOP可以通过编程方式使用，或者最好与Spring IoC容器集成。
 */
@NonNullApi
@NonNullFields
package org.springframework.aop;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
