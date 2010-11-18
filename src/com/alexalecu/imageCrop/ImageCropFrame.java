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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

import com.alexalecu.imageCrop.ImageParams.ImageState;
import com.alexalecu.imageCrop.controlPanel.ActionPanel;
import com.alexalecu.imageCrop.controlPanel.CropPropertiesPanel;
import com.alexalecu.imageCrop.controlPanel.ImagePropertiesPanel;
import com.alexalecu.imageCrop.controlPanel.SelectionControlPanel;
import com.alexalecu.imageCrop.imagePanel.ImagePanel;
import com.alexalecu.imageCrop.imagePanel.SelectionPanel;
import com.alexalecu.imageCrop.imagePanel.SelectionPanel.ResizeDirection;
import com.alexalecu.imageUtil.AutoSelectStatus;
import com.alexalecu.imageUtil.GeomEdge;
import com.alexalecu.imageUtil.ImageCropMethod;
import com.alexalecu.imageUtil.ImageFileFilter;
import com.alexalecu.imageUtil.JpgFilter;
import com.alexalecu.util.SwingUtil;

@SuppressWarnings("serial")
public class ImageCropFrame extends JFrame implements ImageCropGUI {

	private ImageCropEngine controller;

	private final JFileChooser fcLoad = new JFileChooser();
	private final JFileChooser fcSave = new JFileChooser();

	private JScrollPane imageScrollPanel;
	private JPanel imageOverlayOuterPanel;
	private JPanel imageOverlayPanel;
	private ImagePanel imagePanel;
	private SelectionPanel selectionPanel;

	// the panel containing the labels showing the current image properties and the scale spinner
	private ImagePropertiesPanel imagePropsPanel;
	private JButton loadImageButton;

	// the tabbed panel which contains the crop, selection and action panels
	private JTabbedPane controlTabbedPanel;	
	private CropPropertiesPanel cropPropsPanel;
	private SelectionControlPanel selectionControlPanel;
	private ActionPanel actionPanel;


	public ImageCropFrame(ImageCropEngine controller) {
		this.controller = controller;

		// initialize the file choosers to accept only the types allowed
		fcLoad.addChoosableFileFilter(new ImageFileFilter());
		fcLoad.setAcceptAllFileFilterUsed(false);
		
		fcSave.addChoosableFileFilter(new JpgFilter());
		fcSave.setAcceptAllFileFilterUsed(false);

		initComponents();
		controlTabbedPanel.setSelectedIndex(0);
		setState(ImageState.StateInit);

		// the close action should be handled by this class
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// set up the main window, center it on the screen and show it
		setTitle(" Crop images");
		pack();
		setLocationRelativeTo(null); // center the frame on the screen
		setVisible(true);
	}

