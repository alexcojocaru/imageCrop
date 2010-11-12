/*
 * Created on 23.06.2005
 */
package com.alexalecu.imageUtil;

import java.io.File;

import com.alexalecu.util.FileUtil;

/**
 * @author alex
 */
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
