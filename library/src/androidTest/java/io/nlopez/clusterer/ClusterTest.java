package io.nlopez.clusterer;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mrm on 12/1/15.
 */
@RunWith(CustomTestRunner.class)
public class ClusterTest {

    private static final TestPoint POINT_MESTALLA = new TestPoint(39.474531, -0.358065);
    private static final TestPoint POINT_CUENCA = new TestPoint(40.070392, -2.137416);

    @Test
    public void test_cluster_creation() {
        Cluster<TestPoint> testPointCluster = new Cluster<>(POINT_MESTALLA);
        assertThat(testPointCluster.getPosition()).isEqualTo(POINT_MESTALLA.getPosition());
    }

    @Test
    public void test_cluster_two_points() {
        Cluster<TestPoint> testPointCluster = new Cluster<>(POINT_MESTALLA);
        testPointCluster.addMarker(POINT_CUENCA);

        assertThat(testPointCluster.getMarkers()).contains(POINT_CUENCA, POINT_MESTALLA);
    }
}
