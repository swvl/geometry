package com.swvl.geometry.shapes;

import com.swvl.geometry.Utilities;
import org.apache.hadoop.io.Text;

import javax.naming.OperationNotSupportedException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Implementation of a Line Segment.
 *
 * @author Hatem Morgan
 */
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

        double minX, minY, maxX, maxY;

        minX = Math.min(p1.x, p2.x);
        maxX = p1.x + p2.x - minX;

        minY = Math.min(p1.y, p2.y);
        maxY = p1.y + p2.y - minY;

        return new Rectangle(minX, minY, maxX, maxY);
    }

    @Override
    public double distanceTo(Point p) throws OperationNotSupportedException {
        validate();

        if (contains(p)) // point on line segment
            return 0;

        Vector p1p = new Vector(p1, p); // transform line p1p to vector
        Vector p1p2 = new Vector(p1, p2); // transform line p1p2 to vector

        /* Calculate unit vector p1p2^ of vector p1p2 */
        Vector p1p2UnitVector = p1p2.unitVector();

        /*
         * Get the scalar projection b of vector p1p on p1p1.
         * s = ||p1p|| * cosθ =  dotProduct(p1p, p1p2^) / ||p1p2^||
         * We use the unit vector of p1p2 because magnitude of unit vector = 1
         */
        double res = p1p.dot(p1p2UnitVector);
        double vectorMagnitude = p1p2.magnitude();

        if (res / vectorMagnitude < 0.0) // closer to a
            return p.distanceTo(p1); // Euclidean distance between p and p1


        if (res / vectorMagnitude > 1.0)  // closer to b
            return p.distanceTo(p2); // Euclidean distance between p and p2

        /*
         * Translate point a by a scaled magnitude u of vector p1p2
         * (multiplying p1p2^ by res to get scaled vector in p1p2 direction)
         */
        p1p2UnitVector.scale(res);
        double cx = p1.x + p1p2UnitVector.x;
        double cy = p1.y + p1p2UnitVector.y;
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
        /*
         * Check for general that one of the line segments has an intersection
         * between any of its end points with the other line segment
         */
        if (this.isIntersected(line.p1) || this.isIntersected(line.p2)
                || line.isIntersected(this.p1) || line.isIntersected(this.p2))
            return true;

        /* check if lines are parallel*/
        if (Math.abs(this.a - line.a) < EPS && Math.abs(this.b - line.b) < EPS)
            return false;

        /* solve simultaneous equation of two 2 line equation with two unknowns (x,y) */
        Point p = new Point();
        p.x = (line.b * this.c - this.b * line.c) / (line.a * this.b - this.a * line.b);

        /* check vertical line (b=0) to avoid dividing by zero */
        if (b < EPS)
            p.y = -(line.a * p.x) - line.c; // invoker is a vertical line so calculate y from line
        else
            p.y = -(this.a * p.x) - this.c;

        /* Check that intersection point is on both lines */
        return this.isIntersected(p) && line.isIntersected(p);
    }

    @Override
    public Shape clone() {
        validate();
        return new LineSegment(p1.clone(), p2.clone());
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

        throw new OperationNotSupportedException("Contains operation in LineSegment does not support " + shape.getClass());
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


    public Point getIntersectionPointIfExist(LineSegment line) throws OperationNotSupportedException {
        /*
         * Check for general that one of the line segments has an intersection
         * between any of its end points with the other line segment
         */
        if (this.isIntersected(line.p1))
            return line.p1;

        if (this.isIntersected(line.p2))
            return line.p2;

        if (line.isIntersected(this.p1))
            return this.p1;

        if (line.isIntersected(this.p2))
            return this.p2;


        /* check if lines are parallel*/
        if (Math.abs(this.a - line.a) < EPS && Math.abs(this.b - line.b) < EPS)
            return null;

        /* solve simultaneous equation of two 2 line equation with two unknowns (x,y) */
        Point p = new Point();
        p.x = (line.b * this.c - this.b * line.c) / (line.a * this.b - this.a * line.b);

        /* check vertical line (b=0) to avoid dividing by zero */
        if (b < EPS)
            p.y = -(line.a * p.x) - line.c; // invoker is a vertical line so calculate y from line
        else
            p.y = -(this.a * p.x) - this.c;

        /* Check that intersection point is on both lines */
        return (this.isIntersected(p) && line.isIntersected(p)) ? p : null;
    }
}
