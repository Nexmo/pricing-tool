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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.pricing.common.utils.FileUtils;
import net.pricing.common.utils.PricingColumn;
import net.pricing.common.utils.PricingConstants;
import net.pricing.common.utils.PricingRow;

import org.apache.log4j.Logger;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class JXLExcelParser {

	private static final Logger logger = Logger.getLogger(JXLExcelParser.class);

	protected String[] columns;
	protected List<PricingColumn> columnsIndexing;
	private int startRowIndex = 0;

	public List<PricingRow> parseFromExcel(File file, String[] columns,
			Integer... sheetNbrs) {
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

		try {
			logger.debug("[parseFromExcel] ---------> Start XLS file parsing");
			Workbook workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet(sheetNbr);
			findHeaderColumns(sheet);
			rowList = this.parseFromExcel(sheet);
			logger.debug("[parseFromExcel] ---------> Parsing completed");

		} catch (BiffException e) {
			logger.error("[parseFromExcel] BiffException " + e.getMessage());
		} catch (IOException e) {
			logger.error("[parseFromExcel] IOException " + e.getMessage());
		}
		return rowList;
	}

	protected void findHeaderColumns(Sheet sheet) {
		Cell[] cells = sheet.getRow(startRowIndex);
		for (int i = 0; i < cells.length; i++) {
			String headerCellValue = cells[i].getContents().toLowerCase();
			for (int j = 0; j < this.columns.length; j++) {
				if (headerCellValue.toLowerCase().equals(
						columns[j].toLowerCase())) {
					PricingColumn currentColumn = new PricingColumn();
					currentColumn.setIndex(i);
					currentColumn.setName(columns[j]);
					columnsIndexing.add(currentColumn);
					break;
				}
			}
		}
		if (columnsIndexing.isEmpty()
				|| columnsIndexing.size() != columns.length) {
			startRowIndex++;
			findHeaderColumns(sheet);

		}
	}

	protected List<PricingRow> parseFromExcel(Sheet sheet) {
		List<PricingRow> resultList = new ArrayList<PricingRow>();

		for (int i = startRowIndex + 1; i < sheet.getRows(); i++) {
			PricingRow pricingRow = new PricingRow();
			Cell[] currentRow = sheet.getRow(i);
			for (int j = 0; j < currentRow.length; j++) {
				for (PricingColumn currentColumn : columnsIndexing) {
					if (currentColumn.getIndex() == j) {
						PricingColumn pColumn = new PricingColumn();
						pColumn.setIndex(currentColumn.getIndex());
						pColumn.setName(currentColumn.getName());
						pColumn.setValue(currentRow[j].getContents());
						pricingRow.addColumn(pColumn);
					}

				}
			}
			if (pricingRow.getColumns() != null) {
				resultList.add(pricingRow);
			}
		}
		return resultList;
	}

}
