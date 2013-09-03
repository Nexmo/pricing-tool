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
package net.pricing.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import net.pricing.common.config.AppConfiguration;
import net.pricing.common.mail.validator.NetworkValidator;
import net.pricing.common.mail.validator.NetworkValidatorFactory;
import net.pricing.common.utils.PricingColumn;
import net.pricing.common.utils.PricingConstants;
import net.pricing.common.utils.PricingRow;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class ParsingUtils {

	private static final Logger logger = Logger.getLogger(ParsingUtils.class);

	/**
	 * Concat two columns in one column named MCCMNC.
	 * 
	 * @param rowList
	 *            list of @PricingRow
	 * @param firstCellName
	 *            String name of the first cell
	 * @param secondCellName
	 *            String name of the second cell
	 * @return updated list of @PricingRow
	 */
	public static List<PricingRow> concatMccMncValues(List<PricingRow> rowList,
			String firstCellName, String secondCellName) {

		List<PricingRow> returnList = new ArrayList<PricingRow>();
		for (PricingRow currentRow : rowList) {
			String firstCellValue = currentRow.getColumnByName(firstCellName) != null ? currentRow
					.getColumnByName(firstCellName).getValue().trim()
					: "";
			String secondCellValue = currentRow.getColumnByName(secondCellName) != null ? currentRow
					.getColumnByName(secondCellName).getValue().trim()
					: "";
			String networkCode = FileUtils.getNameWOExt(firstCellValue.replace(
					"\"", ""))
					+ FileUtils.getNameWOExt(secondCellValue.replace("\"", ""));

			PricingRow rowToWrite = new PricingRow();
			rowToWrite.setErrorRow(currentRow.isErrorRow());
			rowToWrite.setIndex(currentRow.getIndex());
			PricingColumn mccmncCode = new PricingColumn();
			mccmncCode.setValue(networkCode);
			mccmncCode.setName(PricingConstants.MCCMNC);
			if (currentRow.getColumnByName(firstCellName) != null) {
				mccmncCode.setIndex(currentRow.getColumnByName(firstCellName)
						.getIndex());
			}
			rowToWrite.addColumn(mccmncCode);
			for (PricingColumn cl : currentRow.getColumns()) {
				if (!firstCellName.equals(cl.getName())
						&& !secondCellName.equals(cl.getName())) {
					rowToWrite.addColumn(cl);
				}
			}
			returnList.add(rowToWrite);

		}

		return returnList;
	}

	/**
	 * Split column value by given delimiter
	 * 
	 * @param rowList
	 *            list of @PricingRow
	 * @param columnName
	 *            String name of column to be changed
	 * @param delimiter
	 *            String
	 * @return updated list of @PricingRow
	 */
	public static List<PricingRow> splitColumnValue(List<PricingRow> rowList,
			String columnName, String delimiter) {
		List<PricingRow> returnList = new ArrayList<PricingRow>();
		PricingColumn column = null;
		String[] splitedColumn = null;
		for (PricingRow currentRow : rowList) {
			column = currentRow.getColumnByName(columnName);
			if (column == null) {
				returnList.add(currentRow);
				continue;
			}
			splitedColumn = column.getValue().split(delimiter);
			if (splitedColumn.length == 1) {
				returnList.add(currentRow);
			} else {
				for (int i = 0; i < splitedColumn.length; i++) {
					PricingRow newRow = new PricingRow();
					newRow.setErrorRow(currentRow.isErrorRow());
					newRow.setIndex(currentRow.getIndex());
					PricingColumn mncCode = new PricingColumn();
					mncCode.setValue(splitedColumn[i].trim());
					mncCode.setName(columnName);
					mncCode.setIndex(currentRow.getColumnByName(columnName)
							.getIndex());
					for (PricingColumn cl : currentRow.getColumns()) {
						if (!columnName.equals(cl.getName())) {
							newRow.addColumn(cl);
						}
					}
					newRow.addColumn(mncCode);
					returnList.add(newRow);

				}
			}

		}

		return returnList;
	}

	public static void splitMCCMNC(List<PricingRow> rowList, String columnName,
			String delimiter) {
		PricingColumn column = null;
		String[] splitedColumn = null;
		for (PricingRow currentRow : rowList) {
			column = currentRow.getColumnByName(columnName);
			if (column != null) {
				splitedColumn = column.getValue().split(delimiter);
				if (splitedColumn.length == 2) {
					PricingColumn mcccolumn = new PricingColumn();
					mcccolumn.setName(PricingConstants.MCC);
					mcccolumn.setValue(splitedColumn[0].trim());

					currentRow.addColumn(mcccolumn);

					column.setName(PricingConstants.MNC);
					column.setValue(splitedColumn[1].trim());
				}
			}
		}
	}

	/**
	 * Validate column value if it is numeric
	 * 
	 * @param rowList
	 *            list of @PricingRow
	 * @param columnName
	 *            String name of column to be validated
	 */
	public static void validateNumericValues(List<PricingRow> rowList,
			String columnName) {
		for (PricingRow currentRow : rowList) {
			if (!currentRow.isErrorRow()) {
				PricingColumn currentColumn = currentRow
						.getColumnByName(columnName);
				if (currentColumn != null
						&& !isNumeric(currentColumn.getValue())) {
					currentRow.setErrorRow(true);
				}
			}

		}
	}

	/**
	 * Verify if string contains only numeric symbols.
	 * 
	 * @param str
	 *            String to be validated
	 * @return true if str contains only numeric symbols
	 */
	public static boolean isNumeric(String str) {
		return Pattern.matches("[+]?\\d+(\\.|,)?\\d+", str);
	}

	/**
	 * Checks if column value is the valid NetworkCode.
	 * 
	 * @param rowList
	 *            list of @PricingRow
	 * @param column
	 *            String name of columns to be validated
	 */
	public static void validateNetworkCode(List<PricingRow> rowList,
			String column) {
		NetworkValidator networkValidator = NetworkValidatorFactory
				.getInstance(AppConfiguration
						.getProperty(PricingConstants.VALIDATOR_CLASS_PATH));

		for (PricingRow currentRow : rowList) {
			String code = currentRow.getColumnByName(column).getValue();
			String newCode = networkValidator.validateNetwork(code);
			if (newCode == null) {
				currentRow.setErrorRow(true);
			} else {
				currentRow.getColumnByName(column).setValue(newCode);
			}
		}
	}

	/**
	 * Multiply column value by rate.
	 * 
	 * @param rowList
	 *            list of @PricingRow
	 * @param column
	 *            String name of column to be changed
	 * @param rate
	 */
	public static void convertToAnotherCurrency(List<PricingRow> rowList,
			String column, double rate) {
		for (PricingRow currentRow : rowList) {

			String price = currentRow.getColumnByName(column).getValue();
			double val = rate * Double.valueOf(price);
			currentRow.getColumnByName(column).setValue(
					String.format("%.4f", val));
		}
	}

	/**
	 * Trim the currency from column value
	 * 
	 * @param rowList
	 *            list of @PricingRow
	 * @param column
	 *            name of column to be changed
	 * @param currency
	 *            String
	 */
	public static void trimCurrencyFromPrice(List<PricingRow> rowList,
			String column, String currency) {
		for (PricingRow currentRow : rowList) {
			PricingColumn currentColumn = currentRow.getColumnByName(column);
			if (currentColumn != null) {
				String price = currentColumn.getValue();
				String trimPrice = price.replaceAll(currency, "");
				currentRow.getColumnByName(column).setValue(trimPrice.trim());
			}
		}
	}

	/**
	 * Remove duplicate networks from incoming list. NeweastPos equals to 1
	 * means that newest row is the first found row, neweastPos equals to 0
	 * means that newest row is the latest found row.
	 * 
	 * @param rowList
	 *            list of @PricingRow
	 * @param column
	 *            name of column to be verified
	 * @param newestPos
	 *            int position of the newest row(first or latest)
	 */
	public static List<PricingRow> removeDuplicateNetworks(
			List<PricingRow> rowList, String column, int newestPos) {
		int size = rowList.size();
		List<PricingRow> rowsToDelete = new ArrayList<PricingRow>();
		for (PricingRow currentRow : rowList) {
			int startIndex = rowList.indexOf(currentRow);
			for (int i = startIndex; i < size; i++) {
				if (i != rowList.indexOf(currentRow)
						&& (currentRow.getColumnByName(column).getValue())
								.equals(rowList.get(i).getColumnByName(column)
										.getValue())) {
					if (newestPos == 1) {
						rowsToDelete.add(rowList.get(i));
					}
					if (newestPos == 0) {
						rowsToDelete.add(currentRow);
					}
				}
			}

		}
		for (PricingRow rowToRemove : rowsToDelete) {
			rowList.remove(rowToRemove);
		}
		return rowList;
	}

	/**
	 * Removes rows that doesn't contain the value for given column or if the
	 * value of given column is marked as non updated
	 * 
	 * @param columnName
	 *            String name of column to be checked
	 * @param rowList
	 *            list of @PricingRow
	 * @param nonUpdColumnValues
	 *            String value for columns that are marked as non updated
	 */
	public static void removeRowsWOUpdates(String columnName,
			List<PricingRow> rowList, String... nonUpdColumnValues) {

		List<PricingRow> rowsToDelete = new ArrayList<PricingRow>();
		for (PricingRow pricingRow : rowList) {
			PricingColumn column = pricingRow.getColumnByName(columnName);

			if (column == null
					|| column.getValue().isEmpty()
					|| (nonUpdColumnValues != null && Arrays.asList(
							nonUpdColumnValues).contains(column.getValue()))) {
				rowsToDelete.add(pricingRow);
			}
		}

		for (PricingRow currentRow : rowsToDelete) {
			rowList.remove(currentRow);
		}
	}

	public static String getContentOfHTTPPage(String pageAddress,
			String codePage) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br;
		try {
			URL pageURL = new URL(pageAddress);
			URLConnection uc = pageURL.openConnection();
			br = new BufferedReader(new InputStreamReader(uc.getInputStream(),
					codePage));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			br.close();
		} catch (IOException e) {
			logger.error("[getContentOfHTTPPage] IOException " + e.getMessage());
		}

		return sb.toString();
	}

	public static void changeFloatingPoint(List<PricingRow> rows,
			String columnName, String symbForChange) {
		for (PricingRow currentRow : rows) {
			String oldValue = currentRow.getColumnByName(columnName).getValue();
			currentRow.getColumnByName(columnName).setValue(
					oldValue.replace(symbForChange, "."));
		}
	}

	public static List<PricingRow> completeNetworkList(List<PricingRow> rows,
			String columnName) {
		NetworkValidator networkValidator = NetworkValidatorFactory
				.getInstance(AppConfiguration
						.getProperty(PricingConstants.VALIDATOR_CLASS_PATH));

		List<PricingRow> returnRows = new ArrayList<PricingRow>();
		for (PricingRow currentRow : rows) {

			String columnValue = currentRow.getColumnByName(columnName)
					.getValue();

			if (columnValue.length() == 3) {
				List<String> allMatches = networkValidator
						.getNetworkMatches(columnValue);
				for (String match : allMatches) {
					boolean found = false;
					m: for (PricingRow currRow : rows) {
						if (currRow.getColumnByName(columnName).getValue()
								.equals(match)) {
							found = true;
							break m;
						}
					}
					if (!found) {
						PricingRow rowToAdd = new PricingRow();
						for (PricingColumn currentColumn : currentRow
								.getColumns()) {
							if (!columnName.equals(currentColumn.getName())) {
								rowToAdd.addColumn(currentColumn);
							} else {
								rowToAdd.addColumn(new PricingColumn(
										currentColumn.getIndex(), currentColumn
												.getName(), match));
							}
						}
						returnRows.add(rowToAdd);
					}
				}
			} else {
				returnRows.add(currentRow);
			}
		}
		return returnRows;
	}

	public static void replaceValues(List<PricingRow> rows, String columnName,
			String valueToReplace, String newValue) {
		for (PricingRow currentRow : rows) {
			String columnValue = currentRow.getColumnByName(columnName)
					.getValue();
			if (columnValue != null
					&& columnValue.equalsIgnoreCase(valueToReplace)) {
				currentRow.getColumnByName(columnName).setValue(newValue);
			}
		}
	}

}
