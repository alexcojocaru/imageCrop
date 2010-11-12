/*
 * Created on 25.04.2005
 */
package com.alexalecu.imageUtil;

import java.io.File;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.filechooser.FileFilter;

import com.alexalecu.util.FileUtil;

/**
 * @author alex
 */
public class ImageFileFilter extends FileFilter {
	
    /**
     * accept all image files
     */
	public boolean accept(File file) {
		if (file.isDirectory())
			return true;

		// get file extension; return false if the file does not have one
		String ext = FileUtil.getExtension(file);
		if (ext == null || ext.trim().length() == 0)
			return false;

		// return true if an image reader can be retrieved for this file suffix
		java.util.Iterator<ImageReader> writers = ImageIO.getImageReadersBySuffix(ext);
		return writers.hasNext() ? true : false;
	}

	/**
	 * @return the description of this filter
	 */
	public String getDescription() {
		return "All images";
	}
}
