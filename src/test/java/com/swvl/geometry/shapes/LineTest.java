package com.swvl.geometry.shapes;

import org.junit.Assert;
import org.junit.Test;

import javax.naming.OperationNotSupportedException;

public class LineTest {
    @Test
    public void testGetCenterPoint() {
        /* Horizontal line */
        Line l1 = new Line(new Point(3, 4), new Point(5, 4));
        Point mid1 = new Point(4, 4);
        Assert.assertEquals(l1.getCenterPoint(), mid1);

        /* Vertical Line */
        Line l2 = new Line(new Point(3, 5), new Point(3, 9));
        Point mid2 = new Point(3, 7);
        Assert.assertEquals(l2.getCenterPoint(), mid2);

        /* Incline Line (m > 0) */
        Line l3 = new Line(new Point(1, 2), new Point(2, 4));
        Point mid3 = new Point(1.5, 3);
        Assert.assertEquals(l3.getCenterPoint(), mid3);

        /* Incline Line (m < 0) */
        Line l4 = new Line(new Point(2, 1), new Point(1, 2));
        Point mid4 = new Point(1.5, 1.5);
        Assert.assertEquals(l4.getCenterPoint(), mid4);
    }

    @Test
    public void testToDistance() {
        Point p1 = new Point(3, 3);
        Point a1 = new Point(2, 1);
        Point b1 = new Point(4, 1);
        Line l1 = new Line(a1, b1);
        Assert.assertEquals(l1.distanceTo(p1), calcPointDistanceUsingTrigonometry(l1, p1), 0.0);

        Point p2 = new Point(3, 3);
        Point a2 = new Point(1, 2);
        Point b2 = new Point(1, 4);
        Line l2 = new Line(a2, b2);
        Assert.assertEquals(l2.distanceTo(p2), calcPointDistanceUsingTrigonometry(l2, p2), 0.0);

        Point p3 = new Point(0, 2);
        Point a3 = new Point(1, 2);
        Point b3 = new Point(2, 4);
        Line l3 = new Line(a3, b3);
        Assert.assertEquals(l3.distanceTo(p3), calcPointDistanceUsingTrigonometry(l3, p3), 0.0);


        Point p4 = new Point(4, 2);
        Point a4 = new Point(2, 1);
        Point b4 = new Point(4, 1);
        Line l4 = new Line(a4, b4);
        Assert.assertEquals(l4.distanceTo(p4), calcPointDistanceUsingTrigonometry(l4, p4), 0.0);
    }


    /**
     * Calculate Distance from point to line using Trigonometry
     *
     * @param line represented by two points endPoint e and startPoint s
     * @param p    point to calculate distance from it
     */
    private double calcPointDistanceUsingTrigonometry(Line line, Point p) {
        /* Calculate vector ep */
        double epx = p.x - line.endPoint.x;
        double epy = p.y - line.endPoint.y;

        /* Calculate vector se */
        double sex = line.endPoint.x - line.startPoint.x;
        double sey = line.endPoint.y - line.startPoint.y;

        /* Calculate magnitude of ep and se */
        double epMagnitude = Math.sqrt(epx * epx + epy * epy); // hypotenuse side
        double seMagnitude = Math.sqrt(sex * sex + sey * sey); // adjacent side

        System.out.println(epMagnitude + " " + seMagnitude);
        double theta = Math.acos(seMagnitude / epMagnitude);
        System.out.println(Math.toDegrees(theta));
        return Math.sin(theta) / epMagnitude; // opposite side
    }
}
