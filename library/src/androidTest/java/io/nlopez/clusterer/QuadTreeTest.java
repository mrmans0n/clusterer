package io.nlopez.clusterer;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import io.nlopez.clusterer.utils.CustomTestRunner;
import io.nlopez.clusterer.utils.TestPoint;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mrm on 12/1/15.
 */
@RunWith(CustomTestRunner.class)
public class QuadTreeTest {
    private static final QuadTreeBoundingBox WORLD_BOX = new QuadTreeBoundingBox(85, 180, -85, -180);
    private static final TestPoint POINT_MESTALLA = new TestPoint(39.474531, -0.358065);
    private static final TestPoint POINT_CUENCA = new TestPoint(40.070392, -2.137416);
    private static final TestPoint POINT_SEVASTOPOL = new TestPoint(44.616650, 33.525366);

    @Test
    public void test_quadtree_insert_one() {
        QuadTree<TestPoint> quadTree = new QuadTree<>(WORLD_BOX);
        quadTree.insertData(POINT_MESTALLA);

        ArrayList<TestPoint> searched = quadTree.getPointsInRange(WORLD_BOX);
        assertThat(searched).hasSize(1).contains(POINT_MESTALLA).doesNotContain(POINT_CUENCA, POINT_SEVASTOPOL);
    }

    @Test
    public void test_quadtree_insert_multiple() {
        QuadTree<TestPoint> quadTree = new QuadTree<>(WORLD_BOX);
        quadTree.insertData(Arrays.asList(POINT_MESTALLA, POINT_CUENCA, POINT_SEVASTOPOL));
        ArrayList<TestPoint> searched = quadTree.getPointsInRange(WORLD_BOX);
        assertThat(searched).hasSize(3).contains(POINT_MESTALLA, POINT_CUENCA, POINT_SEVASTOPOL);
    }
}
