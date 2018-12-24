package com.swvl.geometry.shapes;

import org.apache.hadoop.io.Text;

import javax.naming.OperationNotSupportedException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Line extends Shape {
    Point a, b;

    public Line() {

    }

    public Line(Point a, Point b) {
        if (a.isSTE(b)) {
            this.a = a;
            this.b = b;
        } else {
            this.a = b;
            this.b = a;
        }
    }

    @Override
    public Rectangle getMBR() {
        /* Calculate vector of line */
        double dx = Math.abs(b.x - a.x); //delta x
        double dy = Math.abs(b.y - a.y); //delta y

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
                a.x - px, a.y - py, // minPoint (bottom-left) is at the bottom-right of a
                b.x, b.y); // the maxPoint (top-right) is fixed
    }

    @Override
    public double distanceTo(Point p) {
        return 0;
    }

    @Override
    public boolean isIntersected(Shape s) throws OperationNotSupportedException {
        return false;
    }

    @Override
    public Shape clone() {
        return null;
    }

    @Override
    public Point getCenterPoint() {
        return null;
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
    }
}
