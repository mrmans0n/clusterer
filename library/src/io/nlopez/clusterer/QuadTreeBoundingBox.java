package io.nlopez.clusterer;

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
        boolean containsX = this.x1 <= data.getPosition().latitude && this.xf >= data.getPosition().latitude;
        boolean containsY = this.y1 <= data.getPosition().longitude && this.yf >= data.getPosition().longitude;
        return containsX && containsY;
    }

    public boolean isIntersecting(QuadTreeBoundingBox other) {
        return (this.x1 <= other.getXf() && this.xf >= other.getX1() &&
                this.y1 <= other.getYf() && this.yf >= other.getY1());
    }
}
