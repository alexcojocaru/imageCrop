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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.alexalecu.component.NotificationButton;
import com.alexalecu.event.BgColorPickedEvent;
import com.alexalecu.event.BgColorSelectedEvent;
import com.alexalecu.event.BgToleranceChangedEvent;
import com.alexalecu.event.EventBus;
import com.alexalecu.event.ToggleBgSelectionEvent;
import com.alexalecu.imageCrop.gui.ImageCropGUI.ControlSet;
import com.alexalecu.util.SwingUtil;
import com.google.common.eventbus.Subscribe;

public class BackgroundPropertiesPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JButton buttonSelBG;
	
	private JSpinner spinnerBGRed;
	private JSpinner spinnerBGGreen;
	private JSpinner spinnerBGBlue;
	private JSpinner spinnerBGTol;

	
	public BackgroundPropertiesPanel() {
		super();

		EventBus.register(this);
		
		initComponents();
	}
	
	/**
	 * initialize the components and add them to the current panel
	 */
	private void initComponents() {

		// the 'select background' button
		buttonSelBG = new NotificationButton.Builder()
				.text("Select background color")
				.event(new ToggleBgSelectionEvent())
				.build();

		// the control for the red component of the background
		JLabel labelBGRed = new JLabel("Background red:");
		spinnerBGRed = buildColorSpinner();

		// the control for the green component of the background
		JLabel labelBGGreen = new JLabel("Background green:");
		spinnerBGGreen = buildColorSpinner();

		// the control for the blue component of the background
		JLabel labelBGBlue = new JLabel("Background blue:");
		spinnerBGBlue = buildColorSpinner();

		// the tolerance control
		JLabel labelBGTol = new JLabel("Tolerance (%):");
		spinnerBGTol = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		spinnerBGTol.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int bgTolerance = ((Number)spinnerBGTol.getValue()).intValue();
				EventBus.post(new BgToleranceChangedEvent(bgTolerance));
			}
		});
		
		
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
	}
	
	/**
	 * set the red, green and blue spinner values to be the corresponding components of the color
	 * passed as parameter
	 * @param event the BgColorPickedEvent containing the color to set
	 */
	@Subscribe
	public void setBackgroundColor(BgColorPickedEvent event) {
		// if the color is null, reset the spinners
		if (event.getColor() != null) {
			spinnerBGRed.setValue(new Integer(event.getColor().getRed()));
			spinnerBGGreen.setValue(new Integer(event.getColor().getGreen()));
			spinnerBGBlue.setValue(new Integer(event.getColor().getBlue()));
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
			case ControlSetPickBackground:
				buttonSelBG.setEnabled(enabled);
				break;
			case ControlSetSetBackground:
				spinnerBGRed.setEnabled(enabled);
				spinnerBGGreen.setEnabled(enabled);
				spinnerBGBlue.setEnabled(enabled);
				spinnerBGTol.setEnabled(enabled);
				break;
		}
	}
	
	
	private JSpinner buildColorSpinner() {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				bgColorSelected();
			}
		});
		return spinner;
	}
	
	/**
	 * notify the container each time the background color changes
	 */
	private void bgColorSelected() {
		Color bgColor = new Color(((Number)spinnerBGRed.getValue()).intValue(),
				((Number)spinnerBGGreen.getValue()).intValue(),
				((Number)spinnerBGBlue.getValue()).intValue());
		
		// send the notification to any registered listeners
		EventBus.post(new BgColorSelectedEvent(bgColor));
	}

}
