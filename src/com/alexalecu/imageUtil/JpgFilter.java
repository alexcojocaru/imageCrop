/*
 * Created on 18.01.2005
 */
package com.alexalecu.imageUtil;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.alexalecu.util.FileUtil;

/**
 * @author alex
 */
public class JpgFilter extends FileFilter {

    /**
     * accept all directories and all jpg files
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = FileUtil.getExtension(f);
        return "jpg".equalsIgnoreCase(extension) || "jpeg".equalsIgnoreCase(extension);
    }

    /**
     * @return the description of this filter
     */
    public String getDescription() {
        return "jpg or jpeg images";
    }

}
