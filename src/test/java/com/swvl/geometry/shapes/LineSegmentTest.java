package com.swvl.geometry.shapes;

import org.junit.Assert;
import org.junit.Test;

import javax.naming.OperationNotSupportedException;

public class LineSegmentTest {
    @Test
    public void testGetCenterPoint() {
        /* Horizontal line */
        LineSegment l1 = new LineSegment(new Point(3, 4), new Point(5, 4));
        Point mid1 = new Point(4, 4);
        Assert.assertEquals(l1.getCenterPoint(), mid1);

        /* Vertical LineSegment */
        LineSegment l2 = new LineSegment(new Point(3, 5), new Point(3, 9));
        Point mid2 = new Point(3, 7);
        Assert.assertEquals(l2.getCenterPoint(), mid2);

        /* Incline LineSegment (m > 0) */
        LineSegment l3 = new LineSegment(new Point(1, 2), new Point(2, 4));
        Point mid3 = new Point(1.5, 3);
        Assert.assertEquals(l3.getCenterPoint(), mid3);

        /* Incline LineSegment (m < 0) */
        LineSegment l4 = new LineSegment(new Point(2, 1), new Point(1, 2));
        Point mid4 = new Point(1.5, 1.5);
        Assert.assertEquals(l4.getCenterPoint(), mid4);
    }

    @Test
    public void testToDistance() {
        Point p1 = new Point(3, 3);
        Point a1 = new Point(2, 1);
        Point b1 = new Point(4, 1);
        LineSegment l1 = new LineSegment(a1, b1);
        Assert.assertTrue(checkAccuracyOfPointDistanceUsingTrigonometry(l1, p1));

        Point p2 = new Point(3, 3);
        Point a2 = new Point(1, 2);
        Point b2 = new Point(1, 4);
        LineSegment l2 = new LineSegment(a2, b2);
        Assert.assertTrue(checkAccuracyOfPointDistanceUsingTrigonometry(l2, p2));

        Point p3 = new Point(0, 2);
        Point a3 = new Point(1, 2);
        Point b3 = new Point(2, 4);
        LineSegment l3 = new LineSegment(a3, b3);
        Assert.assertTrue(checkAccuracyOfPointDistanceUsingTrigonometry(l3, p3));


        Point p4 = new Point(1.5, 0);
        Point a4 = new Point(1, 2);
        Point b4 = new Point(2, 4);
        LineSegment l4 = new LineSegment(a4, b4);
        Assert.assertTrue(checkAccuracyOfPointDistanceUsingTrigonometry(l4, p4));
    }


    /**
     * Check accuracy of distance from point to line using Trigonometry
     *
     * @param line represented by two points endPoint e and startPoint s
     * @param p    point to calculate distance from it
     */
    private boolean checkAccuracyOfPointDistanceUsingTrigonometry(LineSegment line, Point p) {
        /* Calculate vector ep */
        double epx = p.x - line.p1.x;
        double epy = p.y - line.p1.y;

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

        return Math.abs(actualAjacent - expectedAdjacent) < Shape.EPS;
    }


    @Test
    public void testIntersectionWithPoint() throws OperationNotSupportedException {
        LineSegment line = new LineSegment(new Point(1, 2), new Point(4, 8));

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
        LineSegment line = new LineSegment(new Point(1, 2), new Point(4, 8));

        /* y = -2x+8 */
        LineSegment l1 = new LineSegment(new Point(-1, 10), new Point(4, 0));
        Assert.assertTrue(line.isIntersected(l1)); // intersection at p(2,4)

        LineSegment l2 = new LineSegment(new Point(6, 1), new Point(3, 6));
        Assert.assertTrue(line.isIntersected(l2)); // intersection at p(3,6)

        /* y = 8 */
        LineSegment l3 = new LineSegment(new Point(1, 8), new Point(6, 8));
        Assert.assertTrue(line.isIntersected(l3)); // intersection at p(4,8)

        /* x = 4 */
        LineSegment l4 = new LineSegment(new Point(4, 2), new Point(4, 10));
        Assert.assertTrue(line.isIntersected(l4)); // intersection at p(4,8)

        /* y = 2 */
        LineSegment l5 = new LineSegment(new Point(0, 2), new Point(6, 2));
        Assert.assertTrue(line.isIntersected(l5)); // intersection at p(1,2)

        /* x = 1 */
        LineSegment l6 = new LineSegment(new Point(1, 0), new Point(1, 10));
        Assert.assertTrue(line.isIntersected(l6)); // intersection at p(1,2)

        /* y = 4x */
        LineSegment l7 = new LineSegment(new Point(1, 4), new Point(4, 16));
        Assert.assertFalse(line.isIntersected(l7));

        /* y = 2x */
        LineSegment l8 = new LineSegment(new Point(4, 8), new Point(10, 20));
        Assert.assertTrue(line.isIntersected(l8));  // intersection at p(4,8)

        /* y = 2x */
        LineSegment l9 = new LineSegment(new Point(6.000211, 8), new Point(10, 20));
        Assert.assertFalse(line.isIntersected(l9));
    }

