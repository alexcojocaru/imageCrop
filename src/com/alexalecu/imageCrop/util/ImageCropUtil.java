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
package com.alexalecu.imageCrop.util;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * @author Alex Cojocaru
 *
 */
public class ImageCropUtil {

	/**
	 * validate the selection rectangle against the current image position and size
	 * @param image the image to validate the selection against
	 * @param selection the selection to be validated
	 * @return true if the selection is inside the current image bounds
	 */
	public static boolean validateSelectionRectangle(BufferedImage image, Rectangle selection) {
		return selection != null && selection.x >= 0 && selection.y >= 0 &&
				selection.width > 0 && selection.height > 0 &&
				selection.x + selection.width <= image.getWidth() &&
				selection.y + selection.height <= image.getHeight();
	}
}
