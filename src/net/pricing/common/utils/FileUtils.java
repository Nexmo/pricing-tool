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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import net.pricing.common.config.AppConfiguration;

import org.apache.log4j.Logger;

/**
 * @author Yuliia Petrushenko
 * @version
 */
public class FileUtils {
	private static final Logger logger = Logger.getLogger(FileUtils.class);

	/**
	 * Return the substring from the last index of "." to the end of the
	 * incoming string
	 * 
	 * @param fileName
	 *            String
	 * @return substring from "." to the end of the string
	 */
	public static String getExtension(String fileName) {
		int beginIndex = fileName.lastIndexOf(".");
		String extension = fileName.substring(beginIndex + 1);
		return extension;
	}

	/**
	 * Delete the directory and all files and subfolders in it.
	 * 
	 * @param directoryPath
	 *            path to the directory that should be cleaned
	 */
	public static void cleanFolder(String directoryPath) {
		File directory = new File(directoryPath);
		if (!directory.exists() || !directory.isDirectory()) {
			logger.debug("[cleanFolder] " + directoryPath + " does not exist.");
			return;
		}

		logger.debug("[cleanFolder] Deleting " + directoryPath + " directory..");

		File[] files = directory.listFiles();

		for (File f : files) {
			if (f.isDirectory()) {
				cleanFolder(f.getAbsolutePath());
			}
			f.delete();
		}
		directory.delete();
	}

	/**
	 * Check if file could be parsed with the api parsers
	 * 
	 * @param fileName
	 *            String
	 * @return true if file could be parsed with the api parsers
	 */
	public static boolean isFileForParsing(String fileName) {

		if (Arrays.asList(PricingConstants.CSV_FILE_EXTENSION,
				PricingConstants.XLS_FILE_EXTENSION,
				PricingConstants.XLSX_FILE_EXTENSION,
				PricingConstants.ZIP_FILE_EXTENSION,
				PricingConstants.HTML_FILE_EXTENSION).contains(
				FileUtils.getExtension(fileName))) {
			return true;
		}
		return false;
	}

	/**
	 * Check if file extension is match the parser extension
	 * 
	 * @param fileName
	 * @param extension
	 * @return true if file extension is match the parser extensions
	 */
	public static boolean isFileExtMatchesTheParser(String fileName,
			String extension) {
		if (!extension.equals(getExtension(fileName))) {
			return false;
		}
		return true;
	}

	/**
	 * Saves file to the given folder from inputStream.
	 * 
	 * 
	 * @param fileName
	 *            the name of created file
	 * @param folderName
	 *            directory name, where the file will be saved
	 * @param is
	 *            InputStream
	 * @return file File
	 * @throws IOException
	 */
	public static File saveAttacmentFile(String fileName, String folderName,
			InputStream is) throws IOException {
		File returnFile = createFile(folderName + "/" + fileName);
		FileOutputStream fos = new FileOutputStream(returnFile);
		byte[] buf = new byte[4096];
		int bytesRead;
		while ((bytesRead = is.read(buf)) != -1) {
			fos.write(buf, 0, bytesRead);
		}
		fos.close();
		return returnFile;
	}

