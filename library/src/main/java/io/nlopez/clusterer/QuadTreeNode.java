package io.nlopez.clusterer;

import java.util.ArrayList;

/**
 * Created by Nacho L. on 04/11/13.
 */
public class QuadTreeNode<T extends Clusterable> {
    private QuadTreeNode<T> northWest;
    private QuadTreeNode<T> northEast;
    private QuadTreeNode<T> southWest;
    private QuadTreeNode<T> southEast;
    private ArrayList<T> nodeData;
    private int capacity;
    private QuadTreeBoundingBox boundingBox;

    public QuadTreeNode(QuadTreeBoundingBox boundingBox, int capacity) {
        this.boundingBox = boundingBox;
        this.nodeData = new ArrayList<T>(capacity);
        this.capacity = capacity;
    }

    void subdivide() {

        QuadTreeBoundingBox box = getBoundingBox();
        double xMid = box.getMidX();
        double yMid = box.getMidY();

        QuadTreeBoundingBox northWest = new QuadTreeBoundingBox(box.getMinX(), box.getMinY(), xMid, yMid);
        setNorthWest(new QuadTreeNode<T>(northWest, capacity));

        QuadTreeBoundingBox northEast = new QuadTreeBoundingBox(xMid, box.getMinY(), box.getMaxX(), yMid);
        setNorthEast(new QuadTreeNode<T>(northEast, capacity));

        QuadTreeBoundingBox southWest = new QuadTreeBoundingBox(box.getMinX(), yMid, xMid, box.getMaxY());
        setSouthWest(new QuadTreeNode<T>(southWest, capacity));

        QuadTreeBoundingBox southEast = new QuadTreeBoundingBox(xMid, yMid, box.getMaxX(), box.getMaxY());
        setSouthEast(new QuadTreeNode<T>(southEast, capacity));

    }

    boolean insertData(T data) {

        if (!getBoundingBox().containsData(data)) {
            return false;
        }

        if (getCount() < capacity) {
            nodeData.add(data);
            return true;
        }

        if (getNorthWest() == null) {
            subdivide();
        }

        if (northWest.insertData(data)) return true;
        if (northEast.insertData(data)) return true;
        if (southWest.insertData(data)) return true;
        if (southEast.insertData(data)) return true;

        return false;
    }

    void processDataInRange(QuadTreeBoundingBox range, ArrayList<T> points) {
        if (!boundingBox.isIntersecting(range)) {
            return;
        }

        if (points != null) {
            for (T element : nodeData) {
                if (range.containsData(element)) {
                    points.add(element);
                }
            }
        }

        if (northWest == null) {
            return;
        }

        northWest.processDataInRange(range, points);
        northEast.processDataInRange(range, points);
        southWest.processDataInRange(range, points);
        southEast.processDataInRange(range, points);
    }

    QuadTreeNode<T> getNorthWest() {
        return northWest;
    }

    void setNorthWest(QuadTreeNode<T> northWest) {
        this.northWest = northWest;
    }

    public QuadTreeNode<T> getNorthEast() {
        return northEast;
    }

    void setNorthEast(QuadTreeNode<T> northEast) {
        this.northEast = northEast;
    }

    public QuadTreeNode<T> getSouthWest() {
        return southWest;
    }

    void setSouthWest(QuadTreeNode<T> southWest) {
        this.southWest = southWest;
    }

    public QuadTreeNode<T> getSouthEast() {
        return southEast;
    }

    void setSouthEast(QuadTreeNode<T> southEast) {
        this.southEast = southEast;
    }

    public int getCount() {
        return nodeData.size();
    }

    public QuadTreeBoundingBox getBoundingBox() {
        return boundingBox;
    }

}
