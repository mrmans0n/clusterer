package io.nlopez.clusterer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import io.nlopez.clusterer.utils.CustomTestRunner;
import io.nlopez.clusterer.utils.TestPoint;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mrm on 12/1/15.
 */
@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class QuadTreeBoundingBoxTest {
    private static final TestPoint POINT_MESTALLA = new TestPoint(39.474531, -0.358065);
    private static final TestPoint POINT_CUENCA = new TestPoint(40.070392, -2.137416);
    private static final TestPoint POINT_SEVASTOPOL = new TestPoint(44.616650, 33.525366);

    private static final QuadTreeBoundingBox WORLD_BOX = new QuadTreeBoundingBox(85, 180, -85, -180);
    private static final LatLng IBERIAN_NE = new LatLng(43.58039085560786, 3.9990234375);
    private static final LatLng IBERIAN_SW = new LatLng(35.92464453144099, -10.458984375);

    private static final LatLng PORTUGAL_NE = new LatLng(42.06560675405716, -6.064453125);
    private static final LatLng PORTUGAL_SW = new LatLng(36.949891786813296, -9.5361328125);

    private static final LatLng FRANCE_NE = new LatLng(50.764259357116465, 9.4482421875);
    private static final LatLng FRANCE_SW = new LatLng(42.58544425738491, -4.9658203125);

    @Test
    public void test_quadtree_bounding_box_creation() {
        QuadTreeBoundingBox box = fromNESW(IBERIAN_NE, IBERIAN_SW);

        assertThat(box.getMinX()).isEqualTo(IBERIAN_SW.latitude);
        assertThat(box.getMinY()).isEqualTo(IBERIAN_SW.longitude);

        assertThat(box.getMaxX()).isEqualTo(IBERIAN_NE.latitude);
        assertThat(box.getMaxY()).isEqualTo(IBERIAN_NE.longitude);

        assertThat(box.getMidX()).isEqualTo((IBERIAN_NE.latitude + IBERIAN_SW.latitude) / 2);
        assertThat(box.getMidY()).isEqualTo((IBERIAN_NE.longitude + IBERIAN_SW.longitude) / 2);
    }

    @Test
    public void test_quadtree_bounding_box_creation_latlngbounds() {
        QuadTreeBoundingBox box = new QuadTreeBoundingBox(new LatLngBounds(IBERIAN_SW, IBERIAN_NE));

        assertThat(box.getMinX()).isEqualTo(IBERIAN_SW.latitude);
        assertThat(box.getMinY()).isEqualTo(IBERIAN_SW.longitude);

        assertThat(box.getMaxX()).isEqualTo(IBERIAN_NE.latitude);
        assertThat(box.getMaxY()).isEqualTo(IBERIAN_NE.longitude);

        assertThat(box.getMidX()).isEqualTo((IBERIAN_NE.latitude + IBERIAN_SW.latitude) / 2);
        assertThat(box.getMidY()).isEqualTo((IBERIAN_NE.longitude + IBERIAN_SW.longitude) / 2);
    }

    @Test
    public void test_quadtree_bounding_box_contains() {
        QuadTreeBoundingBox box = fromNESW(IBERIAN_NE, IBERIAN_SW);

        assertThat(box.containsData(POINT_CUENCA)).isTrue();
        assertThat(box.containsData(POINT_MESTALLA)).isTrue();
        assertThat(box.containsData(POINT_SEVASTOPOL)).isFalse();

        assertThat(WORLD_BOX.containsData(POINT_CUENCA)).isTrue();
        assertThat(WORLD_BOX.containsData(POINT_MESTALLA)).isTrue();
        assertThat(WORLD_BOX.containsData(POINT_SEVASTOPOL)).isTrue();
    }

    @Test
    public void test_quadtree_bounding_box_intersection() {
        QuadTreeBoundingBox spainBox = fromNESW(IBERIAN_NE, IBERIAN_SW);
        QuadTreeBoundingBox portugalBox = fromNESW(PORTUGAL_NE, PORTUGAL_SW);
        QuadTreeBoundingBox franceBox = fromNESW(FRANCE_NE, FRANCE_SW);

        assertThat(spainBox.isIntersecting(portugalBox)).isTrue();
        assertThat(portugalBox.isIntersecting(spainBox)).isTrue();

        assertThat(franceBox.isIntersecting(portugalBox)).isFalse();
        assertThat(portugalBox.isIntersecting(franceBox)).isFalse();

        assertThat(spainBox.isIntersecting(franceBox)).isTrue();
        assertThat(franceBox.isIntersecting(spainBox)).isTrue();

        assertThat(WORLD_BOX.isIntersecting(spainBox)).isTrue();
        assertThat(WORLD_BOX.isIntersecting(portugalBox)).isTrue();
        assertThat(WORLD_BOX.isIntersecting(franceBox)).isTrue();
    }

    private QuadTreeBoundingBox fromNESW(LatLng northEast, LatLng southWest) {
        return new QuadTreeBoundingBox(southWest.latitude, northEast.longitude, northEast.latitude, southWest.longitude);
    }
}
