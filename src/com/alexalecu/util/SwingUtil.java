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
