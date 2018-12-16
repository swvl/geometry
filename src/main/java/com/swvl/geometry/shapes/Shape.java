/***********************************************************************
 * Copyright (c) 2015 by Regents of the University of Minnesota.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 *************************************************************************/
package com.swvl.geometry.shapes;

import com.swvl.geometry.io.TextSerializable;
import org.apache.hadoop.io.Writable;

import javax.naming.OperationNotSupportedException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;


/**
 * A general 2-dimensions shape.
 *
 * @author Hatem Morgan
 */
public abstract class Shape implements Writable, Cloneable, TextSerializable, Serializable {
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
    public abstract double distanceTo(Point p);

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
     * Check if the shape contains a rectangle
     *
     * @param shape input shape to be tested if it is inside this shape
     * @return <code>true</code> if this shape contains rectangle; <code>false</code> otherwise.
     */
    public abstract boolean contains(Shape shape) throws OperationNotSupportedException;


    /**
     * Check for intersection between any of edges of this shape with any of edges of given shape
     *
     * @param shape The other shape to test for edge intersection with this shape
     * @return <code>true</code> if this shape has edge(s) that intersects with edge(s) of given shape; <code>false</code> otherwise.
     */
    public abstract boolean isEdgeIntersection(Shape shape) throws OperationNotSupportedException;

    @Override
    public abstract void write(DataOutput dataOutput) throws IOException;

    @Override
    public abstract void readFields(DataInput dataInput) throws IOException;
}
