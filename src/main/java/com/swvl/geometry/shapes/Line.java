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
         */
        double thickness = Math.ulp(dy);

        /* Scale the perpendicular vector by factor = thickness*/
        double px = 0, py = 0;
        if (dy == 0) { // horizontal line then perpendicular is given by (dx, -dy) (rotation 45 clockwise)
            px = thickness * dx;
            py = thickness * -dy;
        } else if (dx == 0) { // vertical line then perpendicular is given by (-dx, dy)
            // TODO
        }
        System.out.println(dx + " " + dy + " " + px + " " + py);
        return new Rectangle(a.x - px, a.y - py, b.x + px, b.y + py);
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
        Point p1 = new Point(1, 2);
        Point p2 = new Point(3, 2);
        Line horizontalLine = new Line(p1, p2);

        Rectangle rect1 = horizontalLine.getMBR();
        System.out.println(rect1);
        System.out.println(rect1.isIntersected(p1));
        System.out.println(rect1.isIntersected(p2));

        Point p11 = new Point(1, 1);
        Point p22 = new Point(1, 3);
        Line verticalLine = new Line(p11, p22);

        Rectangle rect2 = verticalLine.getMBR();
        System.out.println(rect2);
        System.out.println(rect2.isIntersected(p1));
        System.out.println(rect2.isIntersected(p2));
    }
}
