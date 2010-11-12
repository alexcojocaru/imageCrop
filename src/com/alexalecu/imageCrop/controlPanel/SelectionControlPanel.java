package com.alexalecu.imageCrop.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.alexalecu.imageCrop.ImageCropGUI;
import com.alexalecu.util.SwingUtil;

@SuppressWarnings("serial")
public class SelectionControlPanel extends JPanel {

	private ImageCropGUI container;

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

	
	public SelectionControlPanel(ImageCropGUI container) {
		super();
		
		this.container = container;
		
		initComponents();
	}
	
	/**
	 * initialize the components and add them to the current panel
	 */
	private void initComponents() {
		
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
		addResizeListener(buttonResizeUpP, 1, 0, 0, 0);
		addResizeListener(buttonResizeLeftP, 0, 1, 0, 0);
		addResizeListener(buttonResizeDownP, 0, 0, 1, 0);
		addResizeListener(buttonResizeRightP, 0, 0, 0, 1);
		addResizeListener(buttonResizeUpM, -1, 0, 0, 0);
		addResizeListener(buttonResizeLeftM, 0, -1, 0, 0);
		addResizeListener(buttonResizeDownM, 0, 0, -1, 0);
		addResizeListener(buttonResizeRightM, 0, 0, 0, -1);
		
		
		
		// set the layout of the main panel to be a grid bag
		setLayout(new GridBagLayout());

		// add the move panel label
		add(labelMove, SwingUtil.getGridBagConstraint(
				0, 0, 2, 1, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));

		// add the move step label and control
		add(labelMoveStep, SwingUtil.getGridBagConstraint(
				0, 1, GridBagConstraints.WEST, new Insets(5, 5, 5, 2)));
		add(spinnerMoveStep, SwingUtil.getGridBagConstraint(
				1, 1, GridBagConstraints.WEST, new Insets(5, 2, 5, 5)));

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
				0, 2, 2, 1, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));


		// add the resize panel label
		add(labelResize, SwingUtil.getGridBagConstraint(0, 3, 2, 1, GridBagConstraints.CENTER, new Insets(10, 5, 5, 5)));

		// add the resize step label and control
		add(labelResizeStep, SwingUtil.getGridBagConstraint(0, 4, GridBagConstraints.WEST, new Insets(5, 5, 5, 2)));
		add(spinnerResizeStep, SwingUtil.getGridBagConstraint(1, 4, GridBagConstraints.WEST, new Insets(5, 2, 5, 5)));

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
				0, 5, 2, 1, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));
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
						
						container.moveSelection(x, y, spinnerVal);
					}
				}
		);
	}

	/**
	 * add an action listener on the given button which resizes the selection
	 * @param button the button to add the action to
	 * @param top one of -1 for up, 0 or 1 for down to apply to the top edge of the selection
	 * @param left one of -1 for left, 0 or 1 for right to apply to the left edge of the selection
	 * @param bottom one of -1 for up, 0 or 1 for down to apply to the bottom edge of the selection
	 * @param right one of -1 for left, 0 or 1 for right to apply to the right edge of the selection
	 */
	private void addResizeListener(JButton button, 
			final int top, final int left, final int bottom, final int right) {
		
		button.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// get the step to resize the selection with
						int spinnerVal = ((Integer)spinnerResizeStep.getValue()).intValue();
						
						container.resizeSelection(top, left, bottom, right, spinnerVal);
					}
				}
		);
	}
	

	/**
	 * enable the active components on this panel
	 * @param enabled true to enable them, false to disable
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
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
	}
}