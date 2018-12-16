package com.swvl.geometry.algorithms.distance;

import com.swvl.geometry.shapes.Shape;

/**
 * Algorithm to calculate the Euclidean distance between two Shapes
 *
 * @author Hatem Morgan
 */
public class EuclideanDistance implements DistanceCalculation {

    @Override
    public double measure(Shape s1, Shape s2) {
        return s1.distanceTo(s2.getCenterPoint());
    }
}
