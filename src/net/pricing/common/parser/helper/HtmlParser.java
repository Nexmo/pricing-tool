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

import net.pricing.common.utils.FileUtils;
import net.pricing.common.utils.PricingColumn;
import net.pricing.common.utils.PricingConstants;
import net.pricing.common.utils.PricingRow;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class HtmlParser {

	private final static Logger logger = Logger.getLogger(HtmlParser.class);

	private String[] columns;
	private List<PricingColumn> columnsIndexing;
	private int headerRowIndex = 0;
	private boolean noDataFound = false;

	public List<PricingRow> parseFile(File file, String[] columns) {
		List<PricingRow> rowList = new ArrayList<PricingRow>();

		if (!FileUtils.isFileExtMatchesTheParser(file.getName(),
				PricingConstants.HTML_FILE_EXTENSION)) {
			return rowList;
		}

		logger.debug("[parseFile] ---------> Start HTML file parsing");

		this.columns = new String[columns.length];
		this.columns = columns;
		columnsIndexing = new ArrayList<PricingColumn>();

		try {
			Document doc = Jsoup.parse(file, null);

			Elements trs = doc.select("table tr");
			findColumnIndexes(trs);
			if (!noDataFound) {
				rowList = this.parse(trs);
				logger.debug("[parseFile] ---------> Parsing completed");
			}
		} catch (IOException e) {
			logger.error("[parseFile] IOException " + e.getMessage());
		}
		return rowList;

	}

	private void findColumnIndexes(Elements trs) {
		if (headerRowIndex < trs.size()) {
			Elements tdsHeader = trs.get(headerRowIndex).select("th");
			if (tdsHeader == null || tdsHeader.isEmpty()) {
				tdsHeader = trs.get(headerRowIndex).select("td");
			}

			int colInd = 0;
			for (Element td : tdsHeader) {
				String tdValue = td.text().toLowerCase().replaceAll(" ", "");
				for (int i = 0; i < this.columns.length; i++) {
					String columnValue = columns[i].toLowerCase().replaceAll(
							" ", "");
					if (tdValue.equals(columnValue)) {
						PricingColumn currentColumn = new PricingColumn();
						currentColumn.setIndex(colInd);
						currentColumn.setName(columns[i]);
						columnsIndexing.add(currentColumn);
						break;
					}
				}
				colInd++;
			}
			trs.remove(headerRowIndex);
			if (columnsIndexing.size() != columns.length) {
				headerRowIndex++;
				findColumnIndexes(trs);
			}
		} else {
			logger.debug("[findColumnIndexes] -->No data for parsing found");
			noDataFound = true;

		}
	}

	private List<PricingRow> parse(Elements trs) {

		List<PricingRow> returnList = new ArrayList<PricingRow>();
		int colInd = 0;
		for (Element tr : trs) {
			colInd = 0;
			PricingRow pricingRow = new PricingRow();
			Elements tds = tr.getElementsByTag("td");
			for (Element td : tds) {
				for (PricingColumn currentColumn : columnsIndexing) {
					if (currentColumn.getIndex() == colInd) {
						PricingColumn pColumn = new PricingColumn();
						pColumn.setIndex(currentColumn.getIndex());
						pColumn.setName(currentColumn.getName());
						pColumn.setValue(td.text());
						pricingRow.addColumn(pColumn);
					}
				}
				colInd++;
			}
			if (pricingRow.getColumns() != null) {
				returnList.add(pricingRow);
			}
		}
		return returnList;
	}

}
