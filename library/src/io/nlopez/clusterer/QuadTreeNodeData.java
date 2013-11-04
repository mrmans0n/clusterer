package io.nlopez.clusterer;

import java.util.ArrayList;

/**
 * Created by Nacho L. on 04/11/13.
 */
public class QuadTreeNodeData<T extends Clusterable> {

    private ArrayList<T> data;
    private QuadTreeBoundingBox bounds;

    public QuadTreeNodeData(QuadTreeBoundingBox bounds, int capacity) {
        this.data = new ArrayList<>(capacity);
        this.bounds = bounds;
    }

    public QuadTreeNodeData(T data, QuadTreeBoundingBox bounds, int capacity) {
        this(bounds, capacity);
        this.data.add(data);
    }

    public ArrayList<T> getData() {
        return data;
    }

    public void setData(ArrayList<T> data) {
        this.data = data;
    }

    public QuadTreeBoundingBox getBounds() {
        return bounds;
    }

    public void setBounds(QuadTreeBoundingBox bounds) {
        this.bounds = bounds;
    }

}
