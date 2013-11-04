package io.nlopez.clusterer;

/**
 * Created by mrm on 04/11/13.
 */
public class QuadTree<T extends Clusterable> {

    QuadTreeNode<T> root;

    public QuadTree(QuadTreeBoundingBox boundingBox, int capacity) {
        root = new QuadTreeNode<T>(boundingBox, capacity);
    }

    public void subdivide() {

        QuadTreeBoundingBox box = root.getBoundingBox();
        double xMid = (box.getXf() + box.getX1()) / 2.0;
        double yMid = (box.getYf() + box.getY1()) / 2.0;

        int capacity = root.getCapacity();

        QuadTreeBoundingBox northWest = new QuadTreeBoundingBox(box.getX1(), box.getY1(), xMid, yMid);
        root.setNorthWest(new QuadTreeNode<T>(northWest, capacity));

        QuadTreeBoundingBox northEast = new QuadTreeBoundingBox(xMid, box.getY1(), box.getXf(), yMid);
        root.setNorthEast(new QuadTreeNode<T>(northEast, capacity));

        QuadTreeBoundingBox southWest = new QuadTreeBoundingBox(box.getX1(), yMid, xMid, box.getYf());
        root.setSouthWest(new QuadTreeNode<T>(southWest, capacity));

        QuadTreeBoundingBox southEast = new QuadTreeBoundingBox(box.getX1(), box.getY1(), xMid, yMid);
        root.setSouthEast(new QuadTreeNode<T>(southEast, capacity));

    }

    public boolean insertData(QuadTreeNodeData<T> data) {
        if (!root.getBoundingBox().containsData(data)) {
            return false;
        }

        if (root.getCount() < root.getCapacity()) {
            root.getNodeData().getData().addAll(data.getData());
            return true;
        }

        if (root.getNorthWest() == null) {
            subdivide();
        }

        // TODO finish
    }
}
