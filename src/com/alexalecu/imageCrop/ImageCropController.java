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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.NebulaSkin;

import SK.gnome.morena.Morena;
import SK.gnome.morena.MorenaException;
import SK.gnome.morena.MorenaImage;
import SK.gnome.morena.MorenaSource;

import com.alexalecu.event.DiscardImageEvent;
import com.alexalecu.event.EventBus;
import com.alexalecu.event.ExitApplicationEvent;
import com.alexalecu.event.LoadImageEvent;
import com.alexalecu.event.SaveImageAsEvent;
import com.alexalecu.event.SaveImageEvent;
import com.alexalecu.event.ToggleWizardEvent;
import com.alexalecu.imageCrop.controller.AutoSelectionController;
import com.alexalecu.imageCrop.controller.ImageConfigController;
import com.alexalecu.imageCrop.controller.SelectionController;
import com.alexalecu.imageCrop.gui.ImageCropGUI;
import com.alexalecu.imageUtil.ImageConvert;
import com.alexalecu.util.FileUtil;
import com.google.common.eventbus.Subscribe;

public class ImageCropController {
	private final Logger logger = Logger.getLogger(this.getClass());
	static {
		PropertyConfigurator.configure("props" + File.separator + "logger.properties");
	}

	private final String tempImage = "original.png";
	
	// the stack containing the config for each image subsequent to the initial image
	private Stack<ImageCropConfig> imageConfigStack;

	// the current image in buffer
	private BufferedImage image;

	private ImageCropGUI gui;
	private ImageCropWizard wizard;


