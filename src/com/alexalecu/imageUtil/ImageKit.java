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

package com.alexalecu.imageUtil;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import javax.imageio.*;

import com.alexalecu.imageUtil.ConvexHull;
import com.alexalecu.imageUtil.GeomPoint;
import com.alexalecu.util.FileUtil;

public class ImageKit {
	static {
		ImageIO.setUseCache(false);
	}
	
	
	/**
	 * compute the rectangle which is the optimized solution for cropping the
	 * source BufferedImage 
	 * @param bi the source BufferedImage
	 * @param cropRectangle the rectangle containing the area to scan
	 * @param bgColor the background color
	 * @param bgTol the tolerance to be used for matching the background color,
	 * between 0 and 255 inclusive
	 * @param nrMatches if -1 or if >= the width or height of the maximum
	 * rectangle, then the max rectangle is computed, otherwise the min one
	 * @return an array containing two Objects; the first one is the resulting Rectangle,
	 * while the 2nd object is an ArrayList containing the polygon edges
	 */
	public static Object[] autoSelectBoundingRectangle(BufferedImage bi,
			Rectangle cropRectangle, Color bgColor, int bgTol, int nrMatches) {
		
		// compute the coordinates of the minimum rectangle which accommodates
		// the whole image
		Rectangle maxRect = getMinBoundingRectangle(bi, cropRectangle, bgColor, bgTol);
		
		// cut just the section that concerns me
		BufferedImage biw = ImageConvert.cropImageNew(bi, maxRect);
		
		// convert the image to 2 color only:
		// the background area to background color
		// the rest to the color opposite to the background one
		Color fgColor = new Color(
				255 - bgColor.getRed(), 
				255 - bgColor.getGreen(), 
				255 - bgColor.getBlue());
		ImageColors.reduceColors(biw, new Rectangle(0, 0, biw.getWidth(), biw.getHeight()), 
	    		bgColor, bgTol, fgColor);
	    
	    /*
		try {writeJpg(biw, -1f, new FileOutputStream("C:\\aa0.jpg"));}
		catch (Exception e) {}
		
	    
	    // apply the 4 filters for computing the edges
		GreyscaleFilter s1 = new GreyscaleFilter();
	    biw = s1.filter(biw);

        try {writeJpg(biw, -1.00f, new FileOutputStream("C:\\aa1.jpg"));}
		catch (Exception e) {}
		
		SobelEdgeDetectorFilter s2 = new SobelEdgeDetectorFilter();
		biw = s2.filter(biw, null, true);

		try {writeJpg(biw, -1.00f, new FileOutputStream("C:\\aa2.jpg"));}
		catch (Exception e) {}

	    int bgGray = GreyscaleFilter.calculateGrey(
	    		bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 
				s1.getGreyscaleType());
	    int foreGray = 255 - bgGray;
	    
	    ThresholdFilter s3 = new ThresholdFilter();
	    s3.setThresholdLimit(bgGray);
	    biw = s3.filter(biw);
	    
	    LineHoughTransformOp s4 = new LineHoughTransformOp();
        s4.setLocalPeakNeighbourhood(7); // 0 .. 20
        s4.run(biw);
        ArrayList edges = s4.getEdges(biw, 0.25d); // 0.00d .. 1.00 d
	    
        int[] edge;
        for (int i = 0; i < edges.size(); i++) {
        	edge = (int[])edges.get(i);
        	edges.set(i, new GeomEdge(edge[0] + x1, edge[1] + y1, 
        			edge[2] + x1, edge[3] + y1));
		}
        */
		
		ConvexHull polygon = new ConvexHull();
        Rectangle polygonRect;
		
		// compute the polygon vertices and shift their coordinates
		List<GeomPoint> vertices = getVertices(biw, bgColor);
        for (int i = 0; i < vertices.size(); i++) {
        	GeomPoint p = vertices.get(i);
        	p.setX(p.getX() + maxRect.x);
        	p.setY(p.getY() + maxRect.y);
        	polygon.addPoint(p);
        }
        
		// if the minimum rectangle calculation is desired...
		if (nrMatches > -1 && maxRect.width > nrMatches &&
				maxRect.height > nrMatches) {
			polygon.computeLargestRectangle();
			polygonRect = new Rectangle(polygon.rectp.getX(),
					polygon.rectp.getY(),
					polygon.rectw, polygon.recth);
		}
		else {
			polygonRect = new Rectangle(maxRect.x, maxRect.y,
					maxRect.width, maxRect.height);
			polygon.computeEdgeList();
		}

		return new Object[] {polygonRect, polygon.edgeList};
	}
	
	
	/**
	 * compute the coordinates of the minimum rectangle which accommodates the
	 * whole image
	 * @param bi the BufferedImage containing the image to scan for
	 * @param cropRectangle the rectangle containing the area to scan
	 * @param x the X coordinate where to start cropping
	 * @param y the X coordinate where to start cropping
	 * @param weight the width of the cropping area
	 * @param height the height of the cropping area
	 * @param bgColor the background color
	 * @param bgTol the background color tolerance
	 * @return the minimum rectangle which contains the whole image
	 */
	private static Rectangle getMinBoundingRectangle(BufferedImage bi,
			Rectangle cropRectangle, Color bgColor, int bgTol) {

		// initialize some local variables
		int left = cropRectangle.x;
		int right = cropRectangle.x + cropRectangle.width - 1;
		int top = cropRectangle.y;
		int bottom = cropRectangle.y + cropRectangle.height - 1;
		
		boolean loopL = true, loopR = true, loopT = true, loopB = true;
		byte directionL = 0, directionR = 0, directionT = 0, directionB = 0;
		int prevL, prevR, prevT, prevB;
		
		// keep processing till no edge can be moved any more
		while (loopL || loopR || loopT || loopB) {
			prevL = left;
			while (loopL) {
				if (ImageColors.isBgColor(bi, left, true, top, bottom, bgColor, bgTol)) {
					// stop if the previous move was backwards or not enough room
					// and move the left forward only if the right is far enough
					if (directionL != -1 && left < right - 1) {
						directionL = 1;
						left++;
					}
					else {
						if (left < right - 1)
							left++;
						loopL = false;
					}
				}
				else {
					// if the left has not moved forward during this step and
					// we're on non-bg color, move it backwards and scan again
					if (directionL != 1 && left > 0) {
						directionL = -1;
						left--;
					}
					else {
						loopL = false;
					}
				}
			}

			prevR = right;
			while (loopR) {
				if (ImageColors.isBgColor(bi, right, true, top, bottom, bgColor, bgTol)) {
					if (directionR != 1 && left < right - 1) {
						directionR = -1;
						right--;
					}
					else {
						if (left < right - 1)
							right--;
						else if (right < bi.getWidth() - 1)
							right++;
						loopR = false;
					}
				}
				else {
					if (directionR != -1 && right < bi.getWidth() - 1) {
						directionR = 1;
						right++;
					}
					else {
						loopR = false;
					}
				}
			}

			// if the left or right edge have changed, make sure we process
			// the top and bottom too
			if (prevL != left || prevR != right) {
				if (!loopT) {
					directionT = 0;
					loopT = true;
				}
				if (!loopB) {
					directionB = 0;
					loopB = true;
				}
			}
			
			prevT = top;
			while (loopT) {
				if (ImageColors.isBgColor(bi, top, false, left, right, bgColor, bgTol)) {
					if (directionT != -1 && top < bottom - 1) {
						directionT = 1;
						top++;
					}
					else {
						if (top < bottom - 1)
							top++;
						loopT = false;
					}
				}
				else {
					if (directionT != 1 && top > 0) {
						directionT = -1;
						top--;
					}
					else {
						loopT = false;
					}
				}
			}
			
			prevB = bottom;
			while (loopB) {
				if (ImageColors.isBgColor(bi, bottom, false, left, right, bgColor, bgTol)) {
					if (directionB != 1 && top < bottom - 1) {
						directionB = -1;
						bottom--;
					}
					else {
						if (top < bottom - 1)
							bottom--;
						else if (bottom < bi.getHeight() - 1)
							bottom++;
						loopB = false;
					}
				}
				else {
					if (directionB != -1 && bottom < bi.getHeight() - 1) {
						directionB = 1;
						bottom++;
					}
					else {
						loopB = false;
					}
				}
			}

			// if the top or bottom edge have changed, make sure we process
			// the left and right too
			if (prevT != top || prevB != bottom) {
				if (!loopL) {
					directionL = 0;
					loopL = true;
				}
				if (!loopR) {
					directionR = 0;
					loopR = true;
				}
			}
		}
		
		return new Rectangle(left, top, right - left + 1, bottom - top + 1);
	}
	
