package com.swvl.geometry.shapes;

import org.junit.Assert;
import org.junit.Test;

import javax.naming.OperationNotSupportedException;

public class RectangleTest {

    @Test
    public void expandTest() throws OperationNotSupportedException {
        Point point1 = new Point(30.4259967, 30.7862083);
        Point point2 = new Point(32.2359967, 32.5661083);

        Rectangle expandedRect = new Rectangle(Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
        expandedRect.expand(point1);
        expandedRect.expand(point2);

        Assert.assertTrue(expandedRect.isIntersected(point1));
        Assert.assertTrue(expandedRect.isIntersected(point2));
        Assert.assertTrue(expandedRect.contains(point1));
        Assert.assertTrue(expandedRect.contains(point2));
    }

    @Test
    public void containsTest() throws OperationNotSupportedException {
        Point point1 = new Point(30.4259967, 30.7862083);
        Point point2 = new Point(32.2359967, 32.5661083);
        Rectangle rectangle = new Rectangle(point1.clone(), point2.clone());

        Point point3 = new Point(31.0, 31.0);
        Assert.assertTrue(rectangle.contains(point1));
        Assert.assertTrue(rectangle.contains(point2));
        Assert.assertTrue(rectangle.contains(point3));

        Point point4 = new Point(33.0, 33.0);
        Point point5 = new Point(30.0, 30.0);

        Assert.assertFalse(rectangle.contains(point4));
        Assert.assertFalse(rectangle.contains(point5));
    }

    @Test
    public void intersectionTest() throws OperationNotSupportedException {
        Point point1 = new Point(30.4259967, 30.7862083);
        Point point2 = new Point(32.2359967, 32.5661083);
        Rectangle rectangle = new Rectangle(point1.clone(), point2.clone());

        Point point3 = new Point(31.0, 31.0);
        Assert.assertTrue(rectangle.isIntersected(point1));
        Assert.assertTrue(rectangle.isIntersected(point2));
        Assert.assertTrue(rectangle.isIntersected(point3));

        Point point4 = new Point(33.0, 33.0);
        Point point5 = new Point(30.0, 30.0);

        Assert.assertFalse(rectangle.isIntersected(point4));
        Assert.assertFalse(rectangle.isIntersected(point5));
    }

    @Test
    public void testContains2() throws OperationNotSupportedException {
        Rectangle rect = new Rectangle(2.0, 2.0, 4.0, 4.0);

        Assert.assertFalse(rect.contains(new Point(1, 2)));
        Assert.assertFalse(rect.contains(new Point(1, 3)));
        Assert.assertFalse(rect.contains(new Point(1, 4)));

        // to the bottom of minPoint
        Assert.assertFalse(rect.contains(new Point(2, 1)));
        Assert.assertFalse(rect.contains(new Point(1, 4)));
        Assert.assertFalse(rect.contains(new Point(5, 1)));

        // to the right of maxPoint
        Assert.assertFalse(rect.contains(new Point(5, 4)));
        Assert.assertFalse(rect.contains(new Point(5, 3)));
        Assert.assertFalse(rect.contains(new Point(5, 2)));

        // above of maxPoint
        Assert.assertFalse(rect.contains(new Point(2, 5)));
        Assert.assertFalse(rect.contains(new Point(3, 6)));
        Assert.assertFalse(rect.contains(new Point(3.2, 4.1)));

        // above maxPoint
        Assert.assertFalse(rect.contains(new Point(4, 5)));
        Assert.assertFalse(rect.contains(new Point(3, 5)));
        Assert.assertFalse(rect.contains(new Point(6, 5)));

        // On bottom border
        Assert.assertTrue(rect.contains(new Point(3, 2)));
        Assert.assertTrue(rect.contains(new Point(4, 2)));

        // On left border
        Assert.assertTrue(rect.contains(new Point(2, 3)));
        Assert.assertTrue(rect.contains(new Point(2, 4)));

        // On upper border
        Assert.assertTrue(rect.contains(new Point(3, 4)));

        // On right border
        Assert.assertTrue(rect.contains(new Point(4, 3)));

        // same minPoint
        Assert.assertTrue(rect.contains(new Point(2, 2)));

        // same maxPoint
        Assert.assertTrue(rect.contains(new Point(4, 4)));

        // inside rectangle
        Assert.assertTrue(rect.contains(new Point(3, 3)));
        Assert.assertTrue(rect.contains(new Point(2.5, 2.4)));
        Assert.assertTrue(rect.contains(new Point(3.1, 2.1)));
        Assert.assertTrue(rect.contains(new Point(3.9, 3.9)));
        Assert.assertTrue(rect.contains(new Point(2.001, 2.0001)));
    }

    @Test
    public void testIntersection2() throws OperationNotSupportedException {
        Rectangle rect1 = new Rectangle(3.4259967, 2.4259967, 6.4259967, 4.4259967);
        Rectangle rect2 = new Rectangle(2.4259967, 4.4259967, 6.4259967, 6.4259967);

        /* Make rectangles open bounded */
        rect1.maxPoint.x += Point.EPS;
        rect1.maxPoint.y += Point.EPS;
        rect2.maxPoint.x += Point.EPS;
        rect2.maxPoint.y += Point.EPS;

        Assert.assertTrue(rect1.contains(new Point(6.0, 4.0)));
        Assert.assertFalse(rect2.contains(new Point(6.0, 4.0)));
    }


    @Test
    public void edgeintersectionTest() throws OperationNotSupportedException {
        Rectangle range = new Rectangle(3.0, 2.0, 6.0, 4.0);
        Rectangle rect = new Rectangle(2.0, 4.0, 6.0, 6.0);

        Assert.assertFalse(range.isIntersected(rect));
        Assert.assertTrue(range.isEdgeIntersection(rect));

        Assert.assertFalse(rect.isIntersected(range));
        Assert.assertTrue(rect.isEdgeIntersection(range));

        Point point = new Point(6.0, 4.0);
        Assert.assertTrue(range.isIntersected(point));
        Assert.assertTrue(rect.isIntersected(point));
    }

    @Test
    public void testUpperOpenBoundedContains() {
        Rectangle rect = new Rectangle(2.0, 2.0, 4.0, 4.0);

        // to the left of minPoint
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(1, 2)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(1, 3)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(1, 4)));

