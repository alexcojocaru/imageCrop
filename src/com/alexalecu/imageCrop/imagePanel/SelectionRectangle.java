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

import java.awt.Rectangle;

import com.alexalecu.imageCrop.imagePanel.SelectionPanel.ResizeDirection;

public class SelectionRectangle extends Rectangle {
	private static final long serialVersionUID = 1L;
	
	public final static int MIN_LENGTH = 4;

	private Rectangle centerRect; // the selection rectangle center marker
	
	private boolean moving; // flag to indicate if the rectangle is moving
	private boolean moveCursor; // flag to indicate the moving cursor
	private int moveOffsetX; // the distance in pixels between the cursor and the top left corner
	private int moveOffsetY; // the distance in pixels between the cursor and the top left corner
	
	private boolean resizing; // flag to indicate if the rectangle is resizing
	private ResizeDirection resizeDirection; // indicates the resize direction
	private boolean resizeCursor; // flag to indicate the resize cursor
	private int resizeOffset; // the distance in pixels between the cursor and the resizing edge
	
	/**
	 * create a new instance, and initialize the center marker if needed
	 */
	public SelectionRectangle(int x, int y, int width, int height) {
		super(x, y, width, height);
		computeCenter();
		resizeDirection = SelectionPanel.ResizeDirection.NONE;
	}

	/**
	 * @return the center marker rectangle
	 */
	public Rectangle getCenterRect() {
		return centerRect;
	}

	/**
	 * @return true is the selection rectangle is in the process of being moved
	 */
	public boolean isMoving() {
		return moving;
	}

	/**
	 * @return true is the selection rectangle is in the process of being resized
	 */
	public boolean isResizing() {
		return resizing;
	}

	/**
	 * @return true if the moveCursor is true
	 */
	public boolean isMoveCursor() {
		return moveCursor;
	}

	/**
	 * @return the resize direction
	 */
	public ResizeDirection getResizeDirection() {
		return resizeDirection;
	}
	
	/**
	 * compute the center marker of the selection rectangle; there is no marker
	 * if the selection rectangle width or height are less than 7px
	 */
	private void computeCenter() {
		if (width < 7 || height < 7) {
			centerRect = null;
			return;
		}
		
		// initialize the center marker rectangle if it does not exist yet
		if (centerRect == null) {
			centerRect = new Rectangle();
			centerRect.width = 5;
			centerRect.height = 5;
		}
		
		centerRect.x = x + width / 2 - 2;
		centerRect.y = y + height / 2 - 2;
	}

	
	/**
	 * update the selection rectangle, keeping the dimensions positive and within bounds;
	 * do not allow to overturn the rectangle
	 * @param posX the x coordinate of the cursor
	 * @param posY the y coordinate of the cursor
	 * @param containerWidth the width of the container of this selection panel
	 * @param containerHeight the height of the container of this selection panel
	 * @return true if the current rectangle has been updated
	 */
	public boolean update(int posX, int posY, int containerWidth, int containerHeight) {
		int width = Math.min(Math.max(posX - x, 0), containerWidth - x);
		int height = Math.min(Math.max(posY - y, 0), containerHeight - y);
		
		if (width == this.width && height == this.height)
			return false;
		
		this.width = width;
		this.height = height;
		
		// update the center marker, repaint the content and notify the parent
		computeCenter();
		
		return true;
	}
	
	
	/**
	 * check if the cursor coordinates are inside the rectangle; if so, start moving
	 * @param cursorX the x coordinate of the cursor
	 * @param cursorY the y coordinate of the cursor
	 * @param scaleFactor the scale factor to be taken into consideration when matching
	 * @return true if the rectangle has been marked as moving
	 */
	public boolean startMoving(int cursorX, int cursorY, double scaleFactor) {
		int allowedOffset = (int)Math.ceil(3 / scaleFactor);
		Rectangle moveRect = new Rectangle(x + allowedOffset, y + allowedOffset,
				width - 2 * allowedOffset, height - 2 * allowedOffset);
		if (moveRect.contains(cursorX, cursorY)) {
			moving = true;
			moveOffsetX = cursorX - x;
			moveOffsetY = cursorY - y;
			return true;
		}
		return false;
	}
	/**
	 * mark the end of the moving process
	 */
	public void stopMoving() {
		moving = false;
		moveOffsetX = 0;
		moveOffsetY = 0;
	}
	
