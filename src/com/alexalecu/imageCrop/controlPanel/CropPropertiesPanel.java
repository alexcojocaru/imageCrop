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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.alexalecu.imageCrop.ImageCropGUI;
import com.alexalecu.imageCrop.ImageCropEngine.CropMethod;
import com.alexalecu.imageCrop.ImageCropGUI.ControlSet;
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
	
	private JSpinner spinnerTimeToAutoSelect;
	
	private JButton buttonAutoSelect;

	
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
		spinnerBGTol = new JSpinner(new SpinnerNumberModel(3, 0, 100, 1));
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
						CropMethod.CropMinimum : CropMethod.CropMaximum);
			}
		});

		// the time to auto-select control
		JLabel labelTimeToAutoSelect = new JLabel();
		labelTimeToAutoSelect.setText("Time allowed:");
		spinnerTimeToAutoSelect = new JSpinner(new SpinnerNumberModel(4, 1, 8, 1));
		spinnerTimeToAutoSelect.setToolTipText(
				"How many seconds to allow the auto-select operation to run");
		spinnerTimeToAutoSelect.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				container.timeToAutoSelectChanged(
						((Number)spinnerTimeToAutoSelect.getValue()).intValue());
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
				0, 1, GridBagConstraints.WEST, new Insets(5, 5, 2, 2)));
		add(comboCropMethod, SwingUtil.getGridBagConstraint(
				1, 1, GridBagConstraints.WEST, new Insets(5, 2, 2, 5)));

		// add the time to auto-select label and control
		add(labelTimeToAutoSelect, SwingUtil.getGridBagConstraint(
				0, 2, GridBagConstraints.WEST, new Insets(2, 5, 5, 2)));
		add(spinnerTimeToAutoSelect, SwingUtil.getGridBagConstraint(
				1, 2, GridBagConstraints.WEST, new Insets(2, 2, 5, 5)));

		// and the button to auto select a picture
		add(buttonAutoSelect, SwingUtil.getGridBagConstraint(
				0, 3, 2, 1, GridBagConstraints.CENTER, new Insets(2, 5, 5, 5)));
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
	public void setAutoCropMethod(CropMethod cropMethod) {
		comboCropMethod.setSelectedIndex(cropMethod == CropMethod.CropMinimum ? 0 : 1);
	}
	
	/**
	 * set the time to auto-select spinner to the value passed as parameter
	 * @param timeToAutoSelect
	 */
	public void setTimeToAutoSelect(int timeToAutoSelect) {
		spinnerTimeToAutoSelect.setValue(new Integer(timeToAutoSelect));
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
				spinnerTimeToAutoSelect.setEnabled(enabled);
				break;
			case ControlSetAutoSelect:
				buttonAutoSelect.setEnabled(enabled);
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
