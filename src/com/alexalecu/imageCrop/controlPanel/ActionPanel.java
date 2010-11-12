package com.alexalecu.imageCrop.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.alexalecu.imageCrop.ImageCropGUI;
import com.alexalecu.imageCrop.ImageCropGUI.ControlSet;
import com.alexalecu.util.SwingUtil;

@SuppressWarnings("serial")
public class ActionPanel extends JPanel {

	private ImageCropGUI container;

	private JButton buttonCrop;
	private JButton buttonRotate;
	private JButton buttonDiscard;
	private JButton buttonSaveAs;
	private JButton buttonSave;

	public ActionPanel(ImageCropGUI container) {
		super();
		
		this.container = container;
		
		initComponents();
	}
	
	/**
	 * initialize the components and add them to the current panel
	 */
	private void initComponents() {
		// create the 'crop picture' button
		buttonCrop = new JButton();
		buttonCrop.setText("Crop picture");
		buttonCrop.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						container.cropSelection();
					}
				}
		);
		
		// create the 'rotate selection' button
		buttonRotate = new JButton();
		buttonRotate.setText("Rotate picture");
		buttonRotate.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						container.rotateSelection();
					}
				}
		);
		
		// create the 'discard current image' button
		buttonDiscard = new JButton();
		buttonDiscard.setText("Discard image");
		buttonDiscard.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						container.discardImage();
					}
				}
		);
		
		// create the 'save as' button which saves the current image in buffer, allowing the
		// user to choose the file name
		buttonSaveAs = new JButton();
		buttonSaveAs.setText("Save image as...");
		buttonSaveAs.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						container.saveAsImage();
					}
				}
		);

		// create the 'save' button which saves the current image in buffer
		buttonSave = new JButton();
		buttonSave.setText("Save image");
		buttonSave.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						container.saveImage();
					}
				}
		);
		

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
