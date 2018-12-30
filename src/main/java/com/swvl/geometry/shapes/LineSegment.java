package com.swvl.geometry.shapes;

import com.swvl.geometry.Utilities;
import org.apache.hadoop.io.Text;

import javax.naming.OperationNotSupportedException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LineSegment extends Shape {
    /* End points of a Line Segment*/
    public Point p1;
    public Point p2;

    /* LineSegment equation params ax + by + c = 0*/
    public double a;
    public double b;
    public double c;

    public LineSegment() {

    }

    /**
     * Validate the two end points of line segment are initialized and calculate
     * the line segment equation.
     */
    private void validate() {
        if (p1 == null || p2 == null)
            throw new IllegalArgumentException("The two points of Line Segment are not" +
                    "Initialized.");


        if (Math.abs(p1.x - p2.x) < EPS) { // vertical line
            this.a = 1;
            this.b = 0;
            this.c = -p1.x;
        } else {
            this.a = -((p2.y - p1.y) / (p2.x - p1.x));
            this.b = 1;  // fix value of b to 1
            this.c = -a * p1.x - p1.y;
        }
    }

    public LineSegment(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;

        validate();
    }

    public void set(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;

        validate();
    }

    @Override
    public Rectangle getMBR() {
        validate();
        return new Rectangle(p1, p2);
    }

    @Override
    public double distanceTo(Point p) {
        validate();

        /* transform line ap to vector*/
        double apx = p.x - p1.x;
        double apy = p.y - p1.y;

        /* transform line ab to vector*/
        double abx = p2.x - p1.x;
        double aby = p2.y - p1.y;

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

        /* use pythagoras to calculate perpendicular distance between ap and ab*/
        if (u < 0.0) // closer to a
            return p.distanceTo(p1); // Euclidean distance between p and a


        if (u > 1.0)  // closer to b
            return p.distanceTo(p2); // Euclidean distance between p and b

        /*
         * Translate point a by a scaled magnitude u of vector ab
         * (multiplying ab^ by u to get scaled vector in ab direction)
         */
        double cx = p1.x + (ux * u);
        double cy = p1.y + (uy * u);
        return p.distanceTo(new Point(cx, cy));
    }

    @Override
    public boolean isIntersected(Shape shape) throws OperationNotSupportedException {
        validate();

        if (shape instanceof Point)
            return Utilities.lineSegmentPointIntersection((Point) shape, this);

        if (shape instanceof Rectangle)
            return shape.isIntersected(this);

        if (shape instanceof LineSegment)
            return isLineSegmentIntersection((LineSegment) shape);

        if (shape instanceof Polygon)
            return shape.isIntersected(this);

        throw new OperationNotSupportedException("isIntersected operation in LineSegment does not support " + shape.getClass());
    }


    private boolean isLineSegmentIntersection(LineSegment line) throws OperationNotSupportedException {

        /* Check for general case that line segments intersect at boundaries*/
        if (this.p1.equals(line.p1)
                || this.p1.equals(line.p2)
                || this.p2.equals(line.p1)
                || this.p2.equals(line.p2))
            return true;

        /* solve simultaneous equation of two 2 line equation with two unknowns (x,y) */
        Point p = new Point();
        p.x = (line.b * this.c - this.b * line.c) / (line.a * this.b - this.a * line.b);

        /* check vertical line (b=0) to avoid dividing by zero */
        if (this.b < EPS && this.b > -EPS)
            p.y = -(line.a * p.x) - line.c; // invoker is a vertical line so calculate y from line
        else
            p.y = -(this.a * p.x) - this.c;

        /* Check that intersection point is on both lines */
        return this.isIntersected(p) && line.isIntersected(p);
    }

    @Override
    public Shape clone() {
        validate();
        return new LineSegment(new Point(p1.x, p1.y),
                new Point(p2.x, p2.y));
    }

    @Override
    public Point getCenterPoint() {
        validate();
        return new Point(
                (p1.x + p2.x) / 2,
                (p1.y + p2.y) / 2);
    }

    @Override
    public boolean contains(Shape shape) throws OperationNotSupportedException {
        validate();

        if (shape instanceof Point)
            return isIntersected(shape);


        if (shape instanceof LineSegment) {
            /* Check that both points of line exist on invoker line */
            LineSegment line = (LineSegment) shape;
            return this.isIntersected(line.p1)
                    && this.isIntersected(line.p2);
        }

        if (shape instanceof Rectangle)
            throw new OperationNotSupportedException("Check if LineSegment contains" +
                    " a Rectangle is a fatal error");

        if (shape instanceof Polygon)
            throw new OperationNotSupportedException("Check if LineSegment contains" +
                    " a Polygon is a fatal error");

        throw new OperationNotSupportedException("Contains operation in LineSegment does not support " + shape.getClass());
    }

    @Override
    public boolean isEdgeIntersection(Shape shape) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("isEdgeIntersection is not supported for line");
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        validate();

        p1.write(dataOutput);
        p2.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        p1 = new Point();
        p1.readFields(dataInput);

        p2 = new Point();
        p2.readFields(dataInput);

        validate();
    }

    @Override
    public Text toText(Text text) {
        validate();

        p1.toText(text);
        p2.toText(text);
        return text;
    }

    @Override
    public void fromText(Text text) {
        p1 = new Point();
        p1.fromText(text);

        p2 = new Point();
        p2.fromText(text);

        validate();
    }

    @Override
    public boolean equals(Object obj) {
        validate();

        if (!(obj instanceof LineSegment))
            return false;

        LineSegment line = (LineSegment) obj;

        return this.p1.equals(line.p1)
                && this.p2.equals(line.p2);
    }
}
