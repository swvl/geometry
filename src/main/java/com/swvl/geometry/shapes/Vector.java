package com.swvl.geometry.shapes;

/**
 * 2D Vector implementation
 *
 * @author Hatem Morgan
 */
public class Vector {

    public double x, y;

    public Vector(double a, double b) {
        x = a;
        y = b;
    }

    public Vector(Point a, Point b) {
        this(b.x - a.x, b.y - a.y);
    }

    public Vector scale(double s) {
        return new Vector(x * s, y * s);
    }              //s is a non-negative value

    public double dot(Vector v) {
        return (x * v.x + y * v.y);
    }

    public double cross(Vector v) {
        return x * v.y - y * v.x;
    }

    public Vector reverse() {
        return new Vector(-x, -y);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector unitVector() {
        double magnitude = this.magnitude();
        return new Vector(this.x / magnitude, this.y / magnitude);
    }
}
