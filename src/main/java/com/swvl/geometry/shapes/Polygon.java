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
        for (Point point : points) {
            xSum += point.x;
            ySum += point.y;
        }

        return new Point(xSum / (points.length - 1),
                ySum / (points.length - 1));
    }

    @Override
    public boolean contains(Shape shape) throws OperationNotSupportedException {
        validate();

        if (shape instanceof Point)
            return this.isIntersected((Point) shape);

        if (shape instanceof LineSegment) {
            LineSegment line = (LineSegment) shape;
            return this.isIntersected(line.p1)
                    && this.isIntersected(line.p2);
        }

        if (shape instanceof Rectangle)
            return containsRectangle((Rectangle) shape);

        if (shape instanceof Polygon)
            return containsPolygon((Polygon) shape);

        throw new OperationNotSupportedException("contains operation in Polygon does not support " + shape.getClass());

    }

    private boolean containsRectangle(Rectangle rect) throws OperationNotSupportedException {
        Point p1 = new Point(rect.maxPoint.x, rect.minPoint.y); // bottom-right point
        Point p2 = new Point(rect.minPoint.x, rect.maxPoint.y); // upper-left point

        /* Edges of a rectangle */
        Point[] rectPoints = new Point[]{
                rect.minPoint,
                p1,
                rect.maxPoint,
                p2
        };

        boolean isContained = true;

        /*
         * Iterate over rectangle points and check if that all points are
         * inside the polygon
         */
        for (Point rectPoint : rectPoints)
            isContained &= this.isIntersected(rectPoint);

        return isContained;
    }

    private boolean containsPolygon(Polygon polygon) throws OperationNotSupportedException {
        boolean isContained = true;

        /* Iterate over polygon's points and check if any point is inside the invoker polygon*/
        for (int i = 0; i < polygon.points.length - 1; ++i)
            isContained &= this.isIntersected(polygon.points[i]);

        return isContained;
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


    public void setPoints(Point[] points) {
        this.points = points;
        validate();
    }
}
