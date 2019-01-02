package com.swvl.geometry;

import com.swvl.geometry.shapes.*;
import sun.security.provider.SHA;

import javax.naming.OperationNotSupportedException;

/**
 * Utilities class to hold common geometrical operations
 *
 * @author Hatem Morgan
 */
public class Utilities {
    /**
     * Winding number algorithm which returns true if the point p lies inside the polygon
     * <p>
     * A point is inside the polygon if either count of intersections is odd or
     * point lies on an edge of polygon.  If none of the conditions is true, then
     * point lies outside.
     */
    public static boolean polygonPointIntersection(Point point, Polygon polygon) throws OperationNotSupportedException {
        double sum = 0.0;
        LineSegment edge = new LineSegment();
        for (int i = 0; i < polygon.points.length - 1; ++i) {
            edge.set(polygon.points[i], polygon.points[i + 1]);

            if (edge.contains(point))
                return true;

            double angle = angle(edge.p1, point, edge.p2);

            if (ccw(point, edge.p1, edge.p2))
                sum += angle;  // left turn/CCW
            else
                sum -= angle; // right turn/CW
        }

        return Math.abs(Math.abs(sum) - 2 * Math.PI) < Shape.EPS;
    }

    public static boolean polygonRectangleIntersection(Rectangle rect, Polygon polygon) throws OperationNotSupportedException {
        Point p1 = new Point(rect.maxPoint.x, rect.minPoint.y); // bottom-right point
        Point p2 = new Point(rect.minPoint.x, rect.maxPoint.y); // upper-left point

        /* Edges of a rectangle */
        LineSegment[] rectEdges = new LineSegment[]{
                new LineSegment(rect.minPoint, p1),
                new LineSegment(p1, rect.maxPoint),
                new LineSegment(rect.maxPoint, p2),
                new LineSegment(p2, rect.minPoint)
        };

        Point[] rectPoints = new Point[]{
                rect.minPoint,
                p1,
                rect.maxPoint,
                p2
        };

        /* Iterate over edges for checking intersection of edges */
        for (LineSegment rectEdge : rectEdges)
            if (polygonLineIntersection(rectEdge, polygon))
                return true;


        /*
         * Check if rectangle is fully inside the polygon. Thus, no edge intersection
         * Iterate over rectangle points and check if any point is inside the polygon
         */
        for (Point rectPoint : rectPoints)
            if (polygonPointIntersection(rectPoint, polygon))
                return true;

        /*
         * Check if polygon is fully inside the rectangle. Thus, no edge intersection
         * Iterate over polygon points and check if any point is inside the rectangle
         */
        for (int i = 0; i < polygon.points.length - 1; ++i)
            if (rect.isIntersected(polygon.points[i]))
                return true;

        return false;
    }

    public static boolean polygonLineIntersection(LineSegment line, Polygon polygon) throws OperationNotSupportedException {
        /* Pointer to current edge in polygon */
        LineSegment edge = new LineSegment();

        /* Iterate over edges of all polygons and check for intersection with line */
        for (int i = 0; i < polygon.points.length - 1; ++i) {
            edge.set(polygon.points[i], polygon.points[i + 1]);

            if (line.isIntersected(edge))
                return true;
        }

        /*
         * Check if Line is inside the polygon where one of its end points must be
         * inside the polygon
         */
        return polygonPointIntersection(line.p1, polygon)
                || polygonPointIntersection(line.p2, polygon);
    }


    public static boolean rectanglePointIntersection(Point point, Rectangle rectangle) {
        double minDiffX = rectangle.minPoint.x - point.x;
        double minDiffY = rectangle.minPoint.y - point.y;
        double maxDiffX = rectangle.maxPoint.x - point.x;
        double maxDiffY = rectangle.maxPoint.y - point.y;

        if (minDiffX >= Shape.EPS) // to the left of rect.minPoint
            return false;

        if (minDiffY >= Shape.EPS) // to the bottom of rect.minPoint
            return false;

        if (maxDiffX <= -Shape.EPS) // to the right of rect.maxPoint
            return false;

        return !(maxDiffY <= -Shape.EPS);
    }


    /**
     * Check if a regtangle intersects line segment be checking that:
     * 1- Line segment has one/both of its end points in the rectangle, or
     * 2- Line segment intersects any of the rectangle's edges.
     *
     * @param lineSegment LineSegment to be checked for intersection
     * @param rect        Rectangle to be checked for intersection
     * @return true if the rectangle intersects the line segment and false otherwise
     */
    public static boolean rectangelLineSegementIntersection(LineSegment lineSegment, Rectangle rect) throws OperationNotSupportedException {
        Point p1 = new Point(rect.maxPoint.x, rect.minPoint.y); // bottom-right point
        Point p2 = new Point(rect.minPoint.x, rect.maxPoint.y); // upper-left point

        /* Edges of a rectangle */
        LineSegment[] rectEdges = new LineSegment[]{
                new LineSegment(rect.minPoint, p1),
                new LineSegment(p1, rect.maxPoint),
                new LineSegment(rect.maxPoint, p2),
                new LineSegment(p2, rect.minPoint)
        };


        /* Iterate over four edges of rectangle and check for edge intersection with
         * line segment
         */
        for (LineSegment edge : rectEdges)
            if (edge.isIntersected(lineSegment))
                return true;

        /* No Edges intersection thus check if the line is in the rectangle */
        return rectanglePointIntersection(lineSegment.p1, rect)
                || rectanglePointIntersection(lineSegment.p2, rect);
    }

    /**
     * Check if a point is on the line segment be checking that:
     * 1- Point lies on the line (by substituting point.x in line equation)
     * 2- Point lies between a and b
     *
     * @param point point to checked for intersection
     * @return true if point intersect line segment and false otherwise
     */
    public static boolean lineSegmentPointIntersection(Point point, LineSegment lineSegment) {
        /* Check that point is on line */
        if (lineSegment.b > Shape.EPS) {
            double y = -(lineSegment.a * point.x) - lineSegment.c;

            if (Math.abs(y - point.y) > Shape.EPS) // point is not on the line
                return false;

        } else {
            if (Math.abs(point.x + lineSegment.c) > Shape.EPS) // vertical line check x that x != -c
                return false;
        }


        /* Check if point is between p1 and p2 */
        double ab = lineSegment.p1.distanceTo(lineSegment.p2);
        double ap = lineSegment.p1.distanceTo(point);
        double pb = point.distanceTo(lineSegment.p2);

        /* point between a and b if dist(a,p) + dist(p, b) == dist(a,b)*/
        return Math.abs(ab - (ap + pb)) < Shape.EPS;
    }

    /**
     * @param p 1st point
     * @param q 2nd point
     * @param r 3rd point
     * @return true if the 3 points lies on the line
     */
    public static boolean areCollinear(Point p, Point q, Point r) {
        Vector pq = new Vector(p, q); // Calculate vector pq^
        Vector qr = new Vector(q, r); // Calculate vector qr^

        /* Cross product between pq^ and qr^ */
        double val = pq.cross(qr);

        /* if val = 0 then points are collinear */
        return Math.abs(val) < Shape.EPS;
    }


    /**
     * Calculate angle AOB
     */
    static double angle(Point a, Point o, Point b) {
        Vector oa = new Vector(o, a), ob = new Vector(o, b);
        return Math.acos(oa.dot(ob) / Math.sqrt(oa.norm_sq() * ob.norm_sq()));
    }

    /**
     * returns true if point r is on the left side of line pq
     */
    static boolean ccw(Point p, Point q, Point r) {
        return new Vector(p, q).cross(new Vector(p, r)) > 0;
    }
}
