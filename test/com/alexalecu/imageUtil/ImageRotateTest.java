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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.alexalecu.imageUtil.ImageRotate.RotateBoundingBox;

/**
 * @author Alex Cojocaru
 *
 */
public class ImageRotateTest {

	@Test
	public void testRotateDegrees() throws IOException {
		RotateBoundingBox[] bbms = new RotateBoundingBox[] {
				RotateBoundingBox.ROTATE_BOUNDING_BOX_EXACT,
				RotateBoundingBox.ROTATE_BOUNDING_BOX_LARGEST,
				RotateBoundingBox.ROTATE_BOUNDING_BOX_OPTIMAL
		};
		
		for (RotateBoundingBox bbm : bbms) {
			InputStream is = new FileInputStream("test/resources/test1.png");
			BufferedImage img = ImageConvert.read(is);
			img = ImageRotate.rotateDegrees(img, 38, bbm, Color.black);
			byte[] b = ImageConvert.toByteArray(img);
			
			InputStream is38deg = new FileInputStream(
					"test/resources/test1-rotated-38deg-" + bbm + ".png");
			BufferedImage img38deg = ImageConvert.read(is38deg);
			byte[] b38deg = ImageConvert.toByteArray(img38deg);

			Assert.assertTrue("The rotated image doesn't match expectations for BoundingBox"
					+ bbm, Arrays.equals(b, b38deg));
		}
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
