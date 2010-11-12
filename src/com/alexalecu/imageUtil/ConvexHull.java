/*
 * Created on 15.04.2005
 */
package com.alexalecu.imageUtil;

import java.util.ArrayList;


@SuppressWarnings("serial")
public class ConvexHull extends ArrayList<GeomPoint> {
    
    private int start, stop; //tangents for iterative convex hull
    private int xmin, xmax, ymin, ymax;  //position of hull
    @SuppressWarnings("unused")
	private int yxmax; //y coordinate of x max  
    
    /* fixed aspect ratio */
    private boolean fixed;
    private int fixedX, fixedY;
    
    /* largest rectangle's attributes */
    public GeomPoint rectp;
    public int recth, rectw;
    
    public ArrayList<GeomEdge> edgeList;
    
    
    public ConvexHull() {
        this.fixed = false;
        this.fixedX = 1;
        this.fixedY = 1;
    }
    
    /* position of point w.r.t. hull edge
     * sign of twice the area of triangle abc
     */
    public boolean onLeft(GeomPoint a, GeomPoint b, GeomPoint c) {
        int area = (b.getX() - a.getX()) * (c.getY() - a.getY()) -
        		(c.getX() - a.getX()) * (b.getY() - a.getY());
        return (area < 0);
    }
    
    /* check if point is outside
     * true is point is on right of all vertices
     * finds tangents if point is outside
     */
    public boolean pointOutside(GeomPoint p) {
        
        boolean ptIn = true, currIn, prevIn = true;
        
        GeomPoint a = (GeomPoint)this.get(0);
        GeomPoint b;
        
        for (int i = 0; i < this.size(); i++) {
            b = (GeomPoint)this.get((i+1)%this.size());
            currIn = onLeft(a, b, p);
            ptIn = ptIn && currIn;
            a = b;
            
            if (prevIn && !currIn) {
            	start = i;
            }  /* next point outside, 1st tangent found */
            if (!prevIn && currIn) {
            	stop = i;
            }  /* 2nd tangent */
            prevIn = currIn;
            
        }
        return !ptIn;
    }
    
    public void addPoint(GeomPoint p) {
		if (size() < 2) {
			add(p);
		}
		else if (size() == 2) {
			GeomPoint ha = (GeomPoint) get(0);
			GeomPoint hb = (GeomPoint) get(1);
			if (onLeft(ha, hb, p))
				add(p);
			else
				add(1, p);
		}
		else {
			addPointToHull(p);
		}
	}
    
    /* check if point is outside, insert it, maintaining general position */
    private boolean addPointToHull(GeomPoint p) {
        
        /* index of tangents */
        start = 0;
        stop = 0;
        
        if (!pointOutside(p)) {
            return false;
        }
        
        /* insert point */
        int numRemove;
        
        if (stop > start) {
            numRemove = stop - start - 1;
            if (numRemove > 0) {
                this.removeRange(start+1, stop);
            }
            this.add(start+1, p); //insertElmentAt(p, start+1);
        }
        else{
            numRemove = stop + this.size() - start - 1;
            if (numRemove > 0) {
                if (start+1 < this.size()) {
                    this.removeRange(start+1, this.size());
                }
                if (stop-1 >= 0) {
                    this.removeRange(0, stop);
                }
            }
            this.add(p);
          
        }
        return true;
    } //addPointToHull
    
    /* compute edge list
     * set xmin, xmax
     * used to find largest rectangle by scanning horizontally
     */
    public void computeEdgeList() {
    	edgeList = new ArrayList<GeomEdge>();
        GeomPoint a, b;
        GeomEdge e;
        a = (GeomPoint)this.get(this.size()-1);
        for (int i = 0; i < this.size(); i++) {
            b = (GeomPoint)this.get(i);
            //b = (GeomPoint)this.elementAt(i+1);
            
            if (i==0) {
                this.xmin = a.getX();
                this.xmax = a.getX();
                this.ymin = a.getY();
                this.ymax = a.getY();
            }
            else {
                if (a.getX() < this.xmin) {
                    this.xmin = a.getX();
                }
                if (a.getX() > this.xmax) {
                    this.xmax  = a.getX();
                    this.yxmax = a.getY();
                }
                if (a.getY() < this.ymin) {
                    this.ymin = a.getY();
                }
                if (a.getY() > this.ymax) {
                    this.ymax  = a.getY();
                }
            }
            e = new GeomEdge(a,b);
            edgeList.add(e);
            a = b;
        } //for
        // b = (GeomPoint)this.elementAt(this.size()-1);
        // a = (GeomPoint)this.elementAt(0);
        // e = new GeomEdge(b,a);
        // l.add(e);
    }
    
