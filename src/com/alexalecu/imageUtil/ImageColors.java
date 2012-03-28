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
import java.awt.image.BufferedImage;

/**
 * Class containing methods to manipulate the BufferedImage colors
 */
public class ImageColors {

	/**
	 * @param bi the BufferedImage to read the pixel color from
	 * @param x the X coordinate of the point to read the color from
	 * @param y the Y coordinate of the point to read the color from
	 * @return the color of the pixel at (x, y)
	 */
	public static Color getPixelColor(BufferedImage bi, int x, int y) {
		return new Color(bi.getRGB(x, y));
	}
	
	/**
	 * try to match two colors given a tolerance between them
	 * @param colorExpected the color to match against
	 * @param colorActual the color to match
	 * @param tolerance the tolerance on each of the (red, green, blue) between
	 * the two colors
	 * @return true if the two colors match, false otherwise
	 */
	public static boolean colorMatch(Color colorExpected, Color colorActual, 
			int tolerance) {
		return colorMatch(
				colorExpected.getRed(), colorExpected.getGreen(), colorExpected.getBlue(),
				colorActual.getRed(), colorActual.getGreen(), colorActual.getBlue(),
				tolerance);
	}

	/**
	 * try to match two colors given a tolerance between them
	 * @param redExpected the red value of the first color
	 * @param greenExpected the green value of the first color
	 * @param blueExpected the blue value of the first color
	 * @param redActual the red value of the second color
	 * @param greenActual the green value of the second color
	 * @param blueActual the blue value of the second color
	 * @param tolerance the tolerance on each of the (red, green, blue) between
	 * the two colors
	 * @return true if the two colors match, false otherwise
	 */
	public static boolean colorMatch(int redExpected, int greenExpected, int blueExpected,
			int redActual, int greenActual, int blueActual, 
			int tolerance) {
		return redActual >= redExpected - tolerance &&
				redActual <= redExpected + tolerance &&
				greenActual >= greenExpected - tolerance &&
				greenActual <= greenExpected + tolerance &&
				blueActual >= blueExpected - tolerance &&
				blueActual <= blueExpected + tolerance;
	}
	
	/**
	 * Verify if the pixel at position (x, y) matches the bgColor
	 * (taking the tolerance into account)
	 * @param bi the image containing the pixel to verify
	 * @param x the x coord of the pixel to verify
	 * @param y the y coord of the pixel to verify
	 * @param bgColor the bg color to match against
	 * @param tolerance the bg color tolerance to apply
	 * @return true if the pixel matches the bg color
	 */
	public static boolean isBgColor(BufferedImage bi, int x, int y, Color bgColor, int tolerance) {
		Color color = getPixelColor(bi, x, y);
		return colorMatch(bgColor, color, tolerance);
	}
	
	/**
	 * checks if the whole line is bg color
	 * @param bi the BufferedImage to scan
	 * @param lineCoord the x or y coordinate of the line to scan - use the
	 * isVerticalLine parameter to decide if it is the x or y coordinate
	 * @param isVerticalLine true if scanning is done on the vertical,
	 * false for horizontal scanning
	 * @param startCoord the start coordinate to start the scan from on the
	 * current line
	 * @param endCoord the end coordinate to end the scan to on the
	 * current line
	 * @param bgColor the background color to search for
	 * @param bgTol the background color tolerance to use when trying to match
	 * the background color
	 * @return true if the whole line is bg color
	 */
	public static boolean isBgColor(BufferedImage bi, 
			int lineCoord, boolean isVerticalLine,
			int startCoord, int endCoord,
			Color bgColor, int bgTol) {
		
		for (int i = startCoord; i <= endCoord; i++) {
			Color pixelColor = isVerticalLine 
					? getPixelColor(bi, lineCoord, i)
					: getPixelColor(bi, i, lineCoord);
			if (!colorMatch(pixelColor, bgColor, bgTol))
				return false;
		}
		return true;
	}
	
