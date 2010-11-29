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

package com.alexalecu.imageCrop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;

import com.alexalecu.imageUtil.ImageSelectMethod;

public interface ImageCropEngine {
	/**
	 * exit the application
	 */
	public void exitApp();
	
	/**
	 * enter / exit the select background color mode
	 */
	public void toggleSelectBackgroundMode();

	/**
	 * auto adjust the selection rectangle to mark the optimum image that can be cropped
	 */
	public void autoSelect();

	/**
	 * crop the selection rectangle out of the current image, and set the new image as the current
	 * one; if the selection rectangle is invalid (coordinates and size are outside the current
	 * image bounds), the original image will be used as new image
	 */
	public void crop();
	
	/**
	 * rotate the current image image in buffer; if there is a selection, it will be lost - the user
	 * is asked to confirm that
	 * @param deg the number of degrees to rotate the image with
	 */
	public void rotate(double deg);

	/**
	 * discard the current image and maintain the selection and reinstate the previous one
	 */
	public void discard();

	/**
	 * load a new image and set it as the current image
	 */
	public void selectImage();

	/**
	 * save the current image in buffer as JPEG, using an unique file name to avoid the overwriting
	 */
	public void save();

	/**
	 * save the current image in buffer as JPEG; the file has to have a JPG extension; if the
	 * original image file is overwritten, it will be reset to the new one
	 * @param imageFile the file to save to; if it exists, the user is asked to confirm the overwriting
	 */
	public void saveAs(File imageFile);

	/**
	 * set the state of the current image being edited; will set the state back on the GUI
	 * @param state the state to be set
	 */	
	public void setState(ImageCropState state);

	
	/**
	 * @return the current image params object
	 */
	public ImageParams getCurrentImageParams();

	/**
	 * @return the filename of the current image
	 */
	public String getImageName();

	/**
	 * @return the size of the current image in buffer, or (0, 0) if there is no such image
	 */
	public Dimension getImageSize();
	
	
	/**
	 * Get notified about changes to the background color
	 * @param color the new background color
	 */
	public void bgColorChanged(Color color);
	
	/**
	 * Get notified about changes to the background tolerance
	 * @param bgTolerance the new background color tolerance
	 */
	public void bgToleranceChanged(int bgTolerance);
	
	/**
	 * Get notified about changes to the auto select method
	 * @param selectMethod the new select method
	 */
	public void autoSelectMethodChanged(ImageSelectMethod selectMethod);

	/**
	 * Get notified about changes to the selection
	 * @param rectangle the selection rectangle; it is null if there is no selection
	 */
	public void selectionChanged(Rectangle rectangle);


	/**
	 * Get notified about changes to the scale factor
	 * @param scaleFactor the new scale factor
	 * @return true if the image has been scaled
	 */
	public boolean scaleFactorChanged(double scaleFactor);
	
	/**
	 * start or stop the wizard mode
	 */
	public void toggleWizard();
	
	
}
