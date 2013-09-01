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

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class PricingConstants {

	public static final String TEMP_FOLDER_NAME = System
			.getProperty("java.io.tmpdir") + "/pricing/";
	public static final String CSV_DELIMITER = ",";
	public static final String CONFIG_DELIMITER = ":";
	public static final String MULTIPART = "multipart";
	public static final String TEXT = "TEXT";
	public static final String HTML = "HTML";

	// run commands
	public static final String CHECKMAIL = "--checkmail";
	public static final String SENDMAIL = "--sendmail";

	// file extensions
	public static final String XLS_FILE_EXTENSION = "xls";
	public static final String CSV_FILE_EXTENSION = "csv";
	public static final String XLSX_FILE_EXTENSION = "xlsx";
	public static final String ZIP_FILE_EXTENSION = "zip";
	public static final String HTML_FILE_EXTENSION = "html";

	// name of fields
	public static final String MCCMNC = "mccmnc";
	public static final String MCC = "mcc";
	public static final String MNC = "mnc";
	public static final String PARSE_ALL = "all";

	// property fields
	public static final String CONFIG_FILE = "config.properties";
	public static final String PARSER_CONFIG_FILE = "parsers_config.properties";
	public static final String EMAIL_LIST = "email.list";
	public static final String FILE_TO_SENT = "file.to.sent";
	public static final String USER = "mail.username";
	public static final String PASS = "mail.password";
	public static final String MSG_BODY = "message.template";
	public static final String MSG_SUBJ = "message.subject";
	public static final String STORE_PROTOCOL = "mail.store.protocol";
	public static final String IMAP_HOST = "mail.imap.host";
	public static final String FILE_STORAGE = "file.path.storage";
	public static final String MAILING_LIST = "mailing.list";
	public static final String ARCHIVES_FILE_STORAGE = "archives.file.path.storage";
	public static final String ERROR_FILE_STORAGE = "error.file.path.storage";
	public static final String VALIDATOR_CLASS_PATH = "network.validator.class.path";
	public static final String MAIL_READER_PATH = "mail.reader";
	public static final String CONVERTER_TO_MAIL_PATH = "mail.converter";
	public static final String RATE_PER_ONE_USD = "rate.per.one.usd";
	public static final String RATE_PER_ONE_GBP = "rate.per.one.gbp";
}
