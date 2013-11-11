package io.nlopez.clusterer;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Nacho Lopez on 28/10/13.
 */
public class Clusterer<T extends Clusterable> {

    private static final int NODE_CAPACITY = 4;
    private static final int CLUSTER_CENTER_PADDING = 120;
    private static final QuadTreeBoundingBox WORLD = new QuadTreeBoundingBox(-85, -180, 85, 180);

    private GoogleMap googleMap;
    private Context context;
    private QuadTree<T> pointsTree;
    private float oldZoomValue = 0f;
    private LatLng oldTargetValue;

    private OnPaintingClusterListener onPaintingCluster;
    private OnPaintingClusterableMarkerListener onPaintingMarker;
    private OnCameraChangeListener onCameraChangeListener;
    private HashMap<T, Marker> pointMarkers;
    private HashMap<Marker, Cluster<T>> clusterMarkers;
    private List<Marker> allMarkers;
    private UpdateMarkersTask task;


    public Clusterer(Context context, GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.context = context;
        this.googleMap.setOnCameraChangeListener(cameraChanged);
        this.googleMap.setOnMarkerClickListener(markerClicked);
        this.pointMarkers = new HashMap<T, Marker>();
        this.clusterMarkers = new HashMap<Marker, Cluster<T>>();
        this.allMarkers = new ArrayList<Marker>();
        initQuadTree();
    }

    private void initQuadTree() {
        this.pointsTree = new QuadTree<T>(WORLD, NODE_CAPACITY);
    }

