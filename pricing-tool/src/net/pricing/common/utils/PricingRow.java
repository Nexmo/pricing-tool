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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class PricingRow {
	private Long index;
	private List<PricingColumn> columns;
	private boolean isErrorRow = false;

	public boolean isErrorRow() {
		return isErrorRow;
	}

	public void setErrorRow(boolean isErrorRow) {
		this.isErrorRow = isErrorRow;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public List<PricingColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<PricingColumn> columns) {
		this.columns = columns;
	}

	public void addColumn(PricingColumn column) {
		if (columns == null) {
			columns = new ArrayList<PricingColumn>();
		}
		this.columns.add(column);
	}

	public PricingColumn getColumnByName(String name) {
		PricingColumn toReturn = null;
		if (this.columns != null) {
			for (PricingColumn currentColumn : this.columns) {
				if (currentColumn.getName().equals(name)) {
					toReturn = currentColumn;
				}
			}
		}
		return toReturn;
	}

	public void changeColumnValue(String nameColumn, String newValue) {
		for (PricingColumn currentColumn : this.columns) {
			if (currentColumn.getName().equals(nameColumn)) {
				currentColumn.setValue(newValue);
			}
		}
	}

	public void removeColumn(String name) {
		PricingColumn toRemove = null;
		for (PricingColumn currentColumn : this.columns) {
			if (currentColumn.getName().equals(name)) {
				toRemove = currentColumn;
			}
		}

		this.columns.remove(toRemove);
	}
}
