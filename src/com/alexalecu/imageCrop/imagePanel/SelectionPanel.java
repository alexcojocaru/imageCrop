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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JComponent;

import com.alexalecu.imageCrop.ImageCropGUI;
import com.alexalecu.imageUtil.GeomEdge;

/**
 * A panel which displays a selection rectangle and the edges of a polygon,
 * allowing the selection to be moved / resized; it also allows content scaling
 */
@SuppressWarnings("serial")
public class SelectionPanel extends JComponent implements MouseListener, MouseMotionListener {
    
	public enum ResizeDirection { NONE, NORTH, WEST, SOUTH, EAST };
	
	private int width; // the width of the panel
	private int height; // the height of the panel
	
	private SelectionRectangle rect; // the selection rectangle
	
	private double scale; // the scale factor
	
	private ImageCropGUI container; // the parent of this SelectionPanel
	
	private List<GeomEdge> edgeList; // the hull edge list
	
	
	/**
	 * initialize a SelectionPanel, setting the scale to 1 and
	 * cursor to cross-hair
	 * @param container the container of the panel
	 * @param width the width of the panel
	 * @param height the height of the panel
	 */
	public SelectionPanel(ImageCropGUI container, int width, int height)
	{
	    this.container = container;
		this.width = width;
		this.height = height;
		
		scale = 1d;
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	
	/**
	 * @return the width of the panel
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * set the width of the panel
	 * @param i
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
	 * @param i
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * set the panel dimensions
	 * @param width the width of the panel
	 * @param height the height of the panel
	 */
	public void setDimension(int width, int height) {
		setWidth(width);
		setHeight(height);
	}

	/**
	 * @return the selection rectangle
	 */
	public Rectangle getRect() {
		return rect;
	}

	/**
	 * set the selection rectangle using the given rectangle
	 * @param rectangle the selection rectangle to set
	 */
	public void setRect(Rectangle rectangle) {
		if (rectangle == null)
			rect = null;
		else
			setRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	/**
	 * set the selection rectangle given its coordinates and size
	 * @param x the x coordinate of the top left corner of the rectangle
	 * @param y the y coordinate of the top left corner of the rectangle
	 * @param width the width of the selection rectangle
	 * @param height the height of the selection rectangle
	 */
	public void setRect(int x, int y, int width, int height) {
		rect = new SelectionRectangle(x, y, width, height);
	}

	/**
	 * set the hull edge list
	 * @param edgeList
	 */
	public void setEdgeList(List<GeomEdge> edgeList) {
		this.edgeList = edgeList;
	}

	/**
	 * @return the unscaled width of this panel, taking the scale factor into account
	 */
	public int getUnscaledWidth() {
		return (int)(width / scale);
	}

	/**
	 * @return the unscaled height of this panel
	 */
	public int getUnscaledHeight() {
		return (int)(height / scale);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // paint the background

		g.setColor(java.awt.Color.RED);

		// draw the selection rectangle in red
		if (rect != null) {
			g.drawRect((int)(scale * rect.x), (int)(scale * rect.y), 
					(int)(scale * (rect.width - 1)), (int)(scale * (rect.height - 1)));

			// draw the selection rectangle center marker in red too, but don't scale its size
			if (rect.getCenterRect() != null) {
				g.fillRect((int)(scale * rect.getCenterRect().x), 
						(int)(scale * rect.getCenterRect().y), 
						rect.getCenterRect().width,
						rect.getCenterRect().height);
			}
		}
		
		g.setColor(java.awt.Color.GREEN);
		
		// draw the hull edges in green
		if (edgeList != null) {
			for (int i = 0; i < edgeList.size(); i++) {
				GeomEdge edge = (GeomEdge)edgeList.get(i);
				g.drawLine((int)(scale * edge.getP().getX()), (int)(scale * edge.getP().getY()), 
						(int)(scale * edge.getQ().getX()), (int)(scale * edge.getQ().getY()));
			}
		}
	}
	
	/**
	 * reset the state of the panel
	 * @param repaint true if a repaint should be performed after reset
	 */
	public void reset(boolean repaint) {
		rect = null;
		width = height = 0;
		scale = 1d;
		
		edgeList = null;

		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		if (repaint)
			repaintComp();
	}
	
	/**
	 * scale the content
	 * @param scale the scale factor
	 * @param repaint true to repaint after setting the scale factor
	 */
	public void scale(double scale, boolean repaint) {
	    this.scale = scale;
	    if (repaint)
	        repaintComp();
	}
	
	/**
	 * trigger a repaint of the whole component
	 */
	public void repaintComp() {
		repaint();
	}
	
	/**
	 * the mouse pressed event handler
	 * @param e the MouseEvent object
	 */
	public void mousePressed(MouseEvent e) {
	    int evtX = (int)(e.getX() / scale);
	    int evtY = (int)(e.getY() / scale);
		
	    // if the user clicked inside the center marker, start a move action
	    if (rect != null && rect.startMoving(evtX, evtY, scale))
			return;

	    // if the user clicked while the cursor was on an edge, start a resize action
		if (rect != null && rect.startResizing()) {
			return;
		}
		
		// if we got so far, then there's no move or resize in progress;
		// so lets [re]initialize a selection rectangle if the cursor is outside the selection
		if (rect == null || !rect.contains(evtX, evtY)) {
			rect = new SelectionRectangle(evtX, evtY, 0, 0);
		
			// and trigger a repaint so that the selection rectangle gets visible
			repaintComp();
			
			// and notify the container about the selection size change
			container.selectionChanged(rect);
		}
	}

	/**
	 * the mouse released event handler
	 * @param e the MouseEvent object
	 */
	public void mouseReleased(MouseEvent e) {
		if (rect == null)
			return;
		
		// invalidate the selection rectangle if its width or height are 0
		if (rect.width < SelectionRectangle.MIN_LENGTH ||
				rect.height < SelectionRectangle.MIN_LENGTH) {
			rect = null;
			
			repaintComp();
			container.selectionChanged(rect);
			
			return;
		}
		
		// mark the end of the move event if needed
		if (rect.isMoving()) {
//			this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
//			moveCursor = false;
			rect.stopMoving();
			return;
		}
		// mark the end of the resize event if needed
		if (rect.isResizing()) {
//			this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
//			resizeCursor = false;
			rect.stopResizing();
			return;
		}
	}

	/**
	 * the mouse dragged event handler
	 * @param e the MouseEvent object
	 */
	public void mouseDragged(MouseEvent e) {
		if (rect == null)
			return;
		
	    int evtX = (int)(e.getX() / scale);
	    int evtY = (int)(e.getY() / scale);
	    
	    // just move the selection rectangle if we're during a move event
		if (rect.isMoving()) {
			if (rect.moveTo(evtX, evtY, getUnscaledWidth(), getUnscaledHeight())) {
				repaintComp();
				container.selectionChanged(rect);
			}
			return;
		}
		
		if (rect.isResizing()) {
			if (rect.resizeTo(evtX, evtY, getUnscaledWidth(), getUnscaledHeight())) {
				repaintComp();
				container.selectionChanged(rect);
			}
			return;
		}

		// if we got here, then the user is just creating the selection rectangle,
		// so let's update the selection rectangle object based on the selection
		if (rect.update(evtX, evtY, getUnscaledWidth(), getUnscaledHeight())) {
			repaintComp();
			container.selectionChanged(rect);
		}
	}
	
	/**
	 * the mouse moved event handler, update the move cursor and the resize
	 * cursor and direction based on the cursor location
	 * @param e the mouse event object
	 */
	public void mouseMoved(MouseEvent e) {
		if (rect == null)
			return;
		
	    int evtX = (int)(e.getX() / scale);
	    int evtY = (int)(e.getY() / scale);
	    
	    // set the move cursor if the cursor is within the selection, or reset to default if not
	    if (rect.updateMoveCursor(evtX, evtY, scale))
	    	this.setCursor(new Cursor(rect.isMoveCursor() ?
	    			Cursor.MOVE_CURSOR : Cursor.CROSSHAIR_CURSOR));
	    if (rect.isMoveCursor())
	    	return;
		
		// check if the cursor is over one of the borders of the selection rectangle
	    // and update it accordingly
	    if (rect.updateResizeCursor(evtX, evtY, scale)) {
	    	switch (rect.getResizeDirection()) {
	    		case NORTH:
	    			this.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
	    			break;
	    		case WEST:
	    			this.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
	    			break;
	    		case SOUTH:
	    			this.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
	    			break;
	    		case EAST:
	    			this.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
	    			break;
	    		default:
					this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	    	}
	    }
	}
	
	
	/**
	 * move the selection rectangle by so many pixels on the horizontal and
	 * vertical
	 * @param x the number of pixels to move the rectangle with on the horizontal
	 * @param y the number of pixels to move the rectangle with on the vertical
	 */
	public void moveRectBy(int x, int y) {
		if (rect == null)
			return;
		
		// scale the number of pixels according to the scale factor
		x = (int)(x / scale);
		y = (int)(y / scale);

		if (rect.moveBy(x, y, getUnscaledWidth(), getUnscaledHeight())) {
			repaintComp();
			container.selectionChanged(rect);
		}
	}
	
	/**
	 * resize the selection rectangle by that amount of pixels on the direction
	 * specified
	 * @param amount the amount of pixels to resize the rectangle with, can be <0
	 * @param resizeEdge the edge to move to achieve the resize
	 * @param direction the direction to resize on
	 */
	public void resizeRectBy(int amount, ResizeDirection edge, ResizeDirection direction) {		
		if (rect == null)
			return;

		if (rect.resizeBy(amount, edge, direction, getUnscaledWidth(), getUnscaledHeight())) {
			repaintComp();
			container.selectionChanged(rect);
		}
	}
	

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

}
