package com.swvl.geometry.shapes;

import com.swvl.geometry.Utilities;
import com.swvl.geometry.io.TextSerializerHelper;
import org.apache.hadoop.io.Text;

import javax.naming.OperationNotSupportedException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Geometrical Polygon implementation
 *
 * @author Hatem Morgan
 */
public class Polygon extends Shape {
    /*
     * points, entered any order either clockwise or anti-clockwise.
     * Array is 0-based indexing with the first vertex being equal to the last vertex
     */
    public Point[] points = new Point[]{};
    /* Maximum x-coordinate */
    public double maxX = Double.MIN_VALUE;
    /* Maximum y-coordinate */
    public double maxY = Double.MIN_VALUE;
    /* Minimum x-coordinate */
    public double minX = Double.MAX_VALUE;
    /* Maximum Y-coordinate */
    public double minY = Double.MAX_VALUE;

    public Polygon() {
    }

    public Polygon(Point[] points) {
        this.points = points;
        validate();
    }

    /**
     * Validate that points of polygon is initialized and calculate
     * the fields of the polygon.
     */
    private void validate() {

        if (points.length < 3)
            throw new IllegalArgumentException("Number of point for polygon must " +
                    "by greater than or equal 3");

        /* Check that the first vertex and last vertex are the same */
        if (!points[points.length - 1].equals(points[0]))
            throw new IllegalArgumentException("First vertex and last vertex must be same");


        for (Point point : points) {
            double x = point.x;
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            double y = point.y;
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
    }

    @Override
    public Rectangle getMBR() {
        validate();

        return new Rectangle(minX, minY, maxX, maxY);
    }

    @Override
    public double distanceTo(Point p) {
        validate();

        /* Pointer to current edge in polygon */
        LineSegment edge = new LineSegment();
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < points.length - 1; ++i) {
            edge.set(this.points[i], this.points[i + 1]);


            minDistance = Math.min(minDistance, edge.distanceTo(p));
        }

        return minDistance;
    }

    @Override
    public boolean isIntersected(Shape shape) throws OperationNotSupportedException {
        validate();

        if (shape instanceof Point)
            return Utilities.polygonPointIntersection((Point) shape, this);

        if (shape instanceof Rectangle)
            return Utilities.polygonRectangleIntersection((Rectangle) shape, this);

        if (shape instanceof Polygon)
            return isPolygonIntersection((Polygon) shape);

        if (shape instanceof LineSegment)
            return Utilities.polygonLineIntersection((LineSegment) shape, this);

        throw new OperationNotSupportedException("isIntersected operation in Polygon does not support " + shape.getClass());
    }


    private boolean isPolygonIntersection(Polygon polygon) throws OperationNotSupportedException {

        /* Iterate over edges for checking intersection of edges without any points intersection */
        LineSegment edge = new LineSegment();
        for (int i = 0; i < points.length - 1; ++i) {
            edge.set(this.points[i], this.points[i + 1]);


            if (polygon.isIntersected(edge))
                return true;
        }

        /*
         * Check if polygon is fully inside the invoker polygon. Thus, no edge intersection
         * Iterate over polygon's points and check if any point is inside the invoker polygon
         */
        for (int i = 0; i < polygon.points.length - 1; ++i)
            if (this.isIntersected(polygon.points[i]))
                return true;

        /*
         * Check if invoker polygon is fully inside this polygon. Thus, no edge intersection
         * Iterate over invoker polygon points and check if any point is inside polygon
         */
        for (int i = 0; i < points.length - 1; ++i)
            if (polygon.isIntersected(points[i]))
                return true;

        return false;
    }


    /**
     * @param p 1st point
     * @param q 2nd point
     * @param r 3rd point
     * @return true if the 3 points lies on the line
     */
    private boolean areCollinear(Point p, Point q, Point r) {
        /* Calculate vector pq^ */
        double pqx = q.x - p.y;
        double pqy = q.y - p.y;

        /* Calculate vector qr^ */
        double qrx = r.x - q.x;
        double qry = r.y - q.y;

        /* Cross product between pq^ and qr^ */
        double val = pqx * qry - pqy * qrx;

        /* if val = 0 then points are collinear */
        return val < EPS && val > -EPS;

    }

    @Override
    public Polygon clone() {
        validate();

        Point[] clonedPoints = new Point[points.length];
        for (int i = 0; i < points.length - 1; ++i)
            clonedPoints[i] = points[i].clone();

        return new Polygon(clonedPoints);
    }

    /**
     * Calculates the centroid of the polygon's vertices (not guaranteed that centroid exist inside the polygon)
     */
    @Override
    public Point getCenterPoint() {
        validate();

        double xSum = 0, ySum = 0;
        for (int i = 0; i < points.length - 1; ++i) {
            xSum += points[i].x;
            ySum += points[i].y;
        }

        return new Point(xSum / (points.length - 1),
                ySum / (points.length - 1));
    }