	/**
	 * initialize the panel components and add them to the main layout / panel
	 */
	private void initComponents() {
		
		this.addWindowListener(
				new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						controller.exitApp();
					}
				}
		);

		// set the layout of the current panel to a grid bag
		getContentPane().setLayout(new GridBagLayout());
		
		// and add the components to it
		initLeftSideComponents();
		initRightSideComponents();
	}

	/**
	 * initialize the panel on the left hand side (the image panel) along with its components
	 */
	private void initLeftSideComponents() {		
		// create the overlay panel which contains the image and the selection panels
		imageOverlayPanel = new JPanel();
		imageOverlayPanel.setLayout(new OverlayLayout(imageOverlayPanel));

		// and add those two panels to it
		imagePanel = new ImagePanel(this, 0, 0);
		selectionPanel = new SelectionPanel(this, 0, 0);
		imageOverlayPanel.add(selectionPanel);
		imageOverlayPanel.add(imagePanel);
		SwingUtil.setAllSizes(imageOverlayPanel, 0, 0);

		// create a container for the overlay panel, just to be able to add it to a scroll panel
		imageOverlayOuterPanel = new JPanel(new GridBagLayout());
		imageOverlayOuterPanel.add(imageOverlayPanel, SwingUtil.getGridBagConstraint(
				0, 0, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));

		// and put it in a scroll panel
		imageScrollPanel = new JScrollPane(imageOverlayOuterPanel);
		imageScrollPanel.setMinimumSize(new Dimension(600, 500));
		imageScrollPanel.setPreferredSize(new Dimension(600, 500));
		//SwingUtil.setAllSize(imageScrollPanel, 600, 500);

		// finally, add the scroll panel (along with its children) to the main panel
		GridBagConstraints constraints = SwingUtil.getGridBagConstraint(
				0, 0, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
		constraints.weightx = 0.9d;
		constraints.weighty = 1.0d;
		constraints.fill = GridBagConstraints.BOTH;
		getContentPane().add(imageScrollPanel, constraints);
	}


	/**
	 * initialize the panel on the right hand side (the control panel) along with its components
	 */
	private void initRightSideComponents() {
		// initialize the right hand panel and set its layout to be a grid bag
		JPanel panelControl = new JPanel(new GridBagLayout());

		// create the tabbed control panel and add components to it
		controlTabbedPanel = new JTabbedPane();
		
		cropPropsPanel = new CropPropertiesPanel(this);
		selectionControlPanel = new SelectionControlPanel(this);
		actionPanel = new ActionPanel(this);
		
		// and add the control inner panels to it
		controlTabbedPanel.addTab("Auto crop", null, cropPropsPanel, "Select the crop parameters and method");
		controlTabbedPanel.addTab("Selection", null, selectionControlPanel, "Selection actions");
		controlTabbedPanel.addTab("Save", null, actionPanel, "Save selection");

		GridBagConstraints constraints = SwingUtil.getGridBagConstraint(
				0, 0, GridBagConstraints.NORTH, new Insets(5, 5, 5, 5));
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panelControl.add(controlTabbedPanel, constraints);

		
		// initialize the image properties panel and the image load button
		imagePropsPanel = new ImagePropertiesPanel(this);
		
		loadImageButton = new JButton();
		loadImageButton.setText("Load image");
		loadImageButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						loadImage();
					}
				}
		);

		// create the panel containing the image properties and the 'load' buttons
		JPanel panelLoadImage = new JPanel(new GridBagLayout());

		panelLoadImage.add(imagePropsPanel, SwingUtil.getGridBagConstraint(
				0, 0, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));
		panelLoadImage.add(loadImageButton, SwingUtil.getGridBagConstraint(
				0, 1, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));

		// and add it to the control panel
		panelControl.add(panelLoadImage, SwingUtil.getGridBagConstraint(
				0, 1, GridBagConstraints.NORTH, new Insets(5, 5, 5, 5)));


		// finally, lets add the control panel to the main panel
		constraints = SwingUtil.getGridBagConstraint(1, 0, GridBagConstraints.NORTHEAST,
				new Insets(5, 5, 5, 5));
		constraints.weightx = 0.1d;
		constraints.weighty = 1.0d;
		getContentPane().add(panelControl, constraints);
	}


	/**
	 * set the state of the components (some labels & all controls) based on the value passed as
	 * parameter
	 * @param imageState the current state of the image
	 */
	public void setState(ImageParams.ImageState imageState) {
		// toggle the UI functionality; if simple mode, some components are never enabled
		boolean simpleMode = false;
		
		switch (imageState) {
			case StateInit:
				setControlSetEnabled(ControlSet.ControlSetLoad, true);
				setControlSetEnabled(ControlSet.ControlSetScale, false);
				setControlSetEnabled(ControlSet.ControlSetBackground, false);
				setControlSetEnabled(ControlSet.ControlSetAutoCrop, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, false);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, false);
				setControlSetEnabled(ControlSet.ControlSetCrop, false);
				setControlSetEnabled(ControlSet.ControlSetRotate, false);
				setControlSetEnabled(ControlSet.ControlSetSave, false);
				
				imagePropsPanel.resetValues();
	
				break;
	
			case StateImageLoaded:
				if (!simpleMode) {
					setControlSetEnabled(ControlSet.ControlSetLoad, true);
					setControlSetEnabled(ControlSet.ControlSetScale, true);
					setControlSetEnabled(ControlSet.ControlSetBackground, true);
					setControlSetEnabled(ControlSet.ControlSetAutoCrop, true);
				}
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, false);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, false);
				if (!simpleMode) {
					setControlSetEnabled(ControlSet.ControlSetCrop, false);
					setControlSetEnabled(ControlSet.ControlSetRotate, true);
					setControlSetEnabled(ControlSet.ControlSetSave, true);
				}
	
				// set the value of the image name and size labels, and reset the crop size label
				if (!simpleMode) {
					imagePropsPanel.setImageName(controller.getImageName());
					imagePropsPanel.setImageSize(controller.getImageSize());
				}
				imagePropsPanel.resetCropSize();

				break;
	
			case StateSelectBackgroundColor:
				setControlSetEnabled(ControlSet.ControlSetLoad, false);
				setControlSetEnabled(ControlSet.ControlSetScale, false);
				setControlSetEnabled(ControlSet.ControlSetAutoCrop, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, false);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, false);
				setControlSetEnabled(ControlSet.ControlSetCrop, false);
				setControlSetEnabled(ControlSet.ControlSetRotate, false);
				setControlSetEnabled(ControlSet.ControlSetSave, false);
				break;
	
			case StateSelection:
				setControlSetEnabled(ControlSet.ControlSetLoad, true);
				setControlSetEnabled(ControlSet.ControlSetScale, true);
				setControlSetEnabled(ControlSet.ControlSetBackground, true);
				setControlSetEnabled(ControlSet.ControlSetAutoCrop, true);
				if (!simpleMode) {
					setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, false);
					setControlSetEnabled(ControlSet.ControlSetAutoSelect, true);
					setControlSetEnabled(ControlSet.ControlSetMoveResize, true);
				}
				setControlSetEnabled(ControlSet.ControlSetCrop, true);
				setControlSetEnabled(ControlSet.ControlSetRotate, true);
				setControlSetEnabled(ControlSet.ControlSetSave, true);
				
				// set the value of the image name and size labels, and reset the crop size label
				if (!simpleMode) {
					imagePropsPanel.setImageName(controller.getImageName());
					imagePropsPanel.setImageSize(controller.getImageSize());
				}
	
				break;
			
			case StateAutoSelecting:
				setControlSetEnabled(ControlSet.ControlSetLoad, false);
				setControlSetEnabled(ControlSet.ControlSetScale, false);
				setControlSetEnabled(ControlSet.ControlSetBackground, false);
				setControlSetEnabled(ControlSet.ControlSetAutoCrop, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, true);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, false);
				setControlSetEnabled(ControlSet.ControlSetCrop, false);
				setControlSetEnabled(ControlSet.ControlSetRotate, false);
				setControlSetEnabled(ControlSet.ControlSetSave, false);
	
				break;
		}
	}

	/**
	 * enable or disable all the controls belonging to the corresponding set
	 * @param controlSet the set of controls to be enabled or disabled
	 * @param enabled true to enable, false otherwise
	 */
	private void setControlSetEnabled(ControlSet controlSet, boolean enabled) {
		switch (controlSet)
		{
			case ControlSetLoad:
				loadImageButton.setEnabled(enabled);
				break;
			case ControlSetScale:
				imagePropsPanel.setEnabled(enabled);
				break;
			case ControlSetMoveResize:
				selectionControlPanel.setEnabled(enabled);
				break;
		}
		
		// the following two panels have their own enabling logic, so lets let them do their job
		cropPropsPanel.setEnabled(controlSet, enabled);
		actionPanel.setEnabled(controlSet, enabled);
	}

	/**
	 * enter / exit the select background color mode
	 */
	public void toggleSelectBackgroundMode() {
		boolean isSelectBgMode = !selectionPanel.isVisible();

		// toggle the state
		isSelectBgMode = !isSelectBgMode;

		// tell the crop properties panel to change accordingly
		cropPropsPanel.enableSelectBackgroundMode(isSelectBgMode);

		selectionPanel.setVisible(!isSelectBgMode);
		imagePanel.toggleCursor(isSelectBgMode);

		ImageParams.ImageState state;
		if (isSelectBgMode)
			state = ImageParams.ImageState.StateSelectBackgroundColor;
		else if (selectionPanel.getRect() != null)
			state = ImageParams.ImageState.StateSelection;
		else
			state = ImageParams.ImageState.StateImageLoaded;
		
		// the controller will call back to let me know what's the new state
		controller.setState(state);
	}

	
	/**
	 * Get notified about changes to the background color
	 * @param color the color to be set
	 */
	public void bgColorChanged(Color color) {
		bgColorChanged(color, true);
	}
	
	/**
	 * Get notified about changes to the background color
	 * @param color the color to be set
	 * @param updateCropPanel true to update the color value in the crop panel
	 */
	public void bgColorChanged(Color color, boolean updateCropPanel) {
		if (updateCropPanel)
			cropPropsPanel.setBackgroundColor(color);
		
		controller.bgColorChanged(color);
		
		// exit the bg selection mode if the selection was made using the color picker
		if (updateCropPanel)
			toggleSelectBackgroundMode();
	}
	
	/**
	 * set the given background color to set on the spinner controls
	 * @param red the red value to set
	 * @param green the green value to set
	 * @param blue the blue value to set
	 */
	public void setBackgroundColor(Color color) {
		cropPropsPanel.setBackgroundColor(color);
	}
	
	/**
	 * Get notified about changes to the background tolerance
	 * @param bgTolerance the background color tolerance to be set
	 */
	public void bgToleranceChanged(int bgTolerance) {
		controller.bgToleranceChanged(bgTolerance);
	}

	/**
	 * set the given background tolerance on the spinner control
	 * @param tolerance the tolerance to set
	 */
	public void setBackgroundTolerance(int tolerance) {
		cropPropsPanel.setBackgroundTolerance(tolerance);
	}
	
	/**
	 * Get notified about changes to the auto crop method
	 * @param cropMethod the crop method to be set
	 */
	public void autoCropMethodChanged(ImageCropMethod cropMethod) {
		controller.autoCropMethodChanged(cropMethod);
	}


	/**
	 * callback method to allow the component to be notified of selection changes
	 * @param rectangle the selection rectangle; it is null if there is no selection
	 */
	public void selectionChanged(Rectangle rectangle) {
		controller.selectionChanged(rectangle);
	}

	/**
	 * Get notified about changes to the scale factor
	 * @param scaleFactor the new scale factor
	 */
	public void scaleFactorChanged(int scaleFactor) {
		controller.scaleFactorChanged(scaleFactor / 100d);
	}

	
	/**
	 * set the background color on the inner components
	 * @param color the color to be set
	 */
	public void setBgColor(Color color) {
		cropPropsPanel.setBackgroundColor(color);
	}

	/**
	 * set the background tolerance on the inner components
	 * @param bgTolerance the background color tolerance to be set
	 */
	public void setBgTolerance(int bgTolerance) {
		cropPropsPanel.setBackgroundTolerance(bgTolerance);
	}

	/**
	 * set the new crop method in the corresponding panel
	 * @param cropMethod the crop method to be set
	 */
	public void setAutoCropMethod(ImageCropMethod cropMethod) {
		cropPropsPanel.setAutoCropMethod(cropMethod);
	}

	/**
	 * apply the scale factor to the selection and image panels
	 * @param image the current BufferedImage
	 * @param scaleFactor the scale factor to be applied
	 */
	public void setScaleFactor(BufferedImage image, double scaleFactor) {
		selectionPanel.reset(false);
		selectionPanel.scale(scaleFactor, false); // don't repaint, as the next method will do that

		imagePanel.setImage(image, scaleFactor, false);
		selectionPanel.setDimension(imagePanel.getWidth(), imagePanel.getHeight());

		// nail the image and selection panels sizes
		SwingUtil.setAllSizes(imageOverlayPanel, imagePanel.getWidth(), imagePanel.getHeight());

		repaintImagePanel();
		
		imagePropsPanel.setScaleFactor((int)(scaleFactor * 100));
	}


	/**
	 * moves the image selection to the direction given by x and y
	 * @param x the x direction to move the selection to; one of -1 for left, 0 or 1 for right
	 * @param y the y direction to move the selection to; one of -1 for left, 0 or 1 for right
	 */
	public void moveSelection(int x, int y, int step) {
		if (selectionPanel.getRect() == null || step <= 0)
			return;

		// and tell the selection panel component to move the selection
		selectionPanel.moveRectBy(x * step, y * step);
	}

	/**
	 * resize the selection rectangle
	 * @param top one of -1 for up, 0 or 1 for down to apply to the top edge of the selection
	 * @param left one of -1 for left, 0 or 1 for right to apply to the left edge of the selection
	 * @param bottom one of -1 for up, 0 or 1 for down to apply to the bottom edge of the selection
	 * @param right one of -1 for left, 0 or 1 for right to apply to the right edge of the selection
	 */
	public void resizeSelection(int top, int left, int bottom, int right, int step) {
		if (selectionPanel.getRect() == null)
			return;

		if (step <= 0)
			return;

		// and resize the selection by moving one of its edges in the right direction
		if (top != 0)
			selectionPanel.resizeRectBy(step, ResizeDirection.NORTH,
					top == -1 ? ResizeDirection.SOUTH : ResizeDirection.NORTH);
		else if (bottom != 0)
			selectionPanel.resizeRectBy(step, ResizeDirection.SOUTH,
					bottom == -1 ? ResizeDirection.NORTH : ResizeDirection.SOUTH);
		else if (left != 0)
			selectionPanel.resizeRectBy(step, ResizeDirection.WEST,
					left == -1 ? ResizeDirection.EAST : ResizeDirection.WEST);
		else if (right != 0)
			selectionPanel.resizeRectBy(step, ResizeDirection.EAST,
					right == -1 ? ResizeDirection.WEST : ResizeDirection.EAST);
	}
	
	/**
	 * crop the image selection
	 */
	public void cropSelection() {
		controller.crop();
	}
	
	/**
	 * rotate the current image in buffer
	 */
	public void rotateSelection() {
		String degStr = showInputDialog("Degrees to rotate the image anticlockwise (ex: 5.7): ");
		if (degStr == null || degStr.trim().length() == 0)
			return;
		try {
			double deg = Double.parseDouble(degStr);
			controller.rotate(deg);
		}
		catch (NumberFormatException ex) {
			showErrorDialog("Invalid numeric value !");
		}
	}
	
	/**
	 * discard the image in buffer, only if it is okay with the user
	 */
	public void discardImage() {
		if (showConfirmDialog("Are you sure you want to discard current picture ?"))
			controller.discard();
	}
	
	/**
	 * save the current image in buffer, using an unique file name to avoid the overwriting
	 */
	public void saveImage() {
		controller.save();
	}
	
	/**
	 * save the current image in buffer, allowing to choose the filename
	 */
	public void saveAsImage() {
		// use the same directory as the file loader dialog
		if (fcSave.getSelectedFile() == null)
			fcSave.setSelectedFile(fcLoad.getSelectedFile());

		// and save the file
		int returnVal = fcSave.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			controller.saveAs(fcSave.getSelectedFile());
	}
	
	/**
	 * display the image loading dialog to choose an image file to load
	 */
	private void loadImage() {
		// if there is an image being edited, let the use choose to discard it or not
		if (controller.isImageInBuffer()) {
			if (!showConfirmDialog("Are you sure you want to discard current picture ?"))
				return;
		}

		int returnVal = fcLoad.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			controller.selectImage(fcLoad.getSelectedFile());
	}
	
	/**
	 * set the selection rectangle on the SelectionPanel and update the crop size too
	 * @param selection the selection rectangle
	 * @param repaint true to repaint the selection panel
	 */
	public void setSelectionRect(Rectangle selection, boolean repaint) {
		selectionPanel.setRect(selection);
		if (repaint)
			selectionPanel.repaintComp();

		if (selection == null)
			imagePropsPanel.setCropSize(new Dimension(0, 0));
		else
			imagePropsPanel.setCropSize(new Dimension(selection.width, selection.height));
	}
	
	/**
	 * set the edge list on the SelectionPanel
	 * @param edgeList
	 * @param repaint true to repaint the selection panel
	 */
	public void setSelectionEdgeList(ArrayList<GeomEdge> edgeList, boolean repaint) {
		selectionPanel.setEdgeList(edgeList);
		if (repaint)
			selectionPanel.repaintComp();
	}
	
	/**
	 * set the auto-select task status on the corresponding component
	 * @param status
	 */
	public void setAutoSelectStatus(AutoSelectStatus status) {
		cropPropsPanel.setAutoSelectStatus(status);
	}

	/**
	 * reset the state of the panel
	 * @param repaint true if a repaint should be performed after reset
	 */
	public void resetSelectionPanel(boolean repaint) {
		selectionPanel.reset(repaint);
	}
	

	/**
	 * @return the size of the image panel view port
	 */
	public Dimension getImagePanelSize() {
		return imageScrollPanel.getViewportBorderBounds().getSize();
	}


	/**
	 * force a repaint to the image panel to make sure that the image gets updated
	 */
	public void repaintImagePanel() {
		imageOverlayOuterPanel.revalidate();
		imageOverlayOuterPanel.repaint();

		// and now the scroll container
		imageScrollPanel.revalidate();
		imageScrollPanel.repaint();
	}

	/**
	 * update the image size label value based on the new image size
	 * @param imageSize the new image size
	 */
	public void setImageSize(Dimension imageSize) {
		imagePropsPanel.setImageSize(imageSize);
	}


	/**
	 * request to auto select the picture marked by the selection rectangle
	 */
	public void autoSelectPicture() {
		controller.autoSelect();
	}


	/**
	 * show an error message
	 * @param msg the message to be displayed
	 */
	public void showErrorDialog(String msg) {
		JOptionPane.showMessageDialog(this,msg," Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * show an information message
	 * @param msg the message to be displayed
	 */
	public void showInfoDialog(String msg) {
		JOptionPane.showMessageDialog(this,msg," Message", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * show a confirmation message
	 * @param msg the message to be displayed
	 * @return true if the user accepted, false if not
	 */
	public boolean showConfirmDialog(String msg) {
		return JOptionPane.showConfirmDialog(this, msg, " Question", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION;
	}

	/**
	 * show an input message
	 * @param msg the label to display
	 * @return the string entered by the user
	 */
	public String showInputDialog(String msg) {
		return JOptionPane.showInputDialog(this, msg, " Enter value", JOptionPane.QUESTION_MESSAGE);
	}
}
