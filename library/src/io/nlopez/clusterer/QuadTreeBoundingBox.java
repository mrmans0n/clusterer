package io.nlopez.clusterer;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Nacho L. on 04/11/13.
 */
public class QuadTreeBoundingBox {

    private double x1, y1, xf, yf;

    public QuadTreeBoundingBox(double x1, double y1, double xf, double yf) {
        this.x1 = x1;
        this.y1 = y1;
        this.xf = xf;
        this.yf = yf;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getXf() {
        return xf;
    }

    public double getYf() {
        return yf;
    }

    public boolean containsData(Clusterable data) {
        return containsPoint(data.getPosition().latitude, data.getPosition().longitude);
    }

    public boolean isIntersecting(QuadTreeBoundingBox other) {
        return containsPoint(other.getX1(), other.getY1()) || containsPoint(other.getXf(), other.getYf());
    }

    private boolean containsPoint(double x, double y) {
        boolean containsX = isBetween(this.x1, this.xf, x);
        boolean containsY = isBetween(this.y1, this.yf, y);
        return containsX && containsY;
    }

    private static boolean isBetween(double a, double b, double c) {
        return b > a ? c > a && c < b : c > b && c < a;
    }

}
