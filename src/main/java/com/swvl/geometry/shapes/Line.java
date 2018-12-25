package com.swvl.geometry.shapes;

import org.apache.hadoop.io.Text;

import javax.naming.OperationNotSupportedException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Line extends Shape {
    Point startPoint; // Start point of line segment
    Point endPoint; // End point of line segment

    /* Line equation params ax + by + c = 0*/
    double a;
    double b;
    double c;

    public Line() {

    }

    private void init(Point p1, Point p2) {
        if (p1.x - p2.x < Point.EPS && p1.y - p2.y < Point.EPS) {
            this.startPoint = p1;
            this.endPoint = p2;
        } else {
            this.startPoint = p2;
            this.endPoint = p1;
        }

        if (Math.abs(p1.x - p2.x) < Point.EPS) { // vertical line
            this.a = 1;
            this.b = 0;
            this.c = -p1.x;
        } else {
            this.a = -((endPoint.y - startPoint.y) / (endPoint.x - startPoint.x));
            this.b = 1;  // fix value of b to 1
            this.c = -(a * startPoint.x) - startPoint.y;
        }
    }

    public Line(Point a, Point b) {
        init(a, b);
    }

    @Override
    public Rectangle getMBR() {
        /* Calculate vector of line */
        double dx = Math.abs(endPoint.x - startPoint.x); //delta x
        double dy = Math.abs(endPoint.y - startPoint.y); //delta y

        /* Calculate the unit vector (dx, dy) pointing in the direction of the line  */
        double vectorMagnitude = Math.sqrt(dx * dx + dy * dy);
        dx /= vectorMagnitude;
        dy /= vectorMagnitude;

        /*
         * Calculate the perpendicular vector, which is given by (-dy, dx)
         *
         * Math#ulp(double)} to compute the smallest non-zero thickness that
         *
         * Rotation Matrix r = {{cosθ, -sinθ}, {sinθ, cosθ}}
         * px = dx*cos(90) - dy*sin(90) = 0 - dy = -dy
         * py = dx*sin(90) + dy*cos(90) = dx + 0 = dx
         */
        double px = -dy;
        double py = dx;

        /* Scale the perpendicular vector by factor = thickness*/
        double thickness = Math.ulp(dy);
        px *= thickness;
        py *= thickness;

        /* We choose to fix the maxPoint. For fixing minPoint, maxPoint = (b.x + px, b.y = a.y + py) */
        return new Rectangle(
                startPoint.x - px, startPoint.y - py, // minPoint (bottom-left) is at the bottom-right of a
                endPoint.x, endPoint.y); // the maxPoint (top-right) is fixed
    }

    @Override
    public double distanceTo(Point p) {
        /* transform line ap to vector*/
        double apx = p.x - startPoint.x;
        double apy = p.y - startPoint.y;

        /* transform line ab to vector*/
        double abx = endPoint.x - startPoint.x;
        double aby = endPoint.y - startPoint.y;

        /* Calculate unit vector ab^ of vector ab */
        double vectorMagnitude = Math.sqrt(abx * abx + aby * aby);
        double ux = abx / vectorMagnitude;
        double uy = aby / vectorMagnitude;

        /*
         * Get the scalar projection b of vector ap on ab.
         * s = ||ap|| * cosθ =  dotProduct(ap, ab^) / ||ab^||
         * We use the unit vector of ab because magnitude of unit vector = 1
         */
        double u = apx * ux + apy * uy;


        Point c;
        /* use pythagoras to calculate perpendicular distance between ap and ab*/
        if (u < 0.0) // closer to a
            return p.distanceTo(startPoint); // Euclidean distance between p and a


        if (u > 1.0)  // closer to b
            return p.distanceTo(endPoint); // Euclidean distance between p and b

        /*
         * Translate point a by a scaled magnitude u of vector ab
         * (multiplying ab^ by u to get scaled vector in ab direction)
         */
        double cx = startPoint.x + (ux * u);
        double cy = startPoint.y + (uy * u);
        return p.distanceTo(new Point(cx, cy));
    }

    @Override
    public boolean isIntersected(Shape s) throws OperationNotSupportedException {
        if (s instanceof Point)
            return isPointIntersection((Point) s);

        return false;
    }

    /**
     * Check if a point is on the line segment be checking that:
     * 1- Point lies on the line (by substituting point.x in line equation)
     * 2- Point lies between a and b
     *
     * @param point point to checked for intersection
     * @return true if point intersect line segment and false otherwise
     */
    private boolean isPointIntersection(Point point) {
        /* Check that point is on line */
        if (b != 0) {
            double y = -(a * point.x) - c;

            if (y != point.y) // point is not on the line
                return false;
        } else {
            if (point.x != -c) // vertical line and x != -c
                return false;
        }


        /* Check if point is between a and b */
        double ab = startPoint.distanceTo(endPoint);
        double ap = startPoint.distanceTo(point);
        double pb = point.distanceTo(endPoint);

        /* point between a and b if dist(a,p) + dist(p, b) == dist(a,b)*/
        return ap + pb == ab;
    }

    @Override
    public Shape clone() {
        return null;
    }

    @Override
    public Point getCenterPoint() {
        return new Point(
                (startPoint.x + endPoint.x) / 2,
                (startPoint.y + endPoint.y) / 2);
    }

    @Override
    public boolean contains(Shape shape) throws OperationNotSupportedException {
        return false;
    }

    @Override
    public boolean isEdgeIntersection(Shape shape) throws OperationNotSupportedException {
        return false;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

    }

    @Override
    public Text toText(Text text) {
        return null;
    }

    @Override
    public void fromText(Text text) {

    }

    public static void main(String[] args) throws OperationNotSupportedException {
        Point p11 = new Point(1, 2);
        Point p21 = new Point(3, 2);
        Line horizontalLine = new Line(p11, p21);

        Rectangle rect1 = horizontalLine.getMBR();
        System.out.println(rect1);
        System.out.println(rect1.isIntersected(p11));
        System.out.println(rect1.isIntersected(p21));

        Point p12 = new Point(1, 1);
        Point p22 = new Point(1, 3);
        Line verticalLine = new Line(p12, p22);

        Rectangle rect2 = verticalLine.getMBR();
        System.out.println(rect2);
        System.out.println(rect2.isIntersected(p12));
        System.out.println(rect2.isIntersected(p22));

        Point p13 = new Point(1, 1);
        Point p23 = new Point(2, 7);
        Line inclineLine = new Line(p13, p23);

        Rectangle rect3 = inclineLine.getMBR();
        System.out.println(rect3);
        System.out.println(rect3.isIntersected(p13));
        System.out.println(rect3.isIntersected(p23));


        Point p1 = new Point(3, 3);
        Point a1 = new Point(2, 1);
        Point b1 = new Point(4, 1);
        Line l1 = new Line(a1, b1);
        System.out.println(l1.distanceTo(p1));
        System.out.println("Center Point => " + l1.getCenterPoint());
        System.out.println();

        Point p2 = new Point(3, 3);
        Point a2 = new Point(1, 2);
        Point b2 = new Point(1, 4);
        Line l2 = new Line(a2, b2);
        System.out.println(l2.distanceTo(p2));
        System.out.println("Center Point => " + l2.getCenterPoint());
        System.out.println();

        Point p3 = new Point(0, 2);
        Point a3 = new Point(1, 2);
        Point b3 = new Point(2, 4);
        Line l3 = new Line(a3, b3);
        System.out.println(l3.distanceTo(p3));
        System.out.println("Center Point => " + l3.getCenterPoint());
        System.out.println();


        Point p4 = new Point(4, 2);
        Point a4 = new Point(2, 1);
        Point b4 = new Point(4, 1);
        Line l4 = new Line(a4, b4);
        System.out.println(l4.distanceTo(p4));
    }
}
