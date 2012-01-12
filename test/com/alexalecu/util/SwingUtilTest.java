package com.alexalecu.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JButton;

import org.junit.Assert;
import org.junit.Test;

public class SwingUtilTest {

	@Test
	public void testGetGridBagConstraint() {
		Insets insets = new Insets(1, 9, 4, 2);
		GridBagConstraints gbc = SwingUtil.getGridBagConstraint(6, 11, 3, 19, 99, insets);
		Assert.assertEquals("gridx doesn't match", 6, gbc.gridx);
		Assert.assertEquals("gridy doesn't match", 11, gbc.gridy);
		Assert.assertEquals("gridwidth doesn't match", 3, gbc.gridwidth);
		Assert.assertEquals("gridheight doesn't match", 19, gbc.gridheight);
		Assert.assertEquals("anchor doesn't match", 99, gbc.anchor);
		Assert.assertEquals("insets don't match", insets, gbc.insets);
	}
	
	@Test
	public void setAllSizes() {
		JButton button = new JButton();
		Dimension dimension = new Dimension(12, 49);
		SwingUtil.setAllSizes(button, dimension.width, dimension.height);
		
		Assert.assertEquals("Minimum size doesn't match", dimension, button.getMinimumSize());
		Assert.assertEquals("Maximum size doesn't match", dimension, button.getMaximumSize());
		Assert.assertEquals("Preferred size doesn't match", dimension, button.getPreferredSize());
	}
}