	/**
	 * 
	 * Uncompress zip-archive to the list of files
	 * 
	 * @param zipArchive
	 *            String, path to zip archive
	 * @return list of File from archive
	 */
	public static List<File> uncompressZipArchive(File zipArchive) {
		logger.debug("[uncompressZipArchive] ---------> Uncomressing zip file..");
		ArrayList<File> unpackedFiles = new ArrayList<File>();
		try {
			ZipFile zipFile = new ZipFile(zipArchive);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				if (entry.isDirectory()) {
					logger.debug("[uncompressZipArchive] -----------> Extracting directory: "
							+ entry.getName());
					(new File(entry.getName())).mkdir();
					continue;
				}

				logger.debug("[uncompressZipArchive] -----------> Extracting file: "
						+ entry.getName());
				saveAttacmentFile(entry.getName(),
						PricingConstants.TEMP_FOLDER_NAME,
						zipFile.getInputStream(entry));
				unpackedFiles.add(createFile(PricingConstants.TEMP_FOLDER_NAME
						+ entry.getName()));
			}

			zipFile.close();
		} catch (IOException e) {
			logger.error("[uncompressZipArchive] IOException " + e.getMessage());
		}
		return unpackedFiles;
	}

	/**
	 * Return the substring from the beginning of the incoming string to the
	 * last index of "."
	 * 
	 * @param name
	 *            String
	 * @return String
	 */
	public static String getNameWOExt(String name) {
		int index = name.lastIndexOf(".");
		if (index == -1) {
			return name;
		}
		return name.substring(0, name.lastIndexOf("."));
	}

	public static File createFile(String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				logger.error("[createFile] IOException " + e.getMessage());
			}
		}
		return f;

	}

	/**
	 * Check if incoming string is a valid email address
	 * 
	 * @param email
	 *            String
	 * @return true if the string i valid email address
	 */
	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException e) {
			logger.error("[isValidEmailAddress] AddressException "
					+ e.toString());
			result = false;
		}
		return result;
	}

	/**
	 * Save the data to the .csv file
	 * 
	 * @param listToWrite
	 *            list of @PricingRow to be saved
	 * @param fileName
	 *            name of the output file
	 * @param filePath
	 *            path to the folder
	 * @param columns
	 *            String[] name of @PricingColumn that should be saved to .csv
	 *            file
	 * @return
	 */
	public static File saveCsvFile(List<PricingRow> listToWrite,
			String fileName, String filePath, String[] columns) {

		if (listToWrite == null || listToWrite.isEmpty()) {
			return null;
		}
		List<PricingRow> errorList = new ArrayList<PricingRow>();
		fileName = getNameWOExt(fileName);
		File returnFile = null;
		try {
			String dateIdentifier = String.valueOf(Calendar.getInstance().get(
					Calendar.DAY_OF_MONTH))
					+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
					+ Calendar.getInstance().get(Calendar.MINUTE)
					+ Calendar.getInstance().get(Calendar.MILLISECOND);
			returnFile = FileUtils.createFile(AppConfiguration
					.getProperty(filePath)
					+ "/"
					+ fileName
					+ "_"
					+ dateIdentifier
					+ "."
					+ PricingConstants.CSV_FILE_EXTENSION);

			FileWriter fstream = new FileWriter(returnFile);
			BufferedWriter out = new BufferedWriter(fstream);
			String forWrite;

			for (PricingRow csvRow : listToWrite) {
				forWrite = new String();
				if (csvRow.isErrorRow()) {
					csvRow.setErrorRow(false);
					errorList.add(csvRow);

				} else {
					for (String currentField : columns) {
						PricingColumn column = csvRow
								.getColumnByName(currentField);
						forWrite += (column != null ? column.getValue() : "")
								+ PricingConstants.CSV_DELIMITER;
					}
					forWrite += "\n";
					out.write(forWrite);
				}
			}
			out.close();
		} catch (IOException e) {
			logger.error("[saveCsvFile] IOException " + e.getMessage());
		}
		if (!errorList.isEmpty()) {
			logger.debug("[saveCsvFile] ---------> THERE WAS SOME ERRORS DURING PARSING");
			saveCsvFile(errorList, "error_" + fileName,
					PricingConstants.ERROR_FILE_STORAGE, columns);
			if (errorList.size() == listToWrite.size()) {
				returnFile.delete();
			}
		}
		logger.debug("[saveCsvFile] ---------> " + fileName + " was saved to "
				+ AppConfiguration.getProperty(filePath));
		return returnFile;
	}

	public static void moveFileToArchive(File file) {
		InputStream in;
		try {
			File archiveFile = FileUtils.createFile(AppConfiguration
					.getProperty(PricingConstants.ARCHIVES_FILE_STORAGE)
					+ "/"
					+ file.getName());
			in = new FileInputStream(file);
			OutputStream out = new FileOutputStream(archiveFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			file.delete();
		} catch (FileNotFoundException e) {
			logger.error("[moveFileToArchive] FileNotFoundException "
					+ e.getMessage());
		} catch (IOException e) {
			logger.error("[moveFileToArchive] IOException " + e.getMessage());
		}
	}

	public static void moveFilesToArchive(String namePattern) {
		File[] fList;
		File directory = new File(
				AppConfiguration.getProperty(PricingConstants.FILE_STORAGE));
		fList = directory.listFiles();
		if (fList != null) {
			for (int i = 0; i < fList.length; i++) {
				if (fList[i].isFile()
						&& fList[i].getName().toLowerCase()
								.contains(namePattern.toLowerCase())) {
					moveFileToArchive(fList[i]);
				}
			}
		}
	}
}
