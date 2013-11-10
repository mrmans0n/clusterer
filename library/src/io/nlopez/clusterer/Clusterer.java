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

    private static final int GRID_SIZE = 50;
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

    public Clusterer(Context context, GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.context = context;
        this.googleMap.setOnCameraChangeListener(cameraChanged);
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
            this.gridInPixels = (int) (getSizeForZoomScale((int)map.getCameraPosition().zoom) * context.getResources().getDisplayMetrics().density + 0.5f);
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

            // Get x1,y1,xf,yf from bounds

            double x1 = bounds.southwest.latitude;
            double y1 = bounds.northeast.longitude;
            double xf = bounds.northeast.latitude;
            double yf = bounds.southwest.longitude;
            QuadTreeBoundingBox boundingBox = new QuadTreeBoundingBox(x1, y1, xf, yf);
            ArrayList<T> pointsInRegion = new ArrayList<T>();
            tree.getPointsInRange(boundingBox, pointsInRegion);

            for (Clusterable marker : pointsInRegion) {
                Point position = projection.toScreenLocation(marker.getPosition());
                boolean addedToCluster = false;

                for (Point storedPoint : clusters.keySet()) {

                    if (isInDistance(position, storedPoint)) {
                        clusters.get(storedPoint).addMarker(marker);
                        addedToCluster = true;
                        break;
                    }
                }

                if (!addedToCluster) {
                    clusters.put(position, new Cluster(marker));
                }

            }
            return clusters;
        }

        @Override
        protected void onPostExecute(HashMap<Point, Cluster> result) {
            map.clear();
            for (Cluster cluster : result.values()) {
                if (cluster.isCluster()) {
                    if (onPaintingCluster != null) {
                        Marker marker = map.addMarker(onPaintingCluster.onCreateClusterMarkerOptions(cluster));
                        onPaintingCluster.onMarkerCreated(marker, cluster);
                    } else {
                        map.addMarker(new MarkerOptions().position(cluster.getCenter())
                                .title(Integer.valueOf(cluster.getWeight()).toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                } else {
                    if (onPaintingClusterableMarker != null) {
                        Marker marker = map.addMarker(onPaintingClusterableMarker.onCreateMarkerOptions(cluster.getMarkers().get(0)));
                        onPaintingClusterableMarker.onMarkerCreated(marker, cluster.getMarkers().get(0));
                    } else {
                        map.addMarker(new MarkerOptions().position(cluster.getCenter()));
                    }
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
