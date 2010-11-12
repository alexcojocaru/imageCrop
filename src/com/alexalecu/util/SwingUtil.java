package com.alexalecu.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;

public class SwingUtil {
	
	/**
	 * helper method to create a GridBagConstrained configured according to the parameters
	 * @param x the gridx value
	 * @param y the gridy value
	 * @param anchor the value of the anchor property
	 * @param insets the insets to be used by this constraint
	 * @return a GridBagConstraint object
	 */
	public static GridBagConstraints getGridBagConstraint(int x, int y, int anchor, Insets insets) {
		return getGridBagConstraint(x, y, 1, 1, anchor, insets);
	}
	
	/**
	 * helper method to create a GridBagConstrained configured according to the parameters
	 * @param x the gridx value
	 * @param y the gridy value
	 * @param gridwidth the gridwidth value
	 * @param gridheight the gridheight value
	 * @param anchor the value of the anchor property
	 * @param insets the insets to be used by this constraint
	 * @return a GridBagConstraint object
	 */
	public static GridBagConstraints getGridBagConstraint(int x, int y,
			int gridwidth, int gridheight, int anchor, Insets insets) {

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = x;
		gridBagConstraints.gridy = y;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.anchor = anchor;
		gridBagConstraints.insets = insets;
		return gridBagConstraints;
	}


	/**
	 * set the component's minimum, maximum and preferred sizes
	 * @param component the component to set the sizes on
	 * @param width the width to be set
	 * @param height the height to be set
	 */
	public static void setAllSizes(JComponent component, int width, int height) {
		Dimension dim = new Dimension(width, height);
		component.setMinimumSize(dim);
		component.setMaximumSize(dim);
		component.setPreferredSize(dim);
	}

}