    /* compute y intersection with an edge
     * first pixel completely inside
     * ceil function if edge is on top, floor otherwise
     * (+y is down)
     */
    public int yIntersect(int xi, GeomEdge e) {
        int y;
        double yfirst = (e.m()) * (xi-0.5) + e.b();
        double ylast = (e.m()) * (xi+0.5) + e.b();
        
        if (!e.isTop()) {
            y = (int)Math.floor(Math.min(yfirst, ylast));
        }
        else {
            y = (int)Math.ceil(Math.max(yfirst, ylast));
        }
        return y;
    }
    
    /* find largest pixel completely inside
     * look through all edges for intersection
     */
    public int xIntersect(int y, ArrayList<GeomEdge> l) {
        int x = 0;
        double x0 = 0, x1 = 0;
        for (int i = 0; i < this.size(); i++) {
            GeomEdge e = (GeomEdge)l.get(i);
            if (e.isRight() && e.ymin() <= y && e.ymax() >= y) {
                x0 = (double)(y+0.5 - e.b()) / e.m();
                x1 = (double)(y-0.5 - e.b()) / e.m();
            }
        }
        x = (int)Math.floor(Math.min(x0,x1));
        return x;
    }
    
    public GeomEdge findEdge(int x, boolean isTop, ArrayList<GeomEdge> l){
        GeomEdge e, emax = (GeomEdge)l.get(0);
        //int count = 0;
        for (int i = 0; i < this.size(); i++) {
            e = (GeomEdge)l.get(i);
            if (e.xmin() == x) {
                //count++;
                //if (count == 1){
                //    emax = e;
                //}
                //else{
                if (e.xmax() != e.xmin()) {
                    if ((e.isTop() && isTop) || (!e.isTop() && !isTop)) {
                        emax = e;
                    }
                }
            }
            
        }
        return emax;
    }
    
    /* compute 3 top and bottom 3 corner rectangle for each xi
     * find largest 2 corner rectangle
     */
    public void computeLargestRectangle() {
    	
    	computeEdgeList();
        
        GeomEdge top, bottom;
        int ymax, ymin, xright, xlo, xhi;
        int area, maxArea = 0;
        int width, height, maxh = 0, maxw = 0;
        
        GeomPoint maxp = new GeomPoint(0,0);
        
        ArrayList<GeomPoint> xint = new ArrayList<GeomPoint>();
        
        for (int i = 0; i <= this.ymax; i++) {
            int x = xIntersect(i, edgeList);
            GeomPoint px = new GeomPoint(x, i);
            xint.add(px);
        }
        //find first top and bottom edges
        top = findEdge(this.xmin, true, edgeList);
        bottom = findEdge(this.xmin, false, edgeList);
        
        //scan for rectangle left position
        for (int xi = this.xmin; xi < this.xmax; xi++) {
            
            ymin = yIntersect(xi, top);
            ymax = yIntersect(xi, bottom);
            
            for (int ylo = ymax;ylo >= ymin; ylo--) {//ylo from to to bottom
                
                for (int yhi = ymin; yhi <= ymax; yhi++) {
                
                    if (yhi > ylo) {
                        
                        xlo = (int)((GeomPoint)xint.get(ylo)).getX();
                        	// xIntersect(ylo,edgeList);
                        xhi = (int)((GeomPoint)xint.get(yhi)).getX();
                        	// xIntersect(yhi,edgeList);
                        
                        xright = Math.min(xlo, xhi);
                        
                        height = yhi-ylo;
                        width = xright - xi;
                            
                        if (!this.fixed) {
                        }//!fixed
                        else {
                          int fixedWidth = (int) Math
									.ceil(((double) height * this.fixedX)
											/ ((double) this.fixedY));
                          if (fixedWidth <= width) {
                              width = fixedWidth;
                          }
                          else {
                              width = 0;
                          }
                       }
                       area = width * height;
                        
                       if (area > maxArea) {
                            maxArea = area;
                            maxp = new GeomPoint(xi, ylo);
                            maxw = width;
                            maxh = height;
                       }
                    }  // end if yhi > ylo
                }  // end for yhi
            }  // end for ylo
            if (xi == top.xmax()) {
                top = findEdge(xi,  true, edgeList);
            }
            if (xi == bottom.xmax()) {
                bottom = findEdge(xi, false, edgeList);
            }
        }  // end for xi
        this.rectp = maxp;
        this.recth = maxh;
        this.rectw = maxw;
    }
    
}   
