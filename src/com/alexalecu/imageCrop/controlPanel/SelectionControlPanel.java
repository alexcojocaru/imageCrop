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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.alexalecu.dataBinding.JBus;
import com.alexalecu.imageCrop.NotificationType;
import com.alexalecu.imageCrop.ImageCropGUI.ControlSet;
import com.alexalecu.imageCrop.imagePanel.SelectionPanel.ResizeDirection;
import com.alexalecu.imageUtil.AutoSelectStatus;
import com.alexalecu.imageUtil.ImageSelectMethod;
import com.alexalecu.util.SwingUtil;

@SuppressWarnings("serial")
public class SelectionControlPanel extends JPanel {

	public static final Vector<String> selectMethodList = new Vector<String>();
	static {
		selectMethodList.add("Minimum");
		selectMethodList.add("Maximum");
	}

	private JComboBox comboSelectMethod;
	private JButton buttonAutoSelect;
	private JProgressBar progressBarAutoSelect;

	private JSpinner spinnerMoveStep;
	private JButton buttonMoveUp;
	private JButton buttonMoveLeft;
	private JButton buttonMoveDown;
	private JButton buttonMoveRight;

	private JSpinner spinnerResizeStep;
	private JButton buttonResizeUpP;
	private JButton buttonResizeLeftP;
	private JButton buttonResizeDownP;
	private JButton buttonResizeRightP;
	private JButton buttonResizeUpM;
	private JButton buttonResizeLeftM;
	private JButton buttonResizeDownM;
	private JButton buttonResizeRightM;

	
	public SelectionControlPanel() {
		super();
		
		initComponents();
	}
	
