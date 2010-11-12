package com.alexalecu.imageCrop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.alexalecu.imageCrop.ImageCropEngine.CropMethod;
import com.alexalecu.imageUtil.GeomEdge;

/**
 * represents a container for an ImagePanel / SelectionPanel object
 * @author alex
 */
public interface ImageCropGUI {

	public enum ControlSet { ControlSetLoad, ControlSetScale, ControlSetBackground,
		ControlSetAutoCrop, ControlSetAutoSelect, ControlSetMoveResize, ControlSetCrop,
		ControlSetRotate, ControlSetSave
	}


	/**
	 * set the state of the components (some labels & all controls) based on the value passed as
	 * parameter
	 * @param imageState the current state of the image
	 */
	public void setState(ImageParams.ImageState imageState);

	/**
	 * set the background color on the inner components
	 * @param color the color to be set
	 */
	public void setBgColor(Color color);

	/**
	 * set the background tolerance on the inner components
	 * @param bgTolerance the background color tolerance to be set
	 */
	public void setBgTolerance(int bgTolerance);

	/**
	 * set the new crop method in the corresponding panel
	 * @param cropMethod the crop method to be set
	 */
	public void setAutoCropMethod(CropMethod cropMethod);
	
	/**
	 * apply the scale factor to the selection and image panels
	 * @param image the current BufferedImage
	 * @param scaleFactor the scale factor to be applied
	 */
	public void setScaleFactor(BufferedImage image, double scaleFactor);

	/**
	 * update the image size label value based on the new image size
	 * @param imageSize the new image size
	 */
	public void setImageSize(Dimension imageSize);
	
	/**
	 * set the selection rectangle on the SelectionPanel
	 * @param selection the selection rectangle
	 * @param repaint true to repaint the selection panel
	 */
	public void setSelectionRect(Rectangle selection, boolean repaint);
	
	/**
	 * set the edge list on the SelectionPanel
	 * @param edgeList
	 * @param repaint true to repaint the selection panel
	 */
	public void setSelectionEdgeList(ArrayList<GeomEdge> edgeList, boolean repaint);

	/**
	 * reset the state of the panel
	 * @param repaint true if a repaint should be performed after reset
	 */
	public void resetSelectionPanel(boolean repaint); 
	
	/**
	 * set the size of the crop rectangle
	 * @param selectionSize
	 */
	public void setSelectionCropSize(Dimension selectionSize);
	
	
	/**
	 * @return the size of the image panel view port
	 */
	public Dimension getImagePanelSize();
	
	
	/**
	 * Get notified about changes to the background color
	 * @param color the color to be set
	 */
	public void bgColorChanged(Color color);
	
	/**
	 * Get notified about changes to the background color
	 * @param color the color to be set
	 * @param updateCropPanel true to update the color value in the crop panel
	 */
	public void bgColorChanged(Color color, boolean updateCropPanel);
	
	/**
	 * Get notified about changes to the background tolerance
	 * @param bgTolerance the background color tolerance to be set
	 */
	public void bgToleranceChanged(int bgTolerance);
	
	/**
	 * Get notified about changes to the auto crop method
	 * @param cropMethod the crop method to be set
	 */
	public void autoCropMethodChanged(CropMethod cropMethod);
	
	/**
	 * Get notified that the selection has changed
	 * @param rectangle the selection rectangle; it is null if there is no selection
	 */
	public void selectionChanged(Rectangle rectangle);

	/**
	 * Get notified about changes to the scale factor
	 * @param scaleFactor the new scale factor
	 */
	public void scaleFactorChanged(int scaleFactor);

	
	/**
	 * enter / exit the select background color mode
	 */
	public void toggleSelectBackgroundMode();
	
	/**
	 * request to auto select the picture marked by the selection rectangle
	 */
	public void autoSelectPicture();

	/**
	 * trigger a request to moves the image selection to the direction given by x and y
	 * @param x the x direction to move the selection to; one of -1 for left, 0 or 1 for right
	 * @param y the y direction to move the selection to; one of -1 for left, 0 or 1 for right
	 * @param step the step in pixels to move with
	 */
	public void moveSelection(int x, int y, int step);

	/**
	 * trigger a request to resize the selection rectangle
	 * @param top one of -1 for up, 0 or 1 for down to apply to the top edge of the selection
	 * @param left one of -1 for left, 0 or 1 for right to apply to the left edge of the selection
	 * @param bottom one of -1 for up, 0 or 1 for down to apply to the bottom edge of the selection
	 * @param right one of -1 for left, 0 or 1 for right to apply to the right edge of the selection
	 * @param step the step in pixels to resize with
	 */
	public void resizeSelection(int top, int left, int bottom, int right, int step);
	
	/**
	 * crop the image selection
	 */
	public void cropSelection();
	
	/**
	 * rotate the image selection
	 */
	public void rotateSelection();
	
	/**
	 * discard the image in buffer
	 */
	public void discardImage();
	
	/**
	 * save the selection, overwriting the original image file
	 */
	public void saveImage();
	
	/**
	 * save the selection, allowing to choose the filename
	 */
	public void saveAsImage();
	
	/**
	 * dispose the window component
	 */
	public void dispose();

	
	/**
	 * display an error dialog
	 * @param msg the message to be displayed
	 */
	public void showErrorDialog(String msg);

	/**
	 * display an info dialog
	 * @param msg the message to be displayed
	 */
	public void showInfoDialog(String msg);

	/**
	 * show a confirmation message
	 * @param msg the message to be displayed
	 * @return true if the user accepted, false if not
	 */
	public boolean showConfirmDialog(String msg);

	/**
	 * show an input message
	 * @param msg the label to display
	 * @return the string entered by the user
	 */
	public String showInputDialog(String msg);
}