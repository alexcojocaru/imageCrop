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

import com.alexalecu.util.FileUtil;

public class FileFilter extends javax.swing.filechooser.FileFilter {
	private String[] ext;
	private String descr;
	
	public FileFilter(String[] ext) {
		this.ext = ext == null ? new String[0] : ext;
		
		// create the filter description to include all acceptable file types
		StringBuilder sb = new StringBuilder();
		if (ext.length > 0) {
			sb.append(ext[0]);
			for (int i = 1; i < ext.length; i++) {
				sb.append(", ").append(ext[i]);
			}
		}
		sb.append(" files");
		descr = sb.toString();
	}

    /**
     * accept all directories and all file types defined in the constructor
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = FileUtil.getExtension(f);

		// the extension includes the dot, lets get rid of it
        if (extension.length() > 0)
        	extension = extension.substring(1);
        
        for (int i = 0; i < ext.length; i++)
        	if (ext[i].equals(extension))
        		return true;
        
        return false;
    }

    /**
     * @return the description of this filter
     */
    public String getDescription() {
        return descr;
    }

}
