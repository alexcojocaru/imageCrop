package com.alexalecu.imageUtil;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Class containing methods to manipulate the BufferedImage colors
 * @author alex
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
	 * @param color the color to match against
	 * @param color2Match the color to match
	 * @param tolerance the tolerance on each of the (red, green, blue) between
	 * the two colors
	 * @return true if the two colors match, false otherwise
	 */
	public static boolean colorMatch(Color color, Color color2Match, 
			int tolerance) {
		return colorMatch(
				color.getRed(), color.getGreen(), color.getBlue(),
				color2Match.getRed(), color2Match.getGreen(),
				color2Match.getBlue(),
				tolerance);
	}

	/**
	 * try to match two colors given a tolerance between them
	 * @param r the red value of the first color
	 * @param g the green value of the first color
	 * @param b the blue value of the first color
	 * @param r2Match the red value of the second color
	 * @param g2Match the green value of the second color
	 * @param b2Match the blue value of the second color
	 * @param tolerance the tolerance on each of the (red, green, blue) between
	 * the two colors
	 * @return true if the two colors match, false otherwise
	 */
	public static boolean colorMatch(int r, int g, int b,
			int r2Match, int g2Match, int b2Match, 
			int tolerance) {
		return r >= r2Match - tolerance && r <= r2Match + tolerance &&
				g >= g2Match - tolerance && g <= g2Match + tolerance &&
				b >= b2Match - tolerance && b <= b2Match + tolerance;
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
