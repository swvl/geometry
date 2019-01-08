package com.swvl.geometry.shapes;

import javax.naming.OperationNotSupportedException;
import java.io.Serializable;


/**
 * A general 2-dimensions shape.
 *
 * @author Hatem Morgan
 */
public abstract class Shape implements Cloneable, Serializable {
    public final static double EPS = 1e-9; // Epsilon error for comparing floating points


    /**
     * Calculates the minimum bounding rectangle for this shape.
     *
     * @return The minimum bounding rectangle for this shape
     */
    public abstract Rectangle getMBR();

    /**
     * Gets the Euclidean distance of this shape to a given point.
     *
     * @param p the point
     * @return The Euclidean distance between this object and the given point
     */
    public abstract double distanceTo(Point p) throws OperationNotSupportedException;

    /**
     * Check for intersection of this shape with the given shape
     *
     * @param s The other shape to test for intersection with this shape
     * @return <code>true</code> if this shape intersects with s; <code>false</code> otherwise.
     */
    public abstract boolean isIntersected(final Shape s) throws OperationNotSupportedException;

    /**
     * Clones this shape
     *
     * @return A new object which is a copy of this shape
     */
    public abstract Shape clone();


    /**
     * Calculate center point of any shape
     *
     * @return Center point
     */
    public abstract Point getCenterPoint();


    /**
     * Check if the shape contains another shape
     *
     * @param shape input shape to be tested if it is inside this shape
     * @return <code>true</code> if this shape contains the other shape;
     * <code>false</code> otherwise.
     */
    public abstract boolean contains(Shape shape) throws OperationNotSupportedException;
}
