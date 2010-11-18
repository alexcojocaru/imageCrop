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

package com.alexalecu.imageCrop.controlPanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.alexalecu.imageCrop.ImageCropGUI;
import com.alexalecu.imageCrop.ImageCropGUI.ControlSet;
import com.alexalecu.imageUtil.ImageCropMethod;
import com.alexalecu.util.SwingUtil;

@SuppressWarnings("serial")
public class CropPropertiesPanel extends JPanel {

	public static final Vector<String> cropMethodList = new Vector<String>();
	static {
		cropMethodList.add("Minimum");
		cropMethodList.add("Maximum");
	}

	private ImageCropGUI container;

	private JButton buttonSelBG;
	
	private JSpinner spinnerBGRed;
	private JSpinner spinnerBGGreen;
	private JSpinner spinnerBGBlue;
	private JSpinner spinnerBGTol;

	private JComboBox comboCropMethod;
	
	private JButton buttonAutoSelect;
	
	private JProgressBar progressBarAutoSelect;

	
	public CropPropertiesPanel(ImageCropGUI container) {
		super();
		
		this.container = container;
		
		initComponents();
	}
	
	/**
	 * initialize the components and add them to the current panel
	 */
	private void initComponents() {

		// the 'select background' button
		buttonSelBG = new JButton("Select background color");
		buttonSelBG.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						container.toggleSelectBackgroundMode();
					}
				}
		);

		// the control for the red component of the background
		JLabel labelBGRed = new JLabel("Backgroud red:");
		spinnerBGRed = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
		spinnerBGRed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				bgColorChanged();
			}
		});

		// the control for the green component of the background
		JLabel labelBGGreen = new JLabel("Backgroud green:");
		spinnerBGGreen = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
		spinnerBGGreen.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				bgColorChanged();
			}
		});

		// the control for the blue component of the background
		JLabel labelBGBlue = new JLabel("Backgroud blue:");
		spinnerBGBlue = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
		spinnerBGBlue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				bgColorChanged();
			}
		});

		// the tolerance control
		JLabel labelBGTol = new JLabel();
		labelBGTol.setText("Tolerance (%):");
		spinnerBGTol = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		spinnerBGTol.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				container.bgToleranceChanged(((Number)spinnerBGTol.getValue()).intValue());
			}
		});

		// the label and control for the crop method
		JLabel labelCropMethod = new JLabel("Crop method:");
		comboCropMethod = new JComboBox();
		comboCropMethod.setModel(new DefaultComboBoxModel(cropMethodList));
		comboCropMethod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String cropMethod = (String)comboCropMethod.getSelectedItem();
				container.autoCropMethodChanged(cropMethod == cropMethodList.get(0) ?
						ImageCropMethod.CropMinimum : ImageCropMethod.CropMaximum);
			}
		});
		
		// the 'auto select picture' button which asks the container to auto select the picture
		buttonAutoSelect = new JButton("Auto select picture");
		buttonAutoSelect.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						container.autoSelectPicture();
					}
				}
		);
		
		// the progress bar for the auto-select operation
		progressBarAutoSelect = new JProgressBar();
		progressBarAutoSelect.setIndeterminate(false);
		
		
		GridBagConstraints constraints;
		
		// create a panel for the background controls and set its layout to be a grid bag
		JPanel panelBGControl = new JPanel(new GridBagLayout());

		// add the 'select background' button
		panelBGControl.add(buttonSelBG, SwingUtil.getGridBagConstraint(
				0, 0, 2, 1, GridBagConstraints.CENTER, new Insets(0, 0, 2, 5)));

		// add the label and control for the red component of the background
		panelBGControl.add(labelBGRed, SwingUtil.getGridBagConstraint(
				0, 1, GridBagConstraints.WEST, new Insets(2, 0, 2, 2)));
		panelBGControl.add(spinnerBGRed, SwingUtil.getGridBagConstraint(
				1, 1, GridBagConstraints.WEST, new Insets(2, 2, 2, 0)));

		// add the label and control for the green component of the background
		panelBGControl.add(labelBGGreen, SwingUtil.getGridBagConstraint(
				0, 2, GridBagConstraints.WEST, new Insets(2, 0, 2, 2)));
		panelBGControl.add(spinnerBGGreen, SwingUtil.getGridBagConstraint(
				1, 2, GridBagConstraints.WEST, new Insets(2, 2, 2, 0)));

		// add the label and control for the blue component of the background
		panelBGControl.add(labelBGBlue, SwingUtil.getGridBagConstraint(
				0, 3, GridBagConstraints.WEST, new Insets(2, 0, 5, 2)));
		panelBGControl.add(spinnerBGBlue, SwingUtil.getGridBagConstraint(
				1, 3, GridBagConstraints.WEST, new Insets(2, 2, 5, 0)));

		// add the background tolerance label and control
		panelBGControl.add(labelBGTol, SwingUtil.getGridBagConstraint(
				0, 4, GridBagConstraints.WEST, new Insets(5, 0, 2, 2)));
		panelBGControl.add(spinnerBGTol, SwingUtil.getGridBagConstraint(
				1, 4, GridBagConstraints.WEST, new Insets(5, 2, 2, 0)));


		// set the layout of the current panel to a grid bag
		setLayout(new GridBagLayout());

		// add the background control panel
		add(panelBGControl, SwingUtil.getGridBagConstraint(
				0, 0, 2, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 5)));

		// add the crop method label and control
		add(labelCropMethod, SwingUtil.getGridBagConstraint(
				0, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 2)));
		add(comboCropMethod, SwingUtil.getGridBagConstraint(
				1, 1, GridBagConstraints.WEST, new Insets(5, 2, 5, 5)));

		// add the button to auto select a picture
		add(buttonAutoSelect, SwingUtil.getGridBagConstraint(
				0, 2, 2, 1, GridBagConstraints.CENTER, new Insets(5, 5, 2, 5)));

		// and the progress bar for the auto-select operation
		constraints = SwingUtil.getGridBagConstraint(
				0, 3, 2, 1, GridBagConstraints.CENTER, new Insets(2, 5, 5, 5));
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(progressBarAutoSelect, constraints);
	}
	
	/**
	 * set the red, green and blue spinner values to be the red, green and blue components of the
	 * color passed as parameter
	 * @param color the color who's red, green and blue should be set to the spinner controls
	 */
	public void setBackgroundColor(Color color) {
		// if the color is null, reset the spinners
		if (color != null) {
			spinnerBGRed.setValue(new Integer(color.getRed()));
			spinnerBGGreen.setValue(new Integer(color.getGreen()));
			spinnerBGBlue.setValue(new Integer(color.getBlue()));
		}
		else {
			spinnerBGRed.setValue(new Integer(0));
			spinnerBGGreen.setValue(new Integer(0));
			spinnerBGBlue.setValue(new Integer(0));
		}
	}
	
	/**
	 * set the tolerance spinner value to be the value passed as parameter
	 * @param tolerance the background color tolerance to be set to the spinner control
	 */
	public void setBackgroundTolerance(int tolerance) {
		spinnerBGTol.setValue(new Integer(tolerance));
	}

	/**
	 * set the new crop method in the combobox
	 * @param cropMethod the crop method to be set
	 */
	public void setAutoCropMethod(ImageCropMethod cropMethod) {
		comboCropMethod.setSelectedIndex(cropMethod == ImageCropMethod.CropMinimum ? 0 : 1);
	}


	/**
	 * enable the 'select background' mode on this panel; if true, the text on the button changes
	 * @param enabled true to enable them, false to disable
	 */
	public void enableSelectBackgroundMode(boolean enabled) {
		buttonSelBG.setText(enabled ? "Finish selecting" : "Select background color");
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
			case ControlSetBackground:
				buttonSelBG.setEnabled(enabled);
				break;
			case ControlSetAutoCrop:
				spinnerBGRed.setEnabled(enabled);
				spinnerBGGreen.setEnabled(enabled);
				spinnerBGBlue.setEnabled(enabled);
				spinnerBGTol.setEnabled(enabled);
				comboCropMethod.setEnabled(enabled);
				break;
			case ControlSetAutoSelect:
				buttonAutoSelect.setEnabled(enabled);
				if (enabled)
					buttonAutoSelect.setText("Auto select picture");
				break;
			case ControlSetAutoSelectOp:
				buttonAutoSelect.setEnabled(enabled);
				progressBarAutoSelect.setIndeterminate(enabled);
				if (enabled)
					buttonAutoSelect.setText("Stop auto selecting picture");
				break;
		}
	}
	
	
	/**
	 * notify the container each time the background color changes
	 */
	private void bgColorChanged() {
		Color bgColor = new Color(((Number)spinnerBGRed.getValue()).intValue(),
				((Number)spinnerBGGreen.getValue()).intValue(),
				((Number)spinnerBGBlue.getValue()).intValue());
		
		container.bgColorChanged(bgColor, false);
	}

}
