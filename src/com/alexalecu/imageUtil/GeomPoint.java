/*
 * Created on 15.04.2005
 */
package com.alexalecu.imageUtil;

/**
 * @author alex
 */

public class GeomPoint {
    private int x;
    private int y;
    
    /**
     * create a point with no set coordinates
     */
    public GeomPoint() {
    }
    
    /**
     * create a point given its coordinates
     * @param x
     * @param y
     */
    public GeomPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return the x coordinate of this point
     */
	public int getX() {
		return x;
	}

	/**
	 * set the x coordinate of this point
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}

    /**
     * @return the y coordinate of this point
     */
	public int getY() {
		return y;
	}

	/**
	 * set the y coordinate of this point
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return String.format("GeomPoint[x=%1$d][y=%2$d]", x, y);
	}
}
