/*
 * Created on 26.01.2005
 */
package com.alexalecu.imageCrop;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;

import com.alexalecu.imageCrop.ImageCropEngine.CropMethod;

/**
 * define the current status of an image in the selection panel
 * @author alex
 */
public class ImageParams {
	
	/**
	 * represents the state of an image in the selection panel
	 * @author alex
	 */
	public static enum ImageState {
	    StateInit,
	    StateImageLoaded,
	    StateSelectBackgroundColor,
	    StateSelection
	}
	
	private File imageFile; // the filename of the current image
	private double scaleFactor; // the scale factor
	private Color bgColor; // the background color
	private int bgTolerance; // the tolerance for the background color
	private Rectangle selectionRect; // the selection rectangle properties
	private ImageState state; // the image state
	private CropMethod cropMethod; // the crop method to be used for auto-cropping
	private int timeToAutoSelect; // how many seconds to allow the auto-select operation to run


	/**
	 * create a new instance, setting the scale factor to 1, the background color to black, the
	 * selection rectangle to 0 and the image state to STATE_INIT
	 */
	public ImageParams() {
		scaleFactor = 1d;
		bgColor = Color.BLACK;
		bgTolerance = 3;
		selectionRect = null;
		state = ImageState.StateInit;
		cropMethod = CropMethod.CropMinimum;
		timeToAutoSelect = 4;
	}

	/**
	 * @return the image file name
	 */
	public File getImageFile() {
		return imageFile;
	}

	/**
	 * set the image file name
	 * @param imageFile
	 */
	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}

	/**
	 * @return the scale factor
	 */
	public double getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * set the scale factor
	 * @param scaleFactor
	 */
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	/**
	 * @return the background color
	 */
	public Color getBgColor() {
		return bgColor;
	}

	/**
	 * set the background color
	 * @param bgColor
	 */
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * @return the background color tolerance
	 */
	public int getBgTolerance() {
		return bgTolerance;
	}

	/**
	 * set the background color tolerance
	 * @param bgTolerance
	 */
	public void setBgTolerance(int bgTolerance) {
		this.bgTolerance = bgTolerance;
	}

	/**
	 * @return the selection rectangle
	 */
	public Rectangle getSelectionRect() {
		return selectionRect;
	}

	/**
	 * set the selection rectangle
	 * @param selectionRect
	 */
	public void setSelectionRect(Rectangle selectionRect) {
		this.selectionRect = selectionRect;
	}

	/**
	 * @return the current image state
	 */
	public ImageState getState() {
		return state;
	}

	/**
	 * set the current image state
	 * @param state
	 */
	public void setState(ImageState state) {
		this.state = state;
	}
	
	/**
	 * @return the crop method to be used for auto-selection
	 */
	public CropMethod getCropMethod() {
		return cropMethod;
	}

	/**
	 * set the crop method to be used for auto-selection
	 * @param cropMethod
	 */
	public void setCropMethod(CropMethod cropMethod) {
		this.cropMethod = cropMethod;
	}

	/**
	 * @return the number of seconds to allow the auto-select operation to run
	 */
	public int getTimeToAutoSelect() {
		return timeToAutoSelect;
	}

	/**
	 * set how many seconds to allow the auto-select operation to run
	 * @param timeToAutoSelect
	 */
	public void setTimeToAutoSelect(int timeToAutoSelect) {
		this.timeToAutoSelect = timeToAutoSelect;
	}

	/**
	 * @return true if the selection rectangle is valid
	 */
	public boolean isSelection() {
		return selectionRect.x != 0 || selectionRect.y != 0 ||
			selectionRect.width != 0 || selectionRect.height != 0;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ImageParams imageParams = new ImageParams();
		
		imageParams.imageFile = imageFile;
		imageParams.scaleFactor = scaleFactor;
		imageParams.bgColor = new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
		imageParams.bgTolerance = bgTolerance;
		imageParams.state = state;
		imageParams.cropMethod = cropMethod;
		imageParams.timeToAutoSelect = timeToAutoSelect;
		imageParams.selectionRect = new Rectangle(selectionRect.x, selectionRect.y,
				selectionRect.width, selectionRect.height);
		
		return imageParams;
	}

}
