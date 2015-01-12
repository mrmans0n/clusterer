package io.nlopez.clusterer;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mrm on 12/1/15.
 */
public class TestPoint implements Clusterable {

    private LatLng latLng;

    public TestPoint(double latitude, double longitude) {
        latLng = new LatLng(latitude, longitude);
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }
}
