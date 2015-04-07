package io.nlopez.clusterer;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public interface OnPaintingClusterListener<T extends Clusterable> {
    MarkerOptions onCreateClusterMarkerOptions(Cluster<T> cluster);

    void onMarkerCreated(Marker marker, Cluster<T> cluster);
}