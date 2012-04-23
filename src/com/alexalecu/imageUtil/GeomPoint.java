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
	public boolean equals(Object obj) {
		if (obj instanceof GeomPoint) {
			GeomPoint point = (GeomPoint)obj;
			return x == point.getX() && y == point.getY();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return String.format("%1$d,%2$d", x, y).hashCode();
	}

	@Override
	public String toString() {
		return String.format("GeomPoint[x=%1$d][y=%2$d]", x, y);
	}
}
