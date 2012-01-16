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
package com.alexalecu.imageCrop.event;

import com.alexalecu.imageCrop.imagePanel.SelectionPanel.ResizeDirection;

/**
 * @author Alex Cojocaru
 *
 * Naming convention:
 *   *_PICKED, *_SELECTED, *_CHANGED: the property has been changed by the user;
 *   *_UPDATED: the property has changed programmatically and the GUI has to be changed to reflect
 *   the new value
 */
public class ResizeSelectionEvent {
	private int amount;
	private ResizeDirection edge;
	private ResizeDirection direction;
	
	/**
	 * @param amount how many pixels to move the rectangle edge by
	 * @param edge the rectangle edge to move
	 * @param direction the direction in which the edge should be moved
	 */
	public ResizeSelectionEvent(int amount, ResizeDirection edge, ResizeDirection direction) {
		this.amount = amount;
		this.edge = edge;
		this.direction = direction;
	}
	
	/**
	 * @return how many pixels to resize the rectangle by
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * @return the rectangle edge to move
	 */
	public ResizeDirection getEdge() {
		return edge;
	}
	
	/**
	 * @return the direction in which the edge should be move
	 */
	public ResizeDirection getDirection() {
		return direction;
	}
}