	/**
	 * move the selection rectangle based on the new coordinates of the cursor
	 * @param posX the x coordinate of the cursor
	 * @param posY the y coordinate of the cursor
	 * @param containerWidth the width of the container of this selection panel
	 * @param containerHeight the height of the container of this selection panel
	 * @return true if the rectangle has been moved
	 */
	public boolean moveTo(int posX, int posY, int containerWidth, int containerHeight) {
		// calculate the new coordinates of the top left corner and force them to be inside
		// the bounding container
		int x = Math.min(Math.max(posX - moveOffsetX, 0), containerWidth - width);
		int y = Math.min(Math.max(posY - moveOffsetY, 0), containerHeight - height);
		
		if (x == this.x && y == this.y)
			return false;
		
		this.x = x;
		this.y = y;
		
		// recalculate the selection rectangle center and repaint the content
		computeCenter();
		
		return true;
	}
	
	/**
	 * move the selection rectangle with x, y pixels
	 * @param x the amount to move the selection rectangle on the horizontal
	 * @param y the amount to move the selection rectangle on the vertical
	 * @param containerWidth the width of the container of this selection panel
	 * @param containerHeight the height of the container of this selection panel
	 * @return true if the rectangle has been moved
	 */
	public boolean moveBy(int x, int y, int containerWidth, int containerHeight) {
	    // calculate the new x and y of the top left corner
		x += this.x;
		y += this.y;
		
		// and try to move it
		return moveTo(x, y, containerWidth, containerHeight);
	}

	/**
	 * set the move cursor if the cursor is within the center marker, or reset to default if not
	 * @param posX the x coordinate of the cursor
	 * @param posY the y coordinate of the cursor
	 * @param scaleFactor the scale factor to be taken into consideration when matching
	 * @return true if the moveCursor variable has been updated
	 */
	public boolean updateMoveCursor(int posX, int posY, double scaleFactor) {
		// if the rectangle is moving, there's nothing to update
		if (moving)
			return false;
		
		int allowedOffset = (int)Math.ceil(3 / scaleFactor);
		Rectangle moveRect = new Rectangle(x + allowedOffset, y + allowedOffset,
				width - 2 * allowedOffset, height - 2 * allowedOffset);
		
		if (moveRect.contains(posX, posY)) {
			if (!moveCursor) {
				moveCursor = true;
				return true;
			}
		}
		else {
			if (moveCursor) {
				moveCursor = false;
				return true;
			}
		}
		
		return false;
	}

	
	/**
	 * check if the cursor is over one of the rectangle edges; if so, start resizing
	 * @return true if the rectangle has been marked as resizing
	 */
	public boolean startResizing() {
		// if the cursor is over one of the edges, than the resizeDirection variable is not NONE
		if (resizeDirection != ResizeDirection.NONE) {
			resizing = true;
			return true;
		}
		return false;
	}
	/**
	 * mark the end of the resize process
	 */
	public void stopResizing() {
		resizing = false;
		resizeDirection = SelectionPanel.ResizeDirection.NONE;
	}
	
	/**
	 * resize the selection rectangle; if the side being resized is shorter than MIN_LENGTH px
	 * or the cursor is out of bounds, ignore the request
	 * @param posX the x coordinate of the cursor
	 * @param posY the y coordinate of the cursor
	 * @param containerWidth the width of the container of this selection panel
	 * @param containerHeight the height of the container of this selection panel
	 * @return true if the rectangle has been resized
	 */
	public boolean resizeTo(int posX, int posY, int containerWidth, int containerHeight) {
		int x = this.x;
		int y = this.y;
		int width = this.width;
		int height = this.height;
		
		switch (resizeDirection) {
			case NORTH:
				// make sure that the top edge is inside the container and
				// that the height is at least equal to the minimal length
				y = Math.min(Math.max(posY - resizeOffset, 0), this.y + this.height - MIN_LENGTH);
				height = this.y + this.height - y;
				break;
			case WEST:
				x = Math.min(Math.max(posX - resizeOffset, 0), this.x + this.width - MIN_LENGTH);
				width = this.x + this.width - x;
				break;
			case SOUTH:
				// make sure that the bottom edge is inside the container and
				// that the height is at least equal to the minimal length
				height = Math.min(Math.max(posY -resizeOffset - this.y, MIN_LENGTH),
						containerHeight - this.y);
				break;
			case EAST:
				width = Math.min(Math.max(posX -resizeOffset - this.x, MIN_LENGTH),
						containerWidth - this.x);
				break;
		}

		// do not process further if there is no change
		if (this.x == x && this.y == y && this.width == width && this.height == height)
			return false;
		
		// update the selection rectangle position / dimensions
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		// recalculate the center marker and notify the caller
		computeCenter();
		return true;
	}
	
