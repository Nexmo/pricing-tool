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
 * Loads data for configuration application from config.properties file
 * 
 * @author Yuliia Petrushenko
 * @version
 */
public class AppConfiguration {
	private static final Logger logger = Logger
			.getLogger(AppConfiguration.class);
	private static Properties appConfig;

	public static String getProperty(String name) {
		lazyLoad();
		return appConfig.getProperty(name);

	}

	public static Properties getProperties() {
		lazyLoad();
		return appConfig;
	}

	private static void lazyLoad() {
		if (appConfig == null) {
			appConfig = new Properties();
			try {
				appConfig.load(AppConfiguration.class.getClassLoader()
						.getResourceAsStream(PricingConstants.CONFIG_FILE));
			} catch (IOException e) {
				logger.error("IOException: Config file does not exist "
						+ e.getMessage());
			}
		}
	}
}
