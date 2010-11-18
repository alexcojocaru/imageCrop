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

import javax.swing.filechooser.FileFilter;

import com.alexalecu.util.FileUtil;

public class CommonImageFilter extends FileFilter {

    /**
     * accept all directories and all jpg/bmp/gif/png files
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = FileUtil.getExtension(f);
        return "jpg".equalsIgnoreCase(extension) 
			|| "jpeg".equalsIgnoreCase(extension)
			|| "bmp".equalsIgnoreCase(extension)
			|| "gif".equalsIgnoreCase(extension)
			|| "png".equalsIgnoreCase(extension);
    }

   /**
    * @return the description of this filter
    */
    public String getDescription() {
        return "bmp, gif, jpg, png images";
    }

}
