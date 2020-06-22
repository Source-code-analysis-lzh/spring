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

package org.springframework.web.servlet.view.document;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Office 2007 XLSX格式（受POI-OOXML支持）的Excel文档视图的便利超类。 
 * 与Apache POI 3.5及更高版本兼容。
 *
 * <p>For working with the workbook in subclasses, see
 * <a href="https://poi.apache.org">Apache's POI site</a>.
 *
 * @author Juergen Hoeller
 * @since 4.2
 */
public abstract class AbstractXlsxView extends AbstractXlsView {

	/**
	 * Default Constructor.
	 * <p>Sets the content type of the view to
	 * {@code "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}.
	 */
	public AbstractXlsxView() {
		setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}

	/**
	 * This implementation creates an {@link XSSFWorkbook} for the XLSX format.
	 */
	@Override
	protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
		return new XSSFWorkbook();
	}

}
