package io.nlopez.clusterer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Nacho Lopez on 28/10/13.
 */
public class Cluster<T extends Clusterable> implements Clusterable {

    private Set<T> markers = new HashSet<T>();
    private LatLng center;
    private LatLngBounds bounds;
    private LatLngBounds.Builder boundsBuilder;

    public Cluster(T marker) {
        boundsBuilder = new LatLngBounds.Builder();
        addMarker(marker);
    }

    public void addMarker(T marker) {
        markers.add(marker);
        bounds = boundsBuilder.include(marker.getPosition()).build();
        if (center == null) {
            center = marker.getPosition();
        } else {
            center = bounds.getCenter();
        }
    }

    public Set<T> getMarkers() {
        return markers;
    }

    public LatLngBounds getBounds() {
        return bounds;
    }

    public LatLng getCenter() {
        return center;
    }

    public boolean isCluster() {
        return getWeight() > 1;
    }

    public int getWeight() {
        return markers.size();
    }

    @Override
    public LatLng getPosition() {
        return getCenter();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cluster)) return false;

        Cluster cluster = (Cluster) o;

        return !(center != null ? !center.equals(cluster.center) : cluster.center != null);

    }

    @Override
    public int hashCode() {
        return center != null ? center.hashCode() : 0;
    }
}
