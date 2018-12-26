package com.swvl.geometry.shapes;

import com.swvl.geometry.io.TextSerializerHelper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import javax.naming.OperationNotSupportedException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A class that holds coordinates of a rectangle.
 *
 * @author Hatem Morgan
 */
public class Rectangle extends Shape implements WritableComparable<Rectangle> {
    public Point minPoint; // point with minimum coordinates
    public Point maxPoint; // point with maximum coordinates

    final static double EPS = 1e-9; // Epsilon error for comparing floating points

    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public Rectangle(Point minPoint, Point maxPoint) {
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
    }

    public Rectangle(double x1, double y1, double x2, double y2) {
        this.minPoint = new Point(x1, y1);
        this.maxPoint = new Point(x2, y2);
    }

    public void set(Shape s) {
        if (s == null)
            return;

        Rectangle mbr = s.getMBR();
        set(mbr);
    }

    public void set(Rectangle rect) {
        this.minPoint = rect.minPoint.clone();
        this.maxPoint = rect.maxPoint.clone();
    }

    public void write(DataOutput out) throws IOException {
        minPoint.write(out);
        maxPoint.write(out);
    }

    public void readFields(DataInput in) throws IOException {
        minPoint.readFields(in);
        maxPoint.readFields(in);
    }

    /**
     * Comparison is done by lexicographic ordering of attributes
     * &lt; minPoint, maxPoint &gt;
     */

