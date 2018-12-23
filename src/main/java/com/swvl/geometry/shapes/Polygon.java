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
    public boolean isIntersected(Shape s) throws OperationNotSupportedException {
        return false;
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
