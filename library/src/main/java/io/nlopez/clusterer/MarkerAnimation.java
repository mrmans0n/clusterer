package io.nlopez.clusterer;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by mrm on 04/07/14.
 */
public interface MarkerAnimation {
    public void animateMarker(Marker marker, float interpolation);
}
