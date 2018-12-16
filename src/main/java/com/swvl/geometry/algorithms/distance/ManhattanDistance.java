package com.swvl.geometry.algorithms.distance;

import com.swvl.geometry.shapes.Point;
import com.swvl.geometry.shapes.Shape;

/**
 * The Manhattan distance is the sum of the (absolute) differences of their
 * coordinates. Also known as city block distance.
 *
 * @author Hatem Morgan
 */
public class ManhattanDistance implements DistanceCalculation {

    /**
     * Calculates the Manhattan distance as the sum of the absolute differences
     * of their coordinates.
     *
     * @return the Manhattan distance between the two instances.
     */
    public double measure(Shape s1, Shape s2) {
        Point p1 = s1.getCenterPoint();
        Point p2 = s2.getCenterPoint();

        double sum = 0.0;
        sum += Math.abs(p1.x - p2.x);
        sum += Math.abs(p1.y - p2.y);
        return sum;
    }

}