	/**
	 * create a new instance, initializing the GUI, the image config and the image list
	 */
	public ImageCropController() {
		logger.debug("Initializing application");
		
		EventBus.register(this);
		
		imageConfigStack = new Stack<ImageCropConfig>();
		imageConfigStack.push(new ImageCropConfig());

		JFrame.setDefaultLookAndFeelDecorated(true);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setSkin(new NebulaSkin());
				gui = new ImageCropGUI();
				
				wizard = new ImageCropWizard(ImageCropController.this, gui);
				new ImageConfigController(ImageCropController.this, gui);
				new SelectionController(ImageCropController.this, gui);
				new AutoSelectionController(ImageCropController.this, gui);
			}
		});
	}
	
	
	/**
	 * @return the current image config object
	 */
	public ImageCropConfig getImageConfig() {
		return imageConfigStack.peek();
	}
	
	/**
	 * push the new image crop configuration onto the stack
	 * @param imageConfig
	 */
	public void pushImageConfig(ImageCropConfig imageConfig) {
		imageConfigStack.push(imageConfig);
	}

	/**
	 * reset the current image config and the initial and current images
	 */
	private void resetCurrentConfig() {
		logger.debug("Resetting current config.");
		
		clearImageConfigStack(); // clear the image list
		imageConfigStack.push(new ImageCropConfig()); // reset the image config
		image = null;
	}
	
	/**
	 * @return the size of the image config stack
	 */
	public int getImageConfigStackSize() {
		return imageConfigStack.size();
	}

	/**
	 * remove all the elements from the image parameter stack
	 */
	private void clearImageConfigStack() {
		imageConfigStack.clear();
	}
	
	/**
	 * @return the current image in buffer
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * set the current image in buffer
	 * @param image
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * set the state of the current image being edited; will ask the GUI to change its state too
	 * @param state the state to be set
	 */
	public void setState(ImageCropState state) {
		ImageCropConfig imageCropConfig = imageConfigStack.peek();
		
		if (imageCropConfig.getState() == state)
			return;
		
		imageCropConfig.setState(state);
		
		// and set the GUI state
		gui.setState(imageCropConfig.getState());
	}
	
	/**
	 * start or stop the wizard mode
	 */
	@Subscribe
	public void toggleWizard(ToggleWizardEvent event) {
		wizard.toggleWizard();
	}

	/**
	 * trigger the next step of the wizard
	 * @param restart force the wizard to be started from the beginning
	 */
	public void triggerWizard(boolean restart) {
		wizard.triggerWizard(restart);
	}
	
	/**
	 * scale down the current image to fit the current image panel view port; do not scale up
	 */
	public void setScaleFactorToFit() {
		if (image == null)
			return;
		
		ImageCropConfig imageCropConfig = imageConfigStack.peek();
		
		// if the image size is smaller or equal to the view port size, set scale factor to 1
		Dimension viewportSize = gui.getImagePanelSize();
		if (viewportSize.width >= image.getWidth() &&
				viewportSize.height >= image.getHeight()) {
			imageCropConfig.setScaleFactor(1d);
			return;
		}
		
		// calculate it as the minimum of the horizontal and vertical scale factors
		double hScaleFactor = Math.floor(viewportSize.width * 100d / image.getWidth());
		double vScaleFactor = Math.floor(viewportSize.height * 100d / image.getHeight());
		int scaleFactor = (int)Math.min(hScaleFactor, vScaleFactor);
		
		imageCropConfig.setScaleFactor(scaleFactor / 100d);
	}


	/**
	 * load a new image and set it as the current image
	 * @param event the LoadImageEvent containing the scan property, which is true
	 * if the new image should come from the scanner, false for image file
	 */
	@Subscribe
	public void selectImage(LoadImageEvent event) {
		// if there is an image being edited, let the use choose to discard it or not
		if (image != null) {
			if (!gui.showConfirmDialog("Are you sure you want to discard current picture ?"))
				return;
		}
		
		File imageFile = null;
		BufferedImage imageNew = null;
		if (event.isScan()) {
			imageNew = scanImage();
		}
		else {
			// ask the user which image file to load
			imageFile = gui.showLoadDialog();
			if (imageFile == null) // the user has not chosen any file
				return;

			// load the image from the file
			imageNew = loadImage(imageFile);
		}
		if (imageNew == null)
			return;
		
		ImageCropConfig previousImageConfig = imageConfigStack.peek();
		
		// clear the stack
		clearImageConfigStack();

		// reset the image config and re-use some of the previous ones
		ImageCropConfig imageCropConfig = new ImageCropConfig();
		imageCropConfig.setImageFile(imageFile);
		imageCropConfig.setBgColor(previousImageConfig.getBgColor());
		imageCropConfig.setBgTolerance(previousImageConfig.getBgTolerance());
		imageCropConfig.setState(ImageCropState.StateImageLoaded);
		imageCropConfig.setSelectMethod(previousImageConfig.getSelectMethod());
		
		// add the current config to the stack
		imageConfigStack.push(imageCropConfig);

		image = ImageConvert.cloneImage(imageNew);
		
		// save the original image on the disk if the file was loaded from the scanner
		try {
			ImageConvert.writePng(image, new FileOutputStream(tempImage));
		}
		catch (Exception e) {
			gui.showErrorDialog("Could not save the temporary file; any changes you make to the" +
					" current image are not reversible");
		}
		
		setScaleFactorToFit();

		logCurrentConfig();

		// and update the GUI image and state
		gui.setState(imageCropConfig.getState());
		gui.setScaleFactor(image, imageCropConfig.getScaleFactor());
		gui.setBgColor(imageCropConfig.getBgColor());
		gui.setBgTolerance(imageCropConfig.getBgTolerance());
		gui.setImageName(imageCropConfig.getImageFile() != null
				? imageCropConfig.getImageFile().getName() : "N/A");
		gui.setImageSize(new Dimension(image.getWidth(), image.getHeight()));

		logger.debug("Image selected.");
		
		wizard.triggerWizard(false); // switch to the next state if the wizard is on
	}
	
	/**
	 * scan an image using the TWAIN source attached
	 * @return the scanned image as a BufferedImage 
	 */
	private BufferedImage scanImage() {
		BufferedImage imageNew = null;
		
		try {
			// initialize the morena source
			MorenaSource source = Morena.selectSource(null);
			logger.debug(String.format("Selected TWAIN source: %1$s%n", source));
			
			if (source != null) {
				// hide the scanner interface
				source.setVisible(false);
				
				// and set the source parameters to get the most out of it
				source.setColorMode();
				source.setBitDepth(24);
				source.setContrast(0);
				source.setResolution(300);
				logger.debug(String.format(
						"resolution: %1$.0$f; bit depth: %2$d; contrast: %3$.2f%n",
						source.getResolution(), source.getBitDepth(), source.getContrast()));
				
				// and scan
				MorenaImage morenaImage = new MorenaImage(source);
				
				// now that we have the scanned image, lets convert it
				if (morenaImage.getWidth() > 0 || morenaImage.getHeight() > 0) {
					Image imageTemp = Toolkit.getDefaultToolkit().createImage(morenaImage);
					imageNew = new BufferedImage(imageTemp.getWidth(null),
							imageTemp.getHeight(null), BufferedImage.TYPE_INT_RGB);
					imageNew.createGraphics().drawImage(imageTemp, 0, 0, null);
				}
				else {
					throw new MorenaException("Could not get a valid Morena image");
				}
			}
			else {
				throw new MorenaException("Could not get a scan source");
			}
			
			Morena.close();
		}
		catch (Throwable e) {
			logger.error("Error while scanning", e);
			return null;
		}
		
		return imageNew;
	}
	
	/**
	 * load an image from a file
	 * @param imageFile the file containing the image
	 * @return the BufferedImage from the specified file
	 */
	private BufferedImage loadImage(File imageFile) {
		if (imageFile == null)
			return null;

		try {
			BufferedImage imageNew = ImageConvert.read(new FileInputStream(imageFile));
			if (imageNew == null)
				throw new Exception("invalid image");

			logger.debug("Image succesfully loaded: " + imageFile.getPath());

			return imageNew;
		}
		catch (Exception ex) {
			// log the error and show an error message to the user
			logger.debug("Invalid / corrupt image file: " + imageFile.getPath(), ex);
			gui.showErrorDialog("Invalid / corrupt image file: " + imageFile.getPath());
			return null;
		}
	}

	/**
	 * save the current image in buffer as JPEG; the file has to have a JPG extension; if the
	 * original image file is overwritten, it will be reset to the new one
	 */
	@Subscribe
	public void saveAs(SaveImageAsEvent event) {
		final String lineSeparator = System.getProperty("line.separator");
		
		File imageFile = gui.showSaveDialog();
		
		// exit if the file is invalid (i.e. no selection has been made)
		if (imageFile == null)
			return;
		
		// exit if the file extension is not JPG or JPEG
		String ext = FileUtil.getExtension(imageFile);
		if (ext == null || (!ext.equals(".jpg") && !ext.equals(".jpeg"))) {
			gui.showErrorDialog("Can save only to jpg files !");
			return;
		}

		boolean discardToFirst = false;

		// if the file exists, ask for user confirmation to overwrite it
		if (imageFile.exists()) {
			boolean flag = true;
			
			// if the file to write to is the original one, ask the user if it is okay to overwrite
			String originalPath = imageConfigStack.peek().getImageFile() != null
					? imageConfigStack.peek().getImageFile().getPath() : null;
			if (originalPath != null && imageFile.getPath().equals(originalPath)) {
				flag = gui.showConfirmDialog(
						"You are trying to overwrite the current editing image file." +
						lineSeparator + "Are you sure you want to continue ?");
				
				if (flag) // the user chose to overwrite the original image
					discardToFirst = true;
			}
			else {
				flag = gui.showConfirmDialog("File already exists." + lineSeparator +
						"Are you sure you want to overwrite it ?");
			}
			
			// if the user did not confirm the overwriting, exit
			if (!flag)
				return;
		}
		
		// and try to save
		if (!save(imageFile))
			return;
		
		imageConfigStack.peek().setImageFile(imageFile);
		gui.setImageName(imageFile.getName());

		// if the original image file has been overwritten, reset the image to the new one
		if (discardToFirst)
			discard(false);
	}

	/**
	 * save the current image in buffer as JPEG, using an unique file name to avoid the overwriting
	 */
	@Subscribe
	public void save(SaveImageEvent event) {
		// use the saveAs() method instead if there is no original image file
		if (imageConfigStack.peek().getImageFile() == null) {
			saveAs(null);
			return;
		}
		
		String dirPath = imageConfigStack.peek().getImageFile().getParent();
		
		// generate a unique file name
		String imageFilename = FileUtil.stripExtension(
				imageConfigStack.peek().getImageFile().getName()) + ".jpg";
		imageFilename = FileUtil.generateUniqueFilename(dirPath, imageFilename, 3);
		
		logger.debug("Generating new unique file name: " + imageFilename);
		
		// and try to save the file
		File file = new File(dirPath, imageFilename);
		if (!save(file))
			return;
		
		imageConfigStack.peek().setImageFile(file);
		gui.setImageName(file.getName());
	}

	/**
	 * save the current image in buffer as JPEG; the extension is not checked
	 * @param imageFile the file to save to; if it exists, this method will overwrite it
	 * @return true if the image has been saved, false if an error has occurred
	 */
	public boolean save(File imageFile) {
		final String lineSeparator = System.getProperty("line.separator");
		try {
			// save the current image as JPEG
			ImageConvert.writeJpg(image, new FileOutputStream(imageFile));
			logger.debug("Image saved as: " + imageFile.getPath());
			
			// and notify the user
			gui.showInfoDialog("Image saved as: " + lineSeparator + imageFile.getPath());
			return true;
		}
		catch (Exception e) {
			logger.debug("An error has occured while saving image to file: " +
					imageFile.getPath(), e);
			
			// notify the user about the error
			gui.showErrorDialog("An error has occured while saving image to file: " + 
					lineSeparator + imageFile.getPath());
			return false;
		}
	}
	
	/**
	 * discard the current image and reinstate the previous one, while maintaining the selection
	 */
	@Subscribe
	public void discard(DiscardImageEvent event) {
		if (gui.showConfirmDialog("Are you sure you want to discard current picture ?"))
			discard(true);
	}

	/**
	 * discard the current image and reinstate the previous one
	 * @param keepSelection true to maintain the selection
	 */
	public void discard(boolean keepSelection) {
		logger.debug("Discarding image: imageConfigStack.size() = " + imageConfigStack.size());
		
		// if we only have one image in stack, reset the application state to init
		if (imageConfigStack.size() == 1) {
			ImageCropConfig previousImageConfig = imageConfigStack.peek();
			
			resetCurrentConfig();
			
			ImageCropConfig imageCropConfig = imageConfigStack.peek();
			
			// remember some of the previous settings
			imageCropConfig.setBgColor(previousImageConfig.getBgColor());
			imageCropConfig.setBgTolerance(previousImageConfig.getBgTolerance());
			imageCropConfig.setSelectMethod(previousImageConfig.getSelectMethod());
			
			// and update the GUI
			gui.setScaleFactor(image, imageCropConfig.getScaleFactor());
			gui.setSelectionRect(imageCropConfig.getSelectionRect(), true);
			gui.setState(imageCropConfig.getState());
			
			return;
		}
		else { // otherwise lets reinstate the previous (original) image
			imageConfigStack.pop();
			ImageCropConfig imageCropConfig = imageConfigStack.peek();

			// load the image from the file; if it cannot be done, discard this parameter set too
			BufferedImage imageNew = loadImage(imageCropConfig.getImageFile() != null
					? imageCropConfig.getImageFile() : new File(tempImage));
			if (imageNew == null) {
				discard(keepSelection);
				return;
			}
			
			// reinstate the image
			image = imageNew;

			if (!keepSelection && (imageCropConfig.getState() == ImageCropState.StateSelectionDone
					|| imageCropConfig.getState() == ImageCropState.StateCrop))
				imageCropConfig.setState(ImageCropState.StateSelection);
			
			gui.setBgColor(imageCropConfig.getBgColor());
			gui.setBgTolerance(imageCropConfig.getBgTolerance());
			gui.setAutoSelectMethod(imageCropConfig.getSelectMethod());
			gui.setImageName(imageCropConfig.getImageFile().getName());
			gui.setBgColor(imageCropConfig.getBgColor());
			gui.setBgTolerance(imageCropConfig.getBgTolerance());
			gui.setAutoSelectMethod(imageCropConfig.getSelectMethod());
			gui.setImageName(imageCropConfig.getImageFile() != null
					? imageCropConfig.getImageFile().getName() : "N/A");
			gui.setImageSize(new Dimension(image.getWidth(), image.getHeight()));
			
			// scale the image in buffer if needed, based on the new scale factor
			gui.setScaleFactor(image, imageCropConfig.getScaleFactor());

			// update the selection panel
			gui.setSelectionRect(imageCropConfig.getSelectionRect(), true);

			// reset the GUI state and update the crop size if necessary
			gui.setState(imageCropConfig.getState());
		}
	}


	/**
	 * print the current config to the log file (the image list size, state, image filename,
	 * image width / height, scale factor)
	 */
	public void logCurrentConfig() {
		ImageCropConfig imageCropConfig = imageConfigStack.peek();
		
		logger.debug("imageConfigStack.size() = " + imageConfigStack.size());
		logger.debug("state = " + imageCropConfig.getState());
		
		logger.debug("imageFile = " + (imageCropConfig.getImageFile() != null ?
				imageCropConfig.getImageFile().getPath() : "null"));
		logger.debug("imageWidth = " + image.getWidth());
		logger.debug("imageHeight = " + image.getHeight());
		
		logger.debug("imageScale = " + imageCropConfig.getScaleFactor());
	}

	/**
	 * exit the application
	 */
	@Subscribe
	public void exitApp(ExitApplicationEvent event) {
		logger.debug("Exiting application." + System.getProperty("line.separator"));

		// dispose the GUI
		if (gui != null)
			gui.dispose();

		// and exit the app
		System.exit(0);
	}


	public static void main(String args[]) {
		new ImageCropController();
	}
}
