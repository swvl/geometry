package com.swvl.geometry.shapes;

import com.swvl.geometry.Utilities;

import javax.naming.OperationNotSupportedException;

/**
 * Implementation of a point represented by x & y coordinates.
 *
 * @author Hatem Morgan
 */
public class Point implements Shape, Comparable<Point> {
    public double x;
    public double y;

    public Point() {
        this(Double.MIN_VALUE, Double.MIN_VALUE);
    }

    public Point(double x, double y) {
        set(x, y);
    }


    /**
     * A copy constructor from any shape of type Point (or subclass of Point)
     *
     * @param s
     */
    public Point(Point s) {
        this.x = s.x;
        this.y = s.y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(this.x);
        result = (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.y);
        result = 31 * result + (int) (temp ^ temp >>> 32);
        return result;
    }

    @Override
    public double distanceTo(Point p) {
        double dx = p.x - this.x;
        double dy = p.y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public Point clone() {
        return new Point(this.x, this.y);
    }

    @Override
    public Point getCenterPoint() {
        return this;
    }

    @Override
    public boolean contains(Shape shape) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Check if point contains a shape is a fatal error");
    }

    /**
     * Returns the minimal bounding rectangle of this point. This method returns
     * the smallest rectangle that contains this point. For consistency with
     * other methods such as {@link Rectangle#isIntersected(Shape)}, the rectangle
     * cannot have a zero width or height. Thus, we use the method
     * {@link Math#ulp(double)} to compute the smallest non-zero rectangle that
     * contains this point. Thus point = center point of rectangle
     */
    @Override
    public Rectangle getMBR() {
        return new Rectangle(x, y, x, y);
    }

    @Override
    public boolean isIntersected(Shape shape) throws OperationNotSupportedException {
        if (shape instanceof Point)
            return this.equals(shape);

        if (shape instanceof Rectangle)
            return Utilities.rectanglePointIntersection(this, (Rectangle) shape);

        if (shape instanceof LineSegment)
            return Utilities.lineSegmentPointIntersection(this, (LineSegment) shape);

        if (shape instanceof Polygon)
            return Utilities.polygonPointIntersection(this, (Polygon) shape);

        throw new OperationNotSupportedException("isIntersected operation in Point " +
                "is not supported for " + shape.getClass());
    }

    @Override
    public String toString() {
        return "Point: (" + x + "," + y + ")";
    }

    @Override
    public int compareTo(Point pt2) {
        /* Sort on x then y for resolving ties*/
        if (this.x - pt2.x < -EPS)
            return -1;

        if (this.x - pt2.x > EPS)
            return 1;

        if (this.y - pt2.y < -EPS)
            return -1;

        if (this.y - pt2.y > EPS)
            return 1;

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point))
            return false;

        Point point = (Point) obj;
        double xDiff = this.x - point.x;
        double yDiff = this.y - point.y;

        return xDiff > -EPS && yDiff > -EPS
                && xDiff < EPS && yDiff < EPS;
    }

    public boolean isGTE(Point point) {
        double xDiff = this.x - point.x;
        double yDiff = this.y - point.y;

        if (xDiff >= EPS) // this.x > point.x
            return true;

        if (xDiff >= -EPS && yDiff >= EPS) // this.x = point.x && this.y > point.y
            return true;

        // this.x = point.x && this.y = point.y
        return xDiff >= -EPS && yDiff >= -EPS;
    }


    public boolean isSTE(Point point) {
        double xDiff = this.x - point.x;
        double yDiff = this.y - point.y;

        if (xDiff <= -EPS) // this.x < point.x
            return true;

        if (xDiff <= EPS && yDiff <= -EPS) // this.x = point.x && this.y < point.y
            return true;

        // this.x = point.x && this.y = point.y
        return xDiff <= EPS && yDiff <= EPS;
    }

    public boolean isGT(Point point) {
        double xDiff = this.x - point.x;
        double yDiff = this.y - point.y;

        if (xDiff >= EPS) // this.x > point.x
            return true;

        // this.x = point.x && this.y > point.y
        return xDiff >= -EPS && yDiff >= EPS;
    }


    public boolean isST(Point point) {
        double xDiff = this.x - point.x;
        double yDiff = this.y - point.y;

        if (xDiff <= -EPS) // this.x < point.x
            return true;

        // this.x = point.x && this.y < point.y
        return xDiff <= EPS && yDiff <= -EPS;
    }

    public Point translate(Vector vector) {
        return new Point(this.x + vector.x, this.y + vector.y);
    }
}