    @Test
    public void testContainsWithLine() throws OperationNotSupportedException {
        /* y= 2x */
        LineSegment line = new LineSegment(new Point(1, 2), new Point(4, 8));

        /* y = -2x+8 */
        LineSegment l1 = new LineSegment(new Point(-1, 10), new Point(4, 0));
        Assert.assertFalse(line.contains(l1)); // intersection at p(2,4)

        LineSegment l2 = new LineSegment(new Point(6, 1), new Point(3, 6));
        Assert.assertFalse(line.contains(l2)); // intersection at p(3,6)

        /* y = 8 */
        LineSegment l3 = new LineSegment(new Point(1, 8), new Point(6, 8));
        Assert.assertFalse(line.contains(l3)); // intersection at p(4,8)

        /* x = 4 */
        LineSegment l4 = new LineSegment(new Point(4, 2), new Point(4, 10));
        Assert.assertTrue(line.isIntersected(l4)); // intersection at p(4,8)

        /* y = 2 */
        LineSegment l5 = new LineSegment(new Point(0, 2), new Point(6, 2));
        Assert.assertTrue(line.isIntersected(l5)); // intersection at p(1,2)

        /* x = 1 */
        LineSegment l6 = new LineSegment(new Point(1, 0), new Point(1, 10));
        Assert.assertFalse(line.contains(l6)); // intersection at p(1,2)

        /* y = 4x */
        LineSegment l7 = new LineSegment(new Point(1, 4), new Point(4, 16));
        Assert.assertFalse(line.contains(l7));

        /* y = 2x */
        LineSegment l8 = new LineSegment(new Point(4, 8), new Point(10, 20));
        Assert.assertFalse(line.contains(l8));  // intersection at p(4,8)

        /* y = 2x */
        LineSegment l9 = new LineSegment(new Point(6.000211, 8), new Point(10, 20));
        Assert.assertFalse(line.contains(l9));

        /* y = 2x */
        LineSegment l10 = new LineSegment(new Point(2, 4), new Point(3, 6));
        Assert.assertTrue(line.contains(l10));

        /* y = 2x */
        LineSegment l11 = new LineSegment(new Point(2, 4), new Point(5, 10));
        Assert.assertFalse(line.contains(l11));

        /* y = 2x */
        LineSegment l12 = new LineSegment(new Point(1, 2), new Point(4, 8));
        Assert.assertTrue(line.contains(l12));

        /* y = 2x */
        LineSegment l13 = new LineSegment(new Point(0, 0), new Point(3, 6));
        Assert.assertFalse(line.contains(l13));
    }

    /**
     * LineSegment intersect the left side of rectangle
     */
    @Test(expected = OperationNotSupportedException.class)
    public void testIntersectionWithRectangleLeftSide() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(3, 2, 6, 4);

        /* horizontal line (m=0) */
        LineSegment line1 = new LineSegment(new Point(2, 3), new Point(4, 3));

        /*  right-inclined LineSegment (m >1) */
        LineSegment line2 = new LineSegment(new Point(2, 2), new Point(4, 3));

        /* left-inclined LineSegment (m <1) */
        LineSegment line3 = new LineSegment(new Point(4, 3), new Point(2, 5));


        Assert.assertTrue(line1.isIntersected(rect));
        Assert.assertTrue(line2.isIntersected(rect));
        Assert.assertTrue(line3.isIntersected(rect));