    @Override
    public boolean contains(Shape shape) throws OperationNotSupportedException {
        validate();

        if (shape instanceof Point)
            return containsPoints((Point) shape);

        if (shape instanceof LineSegment)
            return containsLineSegment((LineSegment) shape);

        if (shape instanceof Rectangle)
            return containsPoints(((Rectangle) shape).minPoint,
                    new Point(((Rectangle) shape).minPoint.x, ((Rectangle) shape).maxPoint.y),
                    new Point(((Rectangle) shape).maxPoint.x, ((Rectangle) shape).minPoint.y),
                    ((Rectangle) shape).maxPoint);

        if (shape instanceof Polygon)
            return containsPoints(((Polygon) shape).points);

        throw new OperationNotSupportedException("contains operation in Polygon does not support " + shape.getClass());

    }

    /**
     * Iterate over points and check if that all points are inside the polygon
     */
    private boolean containsPoints(Point... points) throws OperationNotSupportedException {
        for (Point point : points)
            if (!this.isIntersected(point))
                return false;

        return true;
    }

    private boolean containsLineSegment(LineSegment lineSegment) throws OperationNotSupportedException {
        /* Check that both the endpoints of line segment are inside the polygon*/
        if (!containsPoints(lineSegment.p1, lineSegment.p2))
            return false;

        /*
         * Both of the end points are inside the polygon.
         *
         * if the line is on one of the polygon's edges then return true
         */
        LineSegment edge = new LineSegment();
        for (int i = 0; i < points.length - 1; ++i) {
            edge.set(points[i], points[i + 1]);
            if (edge.contains(lineSegment)) // line segment on the edge
                return true;
        }

        /*
         * Both of the end points are inside the polygon and line segment is not on any
         * of the polygon's edges.
         *
         * Check if the line segment goes out and then in the polygon (concave case)
         * by checking for any edge intersection with the line segment.
         */

        edge = new LineSegment();
        for (int i = 0; i < points.length - 1; ++i) {
            edge.set(points[i], points[i + 1]);
            if (lineSegmentWithoutEndPointsIntersection(edge, lineSegment)) // edge intersects the line segment
                return false;
        }

        /* Both end points are inside polygon and the line is fully inside the polygon */
        return true;
    }

    private boolean lineSegmentWithoutEndPointsIntersection(LineSegment l1, LineSegment l2) throws OperationNotSupportedException {

        /* check if lines are parallel*/
        if (Math.abs(l1.a - l2.a) < EPS && Math.abs(l1.b - l2.b) < EPS)
            return false;

        /* solve simultaneous equation of two 2 line equation with two unknowns (x,y) */
        Point p = new Point();
        p.x = (l2.b * l1.c - l1.b * l2.c) / (l2.a * l1.b - l1.a * l2.b);

        /* check vertical line (b=0) to avoid dividing by zero */
        if (Math.abs(l1.b) < EPS)
            p.y = -(l2.a * p.x) - l2.c; // invoker is a vertical line so calculate y from line
        else
            p.y = -(l1.a * p.x) - l1.c;

        /* Check that p not equal to any of the end points  */
        if ((p.equals(l1.p1) || p.equals(l1.p2)
                || p.equals(l2.p1) || p.equals(l2.p2)))
            return false;

        /* Check that intersection point is on both lines */
        return l1.isIntersected(p) && l2.isIntersected(p);
    }

    @Override
    public boolean isEdgeIntersection(Shape shape) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("isEdgeIntersection is not supported for Polygon");
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        validate();

        dataOutput.writeInt(points.length);
        for (Point point : points) point.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        int size = dataInput.readInt();
        points = new Point[size];
        for (int i = 0; i < points.length; ++i) {
            points[i] = new Point();
            points[i].readFields(dataInput);
        }
    }

    @Override
    public Text toText(Text text) {
        validate();

        TextSerializerHelper.serializeInt(points.length, text, ',');
        for (Point point : points) point.toText(text);
        return text;
    }

    @Override
    public void fromText(Text text) {
        int size = TextSerializerHelper.consumeInt(text, ',');
        points = new Point[size];
        for (int i = 0; i < points.length; ++i) {
            points[i] = new Point();
            points[i].fromText(text);
        }
    }


    @Override
    public boolean equals(Object obj) {
        validate();

        if (!(obj instanceof Polygon))
            return false;

        Polygon polygon = (Polygon) obj;
        if (this.points.length != polygon.points.length)
            return false;

        for (int i = 0; i < points.length; ++i)
            if (!this.points[i].equals(polygon.points[i]))
                return false;

        return true;
    }


}
