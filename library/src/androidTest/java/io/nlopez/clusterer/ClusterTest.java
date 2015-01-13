package io.nlopez.clusterer;

import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.nlopez.clusterer.utils.CustomTestRunner;
import io.nlopez.clusterer.utils.TestPoint;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mrm on 12/1/15.
 */
@RunWith(CustomTestRunner.class)
public class ClusterTest {

    private static final TestPoint POINT_MESTALLA = new TestPoint(39.474531, -0.358065);
    private static final TestPoint POINT_CUENCA = new TestPoint(40.070392, -2.137416);
    private static final TestPoint POINT_SEVASTOPOL = new TestPoint(44.616650, 33.525366);

    private LatLngBounds boundsForTwo;
    private LatLngBounds boundsForThree;

    @Before
    public void setup() {
        boundsForTwo = new LatLngBounds.Builder().include(POINT_CUENCA.getPosition()).include(POINT_MESTALLA.getPosition()).build();
        boundsForThree = new LatLngBounds.Builder().include(POINT_CUENCA.getPosition()).include(POINT_MESTALLA.getPosition()).include(POINT_SEVASTOPOL.getPosition()).build();
    }

    @Test
    public void test_cluster_creation() {
        Cluster<TestPoint> testPointCluster = new Cluster<>(POINT_MESTALLA);

        assertThat(testPointCluster.getPosition()).isEqualTo(POINT_MESTALLA.getPosition());
        assertThat(testPointCluster.getCenter()).isEqualTo(POINT_MESTALLA.getPosition());
        assertThat(testPointCluster.isCluster()).isFalse();
        assertThat(testPointCluster.getWeight()).isEqualTo(1);
        assertThat(testPointCluster.getBounds()).isEqualTo(new LatLngBounds.Builder().include(POINT_MESTALLA.getPosition()).build());

    }

    @Test
    public void test_cluster_two_points() {
        Cluster<TestPoint> testPointCluster = new Cluster<>(POINT_MESTALLA);
        testPointCluster.addMarker(POINT_CUENCA);

        assertThat(testPointCluster.getMarkers()).hasSize(2).contains(POINT_CUENCA, POINT_MESTALLA).doesNotContain(POINT_SEVASTOPOL);
        assertThat(testPointCluster.getCenter()).isEqualTo(boundsForTwo.getCenter());
        assertThat(testPointCluster.getPosition()).isEqualTo(boundsForTwo.getCenter());
        assertThat(testPointCluster.getBounds()).isEqualTo(boundsForTwo);
        assertThat(testPointCluster.isCluster()).isTrue();
        assertThat(testPointCluster.getWeight()).isEqualTo(2);
    }

    @Test
    public void test_cluster_three_points() {
        Cluster<TestPoint> testPointCluster = new Cluster<>(POINT_MESTALLA);
        testPointCluster.addMarker(POINT_CUENCA);
        testPointCluster.addMarker(POINT_SEVASTOPOL);

        assertThat(testPointCluster.getMarkers()).hasSize(3).contains(POINT_CUENCA, POINT_MESTALLA, POINT_SEVASTOPOL);
        assertThat(testPointCluster.getCenter()).isEqualTo(boundsForThree.getCenter());
        assertThat(testPointCluster.getPosition()).isEqualTo(boundsForThree.getCenter());
        assertThat(testPointCluster.getBounds()).isEqualTo(boundsForThree);
        assertThat(testPointCluster.isCluster()).isTrue();
        assertThat(testPointCluster.getWeight()).isEqualTo(3);
    }
}