        /* Should throw exception because line segment does not contain rectangle! */
        line1.contains(rect);
        line2.contains(rect);
        line3.contains(rect);
    }

    /**
     * LineSegment intersect the right side of rectangle
     */
    @Test(expected = OperationNotSupportedException.class)
    public void testIntersectionWithRectangleRightSide() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(3, 2, 6, 4);

        /* horizontal line (m=0) */
        LineSegment line1 = new LineSegment(new Point(7, 3), new Point(4, 3));
        /*  right-inclined LineSegment (m >1) */
        LineSegment line2 = new LineSegment(new Point(4, 3), new Point(7, 4));
        /* left-inclined LineSegment (m <1) */
        LineSegment line3 = new LineSegment(new Point(7, 1), new Point(3, 3));

        Assert.assertTrue(line1.isIntersected(rect));
        Assert.assertTrue(line2.isIntersected(rect));
        Assert.assertTrue(line3.isIntersected(rect));

        /* Should throw exception because line segment does not contain rectangle! */
        line1.contains(rect);
        line2.contains(rect);
        line3.contains(rect);
    }

    /**
     * LineSegment intersect the upper side of rectangle
     */
    @Test(expected = OperationNotSupportedException.class)
    public void testIntersectionWithRectangleUpperSide() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(3, 2, 6, 4);

        /* vertical line (m not defined) */
        LineSegment line1 = new LineSegment(new Point(4, 3), new Point(4, 5));

        /*  right-inclined LineSegment (m >1) */
        LineSegment line2 = new LineSegment(new Point(4, 3), new Point(5, 6));

        /* left-inclined LineSegment (m <1) */
        LineSegment line3 = new LineSegment(new Point(5, 3), new Point(4, 6));

        Assert.assertTrue(line1.isIntersected(rect));
        Assert.assertTrue(line2.isIntersected(rect));
        Assert.assertTrue(line3.isIntersected(rect));

        /* Should throw exception because line segment does not contain rectangle! */
        line1.contains(rect);
        line2.contains(rect);
        line3.contains(rect);
    }


    /**
     * LineSegment intersect the bottom side of rectangle
     */
    @Test(expected = OperationNotSupportedException.class)
    public void testIntersectionWithRectangleBottomSide() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(3, 2, 6, 4);

        /* vertical line (m not defined) */
        LineSegment line1 = new LineSegment(new Point(4, 1), new Point(4, 3));

        /*  right-inclined LineSegment (m >1) */
        LineSegment line2 = new LineSegment(new Point(2, 2), new Point(4, 3));

        /* left-inclined LineSegment (m <1) */
        LineSegment line3 = new LineSegment(new Point(5, 1), new Point(4, 3));

        Assert.assertTrue(line1.isIntersected(rect));
        Assert.assertTrue(line2.isIntersected(rect));
        Assert.assertTrue(line3.isIntersected(rect));

        /* Should throw exception because line segment does not contain rectangle! */
        line1.contains(rect);
        line2.contains(rect);
        line3.contains(rect);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void testLineIsInRectangle() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(3, 2, 6, 6);

        /* Horizontal LineSegment */
        LineSegment l1 = new LineSegment(new Point(4, 3), new Point(5, 3));
        /* Inclined LineSegment */
        LineSegment l2 = new LineSegment(new Point(3, 2), new Point(4, 3));
        /* Vertical LineSegment */
        LineSegment l3 = new LineSegment(new Point(4, 3), new Point(4, 5));

        Assert.assertTrue(l1.contains(rect));
        Assert.assertTrue(l2.contains(rect));
        Assert.assertTrue(l3.contains(rect));
        Assert.assertTrue(l1.isIntersected(rect));
        Assert.assertTrue(l2.isIntersected(rect));
        Assert.assertTrue(l3.isIntersected(rect));


        /* Edge intersection lines */

        /* Horizontal LineSegment */
        LineSegment l4 = new LineSegment(new Point(3, 3), new Point(6, 3));
        /* Inclined LineSegment (m > 0)*/
        LineSegment l5 = new LineSegment(new Point(3, 2), new Point(6, 6));
        /* Inclined LineSegment (m < 0)*/
        LineSegment l6 = new LineSegment(new Point(6, 2), new Point(3, 6));
        /* Vertical LineSegment */
        LineSegment l7 = new LineSegment(new Point(4, 3), new Point(4, 6));

        /* Should throw exception because line segment does not contain rectangle! */
        l4.contains(rect);
        l5.contains(rect);
        l6.contains(rect);
        l7.contains(rect);

        Assert.assertTrue(l4.isIntersected(rect));
        Assert.assertTrue(l5.isIntersected(rect));
        Assert.assertTrue(l6.isIntersected(rect));
        Assert.assertTrue(l7.isIntersected(rect));

        /* Parallel lines */
        LineSegment l8 = new LineSegment(new Point(1, 0), new Point(1, 20));
        LineSegment l9 = new LineSegment(new Point(10, 40), new Point(10, 80));

        Assert.assertFalse(l8.isIntersected(l9));
        Assert.assertFalse(l8.contains(l9));

        Assert.assertEquals(l8.a, l9.a, 0.0);
        Assert.assertEquals(l8.b, l9.b, 0.0);


        LineSegment l10 = new LineSegment(new Point(1, 2), new Point(4, 8));
        LineSegment l11 = new LineSegment(new Point(3, 6), new Point(5, 10));

        System.out.println(l10.isIntersected(l11));

    }
}
