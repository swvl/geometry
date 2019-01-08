package com.swvl.geometry.shapes;

import com.swvl.geometry.Utilities;

import javax.naming.OperationNotSupportedException;

/**
 * Implementation of a Line Segment.
 *
 * @author Hatem Morgan
 */
public class LineSegment implements Shape {
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
    public void validate() {
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

        /*
         * Get the scalar projection b of vector p1p on p1p1.
         * s = ||p1p|| * cosθ =  dotProduct(p1p, p1p2^) / ||p1p2^||
         * We use the unit vector of p1p2 because magnitude of unit vector = 1
         */
        double res = p1p.dot(p1p2) / p1p2.norm2();

        if (res < 0.0) // closer to a
            return p.distanceTo(p1); // Euclidean distance between p and p1


        if (res > 1.0)  // closer to b
            return p.distanceTo(p2); // Euclidean distance between p and p2

        /*
         * Translate point a by a scaled magnitude u of vector p1p2
         * (multiplying p1p2^ by res to get scaled vector in p1p2 direction)
         */
        Point c = p1.translate(p1p2.scale(res));
        return p.distanceTo(c);
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