	/**
	 * Scan the hull located on the image and find the hull vertices
	 * @param bi the BufferedImage containing the hull of color != bgColor
	 * @param bgColor the background color of the image
	 * @return a list of GeomPoint objects representing the hull vertices
	 */
	public static List<GeomPoint> getVertices(BufferedImage bi, Color bgColor) {
		
		Stack<GeomPoint> vertices = new Stack<GeomPoint>();
		
		// scan the image to find the hull envelope points
		List<GeomPoint> points = getEnvelopePoints(bi,
				new Rectangle(0, 0, bi.getWidth(), bi.getHeight()), bgColor, 0);
		if (points.isEmpty())
			return vertices;
		// convert to a collection more suitable for our purposes
		points = new ArrayList<GeomPoint>(points);
		
		GeomPoint pOrig = points.get(0);
		int offsetX = pOrig.getX();
		int offsetY = pOrig.getY();
		
		// shift the axis origin to the top left point, update all coordinates
		for (int i = 0; i < points.size(); i++) {
			GeomPoint p = points.get(i);
			p.setX(p.getX() - offsetX);
			p.setY(p.getY() - offsetY);
		}
		
		// scan the envelope point list and remove those located on the same
		// edge
		double tanPrv = 0d;
		double tanCrt;
		vertices.push(pOrig);
		for (int i = 1; i < points.size(); i++) {
			GeomPoint p = points.get(i);
			tanCrt = ((double)p.getY() - vertices.peek().getY()) /
					(p.getX() - vertices.peek().getX());
			
			// if the previous tangent is not the same as the current one, we
			// are on a new edge, so lets update the variables
			if (tanPrv != tanCrt || i == 1) {
				tanPrv = tanCrt;
				vertices.add(p);
			}
			else {
				// same edge here, update the last vertex to the current point
				vertices.pop();
				vertices.push(p);
			}
		}
		
		// shift the axis origin back, update the vertex coordinates
		for (int i = 0; i < vertices.size(); i++) {
			GeomPoint p = vertices.get(i);
			p.setX(p.getX() + offsetX);
			p.setY(p.getY() + offsetY);
		}
		
		return vertices;
	}
	
