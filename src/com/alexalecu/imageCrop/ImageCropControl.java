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
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.NebulaSkin;

import com.alexalecu.imageUtil.AutoSelectStatus;
import com.alexalecu.imageUtil.AutoSelectTask;
import com.alexalecu.imageUtil.GeomEdge;
import com.alexalecu.imageUtil.ImageColors;
import com.alexalecu.imageUtil.ImageConvert;
import com.alexalecu.imageUtil.ImageCropMethod;
import com.alexalecu.imageUtil.ImageKit;
import com.alexalecu.imageUtil.ImageRotate;
import com.alexalecu.util.FileUtil;

public class ImageCropControl implements ImageCropEngine {

	// initialize the logger
	private final static Logger appLogger = Logger.getLogger(ImageCropControl.class);
	static {
		PropertyConfigurator.configure("props" + File.separator + "logger.properties");
	}


	private final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	
	// the stack containing the list of parameters for each image subsequent to the initial image
	private Stack<ImageParams> imageParamStack;

	// the current image in buffer
	private BufferedImage imageCrt;

	private ImageCropGUI gui;
	
	private AutoSelectTask autoSelectTask;
	
	private boolean wizardMode;


	/**
	 * create a new instance, initializing the GUI, the image parameters and the image list
	 */
	public ImageCropControl() {
		imageParamStack = new Stack<ImageParams>();
		imageParamStack.push(new ImageParams());

		JFrame.setDefaultLookAndFeelDecorated(true);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setSkin(new NebulaSkin());
				gui = new ImageCropFrame(ImageCropControl.this);
			}
		});
		
		appLogger.debug("Application initialized.");
	}


	/**
	 * load a new image and set it as the current image
	 * @param imageFile the file containing the image to be loaded
	 */
	public void selectImage(File imageFile) {
		// load the image from the file
		BufferedImage image = loadImage(imageFile);
		if (image == null)
			return;
		
		ImageParams previousImageParams = imageParamStack.peek();
		
		// clear the stack
		clearImageParamsStack();

		// reset the image parameters and re-use some of the previous ones
		ImageParams imageParams = new ImageParams();
		imageParams.setImageFile(imageFile);
		imageParams.setBgColor(previousImageParams.getBgColor());
		imageParams.setBgTolerance(previousImageParams.getBgTolerance());
		imageParams.setState(ImageParams.ImageState.StateImageLoaded);
		imageParams.setCropMethod(previousImageParams.getCropMethod());
		
		// add the current parameters to the stack
		imageParamStack.push(imageParams);

		imageCrt = ImageConvert.cloneImage(image);
		
		setScaleFactorToFit();

		logCurrentParams();

		// and update the GUI image and state
		gui.setState(imageParams.getState());
		gui.setScaleFactor(imageCrt, imageParams.getScaleFactor());
		gui.setBgColor(imageParams.getBgColor());
		gui.setBgTolerance(imageParams.getBgTolerance());

		appLogger.debug("Image selected.");
	}
	
	/**
	 * load an image from a file
	 * @param imgFile the file containing the image
	 * @return the BufferedImage from the specified file
	 */
	private BufferedImage loadImage(File imgFile) {
		if (imgFile == null)
			return null;

		try {
			BufferedImage image = ImageConvert.read(new FileInputStream(imgFile));
			if (image == null)
				throw new Exception("invalid image");

			appLogger.debug("Image succesfully loaded: " + imgFile.getPath());

			return image;
		}
		catch (Exception ex) {
			// log the error and show an error message to the user
			appLogger.debug("Invalid / corrupt image file: " + imgFile.getPath(), ex);
			gui.showErrorDialog("Invalid / corrupt image file: " + imgFile.getPath());
			return null;
		}
	}

	/**
	 * reset the current image parameters and the initial and current images
	 */
	private void resetCurrentParams() {
		// clear the image list
		clearImageParamsStack();
		
		// reset the image parameters
		imageParamStack.push(new ImageParams());
		
		imageCrt = null;

		appLogger.debug("Resetting current params.");
	}

	
	/**
	 * Get notified about changes to the selection
	 * @param rectangle the selection rectangle; it is null if there is no selection
	 */
	public void selectionChanged(Rectangle rectangle) {
		ImageParams imageParams = imageParamStack.peek();
		
		imageParams.setSelectionRect(rectangle == null ?
				null : new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
		imageParams.setState(rectangle != null ?
				ImageParams.ImageState.StateSelection : ImageParams.ImageState.StateImageLoaded);
		
		gui.setState(imageParams.getState());
	}

	/**
	 * Get notified about changes to the background color
	 * @param color the new background color
	 */
	public void bgColorChanged(Color color) {
		imageParamStack.peek().setBgColor(color);
	}
	
	/**
	 * Get notified about changes to the background tolerance
	 * @param bgTolerance the new background color tolerance
	 */
	public void bgToleranceChanged(int bgTolerance) {
		imageParamStack.peek().setBgTolerance(bgTolerance);
	}
	
	/**
	 * Get notified about changes to the auto select method
	 * @param cropMethod the new select method
	 */
	public void autoCropMethodChanged(ImageCropMethod cropMethod) {
		imageParamStack.peek().setCropMethod(cropMethod);
	}

	/**
	 * apply the scale factor to the image in buffer
	 * @param scaleFactor the scale factor to apply
	 * @return true if the image has been scaled
	 */
	public boolean scaleFactorChanged(double scaleFactor) {
		ImageParams imageParams = imageParamStack.peek();
		
		// skip scaling if the is no change in the scale factor
		if (scaleFactor == imageParams.getScaleFactor())
			return false;
		
		if (imageCrt == null)
			return false;

		appLogger.debug("Scale to: " + scaleFactor);

		imageParams.setScaleFactor(scaleFactor);

		// tell the GUI to update the displayed image to reflect the new scale factor
		gui.setScaleFactor(imageCrt, scaleFactor);
		gui.setSelectionRect(imageParams.getSelectionRect(), true);
		
		return true;
	}


	/**
	 * make this image the current one in buffer; if the previous image is the initial one, add
	 * this image to the stack, otherwise replace the last image in stack with this one
	 * @param image the new image to use
	 */
	private void pushImage(BufferedImage image) {
		appLogger.debug("imageParamStack.size() = " + imageParamStack.size());

		if (imageParamStack.size() == 1) { // we only have the initial image in stack; add the new one
			ImageParams imageParams;
			try {
				imageParams = (ImageParams)imageParamStack.peek().clone();
			}
			catch (CloneNotSupportedException e) {
				appLogger.error("Cannot create new image params object", e);
				gui.showErrorDialog("Cannot create a new image!");
				return;
			}
			imageParamStack.push(imageParams);

			appLogger.debug("Adding image in images stack");
		}
		else { // replace the last element with the current one
			appLogger.debug("Replacing last image in images stack");
		}

		ImageParams imageParams = imageParamStack.peek();
		
		// set the image parameters
		imageParams.setScaleFactor(1d);
		imageParams.setState(ImageParams.ImageState.StateImageLoaded);
		imageParams.setSelectionRect(null);

		imageCrt = image;

		setScaleFactorToFit();

		logCurrentParams();

		// update the GUI to match the current state
		gui.setSelectionRect(imageParams.getSelectionRect(), true);
		gui.setState(imageParams.getState());
		gui.setScaleFactor(imageCrt, imageParams.getScaleFactor());
	}

	/**
	 * discard the current image and reinstate the previous one, while maintaining the selection
	 */
	public void discard() {
		discard(true);
	}

	/**
	 * discard the current image and reinstate the previous one
	 * @param keepSelection true to maintain the selection
	 */
	public void discard(boolean keepSelection) {
		appLogger.debug("Discarding image: imageParamStack.size() = " + imageParamStack.size());
		
		// if we only have one image in stack, reset the application state to init
		if (imageParamStack.size() == 1) {
			ImageParams previousImageParams = imageParamStack.peek();
			
			resetCurrentParams();
			
			ImageParams imageParams = imageParamStack.peek();
			
			// remember some of the previous settings
			imageParams.setBgColor(previousImageParams.getBgColor());
			imageParams.setBgTolerance(previousImageParams.getBgTolerance());
			imageParams.setCropMethod(previousImageParams.getCropMethod());
			
			// and update the GUI
			gui.setState(imageParams.getState());
			gui.setScaleFactor(imageCrt, imageParams.getScaleFactor());
			gui.setSelectionRect(imageParams.getSelectionRect(), true);
			
			return;
		}
		else { // otherwise lets reinstate the previous image
			double previousScaleFactor = imageParamStack.peek().getScaleFactor();
			
			imageParamStack.pop();
			
			ImageParams imageParams = imageParamStack.peek();
			double newScaleFactor = imageParamStack.peek().getScaleFactor();
			
			// reset the scale factor to its previous value, will do the scaling manually later
			imageParams.setScaleFactor(previousScaleFactor);

			// load the image from the file; if it cannot be done, discard this parameter set too
			BufferedImage image = loadImage(imageParams.getImageFile());
			if (image == null) {
				discard();
				return;
			}
			
			// reinstantiate the image
			imageCrt = image;

			if (!keepSelection && imageParams.getState() == ImageParams.ImageState.StateSelection)
				imageParams.setState(ImageParams.ImageState.StateImageLoaded);
			
			gui.setBgColor(imageParams.getBgColor());
			gui.setBgTolerance(imageParams.getBgTolerance());
			gui.setAutoCropMethod(imageParams.getCropMethod());
			
			// scale the image in buffer if needed, based on the new scale factor
			if (!scaleFactorChanged(newScaleFactor))
				gui.setScaleFactor(imageCrt, imageParams.getScaleFactor());

			// update the selection panel
			gui.setSelectionRect(imageParams.getSelectionRect(), true);

			// reset the GUI state and update the crop size if necessary
			gui.setState(imageParams.getState());
		}
	}


	/**
	 * crop the cropRectangle out of the current image, and set the new image as the current one;
	 * if the crop rectangle is invalid (coordinates and size are outside the current image bounds),
	 * the original image will be used as new image
	 * @param cropRectangle the rectangle to crop
	 */
	public void crop() {
		Rectangle rect = imageParamStack.peek().getSelectionRect();
		
		if (rect == null) {
			gui.showErrorDialog("No selection found !");
			return;
		}
		
		appLogger.debug("Crop image (x, y, w, h): " + rect.x + ", " + rect.y +
				", " + rect.width + ", " + rect.height);
		
		if (rect.x < 0 || rect.y < 0 || rect.width <= 0 || rect.height <= 0 ||
				rect.x + rect.width >= imageCrt.getWidth() ||
				rect.y + rect.height >= imageCrt.getHeight())
			pushImage(imageCrt);
		else
			pushImage(ImageConvert.cropImage(imageCrt, rect));
	}

	/**
	 * auto adjust the selection rectangle to mark the optimum image that can be cropped
	 */
	public void autoSelect() {
		ImageParams imageParams = imageParamStack.peek();

		if (!imageParams.isSelection()) {
			gui.showInfoDialog("First draw a selection inside the image !");
			return;
		}
		
		if (imageParams.getState() == ImageParams.ImageState.StateAutoSelecting) {
			gui.setAutoSelectStatus(AutoSelectStatus.Canceled);
			autoSelectTask.cancel(true);
		}
		else {
			autoSelectTask = new AutoSelectTask();
			// connect the task property change events to the current object actions
			autoSelectTask.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					if (arg0.getPropertyName().equals("autoSelectStatus")) {
						gui.setAutoSelectStatus((AutoSelectStatus)arg0.getNewValue());
					}
					else if (arg0.getPropertyName().equals("result")) {
						autoSelectDone( (Object[])arg0.getNewValue() );
					}
				}
			});
			autoSelectTask.setImage(imageCrt);
			autoSelectTask.setSelectionRect(imageParams.getSelectionRect());
			autoSelectTask.setBgColor(imageParams.getBgColor());
			autoSelectTask.setBgTolerance(imageParams.getBgTolerance());
			autoSelectTask.setCropMethod(imageParams.getCropMethod());
			
			imageParams.setState(ImageParams.ImageState.StateAutoSelecting);
			gui.setState(imageParams.getState());

			// and let it roll
			autoSelectTask.execute();
		}
	}
	
	/**
	 * called when the auto selection process is done
	 * @param rectProps a two element array containing the selection rectangle properties; first
	 * element is the rectangle bounding the polygon, the second is the list of polygon edges
	 */
	private void autoSelectDone(Object[] rectProps) {
		boolean isCanceled = autoSelectTask.isCancelled();
		autoSelectTask = null;
		
		Rectangle polygonRect = (Rectangle)rectProps[0];
		@SuppressWarnings("unchecked")
		ArrayList<GeomEdge> edgeList = (ArrayList<GeomEdge>)rectProps[1];

		ImageParams imageParams = imageParamStack.peek();
		
		if (isCanceled) { // operation was canceled, reset state to previous
			imageParams.setState(ImageParams.ImageState.StateSelection);
			gui.setAutoSelectStatus(AutoSelectStatus.Canceled);
			gui.setState(imageParams.getState());
			return;
		}

		gui.setAutoSelectStatus(AutoSelectStatus.Finished);
		
		appLogger.debug("Auto select method: " + imageParams.getCropMethod());
		appLogger.debug("Auto select result (x, y, w, h): " +
				(polygonRect == null ? "null" : polygonRect.x + ", " + polygonRect.y + ", " +
						polygonRect.width + ", " + polygonRect.height));

		// reject the result if it is not valid
		if (!validateSelectionRectangle(imageCrt, polygonRect)) {
			imageParams.setState(ImageParams.ImageState.StateSelection);
			gui.setState(imageParams.getState());
			gui.showErrorDialog("An error has occured !\nCheck the selection, background color" +
					" and tolerance and try again.");
			appLogger.error("An error has occured: invalid selection rectangle");
			return;
		}

		imageParams.setSelectionRect(polygonRect);

		// update the GUI properties
		gui.setSelectionRect(imageParams.getSelectionRect(), false);
		gui.setSelectionEdgeList(edgeList, true);

		// and finally set the state to 'selection'
		if (imageParams.getState() != ImageParams.ImageState.StateSelection) {
			imageParams.setState(ImageParams.ImageState.StateSelection);
			gui.setState(imageParams.getState());
		}
	}

	/**
	 * rotate the current image image in buffer; if there is a selection, it will be lost - the user
	 * is asked to confirm that
	 * @param deg the number of degrees to rotate the image with
	 */
	public void rotate(double deg) {
		// exit is there is a select and the user does not want to discard it
		if (imageParamStack.peek().getSelectionRect() != null && !gui.showConfirmDialog(
				"Rotating image will lost the current selection." + LINE_SEPARATOR +
				"Do you want to continue ?"))
			return;

		appLogger.debug("Rotate image; deg = " + deg);

		// rotate the initial image, the result will be a new image
		BufferedImage image = null;
		try {
			image = ImageRotate.rotateDegrees(imageCrt, deg, 
					ImageRotate.ROTATE_BOUNDING_BOX_OPTIMAL, null);
		}
		catch (Throwable tr) {
			appLogger.debug("", tr);
			
			// we might not have enough memory; tell the user to crop first to reduce the image size
			String msg = "An error has occured while rotating image !";
			if (tr instanceof OutOfMemoryError)
				msg += LINE_SEPARATOR + "The image is too big. Try croping it first.";
			gui.showErrorDialog(msg);
			
			return;
		}

		// compute the maximum hull which fits inside the rotated image
		Object res[] = ImageKit.autoSelectBoundingRectangle(image,
				new Rectangle(0, 0, image.getWidth(), image.getHeight()), Color.BLACK, 0, -1);
		
		Rectangle cropRect = (Rectangle)res[0];

		appLogger.debug("Auto select result (x, y, w, h): " + cropRect.x + ", " + cropRect.y + ", "
				+ cropRect.width + ", " + cropRect.height);

		// make sure that the resulting rectangle is valid and fits inside the current image
		if (!validateSelectionRectangle(image, cropRect)) {
			gui.showErrorDialog("An error has occured. Try again !");
			appLogger.debug("An error has occured; rotated image size: " +
					image.getWidth() + "x" + image.getHeight());
			return;
		}

		// crop the image according to the resulting rectangle and make it the current image in buffer
		pushImage(ImageConvert.cropImage(image, cropRect));
	}
	
	/**
	 * validate the selection rectangle against the current image position and size
	 * @param image the image to validate the selection against
	 * @param selection the selection to be validated
	 * @return true if the selection is inside the current image bounds
	 */
	private boolean validateSelectionRectangle(BufferedImage image, Rectangle selection) {
		return selection != null && selection.x >= 0 && selection.y >= 0 &&
				selection.width > 0 && selection.height > 0 &&
				selection.x + selection.width <= image.getWidth() &&
				selection.y + selection.height <= image.getHeight();
	}



	/**
	 * save the current image in buffer as JPEG; the file has to have a JPG extension; if the
	 * original image file is overwritten, it will be reset to the new one
	 * @param imageFile the file to save to; if it exists, the user is asked to confirm the overwriting
	 */
	public void saveAs(File imageFile) {
		// exit if the file is invalid
		if (imageFile == null) {
			gui.showErrorDialog("Invalid file to save to !");
			return;
		}
		
		// exit if the file extension is not JPEG
		String ext = FileUtil.getExtension(imageFile);
		if (ext == null || (!ext.equals("jpg") && !ext.equals("jpeg"))) {
			gui.showErrorDialog("Can save only to jpg files !");
			return;
		}

		boolean discardToFirst = false;

		// if the file exists, ask for user confirmation to overwrite it
		if (imageFile.exists()) {
			boolean flag = true;
			
			// if the file to write to is the original one, ask the user if it is okay to overwrite
			if (imageFile.getPath().equals(imageParamStack.peek().getImageFile().getPath())) {
				flag = gui.showConfirmDialog(
						"You are trying to overwrite the current editing image file." +
						LINE_SEPARATOR + "Are you sure you want to continue ?");
				
				if (flag) // the user chose to overwrite the original image
					discardToFirst = true;
			}
			else {
				flag = gui.showConfirmDialog("File already exists." + LINE_SEPARATOR +
						"Are you sure you want to overwrite it ?");
			}
			
			// if the user did not confirm the overwriting, exit
			if (!flag)
				return;
		}
		
		// and save
		save(imageFile);

		// if the original image file has been overwritten, reset the image to the new one
		if (discardToFirst)
			discard(false);
	}

	/**
	 * save the current image in buffer as JPEG, using an unique file name to avoid the overwriting
	 */
	public void save() {
		String dirPath = imageParamStack.peek().getImageFile().getParent();
		
		// create the file name; add an unique 3-digit number suffix to make sure the name is unique
		String imgName = FileUtil.stripExtension(imageParamStack.peek().getImageFile().getName()) + ".jpg";
		imgName = FileUtil.generateUniqueFileName(dirPath, imgName, 3);
		
		appLogger.debug("Generating new unique file name: " + imgName);
		
		// and save the file
		save(new File(dirPath, imgName));
	}

	/**
	 * save the current image in buffer as JPEG; the extension is not checked
	 * @param imageFile the file to save to; if it exists, this method will overwrite it
	 */
	public void save(File imageFile) {
		try {
			// save the current image as JPEG
			ImageConvert.writeJpg(imageCrt, new FileOutputStream(imageFile));
			appLogger.debug("Image saved as: " + imageFile.getPath());
			
			// and notify the user
			gui.showInfoDialog("Image saved as: " + LINE_SEPARATOR + imageFile.getPath());
		}
		catch (Exception e) {
			appLogger.debug("An error has occured while saving image to file: " +
					imageFile.getPath(), e);
			
			// notify the user about the error
			gui.showErrorDialog("An error has occured while saving image to file: " + 
					LINE_SEPARATOR + imageFile.getPath());
		}
	}

	/**
	 * get the pixel color at the specified coordinates
	 * @param x the x coordinate of the pixel
	 * @param y the y coordinate of the pixel
	 * @return the pixel color, or null if the image is null or the coordinates are out of bounds
	 */
	public Color getPixelColor(int x, int y) {
		// return null if there is no image
		if (imageCrt == null)
			return null;
		
		// also, if the coordinates requested are out of bounds
		if (x < 0 || x >= imageCrt.getWidth() || y < 0 || y >= imageCrt.getHeight())
			return null;

		return ImageColors.getPixelColor(imageCrt, x, y);
	}
	
	
	/**
	 * remove all the elements from the image parameter stack
	 */
	private void clearImageParamsStack() {
		while (!imageParamStack.isEmpty())
			imageParamStack.pop();
	}

	/**
	 * @return the filename of the current image
	 */
	public String getImageName() {
		return imageParamStack.peek().getImageFile() != null ?
				imageParamStack.peek().getImageFile().getName() : null;
	}
	
	/**
	 * @return the size of the current image in buffer, or (0, 0) if there is no such image
	 */
	public Dimension getImageSize() {
		return imageCrt == null ?
				new Dimension(0, 0) : new Dimension(imageCrt.getWidth(), imageCrt.getHeight());
	}

	/**
	 * @return true if there is at least one image in the editing buffer
	 */
	public boolean isImageInBuffer() {
		return imageParamStack.peek().getImageFile() != null;
	}

	/**
	 * set the state of the current image being edited; will set the state back on the GUI
	 * @param state the state to be set
	 */
	public void setState(ImageParams.ImageState state) {
		ImageParams imageParams = imageParamStack.peek();
		
		if (imageParams.getState() == state)
			return;
		
		imageParams.setState(state);
		
		// and set the GUI state
		gui.setState(imageParams.getState());
	}
	
	/**
	 * scale down the current image to fit the current image panel view port; do not scale up
	 */
	private void setScaleFactorToFit() {
		if (imageCrt == null)
			return;
		
		ImageParams imageParams = imageParamStack.peek();
		
		// if the image size is smaller or equal to the view port size, set scale factor to 1
		Dimension viewportSize = gui.getImagePanelSize();
		if (viewportSize.width >= imageCrt.getWidth() &&
				viewportSize.height >= imageCrt.getHeight()) {
			imageParams.setScaleFactor(1d);
			return;
		}
		
		// calculate it as the minimum of the horizontal and vertical scale factors
		double hScaleFactor = Math.floor(viewportSize.width * 100d / imageCrt.getWidth());
		double vScaleFactor = Math.floor(viewportSize.height * 100d / imageCrt.getHeight());
		int scaleFactor = (int)Math.min(hScaleFactor, vScaleFactor);
		
		imageParams.setScaleFactor(scaleFactor / 100d);
	}
	
	/**
	 * start or stop the wizard mode
	 */
	public void toggleWizard() {
		
	}


	/**
	 * print the current parameters to the log file (the image list size, state, image filename,
	 * image width / height, scale factor)
	 */
	public void logCurrentParams() {
		ImageParams imageParams = imageParamStack.peek();
		
		appLogger.debug("imageParamStack.size() = " + imageParamStack.size());
		appLogger.debug("state = " + imageParams.getState());
		
		appLogger.debug("imageFile = " + (imageParams.getImageFile() != null ?
				imageParams.getImageFile().getPath() : "null"));
		appLogger.debug("imageWidth = " + imageCrt.getWidth());
		appLogger.debug("imageHeight = " + imageCrt.getHeight());
		
		appLogger.debug("imageScale = " + imageParams.getScaleFactor());
	}

	/**
	 * exit the application
	 */
	public void exitApp() {
		// dispose the GUI
		if (gui != null)
			gui.dispose();

		// log the exiting message
		appLogger.debug("Exiting application." + LINE_SEPARATOR);

		// and exit the app
		System.exit(0);
	}


	public static void main(String args[]) {
		appLogger.debug("Starting application.");
		
		new ImageCropControl();
	}
}
