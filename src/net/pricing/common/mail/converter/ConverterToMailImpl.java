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
package net.pricing.common.mail.converter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.pricing.common.config.AppConfiguration;
import net.pricing.common.mail.generator.MailGeneratorImpl;
import net.pricing.common.parser.Parser;
import net.pricing.common.parser.ParserSelectorImpl;
import net.pricing.common.utils.FileUtils;
import net.pricing.common.utils.ParsingUtils;
import net.pricing.common.utils.PricingConstants;

import org.apache.log4j.Logger;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class ConverterToMailImpl implements ConverterToMail {
	private static final Logger logger = Logger
			.getLogger(ConverterToMailImpl.class);

	@Override
	public void sendMessage() {

		logger.debug("[sendMessage] --> START SENDING MESSAGES");

		File emailListFile = new File(
				AppConfiguration.getProperty(PricingConstants.EMAIL_LIST));
		List<String> emailList = this.getEmailList(emailListFile);
		File file = new File(
				AppConfiguration.getProperty(PricingConstants.FILE_TO_SENT));
		if (emailList.isEmpty()) {
			logger.debug("[sendMessage] --> NO EMAIL FOUND");
		}
		for (String currentEmail : emailList) {

			Parser parser;
			logger.debug("\n[sendMessage] --> Sending email to: "
					+ currentEmail);
			try {
				parser = ParserSelectorImpl.getInstance().getParser(
						currentEmail);
				if (parser != null) {
					File fileToSend = parser.parseFromCsv(file);
					new MailGeneratorImpl().generateMail(currentEmail,
							fileToSend);
				} else {
					logger.debug("[sendMessage] --> No email send to "
							+ currentEmail);
				}
			} catch (IOException e) {
				logger.error("[sendMessage] IOException " + e.getMessage());
			}
		}
		FileUtils.cleanFolder(PricingConstants.TEMP_FOLDER_NAME);
		logger.debug("\n[sendMessage] EXIT MailSender");

	}

	@Override
	public List<String> getEmailList(File file) {
		List<String> emailList = new ArrayList<String>();
		try {
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				if (ParsingUtils.isValidEmailAddress(strLine)) {
					emailList.add(strLine);
				}
			}
			in.close();

		} catch (IOException e) {
			logger.error("[getEmailList] IOException " + e.getMessage());
		}

		return emailList;
	}
}
