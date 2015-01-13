package io.nlopez.clusterer;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Nacho L. on 04/11/13.
 */
public class QuadTreeBoundingBox {

    private double minX, minY, maxX, maxY, midX, midY;

    public QuadTreeBoundingBox(LatLngBounds bounds) {
        this(bounds.southwest.latitude, bounds.northeast.longitude, bounds.northeast.latitude, bounds.southwest.longitude);
    }

    public QuadTreeBoundingBox(double x1, double y1, double xf, double yf) {
        minX = Math.min(x1, xf);
        minY = Math.min(y1, yf);
        maxX = Math.max(x1, xf);
        maxY = Math.max(y1, yf);

        midX = (minX + maxX) / 2;
        midY = (minY + maxY) / 2;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMidX() {
        return midX;
    }

    public double getMidY() {
        return midY;
    }

    public boolean containsData(Clusterable data) {
        return containsPoint(data.getPosition().latitude, data.getPosition().longitude);
    }

    public boolean isIntersecting(QuadTreeBoundingBox other) {
        return minX < other.getMaxX() && maxX > other.getMinX() && minY < other.getMaxY() && maxY > other.getMinY();
    }

    private boolean containsPoint(double x, double y) {
        return minX <= x && maxX >= x && minY <= y && maxY >= y;
    }

}
