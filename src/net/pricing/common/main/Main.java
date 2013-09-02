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
package net.pricing.common.main;

import net.pricing.common.config.AppConfiguration;
import net.pricing.common.mail.converter.ConverterToMail;
import net.pricing.common.mail.converter.ConverterToMailFactory;
import net.pricing.common.mail.reader.MailReader;
import net.pricing.common.mail.reader.MailReaderFactory;
import net.pricing.common.utils.PricingConstants;

import org.apache.log4j.Logger;

/**
 * 
 * Class Launcher. According to the input command runs the checkmail part of
 * application or sendmail part.
 * 
 * @author Yuliia Petrushenko
 * @version
 */
public class Main {

	private static final Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		if (args.length != 1) {
			logger.error("UNKNOWN COMMAND");
		} else if (PricingConstants.CHECKMAIL.equals(args[0])) {
			logger.debug("Run MailReader");
			String mailReaderPath = AppConfiguration
					.getProperty(PricingConstants.MAIL_READER_PATH);
			MailReader mailReader = MailReaderFactory
					.getInstance(mailReaderPath);
			mailReader.readMail();

		} else if (PricingConstants.SENDMAIL.equals(args[0])) {
			logger.debug("Run MailSender");
			String converterToMailPath = AppConfiguration
					.getProperty(PricingConstants.CONVERTER_TO_MAIL_PATH);
			ConverterToMail converterToMail = ConverterToMailFactory
					.getInstance(converterToMailPath);
			converterToMail.sendMessage();
		} else {
			logger.error("UNKNOWN COMMAND");
		}

		System.exit(0);

	}
}
