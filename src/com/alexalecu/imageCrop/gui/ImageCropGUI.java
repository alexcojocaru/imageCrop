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

package com.alexalecu.imageCrop.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;

import com.alexalecu.dataBinding.JBus;
import com.alexalecu.imageCrop.ImageCropState;
import com.alexalecu.imageCrop.NotificationType;
import com.alexalecu.imageCrop.gui.controlPanel.ActionPanel;
import com.alexalecu.imageCrop.gui.controlPanel.BackgroundPropertiesPanel;
import com.alexalecu.imageCrop.gui.controlPanel.ImagePropertiesPanel;
import com.alexalecu.imageCrop.gui.controlPanel.SelectionControlPanel;
import com.alexalecu.imageCrop.imagePanel.ImagePanel;
import com.alexalecu.imageCrop.imagePanel.SelectionPanel;
import com.alexalecu.imageUtil.AutoSelectStatus;
import com.alexalecu.imageUtil.GeomEdge;
import com.alexalecu.imageUtil.ImageSelectMethod;
import com.alexalecu.imageUtil.ImageFileFilter;
import com.alexalecu.imageUtil.JpgFilter;
import com.alexalecu.util.SwingUtil;

public class ImageCropGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	public enum ControlSet { ControlSetLoad, ControlSetScale, ControlSetPickBackground,
		ControlSetSetBackground, ControlSetAutoSelect, ControlSetAutoSelectOp,
		ControlSetMoveResize, ControlSetCrop, ControlSetRotate, ControlSetSave
	}

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
	
	private JButton wizardButton;

	// the tabbed panel which contains the background, selection and action panels
	private JTabbedPane controlTabbedPanel;	
	private BackgroundPropertiesPanel bgPropsPanel;
	private SelectionControlPanel selectionControlPanel;
	private ActionPanel actionPanel;


	public ImageCropGUI() {
		// initialize the file choosers to accept only the types allowed
		fcLoad.addChoosableFileFilter(new ImageFileFilter());
		fcLoad.setAcceptAllFileFilterUsed(false);
		
		fcSave.addChoosableFileFilter(new JpgFilter());
		fcSave.setAcceptAllFileFilterUsed(false);

		initComponents();
		controlTabbedPanel.setSelectedIndex(0);
		setState(ImageCropState.StateInit);

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
						JBus.getInstance().post(NotificationType.EXIT_APP);
					}
				}
		);

		
		// set the layout of the current panel to a grid bag
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints constraints;
		
		
		// add the scroll panel (along with its children) to the main panel
		initLeftSideComponents();
		constraints = SwingUtil.getGridBagConstraint(0, 0, GridBagConstraints.NORTHWEST,
				new Insets(5, 5, 5, 5));
		constraints.weightx = 0.9d;
		constraints.weighty = 1.0d;
		constraints.fill = GridBagConstraints.BOTH;
		getContentPane().add(imageScrollPanel, constraints);

		
		// finally, lets add the control panel to the main panel
		JPanel panelControl = createRightSideComponents();
		constraints = SwingUtil.getGridBagConstraint(1, 0, GridBagConstraints.NORTHEAST,
				new Insets(5, 5, 5, 5));
		constraints.weightx = 0.1d;
		constraints.weighty = 1.0d;
		getContentPane().add(panelControl, constraints);
	}

	/**
	 * initialize the panel on the left hand side (the image panel) along with its components
	 */
	private void initLeftSideComponents() {
		// create the overlay panel which contains the image and the selection panels
		imageOverlayPanel = new JPanel();
		imageOverlayPanel.setLayout(new OverlayLayout(imageOverlayPanel));

		// and add those two panels to it
		imagePanel = new ImagePanel(0, 0);
		selectionPanel = new SelectionPanel(0, 0);
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
	}

	/**
	 * initialize the panel on the right hand side (the control panel) along with its components
	 * @return the newly created panel
	 */
	private JPanel createRightSideComponents() {
		// initialize the right hand panel and set its layout to be a grid bag
		JPanel panelControl = new JPanel(new GridBagLayout());

		// create the tabbed control panel and add components to it
		controlTabbedPanel = new JTabbedPane();
		
		bgPropsPanel = new BackgroundPropertiesPanel();
		selectionControlPanel = new SelectionControlPanel();
		actionPanel = new ActionPanel();
		
		// and add the control inner panels to it
		controlTabbedPanel.addTab("Background", null, bgPropsPanel, "The background properties");
		controlTabbedPanel.addTab("Selection", null, selectionControlPanel, "Selection actions");
		controlTabbedPanel.addTab("Save", null, actionPanel, "Save selection");

		GridBagConstraints constraints = SwingUtil.getGridBagConstraint(
				0, 0, GridBagConstraints.NORTH, new Insets(5, 5, 5, 5));
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panelControl.add(controlTabbedPanel, constraints);


		// add the image properties panel to the control panel
		JPanel panelImageProps = createImagePropsPanel();
		panelControl.add(panelImageProps, SwingUtil.getGridBagConstraint(
				0, 1, GridBagConstraints.NORTH, new Insets(5, 5, 5, 5)));

		
		// add the wizard button
		wizardButton = new JButton();
		setWizardButtonText("Start wizard");
		wizardButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JBus.getInstance().post(NotificationType.TOGGLE_WIZARD_ACTION);
					}
				}
		);
		constraints = SwingUtil.getGridBagConstraint(
				0, 2, GridBagConstraints.CENTER, new Insets(15, 5, 5, 5));
		constraints.ipadx = 10;
		constraints.ipady = 5;
		panelControl.add(wizardButton, constraints);

		return panelControl;
	}
	
	/**
	 * create the panel containing the image property labels and the image load button
	 * @return the newly created panel
	 */
	private JPanel createImagePropsPanel() {
		// initialize the image properties panel and the image load button
		imagePropsPanel = new ImagePropertiesPanel();
		
		loadImageButton = new JButton("Load image");
		loadImageButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JBus.getInstance().post(NotificationType.LOAD_IMAGE_ACTION);
					}
				}
		);

		// create the panel containing the image properties, the 'load' and 'wizard' buttons
		JPanel panelLoadImage = new JPanel(new GridBagLayout());

		panelLoadImage.add(imagePropsPanel, SwingUtil.getGridBagConstraint(
				0, 0, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));
		panelLoadImage.add(loadImageButton, SwingUtil.getGridBagConstraint(
				0, 1, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));
		
		return panelLoadImage;
	}


	/**
	 * set the state of the components (some labels & all controls) based on the value passed as
	 * parameter
	 * @param state the current state of the image
	 */
	public void setState(ImageCropState state) {
		switch (state) {
			case StateInit:
				setControlSetEnabled(ControlSet.ControlSetLoad, true);
				setControlSetEnabled(ControlSet.ControlSetScale, false);
				setControlSetEnabled(ControlSet.ControlSetPickBackground, false);
				setControlSetEnabled(ControlSet.ControlSetSetBackground, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, false);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, false);
				setControlSetEnabled(ControlSet.ControlSetCrop, false);
				setControlSetEnabled(ControlSet.ControlSetRotate, false);
				setControlSetEnabled(ControlSet.ControlSetSave, false);
				
				imagePropsPanel.resetValues();
				controlTabbedPanel.setSelectedIndex(0);
	
				break;
				
			case StateImageLoaded:
			case StateBackgroundColor:
				setControlSetEnabled(ControlSet.ControlSetLoad, true);
				setControlSetEnabled(ControlSet.ControlSetScale, true);
				setControlSetEnabled(ControlSet.ControlSetPickBackground, true);
				setControlSetEnabled(ControlSet.ControlSetSetBackground, true);
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, false);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, false);
				setControlSetEnabled(ControlSet.ControlSetCrop, false);
				setControlSetEnabled(ControlSet.ControlSetRotate, true);
				setControlSetEnabled(ControlSet.ControlSetSave, true);
	
				// reset the crop size label
				imagePropsPanel.resetCropSize();
				controlTabbedPanel.setSelectedIndex(0);

				bgPropsPanel.enableSelectBackgroundMode(false); // change the select bg button name
				selectionPanel.setVisible(true);
				imagePanel.toggleCursor(false);

				break;
	
			case StateSelectingBackgroundColor:
				setControlSetEnabled(ControlSet.ControlSetLoad, false);
				setControlSetEnabled(ControlSet.ControlSetScale, false);
				setControlSetEnabled(ControlSet.ControlSetPickBackground, true);
				setControlSetEnabled(ControlSet.ControlSetPickBackground, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, false);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, false);
				setControlSetEnabled(ControlSet.ControlSetCrop, false);
				setControlSetEnabled(ControlSet.ControlSetRotate, false);
				setControlSetEnabled(ControlSet.ControlSetSave, false);

				bgPropsPanel.enableSelectBackgroundMode(true); // change the select bg button name
				selectionPanel.setVisible(false);
				imagePanel.toggleCursor(true);
				
				break;
	
			case StateSelection:
				setControlSetEnabled(ControlSet.ControlSetLoad, true);
				setControlSetEnabled(ControlSet.ControlSetScale, true);
				setControlSetEnabled(ControlSet.ControlSetPickBackground, true);
				setControlSetEnabled(ControlSet.ControlSetSetBackground, true);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, false);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, false);
				setControlSetEnabled(ControlSet.ControlSetCrop, false);
				setControlSetEnabled(ControlSet.ControlSetRotate, true);
				setControlSetEnabled(ControlSet.ControlSetSave, true);
				
				controlTabbedPanel.setSelectedIndex(1);
	
				break;
			
			case StateAutoSelecting:
				setControlSetEnabled(ControlSet.ControlSetLoad, false);
				setControlSetEnabled(ControlSet.ControlSetScale, false);
				setControlSetEnabled(ControlSet.ControlSetPickBackground, false);
				setControlSetEnabled(ControlSet.ControlSetSetBackground, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, true);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, false);
				setControlSetEnabled(ControlSet.ControlSetCrop, false);
				setControlSetEnabled(ControlSet.ControlSetRotate, false);
				setControlSetEnabled(ControlSet.ControlSetSave, false);
	
				break;
			
			case StateSelectionAutoSelected:
			case StateSelectionDone:
				setControlSetEnabled(ControlSet.ControlSetLoad, true);
				setControlSetEnabled(ControlSet.ControlSetScale, true);
				setControlSetEnabled(ControlSet.ControlSetPickBackground, true);
				setControlSetEnabled(ControlSet.ControlSetSetBackground, true);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, true);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, true);
				setControlSetEnabled(ControlSet.ControlSetCrop, true);
				setControlSetEnabled(ControlSet.ControlSetRotate, true);
				setControlSetEnabled(ControlSet.ControlSetSave, true);
				
				controlTabbedPanel.setSelectedIndex(1);

				bgPropsPanel.enableSelectBackgroundMode(false); // change the select bg button name
				selectionPanel.setVisible(true);
				imagePanel.toggleCursor(false);
	
				break;
			
			case StateCrop:
				setControlSetEnabled(ControlSet.ControlSetLoad, true);
				setControlSetEnabled(ControlSet.ControlSetScale, true);
				setControlSetEnabled(ControlSet.ControlSetPickBackground, true);
				setControlSetEnabled(ControlSet.ControlSetSetBackground, true);
				setControlSetEnabled(ControlSet.ControlSetAutoSelectOp, false);
				setControlSetEnabled(ControlSet.ControlSetAutoSelect, true);
				setControlSetEnabled(ControlSet.ControlSetMoveResize, true);
				setControlSetEnabled(ControlSet.ControlSetCrop, true);
				setControlSetEnabled(ControlSet.ControlSetRotate, true);
				setControlSetEnabled(ControlSet.ControlSetSave, true);
				
				controlTabbedPanel.setSelectedIndex(2);
	
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
		}
		
		// the following two panels have their own enabling logic, so lets let them do their job
		bgPropsPanel.setEnabled(controlSet, enabled);
		selectionControlPanel.setEnabled(controlSet, enabled);
		actionPanel.setEnabled(controlSet, enabled);
	}


	/**
	 * @return the size of the image panel view port
	 */
	public Dimension getImagePanelSize() {
		return imageScrollPanel.getViewportBorderBounds().getSize();
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

		imagePropsPanel.setCropSize(selection);
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
	 * force a repaint to the image panel to make sure that the image gets updated
	 */
	private void repaintImagePanel() {
		imageOverlayOuterPanel.revalidate();
		imageOverlayOuterPanel.repaint();

		// and now the scroll container
		imageScrollPanel.revalidate();
		imageScrollPanel.repaint();
	}

	
	/**
	 * set the background color on the inner components
	 * @param color the color to be set
	 */
	public void setBgColor(Color color) {
		bgPropsPanel.setBackgroundColor(color);
	}

	/**
	 * set the background tolerance on the inner components
	 * @param bgTolerance the background color tolerance to be set
	 */
	public void setBgTolerance(int bgTolerance) {
		bgPropsPanel.setBackgroundTolerance(bgTolerance);
	}

	/**
	 * set the new select method in the corresponding panel
	 * @param selectMethod the select method to be set
	 */
	public void setAutoSelectMethod(ImageSelectMethod selectMethod) {
		selectionControlPanel.setAutoSelectMethod(selectMethod);
	}
	
	/**
	 * set the auto-select task status on the corresponding component
	 * @param status
	 */
	public void setAutoSelectStatus(AutoSelectStatus status) {
		selectionControlPanel.setAutoSelectStatus(status);
	}
	

	/**
	 * update the image name label value based on the new image file name
	 * @param file the new image file name
	 */
	public void setImageName(String fileName) {
		imagePropsPanel.setImageName(fileName);
	}

	/**
	 * update the image size label value based on the new image size
	 * @param imageSize the new image size
	 */
	public void setImageSize(Dimension imageSize) {
		imagePropsPanel.setImageSize(imageSize);
	}

	/**
	 * set the wizard button name
	 * @param name the name to display on the wizard button
	 */
	public void setWizardButtonText(String text) {
		wizardButton.setText("<html><b><font color=red>" + text + "</font></b></html>");
	}

	
	/**
	 * show the load file dialog, allowing the user to choose a file to load
	 * @return the file object chosen
	 */
	public File showLoadDialog() {
		int returnVal = fcLoad.showOpenDialog(this);
		return returnVal == JFileChooser.APPROVE_OPTION ? fcLoad.getSelectedFile() : null;
	}
	
	/**
	 * show the save file dialog, allowing the user to choose a filename to save as
	 */
	public File showSaveDialog() {
		// use the same directory as the file loader dialog
		if (fcSave.getSelectedFile() == null)
			fcSave.setSelectedFile(fcLoad.getSelectedFile());

		// and show the file save dialog
		int returnVal = fcSave.showSaveDialog(this);
		return returnVal == JFileChooser.APPROVE_OPTION ? fcSave.getSelectedFile() : null;
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
