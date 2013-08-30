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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import net.pricing.common.utils.PricingColumn;
import net.pricing.common.utils.PricingRow;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class JsonParser {

	private static final Logger logger = Logger.getLogger(JsonParser.class);

	private String[] columns;

	public List<PricingRow> parseFile(URL url, String[] columns) {
		List<PricingRow> rowList = new ArrayList<PricingRow>();

		logger.debug("[parseFile] ---------> Start JSON parsing");
		this.columns = new String[columns.length];
		this.columns = columns;

		JSONTokener tokener;
		try {
			URLConnection uc = url.openConnection();
			tokener = new JSONTokener(
					new InputStreamReader(uc.getInputStream()));
			JSONArray jsonArray = new JSONArray(tokener);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject currObj = jsonArray.getJSONObject(i);
				PricingRow currRow = new PricingRow();
				for (String colName : this.columns) {
					PricingColumn currColumn = new PricingColumn();
					currColumn.setName(colName);
					currColumn.setValue(currObj.getString(colName));
					currRow.addColumn(currColumn);
				}
				rowList.add(currRow);
			}
		} catch (JSONException e) {
			logger.error("[parseFile] JSONException " + e.getMessage());
		} catch (IOException e) {
			logger.error("[parseFile] IOException " + e.getMessage());
		}
		return rowList;
	}

}
