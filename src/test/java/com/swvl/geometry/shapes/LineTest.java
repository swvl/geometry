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
        Assert.assertTrue(checkAccuracyOfPointDistanceUsingTrigonometry(l1, p1));

        Point p2 = new Point(3, 3);
        Point a2 = new Point(1, 2);
        Point b2 = new Point(1, 4);
        Line l2 = new Line(a2, b2);
        Assert.assertTrue(checkAccuracyOfPointDistanceUsingTrigonometry(l2, p2));

        Point p3 = new Point(0, 2);
        Point a3 = new Point(1, 2);
        Point b3 = new Point(2, 4);
        Line l3 = new Line(a3, b3);
        Assert.assertTrue(checkAccuracyOfPointDistanceUsingTrigonometry(l3, p3));


        Point p4 = new Point(1.5, 0);
        Point a4 = new Point(1, 2);
        Point b4 = new Point(2, 4);
        Line l4 = new Line(a4, b4);
        Assert.assertTrue(checkAccuracyOfPointDistanceUsingTrigonometry(l4, p4));
    }


    /**
     * Check accuracy of distance from point to line using Trigonometry
     *
     * @param line represented by two points endPoint e and startPoint s
     * @param p    point to calculate distance from it
     */
    private boolean checkAccuracyOfPointDistanceUsingTrigonometry(Line line, Point p) {
        /* Calculate vector ep */
        double epx = p.x - line.getEndPoint().x;
        double epy = p.y - line.getEndPoint().y;

        /* Magnitude of ep vector */
        double hypotenuse = Math.sqrt(epx * epx + epy * epy);
        /* Calculate vertical distance between point and line */
        double oppsite = line.distanceTo(p);
        /* Calculate adjacent side using Pythagoras theorem */
        double actualAjacent = Math.sqrt(hypotenuse * hypotenuse - oppsite * oppsite);
        /* Calculate angle between se vector and ep vector */
        double theta = Math.asin(oppsite / hypotenuse);
        /* Calculate adjacent side using trigonometry a = ||h||cos(theta) */
        double expectedAdjacent = hypotenuse * Math.cos(theta);

        return Math.abs(actualAjacent - expectedAdjacent) < Point.EPS;
    }


    @Test
    public void testIntersectionWithPoint() throws OperationNotSupportedException {
        Line line = new Line(new Point(1, 2), new Point(4, 8));

        Point p1 = new Point(2, 4);
        Assert.assertTrue(line.isIntersected(p1));

        Point p2 = new Point(1.5, 3);
        Assert.assertTrue(line.isIntersected(p2));

        Point p3 = new Point(1.1234, 2.2468);
        Assert.assertTrue(line.isIntersected(p3));

        Point p4 = new Point(1.001234, 2.002468);
        Assert.assertTrue(line.isIntersected(p4));

        Point p5 = new Point(1, 2);
        Assert.assertTrue(line.isIntersected(p5));

        Point p6 = new Point(4, 8);
        Assert.assertTrue(line.isIntersected(p6));

        Point p7 = new Point(2.5, 2.5);
        Assert.assertFalse(line.isIntersected(p7));

        Point p8 = new Point(2, 4.0055);
        Assert.assertFalse(line.isIntersected(p8));
    }

    @Test
    public void testIntersectionWithLine() throws OperationNotSupportedException {
        /* y= 2x */
        Line line = new Line(new Point(1, 2), new Point(4, 8));

        /* y = -2x+8 */
        Line l1 = new Line(new Point(-1, 10), new Point(4, 0));
        Assert.assertTrue(line.isIntersected(l1)); // intersection at p(2,4)

        Line l2 = new Line(new Point(6, 1), new Point(3, 6));
        Assert.assertTrue(line.isIntersected(l2)); // intersection at p(3,6)

        /* y = 8 */
        Line l3 = new Line(new Point(1, 8), new Point(6, 8));
        Assert.assertTrue(line.isIntersected(l3)); // intersection at p(4,8)

        /* x = 4 */
        Line l4 = new Line(new Point(4, 2), new Point(4, 10));
        Assert.assertTrue(line.isIntersected(l4)); // intersection at p(4,8)

        /* y = 2 */
        Line l5 = new Line(new Point(0, 2), new Point(6, 2));
        Assert.assertTrue(line.isIntersected(l5)); // intersection at p(1,2)

        /* x = 1 */
        Line l6 = new Line(new Point(1, 0), new Point(1, 10));
        Assert.assertTrue(line.isIntersected(l6)); // intersection at p(1,2)

        /* y = 4x */
        Line l7 = new Line(new Point(1, 4), new Point(4, 16));
        Assert.assertFalse(line.isIntersected(l7));

        /* y = 2x */
        Line l8 = new Line(new Point(4, 8), new Point(10, 20));
        Assert.assertTrue(line.isIntersected(l8));  // intersection at p(4,8)

        /* y = 2x */
        Line l9 = new Line(new Point(6.000211, 8), new Point(10, 20));
        Assert.assertFalse(line.isIntersected(l9));
    }

    @Test
    public void testContainsWithLine() throws OperationNotSupportedException {
        /* y= 2x */
        Line line = new Line(new Point(1, 2), new Point(4, 8));

        /* y = -2x+8 */
        Line l1 = new Line(new Point(-1, 10), new Point(4, 0));
        Assert.assertFalse(line.contains(l1)); // intersection at p(2,4)

        Line l2 = new Line(new Point(6, 1), new Point(3, 6));
        Assert.assertFalse(line.contains(l2)); // intersection at p(3,6)

        /* y = 8 */
        Line l3 = new Line(new Point(1, 8), new Point(6, 8));
        Assert.assertFalse(line.contains(l3)); // intersection at p(4,8)

        /* x = 4 */
        Line l4 = new Line(new Point(4, 2), new Point(4, 10));
        Assert.assertTrue(line.isIntersected(l4)); // intersection at p(4,8)

        /* y = 2 */
        Line l5 = new Line(new Point(0, 2), new Point(6, 2));
        Assert.assertTrue(line.isIntersected(l5)); // intersection at p(1,2)

        /* x = 1 */
        Line l6 = new Line(new Point(1, 0), new Point(1, 10));
        Assert.assertFalse(line.contains(l6)); // intersection at p(1,2)

        /* y = 4x */
        Line l7 = new Line(new Point(1, 4), new Point(4, 16));
        Assert.assertFalse(line.contains(l7));

        /* y = 2x */
        Line l8 = new Line(new Point(4, 8), new Point(10, 20));
        Assert.assertFalse(line.contains(l8));  // intersection at p(4,8)

        /* y = 2x */
        Line l9 = new Line(new Point(6.000211, 8), new Point(10, 20));
        Assert.assertFalse(line.contains(l9));

        /* y = 2x */
        Line l10 = new Line(new Point(2, 4), new Point(3, 6));
        Assert.assertTrue(line.contains(l10));

        /* y = 2x */
        Line l11 = new Line(new Point(2, 4), new Point(5, 10));
        Assert.assertFalse(line.contains(l11));

        /* y = 2x */
        Line l12 = new Line(new Point(1, 2), new Point(4, 8));
        Assert.assertTrue(line.contains(l12));

        /* y = 2x */
        Line l13 = new Line(new Point(0, 0), new Point(3, 6));
        Assert.assertFalse(line.contains(l13));
    }

    /**
     * Line intersect the left side of rectangle
     */
    @Test
    public void testIntersectionWithRectangleLeftSide() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(3, 2, 6, 4);

        /* horizontal line (m=0) */
        Line line1 = new Line(new Point(2, 3), new Point(4, 3));

        /*  right-inclined Line (m >1) */
        Line line2 = new Line(new Point(2, 2), new Point(4, 3));

        /* left-inclined Line (m <1) */
        Line line3 = new Line(new Point(4, 3), new Point(2, 5));


        Assert.assertTrue(line1.isIntersected(rect));
        Assert.assertTrue(line2.isIntersected(rect));
        Assert.assertTrue(line3.isIntersected(rect));

        Assert.assertFalse(line1.contains(rect));
        Assert.assertFalse(line2.contains(rect));
        Assert.assertFalse(line3.contains(rect));
    }

    /**
     * Line intersect the right side of rectangle
     */
    @Test
    public void testIntersectionWithRectangleRightSide() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(3, 2, 6, 4);

        /* horizontal line (m=0) */
        Line line1 = new Line(new Point(7, 3), new Point(4, 3));
        /*  right-inclined Line (m >1) */
        Line line2 = new Line(new Point(4, 3), new Point(7, 4));
        /* left-inclined Line (m <1) */
        Line line3 = new Line(new Point(7, 1), new Point(3, 3));

        Assert.assertTrue(line1.isIntersected(rect));
        Assert.assertTrue(line2.isIntersected(rect));
        Assert.assertTrue(line3.isIntersected(rect));

        Assert.assertFalse(line1.contains(rect));
        Assert.assertFalse(line2.contains(rect));
        Assert.assertFalse(line3.contains(rect));
    }

    /**
     * Line intersect the upper side of rectangle
     */
    @Test
    public void testIntersectionWithRectangleUpperSide() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(3, 2, 6, 4);

        /* vertical line (m not defined) */
        Line line1 = new Line(new Point(4, 3), new Point(4, 5));

        /*  right-inclined Line (m >1) */
        Line line2 = new Line(new Point(4, 3), new Point(5, 6));

        /* left-inclined Line (m <1) */
        Line line3 = new Line(new Point(5, 3), new Point(4, 6));

        Assert.assertTrue(line1.isIntersected(rect));
        Assert.assertTrue(line2.isIntersected(rect));
        Assert.assertTrue(line3.isIntersected(rect));

        Assert.assertFalse(line1.contains(rect));
        Assert.assertFalse(line2.contains(rect));
        Assert.assertFalse(line3.contains(rect));
    }


    /**
     * Line intersect the bottom side of rectangle
     */
    @Test
    public void testIntersectionWithRectangleBottomSide() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(3, 2, 6, 4);

        /* vertical line (m not defined) */
        Line line1 = new Line(new Point(4, 1), new Point(4, 3));

        /*  right-inclined Line (m >1) */
        Line line2 = new Line(new Point(2, 2), new Point(4, 3));

        /* left-inclined Line (m <1) */
        Line line3 = new Line(new Point(5, 1), new Point(4, 3));

        Assert.assertTrue(line1.isIntersected(rect));
        Assert.assertTrue(line2.isIntersected(rect));
        Assert.assertTrue(line3.isIntersected(rect));

        Assert.assertFalse(line1.contains(rect));
        Assert.assertFalse(line2.contains(rect));
        Assert.assertFalse(line3.contains(rect));

    }

    @Test
    public void testLineIsInRectangle() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(3, 2, 6, 6);

        /* Horizontal Line */
        Line l1 = new Line(new Point(4, 3), new Point(5, 3));
        /* Inclined Line */
        Line l2 = new Line(new Point(3, 2), new Point(4, 3));
        /* Vertical Line */
        Line l3 = new Line(new Point(4, 3), new Point(4, 5));

        Assert.assertTrue(l1.contains(rect));
        Assert.assertTrue(l2.contains(rect));
        Assert.assertTrue(l3.contains(rect));
        Assert.assertTrue(l1.isIntersected(rect));
        Assert.assertTrue(l2.isIntersected(rect));
        Assert.assertTrue(l3.isIntersected(rect));


        /* Edge intersection lines */

        /* Horizontal Line */
        Line l4 = new Line(new Point(3, 3), new Point(6, 3));
        /* Inclined Line (m > 0)*/
        Line l5 = new Line(new Point(3, 2), new Point(6, 6));
        /* Inclined Line (m < 0)*/
        Line l6 = new Line(new Point(6, 2), new Point(3, 6));
        /* Vertical Line */
        Line l7 = new Line(new Point(4, 3), new Point(4, 6));

        Assert.assertTrue(l4.contains(rect));
        Assert.assertTrue(l5.contains(rect));
        Assert.assertTrue(l6.contains(rect));
        Assert.assertTrue(l7.contains(rect));

        Assert.assertTrue(l4.isIntersected(rect));
        Assert.assertTrue(l5.isIntersected(rect));
        Assert.assertTrue(l6.isIntersected(rect));
        Assert.assertTrue(l7.isIntersected(rect));

    }
}
