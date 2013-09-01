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
package net.pricing.common.mail.generator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import net.pricing.common.config.AppConfiguration;
import net.pricing.common.utils.PricingConstants;

import org.apache.log4j.Logger;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class MailGeneratorImpl implements MailGenerator {

	private static final Logger logger = Logger
			.getLogger(MailGeneratorImpl.class);

	private class SMTPAuthenticator extends Authenticator {
		private PasswordAuthentication authentication;

		public SMTPAuthenticator(String login, String password) {
			authentication = new PasswordAuthentication(login, password);
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}

	@Override
	public void generateMail(String email, File file) {

		String from = AppConfiguration.getProperty(PricingConstants.USER);
		Authenticator auth = new SMTPAuthenticator(from,
				AppConfiguration.getProperty(PricingConstants.PASS));

		Session session = Session.getInstance(AppConfiguration.getProperties(),
				auth);

		MimeMessage msg = new MimeMessage(session);
		try {
			FileInputStream msgBodyIs = new FileInputStream(new File(
					AppConfiguration.getProperty(PricingConstants.MSG_BODY)));

			DataInputStream msgBodyIn = new DataInputStream(msgBodyIs);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					msgBodyIn));
			String strLine;
			String messageBody = new String();
			while ((strLine = br.readLine()) != null) {
				messageBody += strLine + "\n";
			}
			br.close();

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart
					.setContent(messageBody, "text/plain; charset=UTF-8");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			MimeBodyPart attachmentBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file.getAbsolutePath());
			attachmentBodyPart.setDataHandler(new DataHandler(source));
			attachmentBodyPart.setFileName(MimeUtility.encodeText(source
					.getName()));
			multipart.addBodyPart(attachmentBodyPart);

			msg.setSubject(AppConfiguration
					.getProperty(PricingConstants.MSG_SUBJ));
			msg.setFrom(new InternetAddress(from));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));
			msg.setContent(multipart);
			Transport.send(msg);
		} catch (MessagingException e) {
			logger.error("[generateMail] MessagingException " + e.getMessage());
		} catch (IOException e) {
			logger.error("[generateMail] IOException " + e.getMessage());
		}
	}
}
