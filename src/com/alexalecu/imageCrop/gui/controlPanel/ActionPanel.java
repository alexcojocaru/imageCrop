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

package com.alexalecu.imageCrop.gui.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.alexalecu.component.NotificationButton;
import com.alexalecu.event.CropSelectionEvent;
import com.alexalecu.event.DiscardImageEvent;
import com.alexalecu.event.RotateSelectionEvent;
import com.alexalecu.event.SaveImageAsEvent;
import com.alexalecu.event.SaveImageEvent;
import com.alexalecu.imageCrop.gui.ImageCropGUI.ControlSet;
import com.alexalecu.util.SwingUtil;

public class ActionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JButton buttonCrop;
	private JButton buttonRotate;
	private JButton buttonDiscard;
	private JButton buttonSaveAs;
	private JButton buttonSave;

	public ActionPanel() {
		super();
		
		initComponents();
	}
	
	/**
	 * initialize the components and add them to the current panel
	 */
	private void initComponents() {
		// create the 'crop picture' button
		buttonCrop = new NotificationButton.Builder()
				.text("Crop selection")
				.tooltip("Crop the current selection")
				.event(new CropSelectionEvent())
				.build();
		
		// create the 'rotate selection' button
		buttonRotate = new NotificationButton.Builder()
				.text("Rotate image")
				.tooltip("Rotate the image")
				.event(new RotateSelectionEvent())
				.build();
		
		// create the 'discard current image' button
		buttonDiscard = new NotificationButton.Builder()
				.text("Discard image")
				.tooltip("Discard the image and go back to the previous one, if any")
				.event(new DiscardImageEvent())
				.build();
		
		// create the 'save as' button which saves the current image in buffer, allowing the
		// user to choose the file name
		buttonSaveAs = new NotificationButton.Builder()
				.text("Save image as...")
				.tooltip("Save the image, allowing to choose the file name and location")
				.event(new SaveImageAsEvent())
				.build();

		// create the 'save' button which saves the current image in buffer
		buttonSave = new NotificationButton.Builder()
				.text("Save image")
				.tooltip("Save the image in the same directory, but using an unique name" +
						" to avoid overwriting")
				.event(new SaveImageEvent())
				.build();
		

		// set the layout of the current panel to be a grid bag
		setLayout(new GridBagLayout());

		// and add the buttons one by one
		add(buttonCrop, SwingUtil.getGridBagConstraint(
				0, 0, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));
		add(buttonRotate, SwingUtil.getGridBagConstraint(
				0, 1, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));
		add(buttonDiscard, SwingUtil.getGridBagConstraint(
				0, 2, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));
		add(buttonSaveAs, SwingUtil.getGridBagConstraint(
				0, 3, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));
		add(buttonSave, SwingUtil.getGridBagConstraint(
				0, 4, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));
	}
	
	/**
	 * enable the active components which belong to the specified control set
	 * @param controlSet the control set containing the components which have to be enabled or not
	 * @param enabled true to enable them, false to disable
	 */
	public void setEnabled(ControlSet controlSet, boolean enabled) {
		super.setEnabled(enabled);
		
		switch (controlSet)
		{
			case ControlSetCrop:
				buttonCrop.setEnabled(enabled);
				break;
			case ControlSetRotate:
				buttonRotate.setEnabled(enabled);
				break;
			case ControlSetSave:
				buttonDiscard.setEnabled(enabled);
				buttonSaveAs.setEnabled(enabled);
				buttonSave.setEnabled(enabled);
				break;
		}
	}

}
