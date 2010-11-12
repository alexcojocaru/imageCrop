package com.alexalecu.imageUtil;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageRotate {

	public final static int ROTATE_BOUNDING_BOX_EXACT = 0;
	public final static int ROTATE_BOUNDING_BOX_LARGEST = 1;
	public final static int ROTATE_BOUNDING_BOX_OPTIMAL = 2;
	
	/**
	 * Rotates the specified image the specified number of degrees.  The 
	 * rotation is performed around the center point of the image.
	 * 
	 * @param  img         the image to rotate
	 * @param  radians     the radians to rotate
	 * @param  bbm         the bounding box mode, default is EXACT_BOUNDING_BOX
	 * @param  background  the background paint (texture, color or gradient), 
	 *                     can be null
	 * @return  the image
	 */
	public static BufferedImage rotateDegrees(BufferedImage img, double degrees,
			int bbm, Paint background) {
		return rotateRadians(img, Math.toRadians(degrees), bbm, background);
	}
	
	/**
	 * Rotates the specified image the specified number of radians.  The 
	 * rotation is performed around the center point of the image.  This 
	 * method is provided for convenience of applications using radians.  
	 * For most people, degrees is simpler to use.  
	 * 
	 * @param  img         the image to rotate
	 * @param  radians     the radians to rotate
	 * @param  bbm         the bounding box mode, default is EXACT_BOUNDING_BOX
	 * @param  background  the background paint (texture, color or gradient), 
	 *                     can be null
	 * @return  the image
	 */
	public static BufferedImage rotateRadians(BufferedImage img, double radians,
			int bbm, Paint background) {
		
		// get the original image's width and height
		int iw = img.getWidth();
		int ih = img.getHeight();
		
		// calculate the new image's size based on bounding box mode
		Dimension dim;
		switch (bbm)
		{
			case ROTATE_BOUNDING_BOX_EXACT:
				dim = new Dimension(iw, ih);
				break;
			case ROTATE_BOUNDING_BOX_LARGEST:
				dim = getLargestBoundingBox(iw, ih);
				break;
			default:
				dim = getBoundingBox(iw, ih, Math.toDegrees(radians));
		}
		
		// get the new image's width and height
		int w = dim.width;
		int h = dim.height;
		
		// get the location to draw the original image on the new image
		int x = (w/2) - (iw/2);
		int y = (h/2) - (ih/2);
		
		// need to copy the given image to a new BufferedImage because 
		// it is, in most cases, going to be a larger image so it 
		// needs to be drawn centered on the larger image
		BufferedImage bi = new BufferedImage(w, h, img.getType());
		Graphics2D g2d = bi.createGraphics();
		
		// set some rendering hints for better looking images
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		// draw the background paint, if necessary
		if (background != null) {
			Paint origPaint = g2d.getPaint();
			g2d.setPaint(background);
			g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
			g2d.setPaint(origPaint);
		}
		
		// if not rotating, just draw it normally, else create a transform
		if (radians == 0.0) {
			g2d.drawImage(img, x, y, iw, ih, null);
		}
		else {
			g2d.rotate(radians, w/2, h/2);
			g2d.translate(x, y);
			g2d.drawImage(img, 0, 0, iw, ih, null);
		}
		g2d.dispose();
		return bi;
	} 
 
	/**
	 * Gets the largest bounding box size that can hold an image of the 
	 * specified size at any angle of rotation.  
	 * 
	 * @param  width   the image width
	 * @param  height  the image height
	 * @return  the bounding box size
	 */
	public static Dimension getLargestBoundingBox(int width, int height) {
		double a = (double)width / 2.0;
		double b = (double)height / 2.0;
		// use Math.ceil() to round up to an int value
		int c = (int)Math.ceil(
				(Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2))) * 2.0);
		return new Dimension(c, c);
	}
 
	/**
	 * Gets the optimal/smallest bounding box size that can hold an image of 
	 * the specified size at the specified angle of rotation.  
	 * 
	 * @param  width   the image width
	 * @param  height  the image height
	 * @return  the bounding box size
	 */
	public static Dimension getBoundingBox(int width, int height,
			double degrees) {
		degrees = normalizeDegrees(degrees);
		
		// if no rotation or 180 degrees, the size won't change
		if (degrees == 0.0 || degrees == 180.0) {
			return new Dimension(width, height);
		}
		
		// if 90 or 270 (quarter or 3-quarter rotations) the width becomes 
		// the height, and vice versa
		if (degrees == 90.0 || degrees == 270.0) {
			return new Dimension(height, width);
		}
		
		// for any other rotation, we need to do some trigonometry, 
		// derived from description found at:  
		// http://www.codeproject.com/csharp/rotateimage.asp
		double radians = Math.toRadians(degrees);
		double aW = Math.abs(Math.cos(radians) * width);
		double oW = Math.abs(Math.sin(radians) * width);
		double aH = Math.abs(Math.cos(radians) * height);
		double oH = Math.abs(Math.sin(radians) * height);
		
		// use Math.ceil() to round up to an int value
		int w = (int)Math.ceil(aW + oH);
		int h = (int)Math.ceil(oW + aH);
		
		return new Dimension(w, h);
	}
 
	/**
	 * Normalize the degrees value to be (0 <= d < 360) by rolling up values 
	 * less then 0 or rolling down values greater than, or equal to 360.  
	 * 
	 * @param  degrees  the degrees
	 * @return  the normalized degrees
	 */
	public static double normalizeDegrees(double degrees) {
		if (degrees < 0.0) {
			while (degrees < 0.0) {
				degrees += 360.0;
			}
		}
		else if (degrees >= 360.0) {
			while (degrees >= 360.0) {
				degrees -= 360.0;
			}
		}
		return degrees;
	}
}