	/**
	 * Return a list containing all the points located on the hull envelope,
	 * starting with the top left one and going counter-clockwise on the hull
	 * @param bi the BufferedImage to scan
	 * @param boundingRect the bounding rectangle containing the area to scan
	 * @param bgColor the background color to search for
	 * @param bgTol the tolerance used when trying to match the background color
	 * @return an ArrayList of GeomPoint objects representing the hull vertices
	 */
	public static List<GeomPoint> getEnvelopePoints(BufferedImage bi,
			Rectangle boundingRect, Color bgColor, int bgTol) {

		// set up some helper properties
		int startX = boundingRect.x;
		int startY = boundingRect.y;
		int endX = boundingRect.x + boundingRect.width - 1;
		int endY = boundingRect.y + boundingRect.height - 1;
		
		// the list containing the points on the left side of the hull
		Stack<GeomPoint> pointsL = new Stack<GeomPoint>();
		// the list containing the points on the top, right and bottom sides
		Stack<GeomPoint> pointsR = new Stack<GeomPoint>();
		
		int[] marginsPrev = null;
		boolean breakOut = false;
		
		for (int y = startY; y <= endY + 1; y++) {
			// find the limits of the non-bg color
			int[] margins = ImageColors.getColorMargins(bi,
					y, false, startX, endX, bgColor, bgTol);
			
			// if no limits were found, the whole line is bg color
			if (margins[0] == -1 || margins[1] == -1) {
				// continue scanning if lines containing non-bg color were not
				// found already
				if (!breakOut)
					continue;
				
				// the last found line (which contains non-bg color) represents
				// the bottom edge of the hull, so let's add the points to the
				// envelope point list, taking into account the direction
				if (!pointsR.empty()) {
					pointsR.pop();
					for (int x = marginsPrev[0] + 1; x <= marginsPrev[1]; x++)
						pointsL.push(new GeomPoint(x, y - 1));
				}
				
				// exit the loop as the hull has been scanned successfully
				break;
			}
			
			// if it's the first line containing non-bg color found, then it is
			// the top edge of the hull, so add all its points to the list
			if (!breakOut) {
				breakOut = true;
				pointsL.push(new GeomPoint(margins[0], y));
				for (int x = margins[0] + 1; x <= margins[1]; x++)
					pointsR.push(new GeomPoint(x, y));
			}
			// otherwise add the leftmost pixel to the left list, the rightmost
			// pixel to the right list
			else {
				pointsL.push(new GeomPoint(margins[0], y));
				pointsR.push(new GeomPoint(margins[1], y));
			}
			
			marginsPrev = margins;
		}
		
		// merge the lists
		while (pointsR.size() > 0)
			pointsL.push(pointsR.pop());
		
		return pointsL;
	}
	
	
	public static void main(String args[]) {
//		try {
//			BufferedImage img = read(new FileInputStream("D:\\temp\\crux_al_small.jpg"));
//			BufferedImage img0 = rotateDegrees(img, 45d, ROTATE_BOUNDING_BOX_EXACT, null);
//			writeJpg(img0, new FileOutputStream("D:\\temp\\licenta2_0.jpg"));
//			BufferedImage img1 = rotateDegrees(img, 45d, ROTATE_BOUNDING_BOX_LARGEST, null);
//			writeJpg(img1, new FileOutputStream("D:\\temp\\licenta2_1.jpg"));
//			BufferedImage img2 = rotateDegrees(img, 45d, ROTATE_BOUNDING_BOX_OPTIMAL, null);
//			writeJpg(img2, new FileOutputStream("D:\\temp\\crux_al_small_2.jpg"));

//			BufferedImage img = read(new FileInputStream("D:\\temp\\crux_al_small_2.jpg"));		
//			int corners[] = cropRectangle(img, 76, 100, 104, 61, Color.BLACK, 0, 0, 0, 3);
//			System.out.println(corners[0] + "-" + corners[1] + "-" + corners[2] + "-" + corners[3]);
//		}
//		catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public static void testSave(BufferedImage image) {
		String dirPath = "/home/alex/Desktop";
		
		// create the file name; add an unique 3-digit number suffix to make sure the name is unique
		String imgName = "testCrop.jpg";
		imgName = FileUtil.generateUniqueFileName(dirPath, imgName, 3);

		try {
			ImageConvert.writeJpg(image, new FileOutputStream(new File(dirPath, imgName)));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
