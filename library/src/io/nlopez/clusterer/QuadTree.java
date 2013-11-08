package io.nlopez.clusterer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nacho L. on 04/11/13.
 */
public class QuadTree<T extends Clusterable> {

    QuadTreeNode<T> root;

    public QuadTree(QuadTreeBoundingBox boundingBox, int capacity) {
        root = new QuadTreeNode<T>(boundingBox, capacity);
    }

    public void subdivide() {
        root.subdivide();
    }

    public boolean insertData(QuadTreeNodeData<T> data) {
        return root.insertData(data);
    }

    public void processDataInRange(QuadTreeBoundingBox boundingBox, OnDataInRangeListener<T> listener) {
        root.processDataInRange(boundingBox, listener);
    }

    public void traverseNodes(OnNodeVisitedListener<T> listener) {
        LinkedList<QuadTreeNode<T>> queue = new LinkedList<QuadTreeNode<T>>();
        queue.add(root);
        while (queue.size()>0) {
            QuadTreeNode<T> current = queue.removeFirst();
            if (listener != null) {
                listener.onNodeVisited(current);
            }
            if (current.getNorthWest()!=null) queue.add(current.getNorthWest());
            if (current.getNorthEast()!=null) queue.add(current.getNorthEast());
            if (current.getSouthWest()!=null) queue.add(current.getSouthWest());
            if (current.getSouthEast()!=null) queue.add(current.getSouthEast());
        }
    }

    public interface OnDataInRangeListener<T extends Clusterable> {
        void onClusterablesInRange(List<T> points);
    }

    public interface OnNodeVisitedListener<T extends Clusterable> {
        void onNodeVisited(QuadTreeNode<T> node);
    }

}
