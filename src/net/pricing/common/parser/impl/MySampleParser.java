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
package net.pricing.common.parser.impl;

import java.io.File;
import java.util.List;

import javax.mail.Message;

import net.pricing.common.parser.Parser;
import net.pricing.common.parser.helper.ExcelParser;
import net.pricing.common.utils.FileUtils;
import net.pricing.common.utils.ParsingUtils;
import net.pricing.common.utils.PricingConstants;
import net.pricing.common.utils.PricingRow;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class MySampleParser implements Parser {
	private static final String MCCMNC = "mccmnc";
	private static final String PRICE = "price";
	private static final String CHANGE = "change";

	private String[] columns = { MCCMNC, PRICE };
	private String productName = "SMS";

	private String parseOption;
	private String supplierName;
	private String accountName;

	public MySampleParser(String option, String supplierName) {
		this.parseOption = option;
		this.supplierName = supplierName;

	}

	@Override
	public File parseToCsv(File file) {
		ExcelParser parser = new ExcelParser();
		List<PricingRow> rows = parser.parseFromExcel(columns, file);

		// if parseOption is empty - only rows with updates of prices will be
		// parsed to result file. Otherwise all rows from source file will be
		// parsed
		if (this.parseOption.isEmpty()) {
			ParsingUtils.removeRowsWOUpdates(CHANGE, rows);
		}

		for (PricingRow currentRow : rows) {
			String newValue = FileUtils.getNameWOExt(currentRow
					.getColumnByName(MCCMNC).getValue());
			currentRow.changeColumnValue(MCCMNC, newValue);
		}

		ParsingUtils.validateNetworkCode(rows, MCCMNC);
		ParsingUtils.validatePrice(rows, PRICE);

		ParsingUtils.removeDuplicateNetworks(rows, MCCMNC, 0);

		String[] columnsToWrite = { MCCMNC, PRICE };
		File resultFile = FileUtils.saveCsvFile(rows, getSupplierName() + "_"
				+ getAccountName() + "_" + this.productName,
				PricingConstants.FILE_STORAGE, columnsToWrite);
		return resultFile;
	}

	@Override
	public File parseFromCsv(File file) {
		return new ExcelParser().parseToXLS(file);
	}

	@Override
	public void setAccount(Message message) {
		this.accountName = this.supplierName;

	}

	public String getParseOption() {
		return parseOption;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public String getAccountName() {
		return accountName;
	}

}
