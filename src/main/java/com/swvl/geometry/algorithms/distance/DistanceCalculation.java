package com.swvl.geometry.algorithms.distance;

import com.swvl.geometry.shapes.Shape;

/**
 * Interface for algorithms calculating the distance between shapes
 *
 * @author Hatem Morgan
 */
public interface DistanceCalculation {
    /**
     * Calculates the distance between two instances.
     *
     * @param s1 the first shape
     * @param s2 the second shape
     * @return the distance between the two shapes
     */
    public double measure(Shape s1, Shape s2);

}
