package io.nlopez.clusterer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nacho L. on 04/11/13.
 */
public class QuadTree<T extends Clusterable> {

    private static final int MAX_CAPACITY = 60;
    QuadTreeNode<T> root;

    public QuadTree(QuadTreeBoundingBox boundingBox) {
        this(boundingBox, MAX_CAPACITY);
    }

    public QuadTree(QuadTreeBoundingBox boundingBox, int capacity) {
        root = new QuadTreeNode<T>(boundingBox, capacity);
    }

    public void subdivide() {
        root.subdivide();
    }

    public boolean insertData(T data) {
        return root.insertData(data);
    }

    public void insertData(List<T> data) {
        for (T t : data) {
            root.insertData(t);
        }
    }

    public ArrayList<T> getPointsInRange(QuadTreeBoundingBox boundingBox) {
        ArrayList<T> points = new ArrayList<T>();
        root.processDataInRange(boundingBox, points);
        return points;
    }

    public void traverseNodes(OnNodeVisitedListener<T> listener) {
        LinkedList<QuadTreeNode<T>> queue = new LinkedList<QuadTreeNode<T>>();
        queue.add(root);
        while (queue.size() > 0) {
            QuadTreeNode<T> current = queue.removeFirst();
            if (listener != null) {
                listener.onNodeVisited(current);
            }
            if (current.getNorthWest() != null) queue.add(current.getNorthWest());
            if (current.getNorthEast() != null) queue.add(current.getNorthEast());
            if (current.getSouthWest() != null) queue.add(current.getSouthWest());
            if (current.getSouthEast() != null) queue.add(current.getSouthEast());
        }
    }

    public interface OnNodeVisitedListener<T extends Clusterable> {
        void onNodeVisited(QuadTreeNode<T> node);
    }

}
