/*
 * Copyright (C) 2012 Alex Cojocaru
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
package com.alexalecu.imageUtil;

import java.awt.Dimension;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alex Cojocaru
 *
 */
public class ImageRotateTest {

	@Test
	public void testRotateRadians() {
	}

	@Test
	public void testGetLargestBoundingBox() {
		Dimension box = ImageRotate.getLargestBoundingBox(50, 30);
		Assert.assertEquals("", new Dimension(59, 59), box);

		box = ImageRotate.getLargestBoundingBox(3, 4);
		Assert.assertEquals("", new Dimension(5, 5), box);
	}
	
	@Test
	public void testGetBoundingBox() {
		Dimension box = ImageRotate.getBoundingBox(50, 30, 90);
		Assert.assertEquals("", new Dimension(30, 50), box);

		box = ImageRotate.getBoundingBox(133, 1444, 30);
		Assert.assertEquals("", new Dimension(838, 1318), box);

		box = ImageRotate.getBoundingBox(3, 4, 180);
		Assert.assertEquals("", new Dimension(3, 4), box);
	}
	
	@Test
	public void testNormalizeDegrees() {
		int normalized = (int)ImageRotate.normalizeDegrees(374);
		Assert.assertEquals("Wrong degrees normalization", 14, normalized);

		normalized = (int)ImageRotate.normalizeDegrees(-1);
		Assert.assertEquals("Wrong degrees normalization", 359, normalized);
	}
	
}
