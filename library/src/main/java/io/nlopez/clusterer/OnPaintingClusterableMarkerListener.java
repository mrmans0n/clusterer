package io.nlopez.clusterer;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by mrm on 7/4/15.
 */
public interface OnPaintingClusterableMarkerListener<T> {
    MarkerOptions onCreateMarkerOptions(T item);

    void onMarkerCreated(Marker marker, T item);
}