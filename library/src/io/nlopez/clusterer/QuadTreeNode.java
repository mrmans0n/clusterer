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

    public void subdivide() {

        QuadTreeBoundingBox box = getBoundingBox();
        double xMid = (box.getXf() + box.getX1()) / 2.0;
        double yMid = (box.getYf() + box.getY1()) / 2.0;

        QuadTreeBoundingBox northWest = new QuadTreeBoundingBox(box.getX1(), box.getY1(), xMid, yMid);
        setNorthWest(new QuadTreeNode<T>(northWest, capacity));

        QuadTreeBoundingBox northEast = new QuadTreeBoundingBox(xMid, box.getY1(), box.getXf(), yMid);
        setNorthEast(new QuadTreeNode<T>(northEast, capacity));

        QuadTreeBoundingBox southWest = new QuadTreeBoundingBox(box.getX1(), yMid, xMid, box.getYf());
        setSouthWest(new QuadTreeNode<T>(southWest, capacity));

        QuadTreeBoundingBox southEast = new QuadTreeBoundingBox(xMid, yMid, box.getXf(), box.getYf());
        setSouthEast(new QuadTreeNode<T>(southEast, capacity));

    }

    public boolean insertData(T data) {

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

    public void processDataInRange(QuadTreeBoundingBox range, ArrayList<T> points) {
        if (!boundingBox.isIntersecting(range)) {
            return;
        }
        if (points != null) {
            for (T element : nodeData) {
                if (boundingBox.containsData(element)) {
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

    public QuadTreeNode<T> getNorthWest() {
        return northWest;
    }

    public void setNorthWest(QuadTreeNode<T> northWest) {
        this.northWest = northWest;
    }

    public QuadTreeNode<T> getNorthEast() {
        return northEast;
    }

    public void setNorthEast(QuadTreeNode<T> northEast) {
        this.northEast = northEast;
    }

    public QuadTreeNode<T> getSouthWest() {
        return southWest;
    }

    public void setSouthWest(QuadTreeNode<T> southWest) {
        this.southWest = southWest;
    }

    public QuadTreeNode<T> getSouthEast() {
        return southEast;
    }

    public void setSouthEast(QuadTreeNode<T> southEast) {
        this.southEast = southEast;
    }

    public int getCount() {
        return nodeData.size();
    }

    public QuadTreeBoundingBox getBoundingBox() {
        return boundingBox;
    }

}