	/**
	 * resize the by that amount of pixels on the direction specified
	 * @param amount the amount of pixels to resize the rectangle with, can be <0
	 * @param resizeEdge the edge to move to achieve the resize
	 * @param direction the direction to resize on
	 * @param containerWidth the width of the container of this selection panel
	 * @param containerHeight the height of the container of this selection panel
	 * @return true if the selection rectangle has been resized
	 */
	public boolean resizeBy(int amount,  ResizeDirection edge,
			SelectionPanel.ResizeDirection direction,
			int containerWidth, int containerHeight) {
		
		int x = this.x;
		int y = this.y;
		int width = this.width;
		int height = this.height;
		
		// calculate the new x, y, width and height of the selection based on the resize edge,
		// amount and direction, and force the rectangle to be inside the container area and
		// bigger or equal to than the minimum size
		switch (edge) {
			case WEST:
				x += (direction == ResizeDirection.WEST ? -1 : 1) * amount;
				x = Math.min(Math.max(x, 0), this.x + this.width - MIN_LENGTH);
				width = this.x + this.width - x;
				break;
			case EAST:
				width += (direction == ResizeDirection.WEST ? -1 : 1) * amount;
				width = Math.min(Math.max(width, MIN_LENGTH), containerWidth - x);
				break;
			case NORTH:
				y += (direction == ResizeDirection.NORTH ? -1 : 1) * amount;
				y = Math.min(Math.max(y, 0), this.y + this.height - MIN_LENGTH);
				height = this.y + this.height - y;
				break;
			case SOUTH:
				height += (direction == ResizeDirection.NORTH ? -1 : 1) * amount;
				height = Math.min(Math.max(height, MIN_LENGTH), containerHeight - y);
				break;
		}

		// do not process further if there is no change
		if (this.x == x && this.y == y && this.width == width && this.height == height)
			return false;
		
		// update the selection rectangle position / dimensions
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		// and recalculate the center marker, repaint and notify the parent
		computeCenter();
		
		return true;
	}

	/**
	 * set the resize cursor and direction weather the cursor is over one of the borders
	 * of the rectangle or not
	 * @param posX the x coordinate of the cursor
	 * @param posY the y coordinate of the cursor
	 * @param scaleFactor the scale factor to be taken into consideration when matching
	 * @return true if the resize related variables have been changed
	 */
	public boolean updateResizeCursor(int posX, int posY, double scaleFactor) {
		// do nothing if the rectangle is currently resizing
		if (resizing)
			return false;
		
		boolean withinHorizontalBounds = posX >= x && posX < x + width;
		boolean withinVerticalBounds = posY >= y && posY < y + height;
		
		int allowedOffset = (int)Math.floor(2 / scaleFactor);
		
		// over the top border, with a allowedOffset px error
		if (withinHorizontalBounds && posY >= y && posY <= y + allowedOffset) {
			resizeDirection = ResizeDirection.NORTH;
			resizeCursor = true;
			resizeOffset = posY - y;
			return true;
		}
		// over the left border, with a allowedOffset px error
		if (withinVerticalBounds && posX >= x && posX <= x + allowedOffset) {
			resizeDirection = ResizeDirection.WEST;
			resizeCursor = true;
			resizeOffset = posX - x;
			return true;
		}
		// over the bottom border, with a allowedOffset px error
		if (withinHorizontalBounds && posY >= y + height - allowedOffset && posY <= y + height) {
			resizeDirection = ResizeDirection.SOUTH;
			resizeCursor = true;
			resizeOffset = posY - y - height;
			return true;
		}
		// over the right border, with a allowedOffset px error
		if (withinVerticalBounds && posX >= x + width - allowedOffset && posX <= x + width) {
			resizeDirection = ResizeDirection.EAST;
			resizeCursor = true;
			resizeOffset = posX - x - width;
			return true;
		}
		
		// if we got here, the mouse is not over any border, so reset the
		// resize state and variables
		if (resizeCursor) {
			resizeDirection = ResizeDirection.NONE;
			resizeCursor = false;
			resizeOffset = 0;
			return true;
		}
		
		// no update has been performed to the resize related variables
		return false;
	}
	
	
}
