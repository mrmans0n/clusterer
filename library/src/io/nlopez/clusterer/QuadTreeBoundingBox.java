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

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getXf() {
        return xf;
    }

    public void setXf(double xf) {
        this.xf = xf;
    }

    public double getYf() {
        return yf;
    }

    public void setYf(double yf) {
        this.yf = yf;
    }

    public boolean containsData(QuadTreeNode data) {
        boolean containsX = this.x1 <= data.getBoundingBox().getX1() && this.xf <= data.getBoundingBox().getXf();
        boolean containsY = this.y1 <= data.getBoundingBox().getY1() && this.yf <= data.getBoundingBox().getYf();
        return containsX && containsY;
    }

    public boolean isIntersecting(QuadTreeBoundingBox other) {
        return (this.x1 <= other.getXf() && this.xf >= other.getX1() &&
                this.y1 <= other.getYf() && this.yf >= other.getY1());
    }
}