    public int compareTo(Shape s) {
        Rectangle rect2 = (Rectangle) s;

        // Sort by minPoint's x then y
        if (this.minPoint.x - rect2.minPoint.x < -EPS)
            return -1;
        if (this.minPoint.x - rect2.minPoint.x > EPS)
            return 1;
        if (this.minPoint.y - rect2.minPoint.y < -EPS)
            return -1;
        if (this.minPoint.y - rect2.minPoint.y > EPS)
            return 1;

        // Sort by maxPoint's x then y
        if (this.maxPoint.x - rect2.maxPoint.x < -EPS)
            return -1;
        if (this.maxPoint.x - rect2.maxPoint.x > EPS)
            return 1;
        if (this.maxPoint.y - rect2.maxPoint.y < -EPS)
            return -1;
        if (this.maxPoint.y - rect2.maxPoint.y > EPS)
            return 1;

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Rectangle))
            return false;

        Rectangle r2 = (Rectangle) obj;
        return this.minPoint.equals(r2.minPoint) && this.maxPoint.equals(r2.maxPoint);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(this.minPoint.x);
        result = (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.minPoint.y);
        result = 31 * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.maxPoint.x);
        result = 31 * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.maxPoint.y);
        result = 31 * result + (int) (temp ^ temp >>> 32);
        return result;
    }

    /**
     * Get the distance between point and the center of rectangle
     *
     * @param p the point
     * @return
     */
    @Override
    public double distanceTo(Point p) {
        return this.getCenterPoint().distanceTo(p);
    }

    /**
     * Maximum distance to the perimeter of the Rectangle
     *
     * @param p
     * @return
     */
    public double getMaxDistanceTo(Point p) {
        double dx = Math.max(p.x - this.minPoint.x, this.maxPoint.x - p.x);
        double dy = Math.max(p.y - this.minPoint.y, this.maxPoint.y - p.y);

        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getMinDistanceTo(double px, double py) {
        double dx, dy;
        if (px < this.minPoint.x)
            dx = this.minPoint.x - px;
        else if (px < this.maxPoint.x)
            dx = 0;
        else
            dx = px - this.maxPoint.x;

        if (py < this.minPoint.y)
            dy = this.minPoint.y - py;
        else if (py < this.maxPoint.y)
            dy = 0;
        else
            dy = py - this.maxPoint.y;

        if (dx > 0 && dy > 0)
            return Math.sqrt(dx * dx + dy * dy);
        return Math.max(dx, dy);
    }

    @Override
    public Rectangle clone() {
        return new Rectangle(minPoint.x, minPoint.y, maxPoint.x, maxPoint.y);
    }

    @Override
    public Rectangle getMBR() {
        return new Rectangle(minPoint.x, minPoint.y, maxPoint.x, maxPoint.y);
    }


    @Override
    public boolean isIntersected(Shape shape) throws OperationNotSupportedException {
        if (shape instanceof Point)
            return contains((Point) shape);


        if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;

            /* part of one rectangle is inside the other one */
            return this.maxPoint.x - rect.minPoint.x > -Point.EPS // this.maxPoint.x > rect.minPoint.x
                    && this.maxPoint.y - rect.minPoint.y > -Point.EPS // this.maxPoint.y > rect.minPoint.y
                    && this.minPoint.x - rect.maxPoint.x < Point.EPS // this.minPoint.x < rect.maxPoint.x
                    && this.minPoint.y - rect.maxPoint.y < Point.EPS; // this.minPoint.y < rect.maxPoint.y
        }

        /* For a line segment to intersect a rectangle,
         * at least one of its end points should be inside the rectangle
         */
        if (shape instanceof Line) {
            Line line = (Line) shape;
            return this.isIntersected(line.getStartPoint()) || this.isIntersected(line.getEndPoint());
        }

        throw new OperationNotSupportedException("Contains operation in Rectangle does not support " + shape.getClass());
    }


    @Override
    public boolean isEdgeIntersection(Shape shape) throws OperationNotSupportedException {
        if (shape instanceof Point)
            return isIntersected(shape);

        if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            return rect.isIntersected(this.maxPoint)
                    || this.isIntersected(rect.minPoint)
                    || rect.isIntersected(this.minPoint)
                    || this.isIntersected(rect.maxPoint);
        }

        throw new OperationNotSupportedException("isEdgeIntersection operation in Rectangle does not support " + shape.getClass());

    }

    public Rectangle getIntersection(Shape s) throws OperationNotSupportedException {
        if (!s.isIntersected(this))
            return null;
        Rectangle r = s.getMBR();
        double ix1 = Math.max(this.minPoint.x, r.minPoint.x);
        double ix2 = Math.min(this.maxPoint.x, r.maxPoint.x);
        double iy1 = Math.max(this.minPoint.y, r.minPoint.y);
        double iy2 = Math.min(this.maxPoint.y, r.maxPoint.y);
        return new Rectangle(ix1, iy1, ix2, iy2);
    }

    @Override
    public boolean contains(Shape shape) throws OperationNotSupportedException {
        if (shape instanceof Point)
            return contains((Point) shape);

        if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            double maxXDiff = this.maxPoint.x - rect.maxPoint.x;
            double maxYDiff = this.maxPoint.y - rect.maxPoint.y;
            double minXDiff = this.minPoint.x - rect.minPoint.x;
            double minYDiff = this.minPoint.y - rect.minPoint.y;

            return maxXDiff >= -Point.EPS  // this.maxPoint.x >= rect.maxPoint.x
                    && maxYDiff >= -Point.EPS // this.maxPoint.y >= rect.maxPoint.y
                    && minXDiff <= Point.EPS // this.minPoint.x >= rect.minPoint.x
                    && minYDiff <= Point.EPS; // this.minPoint.y >= rect.minPoint.y

        }

        /* For a rectangle to contain a line segment,
         * the two points of line segment should be inside the rectangle
         */
        if (shape instanceof Line) {
            Line line = (Line) shape;
            return this.isIntersected(line.getStartPoint()) && this.isIntersected(line.getEndPoint());
        }

        throw new OperationNotSupportedException("Contains operation in Rectangle does not support " + shape.getClass());
    }

    private boolean contains(Point p) {
        double minDiffX = this.minPoint.x - p.x;
        double minDiffY = this.minPoint.y - p.y;
        double maxDiffX = this.maxPoint.x - p.x;
        double maxDiffY = this.maxPoint.y - p.y;

        if (minDiffX >= Point.EPS) // to the left of rect.minPoint
            return false;

        if (minDiffY >= Point.EPS) // to the bottom of rect.minPoint
            return false;

        if (maxDiffX <= -Point.EPS) // to the right of rect.maxPoint
            return false;

        if (maxDiffY <= -Point.EPS) // above of rect.maxPoint
            return false;

        /* borders(edges) or inner intersection */
        return true;
    }


    /**
     * Open bounded rectangle is a rectangle which in inclusive at left and bottom edges and exclusive
     * at upper and right edges. Thus, Points on right or upper borders are exclusive
     *
     * @param p
     * @return
     */
    public boolean upperOpenBoundedContains(Point p) {
        double minDiffX = this.minPoint.x - p.x;
        double minDiffY = this.minPoint.y - p.y;
        double maxDiffX = this.maxPoint.x - p.x;
        double maxDiffY = this.maxPoint.y - p.y;

        if (minDiffX >= Point.EPS) // to the left of rect.minPoint
            return false;

        if (minDiffY >= Point.EPS) // to the bottom of rect.minPoint
            return false;

        if (maxDiffX <= -Point.EPS) // to the right of rect.maxPoint
            return false;

        if (maxDiffY <= -Point.EPS) // above of rect.maxPoint
            return false;

        /* p is either to the right or top or equal of rect.minPoint */

        if (maxDiffX >= Point.EPS) // to the right of rect.minPoint and to the left of rect.maxPoint
            if (maxDiffY >= Point.EPS) // to the top of rect.minPoint and to the bottom of rect.maxPoint
                return true;

        /* borders(edges) intersection */

        // the point equal to min Point
        if (this.minPoint.equals(p))
            return true;

        /* bottom border (edge): to the right of rect.minPoint with the same y-coordinate and to the left of rect.maxPoint */
        if (minDiffY < Point.EPS && minDiffY > -Point.EPS)  // y-coordinates of minPoint and Point are equal
            return minDiffX <= -Point.EPS && maxDiffX >= Point.EPS; // to right of minPoint and to the left of maxPoint

        /* left border (edge): above rect.minPoint with the same x-coordinate and below of rect.maxPoint */
        if (minDiffX < Point.EPS && minDiffX > -Point.EPS)  // x-coordinates of minPoint and point are equal
            return minDiffY <= -Point.EPS && maxDiffY >= Point.EPS; // above minPoint and below of maxPoint

        return false;
    }

    public Rectangle union(final Shape s) {
        Rectangle r = s.getMBR();
        double ux1 = Math.min(this.minPoint.x, r.minPoint.x);
        double ux2 = Math.max(this.maxPoint.x, r.maxPoint.x);
        double uy1 = Math.min(this.minPoint.y, r.minPoint.y);
        double uy2 = Math.max(this.maxPoint.y, r.maxPoint.y);
        return new Rectangle(ux1, uy1, ux2, uy2);
    }

    public void expand(Point point) {
        if (point.x < this.minPoint.x)
            this.minPoint.x = point.x;
        if (point.y < this.minPoint.y)
            this.minPoint.y = point.y;
        if (point.x > this.maxPoint.x)
            this.maxPoint.x = point.x;
        if (point.y > this.maxPoint.y)
            this.maxPoint.y = point.y;
    }

    public void expand(final Shape s) {
        Rectangle r = s.getMBR();
        if (r.minPoint.x < this.minPoint.x)
            this.minPoint.x = r.minPoint.x;

        if (r.maxPoint.x > this.maxPoint.x)
            this.maxPoint.x = r.maxPoint.x;

        if (r.minPoint.y < this.minPoint.y)
            this.minPoint.y = r.minPoint.y;

        if (r.maxPoint.y > this.maxPoint.y)
            this.maxPoint.y = r.maxPoint.y;
    }


    @Override
    public Point getCenterPoint() {
        return new Point((this.minPoint.x + this.maxPoint.x) / 2, (this.minPoint.y + this.maxPoint.y) / 2);
    }


    @Override
    public Text toText(Text text) {
        TextSerializerHelper.serializeDouble(minPoint.x, text, ',');
        TextSerializerHelper.serializeDouble(minPoint.y, text, ',');
        TextSerializerHelper.serializeDouble(maxPoint.x, text, ',');
        TextSerializerHelper.serializeDouble(maxPoint.y, text, '\0');
        return text;
    }

    @Override
    public void fromText(Text text) {
        minPoint.x = TextSerializerHelper.consumeDouble(text, ',');
        minPoint.y = TextSerializerHelper.consumeDouble(text, ',');
        maxPoint.x = TextSerializerHelper.consumeDouble(text, ',');
        maxPoint.y = TextSerializerHelper.consumeDouble(text, '\0');
    }

    @Override
    public String toString() {
        return "Rectangle: (" + minPoint.x + "," + minPoint.y + ")-(" + maxPoint.x + "," + maxPoint.y + ")";
    }


    public double getHeight() {
        return maxPoint.y - minPoint.y;
    }

    public double getWidth() {
        return maxPoint.x - minPoint.x;
    }

    /**
     * Returns a new rectangle after translating with the given amount
     */
    public Rectangle translate(double dx, double dy) {
        return new Rectangle(this.minPoint.x + dx, this.minPoint.y + dy, this.maxPoint.x + dx, this.maxPoint.y + dy);
    }

    public String toWKT() {
        double x1 = minPoint.x, y1 = minPoint.y, x2 = maxPoint.x, y2 = maxPoint.y;
        return String.format("POLYGON((%g %g, %g %g, %g %g, %g %g, %g %g))",
                x1, y1, x1, y2, x2, y2, x2, y1, x1, y1);
    }

    @Override
    public int compareTo(Rectangle o) {
        return compareTo((Shape) o);
    }


    public static void main(String[] args) throws OperationNotSupportedException {
        Rectangle range = new Rectangle(3.0, 2.0, 6.0, 4.0);
        Rectangle rect = new Rectangle(2.0, 4.0, 6.0, 6.0);

        System.out.println(range.isIntersected(rect));
        System.out.println(range.isEdgeIntersection(rect));

        System.out.println(rect.isIntersected(range));
        System.out.println(rect.isEdgeIntersection(range));
    }
}
