package com.alexalecu.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * @author alex
 */
public class FileUtil {

	/**
	 * generate an unique file name for the given file, assuming 5 digits for the auto-generated
	 * number to be added at the end of the file name if it exists already in the given directory
	 * @param dir the directory containing the file
	 * @param file the file name for which to generate an unique name
	 * @param sync object to synchronize for when looking up the file
	 * @return an unique file name in the given directory
	 */
	public static String generateUniqueFileName(String dir, String file, Object sync) {
		return generateUniqueFileName(dir, file, sync, 5);
	}

	/**
	 * generate an unique file name for the given file
	 * @param dir the directory containing the file
	 * @param file the file name for which to generate an unique name
	 * @param nr the number of digits for the auto-generated number to be added at the end of the
	 * file name if it exists already in the given directory
	 * @param sync object to synchronize for when looking up the file
	 * @return an unique file name in the given directory
	 */
	public static String generateUniqueFileName(String dir, String file, Object sync, int nr) {
		synchronized (sync) {
			return generateUniqueFileName(dir, file, nr);
		}
	}

	/**
	 * generate an unique file name for the given file, assuming 5 digits for the auto-generated
	 * number to add at the end of the file name if it exists already in the given directory
	 * @param dir the directory containing the file
	 * @param file the file name for which to generate an unique name
	 * @return an unique file name in the given directory
	 */
	public static String generateUniqueFileName(String dir, String fileName) {
		return generateUniqueFileName(dir, fileName, 5);
	}

	/**
	 * generate an unique file name for the given file
	 * @param dir the directory containing the file
	 * @param file the file name for which to generate an unique name
	 * @param nr the number of digits for the auto-generated number to be added at the end of the
	 * file name if it exists already in the given directory
	 * @return an unique file name in the given directory
	 */
	public static String generateUniqueFileName(String dir, String fileName, int nr) {
		// if the file does not exist, return its name
		File f = new File(dir, fileName);
		if (!f.exists())
			return fileName;
		
		fileName = removeAutoGenNumber(fileName, nr);
		String name = stripExtension(fileName);
		String ext = getExtension(fileName);
		
		// set up the pattern for the auto-generated number
		StringBuffer pattern = new StringBuffer();
		for (int i = 0; i < nr; i++)
			pattern.append('0');
		DecimalFormat df = new DecimalFormat(pattern.toString());
		
		// search for the first unique file name in the directory, probing one number after another
		// (append it between the file name and the extension) until we reach the limit
		int count = 1;
		int max = Integer.parseInt("1" + pattern.toString()); // maximum numbers to try
		while (count < max) {
			String sufix = df.format(count);
			count++;
			
			// create the file name by appending the separator and the auto-generated number
			String fileCrt = name + "_" + sufix;
			if (ext != null && ext.length() > 0)
				fileCrt += "." + ext;
			
			// if the file does not exist, return it
			f = new File (dir, fileCrt);
			if (!f.exists())
				return fileCrt;
		}
		
		return null;
	}

	
	/**
	 * remove the auto generated number from the end of the file name, assuming it is 5 digit long
	 * @param fileName the file name to remove the auto-generated number from
	 * @return the file name without the separator and auto-generated number, if any
	 */
	public static String removeAutoGenNumber(String fileName) {
		return removeAutoGenNumber(fileName, 5);
	}
	
	/**
	 * remove the auto generated number from the end of the file name
	 * @param fileName the file name to remove the auto-generated number from
	 * @param nr the number of digits for the auto-generated number
	 * @return the file name without the separator and auto-generated number, if any
	 */
	public static String removeAutoGenNumber(String fileName, int nr) {
		if (fileName.length() <= nr + 1)
			return fileName;
		
		String name = stripExtension(fileName);
		String ext = getExtension(fileName);
		
		boolean autoGenName = true;
		
		// check that the last nr characters are digits, and the character before them is the separator
		if (name.length() < nr + 1 || name.charAt(name.length() - nr - 1) != '_') {
			autoGenName = false;
		}
		else {
			for (int i = 1; i < nr + 1; i++) {
				if (!Character.isDigit(name.charAt(name.length() - i))) {
					autoGenName = false;
					break;
				}
			}
		}
		
		// remove the separator and the auto-generated number and recreate the file name back
		if (autoGenName) {
			fileName = name.substring(0, name.length() - nr - 1);
			if (ext != null && ext.length() > 0)
				fileName += "." + ext;
		}
		
		return fileName;
	}
	
	
	/**
	 * replace the write spaces in the file name with the '_' character
	 * @param fileName
	 * @return the file name with all white spaces replaced by the '_' character
	 */
	public static String replaceWhitespaces(String fileName) {
		if (fileName == null)
			return null;
		return fileName.replace(' ','_');
	}

	/**
	 * @param f the file whose extension has to be determined
	 * @return the extension of the file, or the void string if it has no extension
	 */
	public static String getExtension(File f) {
		return getExtension(f.getName());
	}

	/**
	 * @param fileName the name of the file whose extension has to be determined
	 * @return the extension of the file, or the void string if it has no extension
	 */
	public static String getExtension(String fileName) {
		if (fileName == null || fileName.trim().length() == 0)
			return "";
		
		// look up the last file separator
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		
		int dotPos = fileName.lastIndexOf('.');
		return dotPos > 0 ? fileName.substring(dotPos + 1) : "";
	}
	
	/**
	 * @param fileName the name of the file whose extension has to be removed
	 * @return the name of file without extension
	 */
	public static String stripExtension(String fileName) {
		String ext = getExtension(fileName);
		return ext.equals("") ? fileName : fileName.substring(0, fileName.lastIndexOf(ext) - 1);
	}

	
	/**
	 * @param filePath the path of the file whose existence has to be verified
	 * @return true if the file denoted by filePath exists
	 */
	public static boolean existsFile(String filePath) {
		return new File(filePath).exists();
	}


	/**
	 * write a string to a file; the file will be overwritten if it exists already
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
			throw new IOException("destination file already exists");
		
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
			throw new IOException("source file doesn't exists");
		
		if (!overwrite && dstFile.exists())
			throw new IOException("destination file already exists");
		
		FileChannel srcChannel = null, dstChannel = null;
		
		try {
			// Create channel on the source
			srcChannel = new FileInputStream(srcFile).getChannel();

			// Create channel on the destination
			dstChannel = new FileOutputStream(dstFile).getChannel();

			// Copy file contents from source to destination
			dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
		}
		catch (IOException ioe) {
			throw ioe;
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
		
		// delete the directory content, and then the directory itself
		File fis[] = dir.listFiles();
		for (int i = 0; i < fis.length; i++)
			deleteFileOrDirectory(fis[i]);
		
		return dir.delete();
	}

	

	
	private final static DecimalFormat nf;
	private final static DecimalFormatSymbols dfs;
	static {
		dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		nf = new DecimalFormat("0.00",dfs);
	}
	
	public static String beautifyFileSize(long size) {
		double sizeD = (double)size;
		if (sizeD / 1000000000d > 1d)
			return nf.format(sizeD / 1073741824d) + " Gb";
		
		if (sizeD / 1000000d > 1d)
			return nf.format(sizeD / 1048576d) + " Mb";
		
		if (sizeD / 1000d > 1d)
			return nf.format(sizeD / 1024d) + " Kb";
		
		return size + " b";
	}
	
}
