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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.pricing.common.utils.FileUtils;
import net.pricing.common.utils.PricingColumn;
import net.pricing.common.utils.PricingConstants;
import net.pricing.common.utils.PricingRow;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class XLSParser extends POIExcelParser {

	private static final Logger logger = Logger.getLogger(XLSParser.class);

	public List<PricingRow> parseFromExcel(String[] columns, File file,
			Integer... sheetNbrs) throws IOException {

		int sheetNbr = 0;
		if (sheetNbrs != null && sheetNbrs.length >= 1) {
			sheetNbr = sheetNbrs[0];
		}

		List<PricingRow> rowList = new ArrayList<PricingRow>();
		if (!FileUtils.isFileExtMatchesTheParser(file.getName(),
				PricingConstants.XLS_FILE_EXTENSION)) {
			return rowList;
		}

		this.columns = new String[columns.length];
		this.columns = columns;
		columnsIndexing = new ArrayList<PricingColumn>();

		logger.debug("[parseFromExcel] ---------> Start XLS file parsing");
		FileInputStream fileIn = new FileInputStream(file);
		HSSFWorkbook workbook = new HSSFWorkbook(fileIn);
		HSSFSheet sheet = workbook.getSheetAt(sheetNbr);
		Iterator<Row> rowIterator = sheet.iterator();
		findHeaderColumns(rowIterator);
		rowList = this.parseFromExcel(rowIterator);
		fileIn.close();
		logger.debug("[parseFromExcel] ---------> Parsing completed");
		return rowList;

	}

	public File parseToExcel(File file) {
		if (!FileUtils.isFileExtMatchesTheParser(file.getName(),
				PricingConstants.CSV_FILE_EXTENSION)) {
			return null;
		}

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		parseToExcel(file, sheet);
		File xlsFile = null;
		try {
			xlsFile = FileUtils.createFile(PricingConstants.TEMP_FOLDER_NAME
					+ "priceUpdates.xls");
			FileOutputStream out = new FileOutputStream(xlsFile);

			workbook.write(out);
			out.close();
			logger.debug("[parseToExcel]--------->Excel file was written successfully..");
		} catch (IOException e) {
			logger.error("[parseToExcel] IOException " + e.getMessage());
		}
		return xlsFile;
	}
}
