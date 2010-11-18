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

public class GeomEdge {
    private GeomPoint p, q;
    
    /**
     * create an edge given its end points
     * @param p the first end point
     * @param q the second end point
     */
    public GeomEdge(GeomPoint p, GeomPoint q) {
        this.p = p;
        this.q = q;
    }
    
    /**
     * create an edge given the coordinates of its end points
     * @param xp the x coordinate of the first end point
     * @param yp the y coordinate of the first end point
     * @param xq the x coordinate of the second end point
     * @param yq the x coordinate of the second end point
     */
    public GeomEdge(int xp, int yp, int xq, int yq) {
    	this(new GeomPoint(xp, yp), new GeomPoint(xq, yq));
    }
    
    /**
     * create an edge given an array of coordinates of its end points 
     * @param coord array containing the x coordinate of the first end point,
     * the y of it, the x coordinate of the second end point, the y of it
     */
    public GeomEdge(int[] coord) {
    	this(coord[0], coord[1], coord[2], coord[3]);
    }
    
    /**
     * return the first end point of this edge
     * @return
     */
	public GeomPoint getP() {
		return p;
	}
	/**
	 * set the first end point of this edge
	 * @param p the end point to set
	 */
	public void setP(GeomPoint p) {
		this.p = p;
	}
	
	/**
	 * @return the second end point of this edge
	 */
	public GeomPoint getQ() {
		return q;
	}
	/**
	 * set the second end point of this edge
	 * @param q the point to set
	 */
	public void setQ(GeomPoint q) {
		this.q = q;
	}

	/**
	 * @return the min x coordinate of this edge
	 */
	public int xmin() {
		return Math.min(p.getX(), q.getX());
	}
	/**
	 * @return the max x coordinate of this edge
	 */
	public int xmax() {
		return Math.max(p.getX(), q.getX());
	}
	
	/**
	 * @return the min y coordinate of this edge
	 */
	public int ymin() {
		return Math.min(p.getY(), q.getY());
	}
	/**
	 * @return the max y coordinate of this edge
	 */
	public int ymax() {
		return Math.max(p.getY(), q.getY());
	}
	
	// flag to store weather the edge slope was computed already or not
	private boolean mComputed = false;
	
	// the computed slope of the edge
	private double m;
	
	/**
	 * calculate the slope of this edge and cache the calculated value, so that
	 * subsequent calls will return the calculated one
	 * @return the slope of this edge
	 */
	public double m() {
		// if the slope was calculated already, return the cached value
		if (mComputed)
			return m;
		
		// calculate the slope, store it in the cache and set the flag to true
		m = ((double)(q.getY() - p.getY())) / (q.getX() - p.getX());
		mComputed = true;
		
		return m;
	}
	
	public double b() {
		return p.getY() - m() * p.getX();
	}
	
	/**
	 * @return true if the first end point of the edge is 'higher' than the
	 * second end point
	 */
	public boolean isTop() {
		return p.getX() > q.getX();
	}

	/**
	 * @return true if the first end point of the edge is more to the right
	 * than the second end point
	 */
	public boolean isRight() {
		return p.getY() > q.getY();
	}

	@Override
	public String toString() {
		return String.format("GeomEdge[point=%1$s][point=%2$d]", p.toString(), q.toString());
	}
}
