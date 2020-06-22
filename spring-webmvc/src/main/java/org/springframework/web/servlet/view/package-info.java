/**
 * 提供标准的View和ViewResolver实现，包括用于自定义实现的抽象基类。
 *
 * <p>应用程序开发人员通常不需要实现视图，因为该框架为JSP，FreeMarker，XSLT等提供了标准视图。
 * 但是，如果对视图有特殊要求，则将应用程序中的AbstractView类子类化，则轻松实现自定义视图的功能将非常有用。
 */
@NonNullApi
@NonNullFields
package org.springframework.web.servlet.view;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