    GoogleMap.OnCameraChangeListener cameraChanged = new GoogleMap.OnCameraChangeListener() {

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            if (oldZoomValue != cameraPosition.zoom || oldTargetValue != cameraPosition.target) {
                oldZoomValue = cameraPosition.zoom;
                oldTargetValue = cameraPosition.target;

                updateMarkers();
            }
            if (onCameraChangeListener != null) {
                onCameraChangeListener.onCameraChange(cameraPosition);
            }
        }
    };

    GoogleMap.OnMarkerClickListener markerClicked = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Cluster<T> cluster = clusterMarkers.get(marker);
            if (cluster != null) {
                CameraUpdate update = CameraUpdateFactory.newLatLngBounds(cluster.getBounds(), CLUSTER_CENTER_PADDING);
                googleMap.animateCamera(update, 500, null);
                return true;
            }
            return false;
        }
    };

    public void forceUpdate() {
        updateMarkers();
    }

    public void add(T marker) {
        pointsTree.insertData(marker);
    }

    public void addAll(List<T> markers) {
        pointsTree.insertData(markers);
    }

    public void clear() {
        initQuadTree();
    }

    public OnPaintingClusterListener getOnPaintingClusterListener() {
        return onPaintingCluster;
    }

    public void setOnPaintingClusterListener(OnPaintingClusterListener onPaintingCluster) {
        this.onPaintingCluster = onPaintingCluster;
    }

    public OnPaintingClusterableMarkerListener getOnPaintingMarkerListener() {
        return onPaintingMarker;
    }

    public void setOnPaintingMarkerListener(OnPaintingClusterableMarkerListener onPaintingMarker) {
        this.onPaintingMarker = onPaintingMarker;
    }

    public OnCameraChangeListener getOnCameraChangeListener() {
        return onCameraChangeListener;
    }

    public void setOnCameraChangeListener(OnCameraChangeListener onCameraChangeListener) {
        this.onCameraChangeListener = onCameraChangeListener;
    }

    @SuppressWarnings("unchecked")
    protected void updateMarkers() {
        if (task == null || !task.isLocked()) {
            task = new UpdateMarkersTask(context, googleMap, onPaintingMarker, onPaintingCluster);
            task.execute(pointsTree);
        } else {
            System.out.println("Trying to screw you up!");
        }
    }

    private class UpdateMarkersTask extends AsyncTask<QuadTree<T>, Void, ClusteringProcessResultHolder<T>> {

        private GoogleMap map;
        private LatLngBounds bounds;
        private OnPaintingClusterableMarkerListener onPaintingClusterableMarker;
        private OnPaintingClusterListener onPaintingCluster;
        private Projection projection;
        private int gridInPixels;
        private AtomicBoolean isLocked;

        UpdateMarkersTask(Context context, GoogleMap map, OnPaintingClusterableMarkerListener onPaintingClusterableMarker,
                          OnPaintingClusterListener onPaintingCluster) {
            this.map = map;
            this.bounds = map.getProjection().getVisibleRegion().latLngBounds;
            this.gridInPixels = (int) (getSizeForZoomScale((int) map.getCameraPosition().zoom) * context.getResources().getDisplayMetrics().density + 0.5f);
            this.onPaintingCluster = onPaintingCluster;
            this.onPaintingClusterableMarker = onPaintingClusterableMarker;
            this.projection = map.getProjection();
            this.isLocked = new AtomicBoolean(false);
        }

        private int getSizeForZoomScale(int scale) {
            switch (scale) {
                case 13:
                case 14:
                case 15:
                    return 64;
                case 16:
                case 17:
                case 18:
                    return 32;
                case 19:
                    return 16;
                default:
                    return 88;
            }
        }

        private boolean isInDistance(Point origin, Point other) {
            return origin.x >= other.x - gridInPixels && origin.x <= other.x + gridInPixels && origin.y >= other.y - gridInPixels
                    && origin.y <= other.y + gridInPixels;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.isLocked.set(true);
        }

        @Override
        protected ClusteringProcessResultHolder doInBackground(QuadTree<T>... params) {

            ClusteringProcessResultHolder<T> result = new ClusteringProcessResultHolder<T>();
            QuadTree<T> tree = params[0];

            // Store old points
            List<T> pointsToKeep = new ArrayList<T>(pointMarkers.keySet());
            List<T> pointsToDelete = new ArrayList<T>(pointMarkers.keySet());

            // Get x1,y1,xf,yf from bounds
            double x1 = bounds.southwest.latitude;
            double y1 = bounds.northeast.longitude;
            double xf = bounds.northeast.latitude;
            double yf = bounds.southwest.longitude;
            QuadTreeBoundingBox boundingBox = new QuadTreeBoundingBox(x1, y1, xf, yf);
            ArrayList<T> pointsInRegion = new ArrayList<T>();
            tree.getPointsInRange(boundingBox, pointsInRegion);

            // We got here the points we want to show show
            result.pois.addAll(pointsInRegion);

            // Intersect the new points with the old points = get the points NOT TO delete
            pointsToKeep.retainAll(pointsInRegion);

            // Remove from the old points the ones we don't want to delete = in here we will have everything not showing
            pointsToDelete.removeAll(pointsToKeep);

            // Create all the Clusters
            HashMap<Point, Cluster<T>> positions = new HashMap<Point, Cluster<T>>();
            for (T point : pointsInRegion) {
                Point position = projection.toScreenLocation(point.getPosition());
                boolean addedToCluster = false;

                for (Point storedPoint : positions.keySet()) {

                    if (isInDistance(position, storedPoint)) {
                        positions.get(storedPoint).addMarker(point);
                        addedToCluster = true;
                        break;
                    }
                }

                if (!addedToCluster) {
                    positions.put(position, new Cluster<T>(point));
                }
            }

            // Prepare the result: the pois to delete and the new clusters
            result.poisToDelete.addAll(pointsToDelete);
            result.poisToKeep.addAll(pointsToKeep);
            for (Cluster<T> cluster : positions.values()) {
                if (cluster.isCluster()) {
                    result.clusters.add(cluster);
                    for (T poi : cluster.getMarkers()) {
                        result.pois.remove(poi);
                        result.poisToKeep.remove(poi);
                        result.poisToDelete.add(poi);
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ClusteringProcessResultHolder<T> result) {

            for (Marker marker : clusterMarkers.keySet()) {
                marker.remove();
            }

            for (T poi : result.poisToDelete) {
                Marker marker = pointMarkers.get(poi);
                if (marker != null) {
                    marker.remove();
                }
            }

            for (T poi : pointMarkers.keySet()) {
                if (!result.pois.contains(poi)) {
                    Marker marker = pointMarkers.get(poi);
                    if (marker != null) {
                        marker.remove();
                    }
                }
            }

            clusterMarkers.clear();
            pointMarkers.clear();

            for (Cluster<T> cluster : result.clusters) {
                Marker marker;
                if (onPaintingCluster != null) {
                    marker = map.addMarker(onPaintingCluster.onCreateClusterMarkerOptions(cluster));
                    onPaintingCluster.onMarkerCreated(marker, cluster);
                } else {
                    marker = map.addMarker(new MarkerOptions().position(cluster.getCenter())
                            .title(Integer.valueOf(cluster.getWeight()).toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
                allMarkers.add(marker);
                clusterMarkers.put(marker, cluster);
            }

            for (T poi : result.pois) {
                if (!pointMarkers.containsKey(poi)) {
                    Marker marker;
                    if (onPaintingClusterableMarker != null) {
                        marker = map.addMarker(onPaintingClusterableMarker.onCreateMarkerOptions(poi));
                        onPaintingClusterableMarker.onMarkerCreated(marker, poi);
                    } else {
                        marker = map.addMarker(new MarkerOptions().position(poi.getPosition()));
                    }
                    allMarkers.add(marker);
                    pointMarkers.put(poi, marker);
                }
            }
            this.isLocked.set(false);
        }

        public boolean isLocked() {
            return this.isLocked.get();
        }
    }

    private class ClusteringProcessResultHolder<T extends Clusterable> {
        public ArrayList<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
        public ArrayList<T> pois = new ArrayList<T>();
        public ArrayList<T> poisToDelete = new ArrayList<T>();
        public ArrayList<T> poisToKeep = new ArrayList<T>();
    }

    public interface OnPaintingClusterableMarkerListener {
        MarkerOptions onCreateMarkerOptions(Clusterable clusterable);

        void onMarkerCreated(Marker marker, Clusterable clusterable);
    }

    public interface OnPaintingClusterListener {
        MarkerOptions onCreateClusterMarkerOptions(Cluster cluster);

        void onMarkerCreated(Marker marker, Cluster cluster);
    }

    public interface OnCameraChangeListener {
        void onCameraChange(CameraPosition position);
    }

}
