package com.swvl.geometry.shapes;

import org.junit.Assert;
import org.junit.Test;

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
}
