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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.pricing.common.utils.PricingColumn;
import net.pricing.common.utils.PricingConstants;
import net.pricing.common.utils.PricingRow;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public abstract class POIExcelParser {

	private static final Logger logger = Logger.getLogger(POIExcelParser.class);

	protected String[] columns;
	protected List<PricingColumn> columnsIndexing;

	public abstract List<PricingRow> parseFromExcel(String[] columns,
			File file, Integer... sheetNbrs) throws IOException;

	public abstract File parseToExcel(File file);

	protected void findHeaderColumns(Iterator<Row> rowIterator) {
		Row headerRow = rowIterator.next();
		Iterator<Cell> cellIt = headerRow.cellIterator();

		while (cellIt.hasNext()) {
			Cell headerCell = cellIt.next();
			String headerCellValue = cellValueToString(headerCell)
					.toLowerCase().replaceAll(" ", "");

			for (int i = 0; i < this.columns.length; i++) {
				String columnValue = columns[i].toLowerCase().replaceAll(" ",
						"");
				if (headerCellValue.toLowerCase().equals(columnValue)) {
					PricingColumn currentColumn = new PricingColumn();
					currentColumn.setIndex(headerCell.getColumnIndex());
					currentColumn.setName(columns[i]);
					columnsIndexing.add(currentColumn);
					break;
				}
			}
		}
		if (columnsIndexing.isEmpty()
				|| columnsIndexing.size() != columns.length) {
			findHeaderColumns(rowIterator);
		}
	}

	protected List<PricingRow> parseFromExcel(Iterator<Row> rowIterator) {
		List<PricingRow> resultList = new ArrayList<PricingRow>();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			Iterator<Cell> cellIterator = row.cellIterator();
			PricingRow pricingRow = new PricingRow();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				for (PricingColumn currentColumn : columnsIndexing) {
					if (currentColumn.getIndex() == cell.getColumnIndex()) {
						PricingColumn pColumn = new PricingColumn();
						pColumn.setIndex(currentColumn.getIndex());
						pColumn.setName(currentColumn.getName());
						pColumn.setValue(cellValueToString(cell));
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

	protected void parseToExcel(File fileForParsing, Sheet sheet) {
		try {
			FileInputStream fstream = new FileInputStream(fileForParsing);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			Map<String, Object[]> data = new HashMap<String, Object[]>();
			int counter = 1;
			String strLine;
			while ((strLine = br.readLine()) != null) {

				data.put(String.valueOf(counter++),
						strLine.split(PricingConstants.CSV_DELIMITER));
			}
			in.close();

			Set<String> keyset = data.keySet();
			int rownum = 0;
			for (String key : keyset) {
				Row row = sheet.createRow(rownum++);
				Object[] objArr = data.get(key);
				int cellnum = 0;
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof Date)
						cell.setCellValue((Date) obj);
					else if (obj instanceof Boolean)
						cell.setCellValue((Boolean) obj);
					else if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Double)
						cell.setCellValue((Double) obj);
				}
			}
		} catch (IOException e) {
			logger.error("[parseToExcel] IOException " + e.getMessage());
		}

	}

	protected String cellValueToString(Cell cell) {
		String cellValue = "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC: {
			cellValue = String.valueOf(cell.getNumericCellValue());
			break;
		}
		case Cell.CELL_TYPE_STRING: {
			cellValue = String.valueOf(cell.getStringCellValue());
			break;
		}
		case Cell.CELL_TYPE_BOOLEAN: {
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		}

		}
		return cellValue;
	}
}
