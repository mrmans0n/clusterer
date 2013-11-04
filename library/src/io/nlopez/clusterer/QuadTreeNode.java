package io.nlopez.clusterer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nacho L. on 04/11/13.
 */
public class QuadTreeNode<T extends Clusterable> {
    private QuadTreeNode northWest;
    private QuadTreeNode northEast;
    private QuadTreeNode southWest;
    private QuadTreeNode southEast;
    private QuadTreeNodeData<T> nodeData;
    private int capacity;
    private int count;
    private QuadTreeBoundingBox boundingBox;

    public QuadTreeNode(QuadTreeBoundingBox boundingBox, int capacity) {
        this.boundingBox = boundingBox;
        this.nodeData = null;
        this.count = 0;
        this.capacity = capacity;
    }

    public QuadTreeNode getNorthWest() {
        return northWest;
    }

    public void setNorthWest(QuadTreeNode northWest) {
        this.northWest = northWest;
    }

    public QuadTreeNode getNorthEast() {
        return northEast;
    }

    public void setNorthEast(QuadTreeNode northEast) {
        this.northEast = northEast;
    }

    public QuadTreeNode getSouthWest() {
        return southWest;
    }

    public void setSouthWest(QuadTreeNode southWest) {
        this.southWest = southWest;
    }

    public QuadTreeNode getSouthEast() {
        return southEast;
    }

    public void setSouthEast(QuadTreeNode southEast) {
        this.southEast = southEast;
    }

    public int getCount() {
        return (nodeData != null)? nodeData.getData().size() : -1;
    }

    public QuadTreeBoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(QuadTreeBoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public QuadTreeNodeData<T> getNodeData() {
        return this.nodeData;
    }
}
