package io.nlopez.clusterer;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.nlopez.clusterer.utils.CustomTestRunner;
import io.nlopez.clusterer.utils.TestPoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mrm on 13/1/15.
 */
@RunWith(CustomTestRunner.class)
public class QuadTreeNodeTest {
    private static final int CAPACITY = 60;

    private static final QuadTreeBoundingBox WORLD_BOX = new QuadTreeBoundingBox(85, 180, -85, -180);
    private static final QuadTreeBoundingBox SPAIN_BOX = new QuadTreeBoundingBox(35.924644, 3.999023, 43.580390, -10.458984);
    private static final QuadTreeBoundingBox FRANCE_BOX = new QuadTreeBoundingBox(36.949891, -6.064453, 42.065606, -9.536132);
    private static final QuadTreeBoundingBox PORTUGAL_BOX = new QuadTreeBoundingBox(42.585444, 9.448242, 50.7642593, -4.965820);

    private static final TestPoint POINT_MESTALLA = new TestPoint(39.474531, -0.358065);
    private static final TestPoint POINT_CUENCA = new TestPoint(40.070392, -2.137416);
    private static final TestPoint POINT_SEVASTOPOL = new TestPoint(44.616650, 33.525366);

    @Test
    public void test_quadtreenode_creation() {
        QuadTreeNode<TestPoint> node = new QuadTreeNode<>(WORLD_BOX, 60);
        node.insertData(POINT_MESTALLA);

        assertThat(node.getCount()).isEqualTo(1);
    }

}
