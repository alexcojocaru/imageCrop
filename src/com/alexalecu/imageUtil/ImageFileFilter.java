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

package com.alexalecu.imageUtil;

import java.io.File;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.filechooser.FileFilter;

import com.alexalecu.util.FileUtil;

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
