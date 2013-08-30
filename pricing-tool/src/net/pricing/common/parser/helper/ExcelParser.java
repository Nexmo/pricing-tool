/*
 * Copyright (c) 2011-2013 Nexmo Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pricing.common.parser.helper;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import net.pricing.common.utils.FileUtils;
import net.pricing.common.utils.PricingConstants;
import net.pricing.common.utils.PricingRow;

/**
 * @author Yuliia Petrushenko
 * @version
 */

public class ExcelParser {

	private static final Logger logger = Logger.getLogger(ExcelParser.class);

	public List<PricingRow> parseFromExcel(String[] columns, File file,
			Integer... sheetIndex) {
		try {
			if (PricingConstants.XLS_FILE_EXTENSION.equals(FileUtils
					.getExtension(file.getName()))) {
				return new XLSParser()
						.parseFromExcel(columns, file, sheetIndex);
			} else if (PricingConstants.XLSX_FILE_EXTENSION.equals(FileUtils
					.getExtension(file.getName()))) {
				return new XLSXParser().parseFromExcel(columns, file,
						sheetIndex);
			} else {
				logger.debug("[parseFromExcel] ---------> File extension does not match the parser");
				return Collections.emptyList();
			}

		} catch (Exception e) {
			logger.debug("[parseFromExcel] Exception using Apache POI" + e.toString() +"\n Launching JXLExcelParser..");
			return new JXLExcelParser().parseFromExcel(file, columns,
					sheetIndex);
		}

	}
}