	/**
	 * @param bi the BufferedImage to scan
	 * @param lineCoord the x or y coordinate of the line to scan - use the
	 * isVerticalLine parameter to decide if it is the x or y coordinate
	 * @param isVerticalLine true if scanning is done on the vertical,
	 * false for horizontal scanning
	 * @param startCoord the start coordinate to start the scan from on the
	 * current line
	 * @param endCoord the end coordinate to end the scan to on the
	 * current line
	 * @param bgColor the background color to search for
	 * @param bgTol the background color tolerance to use when trying to match
	 * the background color
	 * @return the start and end coordinates of the color zone for the given
	 * line
	 */
	public static int[] getColorMargins(BufferedImage bi, 
			int lineCoord, boolean isVerticalLine,
			int startCoord, int endCoord,
			Color bgColor, int bgTol) {
		
		int[] res = {-1, -1};
		
		// check the input coordinates
		if (lineCoord < 0)
			return res;
		if (isVerticalLine && lineCoord >= bi.getWidth())
			return res;
		if (!isVerticalLine && lineCoord >= bi.getHeight())
			return res;
		
		// scan the line from the start point to the end point, looking for the
		// first pixel not matching the bg color 
		for (int i = startCoord; i <= endCoord; i++) {
			Color pixelColor = isVerticalLine 
					? getPixelColor(bi, lineCoord, i)
					: getPixelColor(bi, i, lineCoord);
			if (!colorMatch(pixelColor, bgColor, bgTol)) {
				res[0] = i;
				break;
			}
		}
		
		// if no bg color pixel was found yet, then the whole line is non-bg
		if (res[0] == -1)
			return res;
		
		// now start the the end point to the just found point, looking for the
		// first pixel not matching the bg color
		for (int i = endCoord; i > startCoord; i--) {
			Color pixelColor = isVerticalLine 
					? getPixelColor(bi, lineCoord, i)
					: getPixelColor(bi, i, lineCoord);
			if (!colorMatch(pixelColor, bgColor, bgTol)) {
				res[1] = i;
				break;
			}
		}
		
		return res;
	}
	
	/**
	 * all pixels which do not match the bg color are converted to the fg color;
	 * the conversion is applied only within the bounding rectangle 
	 * @param bi the BufferedImage to be converted
	 * @param boundingRect the bounding rectangle where the conversion is
	 * applied
	 * @param bgColor the background color to search for
	 * @param bgTol the tolerance used when trying to match the background color
	 * @param fgColor the color to which the pixels not matching the bg color
	 * are converted to
	 */
	public static void reduceColors(BufferedImage bi, Rectangle boundingRect,
			Color bgColor, int bgTol, Color fgColor) {
		
		// define some easier to use variables
		int startX = boundingRect.x;
		int startY = boundingRect.y;
		int endX = boundingRect.x + boundingRect.width - 1;
		int endY = boundingRect.y + boundingRect.height - 1;
		
		// scan the image on the vertical, from the left edge of the bounding
		// rectangle to the right edge of it, looking for pixels not matching
		// the bg color and converting them to the fg color
		for (int j = startY; j <= endY; j++) {
			
			// get the start and the end coordinates on the current horizontal
			// line where the non-background color zone is located
			int res[] = getColorMargins(bi, j, false, startX, endX,
					bgColor, bgTol);
			
			// if no coordinates have been found, the whole line is bg
			// color; convert it to bg color
			if (res[0] == -1 && res[1] == -1) {
				for (int i = startX; i <= endX; i++)
					bi.setRGB(i, j, bgColor.getRGB());
			}
			else {
				// both res[0] and res[1] are > -1 in this case
				// the first and last sections are bg, the middle one is fg 
				for (int i = startX; i < res[0]; i++)
					bi.setRGB(i, j, bgColor.getRGB());
				for (int i = res[0]; i <= res[1]; i++)
					bi.setRGB(i, j, fgColor.getRGB());
				for (int i = res[1] + 1; i <= endX; i++)
					bi.setRGB(i, j, bgColor.getRGB());
			}
		}
	}
}
