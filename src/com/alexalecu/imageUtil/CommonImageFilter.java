/*
 * Created on 29.03.2005
 */
package com.alexalecu.imageUtil;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.alexalecu.util.FileUtil;

/**
 * @author alex
 */
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
