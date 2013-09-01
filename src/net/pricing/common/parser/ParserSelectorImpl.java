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
package net.pricing.common.parser;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.pricing.common.config.ParserConfiguration;

import org.apache.log4j.Logger;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class ParserSelectorImpl implements ParserSelector {
	private static final Logger logger = Logger.getLogger(ParserSelector.class);
	private static volatile ParserSelector instance;

	private ParserSelectorImpl() {
	};

	public static ParserSelector getInstance() {
		if (instance == null) {
			synchronized (ParserSelectorImpl.class) {
				if (instance == null) {
					instance = new ParserSelectorImpl();
				}
			}
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Parser> T getParser(String email) throws IOException {

		String parserName = ParserConfiguration.getParserProperty(email);
		String parseOption = ParserConfiguration
				.getParsingOptionProperty(email);
		String supplier = ParserConfiguration.getSupplierProperty(email);
		if (parserName == null) {
			logger.debug("[getParser] --> No Parser For Current Email adress. Please check parsers_config.properties file");
			return null;
		}
		logger.debug("[getParser] --> Choosen parser: " + parserName);
		Object object = null;
		try {
			Class<?> classDefinition = Class.forName(parserName);

			Constructor<?> ctor = classDefinition.getDeclaredConstructor(
					String.class, String.class);
			ctor.setAccessible(true);
			object = ctor.newInstance(parseOption, supplier);

		} catch (InvocationTargetException e) {
			logger.error("[getParser] --> InvocationTargetException"
					+ e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.error("[getParser] --> NoSuchMethodException"
					+ e.getMessage());
		} catch (SecurityException e) {
			logger.error("[getParser] --> SecurityException" + e.getMessage());
		} catch (InstantiationException e) {
			logger.error("[getParser] --> InstantiationException"
					+ e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error("[getParser] --> IllegalAccessException"
					+ e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error("[getParser] --> ClassNotFoundException"
					+ e.getMessage());
		}
		return (T) object;
	}

}
