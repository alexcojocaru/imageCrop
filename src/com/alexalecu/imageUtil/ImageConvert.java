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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageConvert {
	static {
		ImageIO.setUseCache(false);
	}

	/**
	 * clone a buffered image
	 * @param img the image to be cloned
	 * @return the cloned image
	 */
	public static BufferedImage cloneImage(BufferedImage img) {
		BufferedImage out = new BufferedImage(
				img.getWidth(), img.getHeight(), img.getType());        
		out.setData(img.copyData(null));
		return out;
	}
	
	/**
	 * clone a JPG image
	 * @param img the image to be cloned
	 * @return the cloned JPG image
	 * @throws IOException if the image cannot be written to the temporary byte
	 * array
	 */
	public static BufferedImage cloneImageJpg(BufferedImage img) 
			throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageConvert.writeJpg(img, baos);
	    return ImageConvert.read(new ByteArrayInputStream(baos.toByteArray()));
	}
	
	
	/**
	 * read a BufferedImage from an imput stream;
	 * @param in the input stream containing the image
	 * @return the BufferedImage read from the stream
	 * @throws IOException if the input stream cannot be read
	 */
	public static BufferedImage read(InputStream in) throws IOException {
		BufferedImage image = ImageIO.read(in);
		if (image == null)
			throw new IOException("Read fails");
		return image;
	}

	/**
	 * read a BufferedImage from a byte array
	 * @param bytes the byte array containing the image
	 * @return the BufferedImage read from the byte array 
	 * @throws IOException if no image can be read
	 */
	public static BufferedImage read(byte[] bytes) throws IOException {
		try {
			return read(new ByteArrayInputStream(bytes));
		}
		catch (IOException e) {
			throw e;
		}
	}

	/**
	 * write a JPG image to an output stream
	 * @param image the BufferedImage to be written
	 * @param out the output stream to write to
	 * @throws IOException if the image cannot be written
	 */
	public static void writeJpg(BufferedImage image, OutputStream out) 
			throws IOException {
		writeJpg(image, -1, out);
	}

	/**
	 * write a JPG image to an ouput stream with a given quality
	 * @param image the BufferedImage to be written
	 * @param quality the jpeg output, if less than 0 the default quality is
	 * used
	 * @param out the output stream to write to
	 * @throws IOException if the image cannot be written
	 */
	public static void writeJpg(BufferedImage image, float quality, 
	        OutputStream out) throws IOException {
		// get the JPG writer
		Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
		if (!writers.hasNext())
			throw new IllegalStateException("No writers found");
		
		// use the first writer found
		ImageWriter writer = writers.next();
		
		ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		writer.setOutput(ios);
		ImageWriteParam param = null;
		
		// set the write quality if >= 0
		if (quality >= 0f) {
			param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
		}
		
		// and write the image, cleaning up the resources afterwards
		writer.write(null, new IIOImage(image, null, null), param);
		ios.flush();
		ios.close();
		writer.dispose();
	}

	/**
	 * write a PNG image to an ouput stream
	 * @param image the BufferedImage to be written
	 * @param out the output stream to write to
	 * @throws IOException if the image cannot be written
	 */
	public static void writePng(BufferedImage image, OutputStream out) throws IOException {
		// get the PNG writer
		Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("png");
		if (!writers.hasNext())
			throw new IllegalStateException("No writers found");
		
		// use the first writer found
		ImageWriter writer = writers.next();
		
		ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		writer.setOutput(ios);
		ImageWriteParam param = null;
		
		// and write the image, cleaning up the resources afterwards
		writer.write(null, new IIOImage(image, null, null), param);
		ios.flush();
		ios.close();
		writer.dispose();
	}

	/**
	 * write a BufferedImage as a JPG image to a byte array
	 * @param image the BufferedImage to be written
	 * @return the byte array containing the image
	 */
	public static byte[] toByteArray(BufferedImage image) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(50000);
		writeJpg(image, out);
		return out.toByteArray();
	}

	/**
	 * write a BufferedImage as a JPG image with a given quality to a byte array
	 * @param image the BufferedImage to be written
	 * @param quality the quality to be used for the JPG image
	 * @return the byte array containing the image
	 */
	public static byte[] toByteArray(BufferedImage image, float quality)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(50000);
		writeJpg(image, quality, out);
		return out.toByteArray();
	}

	/**
	 * compress a BufferedImage as JPG using the given quality
	 * @param image the Buffered image to be compressed
	 * @param quality the quality to be used for the JPG image
	 * @return the compressed BufferedImage
	 * @throws IOException if the image cannot be converted
	 */
	public static BufferedImage compress(BufferedImage image, float quality) 
			throws IOException {
		return read(toByteArray(image, quality));
	}

	/*
	public static void flush(BufferedImage image) {
		try {
			if (image != null)
				image.flush();
		}
		catch (NullPointerException e) {
			//bug in sun's code
		}

	}
	*/
	

	/**
	 * convert the BufferedImage to the specified type
	 * @param src the BufferedImage to be converted
	 * @param targetType to type of convert to 
	 * @return a BufferedImage of type targetType
	 */
	public static BufferedImage convertType(BufferedImage src, int targetType) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		return op.filter(src, null);
	}
	
	/**
	 * resize a BufferedImage
	 * @param source the BufferedImage to be resized
	 * @param targetW the width of the result image
	 * @param targetH the height of the result image
	 * @return a BufferedImage of size targetW x targetH
	 */
	public static BufferedImage resize(BufferedImage source, 
			int targetW, int targetH) {
		int type = source.getType();
		BufferedImage target = null;
		
		// initialize the target image based on the input image type
		if (type == BufferedImage.TYPE_CUSTOM) { //handmade
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(
					targetW, targetH);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		}
		else {
			target = new BufferedImage(targetW, targetH, type);
		}
		
		Graphics2D g = target.createGraphics();
		//smoother than exlax:
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		
		// and resize
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		
		// do some cleanup
		g.dispose();
		
		return target;
	}
	
	/**
	 * crop a portion of a BufferedImage; returns a BufferedImage that is the
	 * subimage of the source BufferedImage, so any change on the crop image
	 * will be reflected on the source image
	 * @param image the source image
	 * @param x the X coordinate of the crop rectangle
	 * @param y the Y coordinate of the crop rectangle
	 * @param w the width of the crop rectangle
	 * @param h the height of the crop rectangle
	 * @return the subimage
	 */
	public static BufferedImage cropImage(BufferedImage image, 
			Rectangle cropRectangle) {
		return image.getSubimage(cropRectangle.x, cropRectangle.y,
				cropRectangle.width, cropRectangle.height);
	}

	/**
	 * crop a portion of a BufferedImage; returns a BufferedImage that is not
	 * backed up by the source image, therefore any changes on it will not
	 * affect the source image
	 * @param image the source image
	 * @param x the X coordinate of the crop rectangle
	 * @param y the Y coordinate of the crop rectangle
	 * @param w the width of the crop rectangle
	 * @param h the height of the crop rectangle
	 * @return the subimage
	 */
	public static BufferedImage cropImageNew(BufferedImage image,
			Rectangle cropRectangle) {
		return ImageConvert.cloneImage(cropImage(image, cropRectangle));
	}
}
