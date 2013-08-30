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
package net.pricing.common.mail.reader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import net.pricing.common.config.AppConfiguration;
import net.pricing.common.parser.Parser;
import net.pricing.common.parser.ParserSelectorImpl;
import net.pricing.common.parser.ParserWOAttachments;
import net.pricing.common.utils.FileUtils;
import net.pricing.common.utils.PricingConstants;

import org.apache.log4j.Logger;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class MailReaderImpl implements MailReader {
	private static final Logger logger = Logger.getLogger(MailReaderImpl.class);

	private Folder folder;
	private static String FOLDER_NAME = "Inbox";

	@Override
	public void readMail() {

		Session session = Session.getDefaultInstance(
				AppConfiguration.getProperties(), null);
		Store store;
		try {
			store = session.getStore(AppConfiguration
					.getProperty(PricingConstants.STORE_PROTOCOL));
			store.connect(
					AppConfiguration.getProperty(PricingConstants.IMAP_HOST),
					AppConfiguration.getProperty(PricingConstants.USER),
					AppConfiguration.getProperty(PricingConstants.PASS));
			folder = store.getFolder(FOLDER_NAME);
			logger.debug("[readMail] Number of unreaded messages: "
					+ folder.getUnreadMessageCount());

			folder.open(Folder.READ_WRITE);

			Message messages[] = folder.search(new FlagTerm(
					new Flags(Flag.SEEN), false));

			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.CONTENT_INFO);
			folder.fetch(messages, fp);
			this.readAllMessages(messages);
			folder.close(true);
			store.close();
			FileUtils.cleanFolder(PricingConstants.TEMP_FOLDER_NAME);
			logger.debug("[readMail] EXIT MailReader");

		} catch (NoSuchProviderException e) {
			logger.error("[readMail] NoSuchProviderException " + e.getMessage());
		} catch (MessagingException e) {
			logger.error("[readMail] MessagingException " + e.getMessage());
		} catch (IOException e) {
			logger.error("[readMail] IOException " + e.getMessage());
		}
	}

	private void readAllMessages(Message[] messages) throws MessagingException,
			IOException {
		for (int i = 0; i < messages.length; i++) {
			logger.debug("\n[readAllMessages] --> MESSAGE " + (i + 1) + ":");
			this.readMessage(messages[i]);
		}

	}

	/**
	 * Chooses the parser for the message according to sender's email address
	 * and launch parsing the message body
	 * 
	 * @param message
	 *            Message to be parsed
	 * @throws MessagingException
	 * @throws IOException
	 */
	private void readMessage(Message message) throws MessagingException,
			IOException {
		Address[] a;
		String senderEmail = new String();

		if ((a = message.getFrom()) != null) {
			String from = new String();
			for (int j = 0; j < a.length; j++) {
				from += a[j].toString();
			}
			senderEmail = this.parseFrom(from);
			logger.debug("[readMessage] --> FROM: " + senderEmail);
		}
		Parser parser = ParserSelectorImpl.getInstance().getParser(senderEmail);
		if (parser != null) {
			message.setFlags(new Flags(Flag.SEEN), true);
			parser.setAccount(message);
			if (parser instanceof ParserWOAttachments) {
				this.parseContent(message, parser);
			} else {
				this.getAndParseAttachments(message, parser);
			}
		} else {
			message.setFlags(new Flags(Flag.SEEN), false);
		}
	}

	/**
	 * Removes "<" from the beginning of the string and ">" from the end.
	 * 
	 * @param from
	 *            String
	 * @return trimed String
	 */
	private String parseFrom(String from) {
		String emailAddress = null;
		int beginIndex = from.indexOf("<");
		int endIndex = from.indexOf(">");
		if (endIndex == -1) {
			endIndex = from.length();
		}
		emailAddress = from.substring(beginIndex + 1, endIndex);

		return emailAddress;
	}

	private void getAndParseAttachments(Message message, Parser parser)
			throws IOException, MessagingException {
		if (message.getContent() instanceof Multipart) {
			Multipart multipart = (Multipart) message.getContent();
			int numberOfParts = multipart.getCount();
			int count = 0;
			for (int j = 0; j < numberOfParts; j++) {
				BodyPart bodyPart = multipart.getBodyPart(j);
				String fileName = bodyPart.getFileName();

				if (fileName != null && FileUtils.isFileForParsing(fileName)) {
					count++;
					File createdFile = FileUtils.saveAttacmentFile(fileName,
							PricingConstants.TEMP_FOLDER_NAME,
							bodyPart.getInputStream());
					logger.debug("\n[getAndParseAttachments] ---------> Temporary file: "
							+ bodyPart.getFileName());
					if (PricingConstants.ZIP_FILE_EXTENSION.equals(FileUtils
							.getExtension(fileName))) {

						List<File> filesFromZip = FileUtils
								.uncompressZipArchive(createdFile);
						for (File f : filesFromZip) {
							if (FileUtils.isFileForParsing(f.getName())) {
								parser.parseToCsv(f);
							} else {
								logger.debug("[getAndParseAttachments] ---------> "
										+ f.getName() + " couldn't be parsed");
							}

						}
					} else {
						File parsedFile = parser.parseToCsv(createdFile);
						if (parsedFile == null) {
							logger.debug("[getAndParseAttachments] ---------> Parsing wasn't done");
						}
					}
				}
				continue;
			}
			if (count == 0) {
				logger.debug("[getAndParseAttachments] ---------> No data for parsing.");
			}
		} else {
			logger.debug("[getAndParseAttachments] ---------> No multipart data.");
		}
	}

	private void parseContent(Message message, Parser parser)
			throws IOException, MessagingException {

		ContentType contentType = new ContentType(message.getContentType());
		if (PricingConstants.MULTIPART.equals(contentType.getPrimaryType())) {
			this.parseMultiPart((MimeMultipart) message.getContent(), parser);
		}
		if (PricingConstants.TEXT.equals(contentType.getPrimaryType())) {
			File createdFile = FileUtils
					.saveAttacmentFile("forParsing.html",
							PricingConstants.TEMP_FOLDER_NAME,
							message.getInputStream());
			File parsedFile = parser.parseToCsv(createdFile);
			if (parsedFile == null) {
				logger.debug("[parseContent] ---------> Something goes wrong");
			}
		}

	}

	private void parseMultiPart(MimeMultipart content, Parser parser) {
		try {
			for (int i = 0; i < content.getCount(); i++) {
				BodyPart part = content.getBodyPart(i);
				ContentType ct = new ContentType(part.getContentType());
				if (PricingConstants.MULTIPART.equals(ct.getPrimaryType())) {
					parseMultiPart((MimeMultipart) part.getContent(), parser);
				} else if (PricingConstants.TEXT.equals(ct.getPrimaryType())
						&& (PricingConstants.HTML.equals(ct.getSubType()))) {

					File createdFile = FileUtils.saveAttacmentFile(
							"forParsing.html",
							PricingConstants.TEMP_FOLDER_NAME,
							part.getInputStream());
					File parsedFile = parser.parseToCsv(createdFile);
					if (parsedFile == null) {
						logger.debug("[parseMultiPart] ---------> Something goes wrong");
					}
				}
			}
		} catch (IOException e) {
			logger.error("[parseMultiPart] IOException " + e.getMessage());
		} catch (MessagingException e) {
			logger.error("[parseMultiPart] MessagingException "
					+ e.getMessage());
		}
	}

	public Folder getInbox() {
		return folder;
	}

	public void setInbox(Folder inbox) {
		this.folder = inbox;
	}

	public static String getFOLDER_NAME() {
		return FOLDER_NAME;
	}

	public static void setFOLDER_NAME(String fOLDER_NAME) {
		FOLDER_NAME = fOLDER_NAME;
	}
}