	/**
	 * initialize the components and add them to the current panel
	 */
	private void initComponents() {

		// the label and control for the crop method
		JLabel labelSelectMethod = new JLabel("Select method:");
		comboSelectMethod = new JComboBox();
		comboSelectMethod.setModel(new DefaultComboBoxModel(selectMethodList));
		comboSelectMethod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selectMethodS = (String)comboSelectMethod.getSelectedItem();
				ImageSelectMethod selectMethod = selectMethodS == selectMethodList.get(0) ?
						ImageSelectMethod.SelectMinimum : ImageSelectMethod.SelectMaximum;
				JBus.getInstance().post(NotificationType.AUTO_SELECT_METHOD_SELECTED, selectMethod);
			}
		});
		
		// the 'auto select picture' button which asks the container to auto select the picture
		buttonAutoSelect = new JButton("Auto select picture");
		buttonAutoSelect.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JBus.getInstance().post(NotificationType.AUTO_SELECT_RECTANGLE);
					}
				}
		);
		
		// the progress bar for the auto-select operation
		progressBarAutoSelect = new JProgressBar();
		progressBarAutoSelect.setIndeterminate(false);
		
		
		// initialize the icons to be used on the buttons; if one cannot be initialized, reset all
		ImageIcon iconUp, iconLeft, iconDown, iconRight;
		try {
			iconUp = new ImageIcon("img/up.jpg");
			iconLeft = new ImageIcon("img/left.jpg");
			iconDown = new ImageIcon("img/down.jpg");
			iconRight = new ImageIcon("img/right.jpg");
		}
		catch (Exception ex) {
			iconUp = null;
			iconLeft = null;
			iconDown = null;
			iconRight = null;
		}


		// initialize the label of the move button group
		JLabel labelMove = new JLabel("Move selection");
		
		// the control to set the step size
		JLabel labelMoveStep = new JLabel("Step:");
		spinnerMoveStep = new JSpinner(new SpinnerNumberModel(10, 1, 20, 1));
		spinnerMoveStep.setToolTipText("step (in pixels) to move the selection when using buttons");
		
		// initialize the move buttons
		buttonMoveUp = new JButton();
		buttonMoveLeft = new JButton();
		buttonMoveDown = new JButton();
		buttonMoveRight = new JButton();
		
		// set the icons on the move buttons, or the text if the icons could not be loaded
		if (iconUp != null) {
			buttonMoveUp.setIcon(iconUp);
			buttonMoveLeft.setIcon(iconLeft);
			buttonMoveDown.setIcon(iconDown);
			buttonMoveRight.setIcon(iconRight);
		}
		else {
			buttonMoveUp.setText("/\\");
			buttonMoveLeft.setText("<");
			buttonMoveDown.setText("\\/");
			buttonMoveRight.setText(">");
		}
		
		// set the tooltip on the move buttons
		buttonMoveLeft.setToolTipText("Move selection left");
		buttonMoveUp.setToolTipText("Move selection up");
		buttonMoveRight.setToolTipText("Move selection right");
		buttonMoveDown.setToolTipText("Move selection down");
		
		// pin all the sizes of each move button
		SwingUtil.setAllSizes(buttonMoveUp,19,14);
		SwingUtil.setAllSizes(buttonMoveLeft,14,19);
		SwingUtil.setAllSizes(buttonMoveDown,19,14);
		SwingUtil.setAllSizes(buttonMoveRight,14,19);
		
		// add an action listener on each move button, which triggers a selection move request
		addMoveListener(buttonMoveUp,0,-1);
		addMoveListener(buttonMoveLeft,-1,0);
		addMoveListener(buttonMoveDown,0,1);
		addMoveListener(buttonMoveRight,1,0);
		

		// initialize the label of the resize button group
		JLabel labelResize = new JLabel("Resize selection");

		// the control to set the step size
		JLabel labelResizeStep = new JLabel("Step:");
		spinnerResizeStep = new JSpinner(new SpinnerNumberModel(10, 1, 20, 1));
		spinnerResizeStep.setToolTipText("step (in pixels) to resize selection when using buttons");

		// initialize the resize buttons
		buttonResizeUpP = new JButton();
		buttonResizeLeftP = new JButton();
		buttonResizeDownP = new JButton();
		buttonResizeRightP = new JButton();
		buttonResizeUpM = new JButton();
		buttonResizeLeftM = new JButton();
		buttonResizeDownM = new JButton();
		buttonResizeRightM = new JButton();

		// set the icons on the resize buttons, or the text if the icons could not be loaded
		if (iconUp != null) {
			buttonResizeUpP.setIcon(iconUp);
			buttonResizeLeftP.setIcon(iconLeft);
			buttonResizeDownP.setIcon(iconDown);
			buttonResizeRightP.setIcon(iconRight);
			buttonResizeUpM.setIcon(iconDown);
			buttonResizeLeftM.setIcon(iconRight);
			buttonResizeDownM.setIcon(iconUp);
			buttonResizeRightM.setIcon(iconLeft);
		}
		else {
			buttonResizeUpP.setText("/\\");
			buttonResizeLeftP.setText("<");
			buttonResizeDownP.setText("\\/");
			buttonResizeRightP.setText(">");
			buttonResizeUpM.setText("\\/");
			buttonResizeLeftM.setText(">");
			buttonResizeDownM.setText("/\\");
			buttonResizeRightM.setText("<");
		}
		
		// set the tooltip on the resize buttons
		buttonResizeLeftP.setToolTipText("Move selection's left edge");
		buttonResizeUpP.setToolTipText("Move selection's top edge");
		buttonResizeRightP.setToolTipText("Move selection's right edge");
		buttonResizeDownP.setToolTipText("Move selection's bottom edge");
		buttonResizeLeftM.setToolTipText("Move selection's left edge");
		buttonResizeUpM.setToolTipText("Move selection's top edge");
		buttonResizeRightM.setToolTipText("Move selection's right edge");
		buttonResizeDownM.setToolTipText("Move selection's bottom edge");
		
		// pin all the sizes of each resize button
		SwingUtil.setAllSizes(buttonResizeUpP,19,14);
		SwingUtil.setAllSizes(buttonResizeLeftP,14,19);
		SwingUtil.setAllSizes(buttonResizeDownP,19,14);
		SwingUtil.setAllSizes(buttonResizeRightP,14,19);
		SwingUtil.setAllSizes(buttonResizeUpM,19,14);
		SwingUtil.setAllSizes(buttonResizeLeftM,14,19);
		SwingUtil.setAllSizes(buttonResizeDownM,19,14);
		SwingUtil.setAllSizes(buttonResizeRightM,14,19);

		// add an action listener on each resize button, which triggers a selection resize request
		addResizeListener(buttonResizeUpP, ResizeDirection.NORTH, ResizeDirection.NORTH);
		addResizeListener(buttonResizeUpM, ResizeDirection.NORTH, ResizeDirection.SOUTH);
		
		addResizeListener(buttonResizeLeftP, ResizeDirection.WEST, ResizeDirection.WEST);
		addResizeListener(buttonResizeLeftM, ResizeDirection.WEST, ResizeDirection.EAST);
		
		addResizeListener(buttonResizeDownP, ResizeDirection.SOUTH, ResizeDirection.SOUTH);
		addResizeListener(buttonResizeDownM, ResizeDirection.SOUTH, ResizeDirection.NORTH);
		
		addResizeListener(buttonResizeRightP, ResizeDirection.EAST, ResizeDirection.EAST);
		addResizeListener(buttonResizeRightM, ResizeDirection.EAST, ResizeDirection.WEST);
		
		
		
		// set the layout of the main panel to be a grid bag
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints;

		// add the crop method label and control
		add(labelSelectMethod, SwingUtil.getGridBagConstraint(
				0, 0, GridBagConstraints.WEST, new Insets(5, 5, 5, 2)));
		add(comboSelectMethod, SwingUtil.getGridBagConstraint(
				1, 0, GridBagConstraints.WEST, new Insets(5, 2, 5, 5)));

		// add the button to auto select a picture
		add(buttonAutoSelect, SwingUtil.getGridBagConstraint(
				0, 1, 2, 1, GridBagConstraints.CENTER, new Insets(5, 5, 2, 5)));

		// and the progress bar for the auto-select operation
		constraints = SwingUtil.getGridBagConstraint(
				0, 2, 2, 1, GridBagConstraints.CENTER, new Insets(2, 5, 5, 5));
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(progressBarAutoSelect, constraints);

		// add the move panel label
		add(labelMove, SwingUtil.getGridBagConstraint(
				0, 3, 2, 1, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));

		// add the move step label and control
		add(labelMoveStep, SwingUtil.getGridBagConstraint(
				0, 4, GridBagConstraints.WEST, new Insets(5, 5, 5, 2)));
		add(spinnerMoveStep, SwingUtil.getGridBagConstraint(
				1, 4, GridBagConstraints.WEST, new Insets(5, 2, 5, 5)));

		// set up the panel which contains all the move buttons
		JPanel panelMoveButtons = new JPanel(new GridBagLayout());

		// add all the move buttons
		panelMoveButtons.add(buttonMoveLeft, SwingUtil.getGridBagConstraint(
				0, 1, GridBagConstraints.EAST, new Insets(0, 0, 0, 0)));
		panelMoveButtons.add(buttonMoveUp, SwingUtil.getGridBagConstraint(
				1, 0, GridBagConstraints.SOUTH, new Insets(0, 0, 0, 0)));
		panelMoveButtons.add(buttonMoveRight, SwingUtil.getGridBagConstraint(
				2, 1, GridBagConstraints.WEST, new Insets(0, 0, 0, 0)));
		panelMoveButtons.add(buttonMoveDown, SwingUtil.getGridBagConstraint(
				1, 2, GridBagConstraints.NORTH, new Insets(0, 0, 0, 0)));
		
		// and add the panel containing the move buttons to the main panel
		add(panelMoveButtons, SwingUtil.getGridBagConstraint(
				0, 5, 2, 1, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));


		// add the resize panel label
		add(labelResize, SwingUtil.getGridBagConstraint(
				0, 6, 2, 1, GridBagConstraints.CENTER, new Insets(10, 5, 5, 5)));

		// add the resize step label and control
		add(labelResizeStep, SwingUtil.getGridBagConstraint(
				0, 7, GridBagConstraints.WEST, new Insets(5, 5, 5, 2)));
		add(spinnerResizeStep, SwingUtil.getGridBagConstraint(
				1, 7, GridBagConstraints.WEST, new Insets(5, 2, 5, 5)));

		// set up the panel which contains all the resize buttons
		JPanel panelResizeButtons = new JPanel(new GridBagLayout());

		// add all the resize buttons
		panelResizeButtons.add(buttonResizeLeftP, SwingUtil.getGridBagConstraint(
				0, 2, GridBagConstraints.EAST, new Insets(0, 0, 0, 0)));
		panelResizeButtons.add(buttonResizeLeftM, SwingUtil.getGridBagConstraint(
				1, 2, GridBagConstraints.WEST, new Insets(0, 0, 0, 0)));
		panelResizeButtons.add(buttonResizeUpP, SwingUtil.getGridBagConstraint
				(2, 0, GridBagConstraints.SOUTH, new Insets(0, 0, 0, 0)));
		panelResizeButtons.add(buttonResizeUpM, SwingUtil.getGridBagConstraint(
				2, 1, GridBagConstraints.NORTH, new Insets(0, 0, 0, 0)));
		panelResizeButtons.add(buttonResizeRightP, SwingUtil.getGridBagConstraint(
				4, 2, GridBagConstraints.WEST, new Insets(0, 0, 0, 0)));
		panelResizeButtons.add(buttonResizeRightM, SwingUtil.getGridBagConstraint(
				3, 2, GridBagConstraints.EAST, new Insets(0, 0, 0, 0)));
		panelResizeButtons.add(buttonResizeDownP, SwingUtil.getGridBagConstraint(
				2, 4, GridBagConstraints.NORTH, new Insets(0, 0, 0, 0)));
		panelResizeButtons.add(buttonResizeDownM, SwingUtil.getGridBagConstraint(
				2, 3, GridBagConstraints.SOUTH, new Insets(0, 0, 0, 0)));
		
		// and add the panel containing the resize buttons to the main panel
		add(panelResizeButtons, SwingUtil.getGridBagConstraint(
				0, 8, 2, 1, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));
	}
	
	
	/**
	 * add an action listener on the given button which moves the selection
	 * @param button the button to add the action to
	 * @param x the x direction to move the selection to; one of -1 for left, 0 or 1 for right
	 * @param y the y direction to move the selection to; one of -1 for left, 0 or 1 for right
	 */
	private void addMoveListener(JButton button, final int x, final int y) {
		button.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// get the step to move the selection with
						int spinnerVal = ((Integer)spinnerMoveStep.getValue()).intValue();
						
						if (spinnerVal <= 0) // do not process invalid values
							return;
						
						JBus.getInstance().post(NotificationType.MOVE_SELECTION,
								x * spinnerVal, y * spinnerVal);
					}
				}
		);
	}

	/**
	 * add an action listener on the given button which resizes the selection
	 * @param button the button to add the action to
	 * @param resizeEdge the edge to move to achieve the resize
	 * @param direction the direction to resize on
	 */
	private void addResizeListener(JButton button, final ResizeDirection edge,
			final ResizeDirection direction) {
		
		button.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// get the step to resize the selection with
						int spinnerVal = ((Integer)spinnerResizeStep.getValue()).intValue();
						
						if (spinnerVal <= 0) // do not process invalid values
							return;

						JBus.getInstance().post(NotificationType.RESIZE_SELECTION,
								spinnerVal, edge, direction);
					}
				}
		);
	}

	/**
	 * set the new select method in the combobox
	 * @param selectMethod the select method to be set
	 */
	public void setAutoSelectMethod(ImageSelectMethod selectMethod) {
		comboSelectMethod.setSelectedIndex(selectMethod == ImageSelectMethod.SelectMinimum ? 0 : 1);
	}

	/**
	 * set the new auto-select status in the progress bar
	 * @param status
	 */
	public void setAutoSelectStatus(AutoSelectStatus status) {
		progressBarAutoSelect.setStringPainted(true);
		switch (status) {
			case Init:
				progressBarAutoSelect.setString("Initializing");
				break;
			case SelectBoundingRectangle:
				progressBarAutoSelect.setString("Finding the  bounding shape");
				break;
			case ReduceImageColors:
				progressBarAutoSelect.setString("Reducing image colors");
				break;
			case FindEdgePoints:
				progressBarAutoSelect.setString("Finding the polygon shape");
				break;
			case FindVertices:
				progressBarAutoSelect.setString("Finding the polygon vertices");
				break;
			case ComputeLargestRectangle:
				progressBarAutoSelect.setString("Computing the polygon");
				break;
			case ComputeEdgeList:
				progressBarAutoSelect.setString("Computing the edge list");
				break;
			case Canceled:
				progressBarAutoSelect.setString("Canceled");
				break;
			case Finished:
				progressBarAutoSelect.setString("Finished");
				break;
			default:
				progressBarAutoSelect.setString("");
		}
	}
	

	/**
	 * enable the active components on this panel
	 * @param controlSet the control set containing the components which have to be enabled or not
	 * @param enabled true to enable them, false to disable
	 */
	public void setEnabled(ControlSet controlSet, boolean enabled) {
		super.setEnabled(enabled);
		
		switch (controlSet)
		{
			case ControlSetMoveResize:
				// enable / disable the selection move controls
				spinnerMoveStep.setEnabled(enabled);
				buttonMoveUp.setEnabled(enabled);
				buttonMoveLeft.setEnabled(enabled);
				buttonMoveDown.setEnabled(enabled);
				buttonMoveRight.setEnabled(enabled);

				// enable / disable the selection resize controls
				spinnerResizeStep.setEnabled(enabled);
				buttonResizeUpP.setEnabled(enabled);
				buttonResizeLeftP.setEnabled(enabled);
				buttonResizeDownP.setEnabled(enabled);
				buttonResizeRightP.setEnabled(enabled);
				buttonResizeUpM.setEnabled(enabled);
				buttonResizeLeftM.setEnabled(enabled);
				buttonResizeDownM.setEnabled(enabled);
				buttonResizeRightM.setEnabled(enabled);
				break;
			case ControlSetAutoSelect:
				comboSelectMethod.setEnabled(enabled);
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
}
