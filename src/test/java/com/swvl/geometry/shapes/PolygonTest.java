package com.swvl.geometry.shapes;

import org.junit.Assert;
import org.junit.Test;

import javax.naming.OperationNotSupportedException;

public class PolygonTest {
    private static Polygon poly1;
    private static Polygon poly2;

    static {
        Point[] p1Points = new Point[]{
                new Point(0, 0),
                new Point(0, 10),
                new Point(5, 15),
                new Point(10, 10),
                new Point(10, 0),
                new Point(0, 0)
        };

        poly1 = new Polygon(p1Points);

        Point[] p2Points = new Point[]{
                new Point(2, 4),
                new Point(8, 5.01922),
                new Point(13.0211, 8.6311),
                new Point(20, 11.2211230),
                new Point(15.3312, 17.45311),
                new Point(20.00111, 20.5112),
                new Point(8.44444, 24.2222),
                new Point(5.11532, 20.111),
                new Point(8, 16),
                new Point(2, 4)
        };

        poly2 = new Polygon(p2Points);
    }

    @Test
    public void testGetCenterPoint() throws OperationNotSupportedException {
        Point p1 = poly1.getCenterPoint();
        Assert.assertTrue(poly1.contains(p1));

        Point p2 = poly2.getCenterPoint();
        Assert.assertTrue(poly2.contains(p2));
    }


    @Test
    public void testPointIntersectionAndContains() throws OperationNotSupportedException {
        Point p1 = new Point(20, 20);
        Assert.assertFalse(poly1.isIntersected(p1));
        Assert.assertFalse(poly1.contains(p1));

        Point p2 = new Point(5, 5);
        Assert.assertTrue(poly1.isIntersected(p2));
        Assert.assertTrue(poly1.contains(p2));

        Point p3 = new Point(5, 15);
        Assert.assertTrue(poly1.isIntersected(p3));
        Assert.assertTrue(poly1.contains(p3));

        Point p4 = new Point(10, 5);
        Assert.assertTrue(poly1.isIntersected(p4));
        Assert.assertTrue(poly1.contains(p4));

        Point p5 = new Point(4, 15);
        Assert.assertFalse(poly1.isIntersected(p5));
        Assert.assertFalse(poly1.contains(p5));

        Point p6 = new Point(-1, 6);
        Assert.assertFalse(poly1.isIntersected(p6));
        Assert.assertFalse(poly1.contains(p6));

        Point p7 = new Point(0, 0);
        Assert.assertTrue(poly1.isIntersected(p7));
        Assert.assertTrue(poly1.contains(p7));

        Point p8 = new Point(0, 10);
        Assert.assertTrue(poly1.isIntersected(p8));
        Assert.assertTrue(poly1.contains(p8));

        Point p9 = new Point(11, 10);
        Assert.assertFalse(poly1.isIntersected(p9));
        Assert.assertFalse(poly1.contains(p9));

        Point p10 = new Point(10.21111, 11.020101); // inside polygon
        Assert.assertTrue(poly2.isIntersected(p10));
        Assert.assertTrue(poly2.contains(p10));

        Point p11 = new Point(3.221, 6.442); // on edge
        Assert.assertTrue(poly2.isIntersected(p11));
        Assert.assertTrue(poly2.contains(p11));

        Point p12 = new Point(15.3312, 17.45311); // vertex
        Assert.assertTrue(poly2.isIntersected(p12));
        Assert.assertTrue(poly2.contains(p12));

        Point p13 = new Point(0, 0);
        Assert.assertFalse(poly2.isIntersected(p13));
        Assert.assertFalse(poly2.contains(p13));

        Point p14 = new Point(6.01011, 15.1211);
        Assert.assertFalse(poly2.isIntersected(p14));
        Assert.assertFalse(poly2.contains(p14));

        Point p15 = new Point(20, 10);
        Assert.assertFalse(poly2.isIntersected(p15));
        Assert.assertFalse(poly2.contains(p15));
    }

    @Test
    public void testLineIntersectionAndContains() throws OperationNotSupportedException {
        LineSegment l1 = new LineSegment(new Point(4.001211, 5.122414),
                new Point(20.121, 5.643)); // horizontal line intersect vertical edge
        Assert.assertTrue(poly1.isIntersected(l1));
        Assert.assertFalse(poly1.contains(l1));

        LineSegment l2 = new LineSegment(new Point(8.01221, 10),
                new Point(11.0121, 14.1121)); // incline line intersect edge
        Assert.assertTrue(poly1.isIntersected(l2));
        Assert.assertFalse(poly1.contains(l2));

        LineSegment l3 = new LineSegment(new Point(4.001211, 15),
                new Point(20.121, 15)); // horizontal line intersection with vertex
        Assert.assertTrue(poly1.isIntersected(l3));
        Assert.assertFalse(poly1.contains(l3));

        LineSegment l4 = new LineSegment(new Point(4, -10),
                new Point(4, 20)); // vertical line with two edge intersection
        Assert.assertTrue(poly1.isIntersected(l4));
        Assert.assertFalse(poly1.contains(l4));

        LineSegment l5 = new LineSegment(new Point(0, -1),
                new Point(4, -1));
        Assert.assertFalse(poly1.isIntersected(l5));
        Assert.assertFalse(poly1.contains(l5));

        LineSegment l6 = new LineSegment(new Point(10, 10),
                new Point(5, 15));  // edge = line
        Assert.assertTrue(poly1.isIntersected(l6));
        Assert.assertTrue(poly1.contains(l6));

        LineSegment l7 = new LineSegment(new Point(10, 10),
                new Point(4, 16)); // edge lies on line
        Assert.assertTrue(poly1.isIntersected(l7));
        Assert.assertFalse(poly1.contains(l7));

        LineSegment l8 = new LineSegment(new Point(4, 8),
                new Point(2, 20)); // start point on edge
        Assert.assertTrue(poly2.isIntersected(l8));
        Assert.assertFalse(poly2.contains(l8));

        LineSegment l9 = new LineSegment(new Point(20.00111, 20.5112),
                new Point(25, 25)); // start point = vertex
        Assert.assertTrue(poly2.isIntersected(l9));
        Assert.assertFalse(poly2.contains(l9));

        LineSegment l10 = new LineSegment(new Point(1, 2),
                new Point(4, 8)); // line in polygon
        Assert.assertTrue(poly1.contains(l10));
        Assert.assertTrue(poly1.isIntersected(l10));

        LineSegment l11 = new LineSegment(new Point(0, 10),
                new Point(10, 10)); // line in polygon and intersect two vertices
        Assert.assertTrue(poly1.isIntersected(l11));
        Assert.assertTrue(poly1.contains(l11));

        LineSegment l12 = new LineSegment(new Point(8, 16),
                new Point(20.00111, 20.5112));
        Assert.assertTrue(poly2.isIntersected(l12));
        Assert.assertTrue(poly2.contains(l12));
    }


    @Test
    public void testRectangleIntersectionAndContains() {

    }

    @Test
    public void testPolygonIntersectionAndContains() {

    }
}
