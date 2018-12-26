package com.swvl.geometry.shapes;

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
     * points, entered in counter clockwise order, 0-based indexing with the first vertex being equal
     * to the last vertex
     */
    Point[] points;

    /* Number of points */
    int numPoints;

    public Polygon() {
    }

    public Polygon(Point[] points, int numPoints) {
        /*
         * check that number of points equal length of points after removing
         * last vertex (duplicate of first vertex)
         */
        if (points.length - 1 != numPoints)
            throw new IllegalArgumentException("Number of points must be equal length of " +
                    "Array(Point) after removing last vertex (duplicate of first vertex)");

        if (numPoints < 3)
            throw new IllegalArgumentException("Number of point for polygon must " +
                    "by greater than or equal 3");

        this.points = points;
        this.numPoints = numPoints;
    }

    @Override
    public Rectangle getMBR() {
        double boundsMinX = Integer.MAX_VALUE;
        double boundsMinY = Integer.MAX_VALUE;
        double boundsMaxX = Integer.MIN_VALUE;
        double boundsMaxY = Integer.MIN_VALUE;

        for (Point point : points) {
            double x = point.x;
            boundsMinX = Math.min(boundsMinX, x);
            boundsMaxX = Math.max(boundsMaxX, x);
            double y = point.y;
            boundsMinY = Math.min(boundsMinY, y);
            boundsMaxY = Math.max(boundsMaxY, y);
        }

        return new Rectangle(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY);
    }

    @Override
    public double distanceTo(Point p) {
        return 0;
    }

    @Override
    public boolean isIntersected(Shape shape) throws OperationNotSupportedException {
        if (shape instanceof Point)
            return isPointIntersection((Point) shape);

        return false;
    }

    /**
     * Returns true if the point p lies inside the polygon
     * <p>
     * A point is inside the polygon if either count of intersections is odd or
     * point lies on an edge of polygon.  If none of the conditions is true, then
     * point lies outside.
     */
    private boolean isPointIntersection(Point point) throws OperationNotSupportedException {
        /* Create a point for line segment from p to infinite */
        Point infinityLine = new Point(Double.MAX_VALUE, point.y);

        /* Pointer to current edge in polygon */
        Line edge = new Line();

        /* Count intersections of the above line with sides of polygon */
        int count = 0;


        for (int i = 0; i < points.length - 1; ++i) {
            edge.startPoint = points[i];
            edge.endPoint = points[i + 1];

            /*
             * Check if the infinityLine line segment intersects
             * with the edge line segment
             */
            if (edge.isIntersected(infinityLine)) {
                /*
                 * If the point 'p' is colinear with edge line segment,
                 * then check if it lies on segment. If it lies, return true,
                 */
                if (areCollinear(points[i], point, points[i + 1]))
                    return edge.contains(point);

                count++;
            }
        }

        return count % 2 == 1;
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
        return val < Point.EPS && val > -Point.EPS;

    }

    @Override
    public Shape clone() {
        return null;
    }

    /**
     * Calculates the centroid of the polygon's vertices (not guaranteed that centroid exist inside the polygon)
     */
    @Override
    public Point getCenterPoint() {
        double xSum = 0, ySum = 0;
        for (Point point : points) {
            xSum += point.x;
            ySum += point.y;
        }

        return new Point(xSum / numPoints, ySum / numPoints);
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


//    private int distanceToLine(Point pt, Point a, Point b){
//
//    }
}
