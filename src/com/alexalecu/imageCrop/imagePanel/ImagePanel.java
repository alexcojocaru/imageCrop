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

package com.alexalecu.imageCrop.imagePanel;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.alexalecu.imageCrop.ImageCropGUI;
import com.alexalecu.imageUtil.ImageColors;
import com.alexalecu.imageUtil.ImageConvert;

/**
 * A JPanel subclass which holds a BufferedImage and notifies the parent about
 * any background color update
 */
@SuppressWarnings("serial")
public class ImagePanel extends JPanel {
	private BufferedImage image;
	private int width;
	private int height;
	private ImageCropGUI container;
	
	/**
	 * creates an ImagePanel instance
	 * @param container a reference to the container containing this image panel
	 * @param width the width of the panel
	 * @param height the height of the panel
	 */
	public ImagePanel(ImageCropGUI container, int width, int height) {
		super(true);
		
		this.container = container;
		this.width = width;
		this.height = height;

		addMouseListener(
			new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					setBackgrondColor(evt.getX(), evt.getY());
				}
			}
		);
	}


	/**
	 * @return the image displayed on this panel
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * sets the image to display on this panel
	 * @param image the BufferedImage to display
	 * @param scaleFactor the scale factor to be applied on the image before showing it
	 * @param repaint true if the panel should be repainted after setting the
	 * image
	 */
	public void setImage(BufferedImage image, double scaleFactor, boolean repaint) {
		// reset the panel size to match the image size
		if (image != null) {
			// if the new scale factor is 1, clone the original image; otherwise scale it
			if (scaleFactor == 1d) {
				this.image = ImageConvert.cloneImage(image);
			}
			else {
				int newW = (int)(scaleFactor * image.getWidth());
				int newH = (int)(scaleFactor * image.getHeight());
				this.image = ImageConvert.resize(image, newW, newH);
			}
			
			width = this.image.getWidth();
			height = this.image.getHeight();
		}
		else {
			this.image = null;
			
			width = 0;
			height = 0;
		}
		
		if (repaint)
			repaint();
	}
	
	/**
	 * @return the width of the panel
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * set the width of the panel
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height of the panel
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * set the height of the panel
	 * @param height
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // paint background
		
		if (image != null)
			g.drawImage(image, 0, 0, this);
	}
	
	/**
	 * reset the panel content, setting the image to null
	 * @param repaint true if a panel repaint has to be performed
	 */
	public void reset(boolean repaint) {
		setImage(null, 1d, repaint);
	}
	
	/**
	 * changes the cursor on this panel
	 * @param crosshair true for the cross-hair cursor, false for the default
	 */
	public void toggleCursor(boolean crosshair) {
		setCursor(new Cursor(crosshair ? Cursor.CROSSHAIR_CURSOR : Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * tells the container that the bg color has be to updated 
	 * @param x the x coordinate of the pixel which is of bg color
	 * @param y the y coordinate of the pixel which is of bg color
	 */
	private void setBackgrondColor(int x, int y) {
		if (image == null)
			return;
		
        container.bgColorChanged(ImageColors.getPixelColor(image, x, y));
	}

}
