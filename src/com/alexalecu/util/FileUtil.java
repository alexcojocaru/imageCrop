/*
 * Copyright (C) 2010 Alex Cojocaru
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alexalecu.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class FileUtil {
	
	private final static int GENERATED_SUFFIX_LENGTH = 3;

	/**
	 * Generate an unique file name for the given file, assuming 5 digits for the auto-generated
	 * number to be added at the end of the file name if it exists already in the given directory
	 * @param dir the directory containing the file
	 * @param filename the filename to generate an unique name for
	 * @param sync object to synchronize on when looking up the file
	 * @return an unique file name in the given directory
	 */
	public static String generateUniqueFilename(String dir, String filename, Object sync) {
		return generateUniqueFilename(dir, filename, sync, GENERATED_SUFFIX_LENGTH);
	}

	/**
	 * Generate an unique file name for the given file
	 * @param dir the directory containing the file
	 * @param filename the filename to generate an unique name for
	 * @param suffixLength the number of digits for the auto-generated number to append to the
	 * file name, if the given filename already exists in the given directory
	 * @param sync object to synchronize on when looking up the file
	 * @return an unique file name in the given directory
	 */
	public static String generateUniqueFilename(String dir, String filename, Object sync,
			int suffixLength) {
		
		synchronized (sync) {
			return generateUniqueFilename(dir, filename, suffixLength);
		}
	}

	/**
	 * Generate an unique file name for the given file, assuming 3 digits for the auto-generated
	 * number to append to the file name, if the given filename already exists
	 * in the given directory
	 * @param dir the directory containing the file
	 * @param filename the file name to generate an unique name for
	 * @return an unique file name in the given directory
	 */
	public static String generateUniqueFilename(String dir, String filename) {
		return generateUniqueFilename(dir, filename, 5);
	}

	/**
	 * Generate an unique file name for the given filename
	 * @param dir the directory containing the file
	 * @param filename the file name to generate an unique name to
	 * @param suffixLength the number of digits for the auto-generated number to append to the
	 * file name, if the given filename exists already in the given directory
	 * @return an unique file name in the given directory
	 */
	public static String generateUniqueFilename(String dir, String filename, int suffixLength) {
		// if the file does not exist, return its name
		File file = new File(dir, filename);
		if (!file.exists())
			return filename;
		
		filename = removeAutoGenNumber(filename, suffixLength);
		String basename = stripExtension(filename);
		String extension = getExtension(filename);
		
		String filenamePattern = basename + "_%1$000" + extension;
		
		// search for the first unique filename in the directory, probing one suffix after another
		// (append it between the filename and the extension) until we reach the limit
		for (int index = 1; index < Math.pow(10, suffixLength); index++)
		{
			filename = String.format(filenamePattern, index);
			
			// if the file does not exist, return it
			file = new File (dir, filename);
			if (!file.exists())
				return filename;
		}
		
		return null;
	}

	
	/**
	 * remove the auto generated suffix from the end of the file name, assuming it is 3 digit long
	 * @param filename the filename to remove the auto-generated suffix from
	 * @return the filename without the separator and auto-generated suffix, if any
	 */
	public static String removeAutoGenNumber(String filename) {
		return removeAutoGenNumber(filename, GENERATED_SUFFIX_LENGTH);
	}
	
	/**
	 * remove the auto generated suffix from the end of the filename
	 * @param filename the filename to remove the auto-generated suffix from
	 * @param suffixLength the number of digits for the auto-generated suffix
	 * @return the filename without the separator and auto-generated suffix, if any
	 */
	public static String removeAutoGenNumber(String filename, int suffixLength) {
		if (filename.length() <= suffixLength + 1)
			return filename;
		
		String extension = getExtension(filename);
		
		Pattern pattern = Pattern.compile("(.+?)_\\d{" + suffixLength + "})");
		Matcher matcher = pattern.matcher(filename);
		
		return matcher.matches() ? matcher.group(1) + extension : filename;
	}
	
	
	/**
	 * replace the write spaces in the filename with the '_' character
	 * @param filename
	 * @return the filename with all the white spaces replaced by the '_' character
	 */
	public static String replaceWhitespaces(String filename) {
		if (Strings.isNullOrEmpty(filename))
			return null;
		
		return filename.replace(' ','_');
	}

	/**
	 * @param file the file whose extension has to be determined
	 * @return the extension of the file (including the .),
	 * or the void string if it has no extension
	 */
	public static String getExtension(File file) {
		return getExtension(file.getName());
	}

	/**
	 * @param filename the name of the file whose extension has to be determined
	 * @return the extension of the file (including the .),
	 * or the void string if it has no extension
	 */
	public static String getExtension(String filename) {
		if (Strings.isNullOrEmpty(filename))
			return "";
		
		Matcher matcher = Pattern.compile(".+?(\\.[^\\.]+)").matcher(filename);
		return matcher.matches() ? matcher.group(1) : "";
	}
	
	/**
	 * @param filename the name of the file whose extension has to be removed
	 * @return the file basename
	 */
	public static String stripExtension(String filename) {
		String extension = getExtension(filename);
		return filename.substring(0, filename.length() - extension.length());
	}

	
	/**
	 * @param filepath the path of the file whose existence has to be verified
	 * @return true if the file denoted by filepath exists
	 */
	public static boolean existsFile(String filepath) {
		return new File(filepath).exists();
	}


	/**
	 * write a string to a file; the file will be overwritten if it already exists
	 * @param source the text to be written
	 * @param outputFile the file to write the text to
	 * @throws IOException
	 */
	public static void writeFile(String source, File outputFile) throws IOException {
		writeFile(source, outputFile, true);
	}

	/**
	 * write a string to a file
	 * @param source the text to be written
	 * @param outputFile the file to write the text to
	 * @param overwrite true to overwrite the output file, if it exists already
	 * @throws IOException
	 */
	public static void writeFile(String source, File outputFile, boolean overwrite)
			throws IOException {
		
		boolean created = false;
		
		if (!outputFile.exists()) {
			outputFile.createNewFile();
			created = true;
		}
		else if (!overwrite)
		{
			throw new IOException("Destination file already exists");
		}
		
		byte[] sourceAsBytes = source.getBytes();
		ByteBuffer bb = ByteBuffer.allocate(sourceAsBytes.length);
		bb.put(sourceAsBytes,0,sourceAsBytes.length);
		bb.flip();
		
		FileChannel fc =  null;
		try {
			fc = new FileOutputStream(outputFile).getChannel();
			fc.write(bb);
		}
		catch (IOException ioe) {
			// if the operation failed, clean up first before throwing the exception
			if (created)
				outputFile.delete();
			throw ioe;
		}
		finally {
			if (fc != null)
				fc.close();
		}
	}


	/**
	 * copy a file, allowing to overwrite if the destination already exists
	 * @param srcFile the source file
	 * @param dstFile the destination file
	 * @param overwrite true to overwrite the destination if it exists already
	 * @throws IOException
	 */
	public static void copyFiles(File srcFile, File dstFile, boolean overwrite) throws IOException {
		if (!srcFile.exists())
			throw new IOException("Source file does not exist");
		
		if (!overwrite && dstFile.exists())
			throw new IOException("Destination file already exists");
		
		FileChannel srcChannel = null, dstChannel = null;
		
		try {
			// Create channel on the source
			srcChannel = new FileInputStream(srcFile).getChannel();

			// Create channel on the destination
			dstChannel = new FileOutputStream(dstFile).getChannel();

			// Copy file contents from source to destination
			dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
		}
		finally {
			// Close the channels
			if (srcChannel != null)
				srcChannel.close();
			
			if (dstChannel != null)
				dstChannel.close();
		}
	}

	/**
	 * delete a file or directory
	 * @param dir the file or directory to delete
	 * @return if dir has been deleted
	 * @throws IOException
	 */
	public static boolean deleteFileOrDirectory(File dir) throws IOException {
		if (!dir.exists())
			return true;
		
		// if it is a file, delete and return the result
		if (dir.isFile())
			return dir.delete();
		
		// recursively delete the directory content, and then the directory itself
		File fis[] = dir.listFiles();
		for (int i = 0; i < fis.length; i++)
			deleteFileOrDirectory(fis[i]);
		
		return dir.delete();
	}

	
	private final static DecimalFormat fileSizeFormatFormat;
	private final static DecimalFormatSymbols decimalFormatSymbol;
	static {
		decimalFormatSymbol = new DecimalFormatSymbols();
		decimalFormatSymbol.setDecimalSeparator('.');
		fileSizeFormatFormat = new DecimalFormat("0.00", decimalFormatSymbol);
	}
	
	/**
	 * @param size the file size in bytes
	 * @return the human readable file size in gb, mb, kb or b, whichever is more appropriate
	 */
	public static String getHumanReadableFileSize(long size) {
		if (size / 1000000000d > 1d)
			return fileSizeFormatFormat.format(size / 1073741824d) + " Gb";
		
		if (size / 1000000d > 1d)
			return fileSizeFormatFormat.format(size / 1048576d) + " Mb";
		
		if (size / 1000d > 1d)
			return fileSizeFormatFormat.format(size / 1024d) + " Kb";
		
		return size + " b";
	}
	
}
