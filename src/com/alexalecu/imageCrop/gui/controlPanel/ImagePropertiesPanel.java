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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.alexalecu.event.EventBus;
import com.alexalecu.event.ScaleFactorChangedEvent;
import com.alexalecu.event.SelectionRectangleChangedEvent;
import com.alexalecu.util.FileUtil;
import com.alexalecu.util.SwingUtil;
import com.google.common.eventbus.Subscribe;

public class ImagePropertiesPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel labelImageNameVal;
	private JLabel labelImageSizeVal;
	private JLabel labelCropSizeVal;

	private JSpinner spinnerImageScale;
	private JLabel labelImageScaleUM;
	private JButton buttonImageScale;

	
	public ImagePropertiesPanel() {
		super();
		
		EventBus.register(this);
		
		initComponents();
	}
	
	/**
	 * initialize the components and add them to the current panel
	 */
	private void initComponents() {
		JLabel labelImageName = new JLabel("Image file:");
		labelImageNameVal = new JLabel("N/A");
		
		JLabel labelImageSize = new JLabel("Image size:");
		labelImageSizeVal = new JLabel("N/A");
		
		JLabel labelCropSize = new JLabel("Crop size:");
		labelCropSizeVal = new JLabel("N/A");
		
		JLabel labelImageScale = new JLabel("Scale image:");
		spinnerImageScale = new JSpinner(new SpinnerNumberModel(100, 10, 400, 10));
		labelImageScaleUM = new JLabel("%");
		
		// the 'apply' button which tells the container that the scale factor has changed
		buttonImageScale = new JButton("Apply");
		buttonImageScale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int scale = ((Number) spinnerImageScale.getValue()).intValue();
				EventBus.post(new ScaleFactorChangedEvent(scale));
			}
		});
		
		
		// set the layout to a grid bag layout
		setLayout(new GridBagLayout());
		
		// and add the components one by one
		add(labelImageName, SwingUtil.getGridBagConstraint(
				0, 0, GridBagConstraints.WEST, new Insets(5, 5, 2, 2)));
		add(labelImageNameVal, SwingUtil.getGridBagConstraint(
				1, 0, GridBagConstraints.WEST, new Insets(5, 2, 2, 5)));
		add(labelImageSize, SwingUtil.getGridBagConstraint(
				0, 1, GridBagConstraints.WEST, new Insets(2, 5, 2, 2)));
		add(labelImageSizeVal, SwingUtil.getGridBagConstraint(
				1, 1, GridBagConstraints.WEST, new Insets(2, 2, 2, 5)));
		add(labelCropSize, SwingUtil.getGridBagConstraint(
				0, 2, GridBagConstraints.WEST, new Insets(2, 5, 5, 2)));
		add(labelCropSizeVal, SwingUtil.getGridBagConstraint(
				1, 2, GridBagConstraints.WEST, new Insets(2, 2, 2, 5)));

		// create a panel for the scale label and controls and set its layout to be a grid bag
		JPanel panelScale = new JPanel();
		panelScale.setLayout(new GridBagLayout());
		
		// and add components to it
		panelScale.add(labelImageScale, SwingUtil.getGridBagConstraint(
				0, 0, GridBagConstraints.WEST, new Insets(0, 0, 2, 2)));
		panelScale.add(spinnerImageScale, SwingUtil.getGridBagConstraint(
				1, 0, GridBagConstraints.WEST, new Insets(0, 2, 2, 2)));
		panelScale.add(labelImageScaleUM, SwingUtil.getGridBagConstraint(
				2, 0, GridBagConstraints.WEST, new Insets(0, 2, 2, 2)));
		panelScale.add(buttonImageScale, SwingUtil.getGridBagConstraint(
				3, 0, GridBagConstraints.WEST, new Insets(0, 2, 2, 0)));
		
		// finally, add the scale panel to the main panel
		add(panelScale, SwingUtil.getGridBagConstraint(
				0, 3, 2, 1, GridBagConstraints.WEST, new Insets(2, 5, 5, 5)));
	}
	
	/**
	 * reset the label values and set the spinner value based on the one from the container
	 */
	public void resetValues() {
		labelImageNameVal.setText("N/A");
		labelImageNameVal.setToolTipText("");
		labelImageSizeVal.setText("N/A");
		resetCropSize();
		resetSpinner();
	}
	/**
	 * reset the crop size label value
	 */
	public void resetCropSize() {
		labelCropSizeVal.setText("N/A");
	}
	/**
	 * reset the scale factor spinner value based on the one from the container
	 */
	public void resetSpinner() {
		spinnerImageScale.setValue(100);
	}
	
	/**
	 * set the image file name value
	 */
	public void setImageName(String imageName) {
		if (imageName.length() <= 21) {
			labelImageNameVal.setText(imageName);
			labelImageNameVal.setToolTipText("");
			return;
		}
		
		// trim the image file name to fit the display area
		String name = FileUtil.stripExtension(imageName);
		String extension = FileUtil.getExtension(imageName);
		name = name.substring(0, 18 - Math.min(extension.length(), 18)) + "...";
		
		labelImageNameVal.setText(name + extension);
		labelImageNameVal.setToolTipText(imageName);
	}
	/**
	 * set the image size label value
	 */
	public void setImageSize(Dimension imageSize) {
		labelImageSizeVal.setText(String.format("%1$d x %2$d px",
				imageSize.width, imageSize.height));
	}
	/**
	 * set the crop size label value
	 * @param event the selection rectangle changed event
	 */
	@Subscribe
	public void setCropSize(SelectionRectangleChangedEvent event) {
		int cropWidth = 0;
		int cropHeight = 0;
		
		if (event.getRectangle() != null) {
			cropWidth = event.getRectangle().width;
			cropHeight = event.getRectangle().height;
		} 
				
		if (cropWidth == 0 && cropHeight == 0)
			resetCropSize();
		else
			labelCropSizeVal.setText(String.format("%1$d x %2$d px", cropWidth, cropHeight));
	}
	/**
	 * set the scale factor to the spinner control
	 * @param scaleFactor
	 */
	public void setScaleFactor(int scaleFactor) {
		spinnerImageScale.setValue(scaleFactor);
	}

	/**
	 * enable the active components on this panel
	 * @param enabled true to enable them, false to disable
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		spinnerImageScale.setEnabled(enabled);
		buttonImageScale.setEnabled(enabled);
	}
	
}
