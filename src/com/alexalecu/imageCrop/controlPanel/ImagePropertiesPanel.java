package com.alexalecu.imageCrop.controlPanel;

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

import com.alexalecu.imageCrop.ImageCropGUI;
import com.alexalecu.util.FileUtil;
import com.alexalecu.util.SwingUtil;

@SuppressWarnings("serial")
public class ImagePropertiesPanel extends JPanel {

	private ImageCropGUI container;
	
	private JLabel labelImageNameVal;
	private JLabel labelImageSizeVal;
	private JLabel labelCropSizeVal;

	private JSpinner spinnerImageScale;
	private JLabel labelImageScaleUM;
	private JButton buttonImageScale;

	
	public ImagePropertiesPanel(ImageCropGUI container) {
		super();
		
		this.container = container;
		
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
		buttonImageScale.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int scale = ((Number)spinnerImageScale.getValue()).intValue();
						container.scaleFactorChanged(scale);
					}
				}
		);
		
		
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
	 */
	public void setCropSize(Dimension cropSize) {
		if (cropSize.width == 0 && cropSize.height == 0)
			resetCropSize();
		else
			labelCropSizeVal.setText(String.format("%1$d x %2$d px",
					cropSize.width, cropSize.height));
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
