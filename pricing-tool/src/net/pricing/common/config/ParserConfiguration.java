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
package net.pricing.common.config;

import java.io.IOException;
import java.util.Properties;

import net.pricing.common.utils.PricingConstants;

import org.apache.log4j.Logger;

/**
 * Loads configuration data for parsing from parsers_config.properties
 * 
 * @author Yuliia Petrushenko
 * @version
 */
public class ParserConfiguration {
	private static final Logger logger = Logger
			.getLogger(ParserConfiguration.class);
	private static Properties parserConfig;

	public static String getParserProperty(String name) {
		lazyLoad();
		String propertyValue = parserConfig.getProperty(name);
		if (propertyValue != null) {
			propertyValue = propertyValue.replace("{", "");
			propertyValue = propertyValue.replace("}", "");
		}
		String parser = propertyValue == null ? null : propertyValue
				.split(PricingConstants.CSV_DELIMITER)[0];
		return parser == null ? null : parser
				.split(PricingConstants.CONFIG_DELIMITER)[1].trim();
	}

	public static String getParsingOptionProperty(String name) {
		lazyLoad();
		String propertyValue = parserConfig.getProperty(name);
		if (propertyValue != null) {
			propertyValue = propertyValue.replace("{", "");
			propertyValue = propertyValue.replace("}", "");
			String[] splitProperties = propertyValue
					.split(PricingConstants.CSV_DELIMITER);
			return splitProperties.length == 2 ? "" : splitProperties[2]
					.split(PricingConstants.CONFIG_DELIMITER)[1].trim();
		} else
			return null;
	}

	public static String getSupplierProperty(String name) {
		lazyLoad();
		String propertyValue = parserConfig.getProperty(name);
		if (propertyValue != null) {
			propertyValue = propertyValue.replace("{", "");
			propertyValue = propertyValue.replace("}", "");
		}
		String supplier = propertyValue == null ? null : propertyValue
				.split(PricingConstants.CSV_DELIMITER)[1];
		return supplier == null ? null : supplier
				.split(PricingConstants.CONFIG_DELIMITER)[1].trim();
	}

	private static void lazyLoad() {
		if (parserConfig == null) {
			parserConfig = new Properties();
			try {

				String parserConfigFile = PricingConstants.PARSER_CONFIG_FILE;
				parserConfig.load(ParserConfiguration.class.getClassLoader()
						.getResourceAsStream(parserConfigFile));
			} catch (IOException e) {
				logger.error("IOException: Config file for parsers does not exists "
						+ e.getMessage());
			}
		}
	}
}
