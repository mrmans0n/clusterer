package io.nlopez.clusterer;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;

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

/**
 * Created by Nacho Lopez on 28/10/13.
 */
public class Clusterer<T extends Clusterable> {

    private static final int NODE_CAPACITY = 4;
    private static final QuadTreeBoundingBox WORLD = new QuadTreeBoundingBox(-85, -180, 85, 180);

    private GoogleMap googleMap;
    private Context context;
    private QuadTree<T> pointsTree;
    private float oldZoomValue = 0f;
    private LatLng oldTargetValue;

    private OnPaintingClusterListener onPaintingCluster;
    private OnPaintingClusterableMarkerListener onPaintingMarker;
    private OnCameraChangeListener onCameraChangeListener;
    private List<T> pointsShown;
    private List<T> pointsToDelete;
    private HashMap<Clusterable, Marker> pointMarkers;
    private List<Marker> clusterMarkers;

    public Clusterer(Context context, GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.context = context;
        this.googleMap.setOnCameraChangeListener(cameraChanged);
        this.pointsShown = new ArrayList<T>();
        this.pointMarkers = new HashMap<Clusterable, Marker>();
        this.clusterMarkers = new ArrayList<Marker>();
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
        UpdateMarkersTask task = new UpdateMarkersTask(context, googleMap, onPaintingMarker, onPaintingCluster);
        task.execute(pointsTree);
    }

    private class UpdateMarkersTask extends AsyncTask<QuadTree<T>, Void, HashMap<Point, Cluster>> {

        private GoogleMap map;
        private LatLngBounds bounds;
        private OnPaintingClusterableMarkerListener onPaintingClusterableMarker;
        private OnPaintingClusterListener onPaintingCluster;
        private Projection projection;
        private int gridInPixels;

        UpdateMarkersTask(Context context, GoogleMap map, OnPaintingClusterableMarkerListener onPaintingClusterableMarker,
                          OnPaintingClusterListener onPaintingCluster) {
            this.map = map;
            this.bounds = map.getProjection().getVisibleRegion().latLngBounds;
            this.gridInPixels = (int) (getSizeForZoomScale((int) map.getCameraPosition().zoom) * context.getResources().getDisplayMetrics().density + 0.5f);
            this.onPaintingCluster = onPaintingCluster;
            this.onPaintingClusterableMarker = onPaintingClusterableMarker;
            this.projection = map.getProjection();
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
        protected HashMap<Point, Cluster> doInBackground(QuadTree<T>... params) {

            HashMap<Point, Cluster> clusters = new HashMap<Point, Cluster>();
            QuadTree<T> tree = params[0];

            // Store old points
            List<T> pointsToKeep = new ArrayList<T>(pointsShown);
            pointsToDelete = new ArrayList<T>(pointsShown);

            // Get x1,y1,xf,yf from bounds
            double x1 = bounds.southwest.latitude;
            double y1 = bounds.northeast.longitude;
            double xf = bounds.northeast.latitude;
            double yf = bounds.southwest.longitude;
            QuadTreeBoundingBox boundingBox = new QuadTreeBoundingBox(x1, y1, xf, yf);
            ArrayList<T> pointsInRegion = new ArrayList<T>();
            tree.getPointsInRange(boundingBox, pointsInRegion);

            // We got here the points we want to show show
            pointsShown = pointsInRegion;

            // Intersect the new points with the old points = get the points NOT TO delete
            pointsToKeep.retainAll(pointsShown);

            // Remove from the old points the ones we don't want to delete = in here we will have everything not showing
            pointsToDelete.removeAll(pointsToKeep);

            for (Clusterable point : pointsInRegion) {
                Point position = projection.toScreenLocation(point.getPosition());
                boolean addedToCluster = false;

                for (Point storedPoint : clusters.keySet()) {

                    if (isInDistance(position, storedPoint)) {
                        if (pointsToKeep.contains(point)) {
                            pointsToKeep.remove(point);
                            pointsToDelete.add((T)point);
                        }
                        clusters.get(storedPoint).addMarker(point);
                        addedToCluster = true;
                        break;
                    }
                }

                if (!addedToCluster) {
                    clusters.put(position, new Cluster(point));
                }

            }
            return clusters;
        }

        @Override
        protected void onPostExecute(HashMap<Point, Cluster> result) {
            // TODO: avoid the map.clear() call and delete knowingly
            map.clear();
            for (Cluster cluster : result.values()) {
                Marker marker;
                if (cluster.isCluster()) {
                    if (onPaintingCluster != null) {
                        marker = map.addMarker(onPaintingCluster.onCreateClusterMarkerOptions(cluster));
                        onPaintingCluster.onMarkerCreated(marker, cluster);
                    } else {
                        marker = map.addMarker(new MarkerOptions().position(cluster.getCenter())
                                .title(Integer.valueOf(cluster.getWeight()).toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                    // clusterMarkers.add(marker);
                } else {
                    if (onPaintingClusterableMarker != null) {
                        marker = map.addMarker(onPaintingClusterableMarker.onCreateMarkerOptions(cluster.getMarkers().get(0)));
                        onPaintingClusterableMarker.onMarkerCreated(marker, cluster.getMarkers().get(0));
                    } else {
                        marker = map.addMarker(new MarkerOptions().position(cluster.getCenter()));
                    }
                    // pointMarkers.put(cluster, marker);
                }
            }
        }

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
