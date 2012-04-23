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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Alex Cojocaru
 *
 */
public class ImageKitTest {

	@Test
	public void testAutoSelectBoundingRectangleMax() throws IOException {
		InputStream is = new FileInputStream("test/resources/test1.png");

		BufferedImage image = ImageConvert.read(is);
		Rectangle container = new Rectangle(25, 25, 150, 50);
		Color bgColor = Color.white;
		int bgTol = 10;
		int nrMatches = -1;
		Object[] result = ImageKit.autoSelectBoundingRectangle(
				image, container, bgColor, bgTol, nrMatches);
		
		Rectangle crop = (Rectangle)result[0];
		Assert.assertEquals("Computed rectangle is wrong", new Rectangle(40, 40, 131, 21), crop);
		
		@SuppressWarnings("unchecked")
		ArrayList<GeomEdge> edges = (ArrayList<GeomEdge>)result[1];
		verifyEdges(edges);
	}

	@Test
	public void testAutoSelectBoundingRectangleMin() throws IOException {
		InputStream is = new FileInputStream("test/resources/test1.png");

		BufferedImage image = ImageConvert.read(is);
		Rectangle container = new Rectangle(25, 25, 150, 50);
		Color bgColor = Color.white;
		int bgTol = 10;
		int nrMatches = 5;
		Object[] result = ImageKit.autoSelectBoundingRectangle(
				image, container, bgColor, bgTol, nrMatches);

		Rectangle crop = (Rectangle)result[0];
		Assert.assertEquals("Computed rectangle is wrong", new Rectangle(61, 40, 88, 20), crop);

		@SuppressWarnings("unchecked")
		ArrayList<GeomEdge> edges = (ArrayList<GeomEdge>)result[1];
		verifyEdges(edges);
	}
	
	private void verifyEdges(ArrayList<GeomEdge> edges) {
		Assert.assertEquals("Wrong edge #1", new GeomEdge(41, 40, 40, 40), edges.get(0));
		Assert.assertEquals("Wrong edge #2", new GeomEdge(40, 40, 60, 60), edges.get(1));
		Assert.assertEquals("Wrong edge #3", new GeomEdge(60, 60, 170, 60), edges.get(2));
		Assert.assertEquals("Wrong edge #4", new GeomEdge(170, 60, 150, 40), edges.get(3));
		Assert.assertEquals("Wrong edge #5", new GeomEdge(150, 40, 41, 40), edges.get(4));
	}
	
	@Test
	public void testGetVertices() {
	}
	
	@Test
	public void testGetEnvelopePoints() {
	}
}
