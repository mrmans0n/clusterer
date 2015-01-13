package io.nlopez.clusterer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import io.nlopez.clusterer.utils.CustomTestRunner;
import io.nlopez.clusterer.utils.TestPoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mrm on 12/1/15.
 */
@RunWith(CustomTestRunner.class)
public class QuadTreeTest {
    private static final QuadTreeBoundingBox WORLD_BOX = new QuadTreeBoundingBox(85, 180, -85, -180);
    private static final QuadTreeBoundingBox SPAIN_BOX = new QuadTreeBoundingBox(35.924644, 3.999023, 43.580390, -10.458984);
    private static final QuadTreeBoundingBox FRANCE_BOX = new QuadTreeBoundingBox(36.949891, -6.064453, 42.065606, -9.536132);
    private static final QuadTreeBoundingBox PORTUGAL_BOX = new QuadTreeBoundingBox(42.585444, 9.448242, 50.7642593, -4.965820);

    private static final TestPoint POINT_MESTALLA = new TestPoint(39.474531, -0.358065);
    private static final TestPoint POINT_CUENCA = new TestPoint(40.070392, -2.137416);
    private static final TestPoint POINT_SEVASTOPOL = new TestPoint(44.616650, 33.525366);

    QuadTree.OnNodeVisitedListener<TestPoint> visitNode;

    @Before
    public void setup() {
        visitNode = mock(QuadTree.OnNodeVisitedListener.class);
    }

    @Test
    public void test_quadtree_insert_one() {
        QuadTree<TestPoint> quadTree = new QuadTree<>(WORLD_BOX);
        quadTree.insertData(POINT_MESTALLA);

        ArrayList<TestPoint> searched = quadTree.getPointsInRange(WORLD_BOX);
        assertThat(searched).hasSize(1).contains(POINT_MESTALLA).doesNotContain(POINT_CUENCA, POINT_SEVASTOPOL);
        searched = quadTree.getPointsInRange(PORTUGAL_BOX);
        assertThat(searched).isEmpty();
    }

    @Test
    public void test_quadtree_insert_multiple() {
        QuadTree<TestPoint> quadTree = new QuadTree<>(WORLD_BOX);
        quadTree.insertData(Arrays.asList(POINT_MESTALLA, POINT_CUENCA, POINT_SEVASTOPOL));
        ArrayList<TestPoint> searched = quadTree.getPointsInRange(WORLD_BOX);
        assertThat(searched).hasSize(3).contains(POINT_MESTALLA, POINT_CUENCA, POINT_SEVASTOPOL);

        searched = quadTree.getPointsInRange(FRANCE_BOX);
        assertThat(searched).isEmpty();

        searched = quadTree.getPointsInRange(SPAIN_BOX);
        assertThat(searched).hasSize(2);

    }

    @Test
    public void test_quadtree_traversal() {
        QuadTree<TestPoint> quadTree = new QuadTree<>(WORLD_BOX);
        quadTree.insertData(Arrays.asList(POINT_MESTALLA, POINT_CUENCA, POINT_SEVASTOPOL));
        quadTree.traverseNodes(visitNode);
        verify(visitNode, atLeastOnce()).onNodeVisited(any(QuadTreeNode.class));
    }

}