        // to the bottom of minPoint
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(2, 1)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(1, 4)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(5, 1)));

        // to the right of maxPoint
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(5, 4)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(5, 3)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(5, 2)));

        // above of maxPoint
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(2, 5)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(3, 6)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(3.2, 4.1)));

        // above maxPoint
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(4, 5)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(3, 5)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(6, 5)));

        // On bottom border
        Assert.assertTrue(rect.upperOpenBoundedContains(new Point(3, 2)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(4, 2)));

        // On left border
        Assert.assertTrue(rect.upperOpenBoundedContains(new Point(2, 3)));
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(2, 4)));

        // On upper border
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(3, 4)));

        // On right border
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(4, 3)));

        // same minPoint
        Assert.assertTrue(rect.upperOpenBoundedContains(new Point(2, 2)));

        // same maxPoint
        Assert.assertFalse(rect.upperOpenBoundedContains(new Point(4, 4)));

        // inside rectangle
        Assert.assertTrue(rect.upperOpenBoundedContains(new Point(3, 3)));
        Assert.assertTrue(rect.upperOpenBoundedContains(new Point(2.5, 2.4)));
        Assert.assertTrue(rect.upperOpenBoundedContains(new Point(3.1, 2.1)));
        Assert.assertTrue(rect.upperOpenBoundedContains(new Point(3.9, 3.9)));
        Assert.assertTrue(rect.upperOpenBoundedContains(new Point(2.001, 2.0001)));
    }

}
