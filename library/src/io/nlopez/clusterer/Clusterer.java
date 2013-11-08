package io.nlopez.clusterer;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
    private static final QuadTreeBoundingBox WORLD = new QuadTreeBoundingBox(19, -166, 72, -53);

    private GoogleMap googleMap;
    private Context context;
    private List<T> pointsOfInterest = new ArrayList<T>();
    private QuadTree<T> pointsTree;
    private float oldZoomValue = 0f;
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
        this.pointsTree = new QuadTree<T>(, NODE_CAPACITY);
    }

    GoogleMap.OnCameraChangeListener cameraChanged = new GoogleMap.OnCameraChangeListener() {

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            if (oldZoomValue != cameraPosition.zoom) {
                oldZoomValue = cameraPosition.zoom;
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
        //pointsOfInterest.add(marker);
        //pointsTree.insertData(new QuadTreeNodeData<T>(T, ))
    }

    public void addAll(ArrayList<T> markers) {
        QuadTreeNodeData<T> allData = new QuadTreeNodeData<T>(WORLD, NODE_CAPACITY);
        allData.setData(markers);
        pointsTree.insertData(allData);

        // this.pointsOfInterest.addAll(markers);
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
        task.execute(pointsOfInterest);
    }

    private class UpdateMarkersTask extends AsyncTask<List<T>, Void, HashMap<Point, Cluster>> {

        private GoogleMap map;
        private OnPaintingClusterableMarkerListener onPaintingClusterableMarker;
        private OnPaintingClusterListener onPaintingCluster;
        private Projection projection;
        private int gridInPixels;

        UpdateMarkersTask(Context context, GoogleMap map, OnPaintingClusterableMarkerListener onPaintingClusterableMarker,
                          OnPaintingClusterListener onPaintingCluster) {
            this.gridInPixels = (int) (GRID_SIZE * context.getResources().getDisplayMetrics().density + 0.5f);
            this.map = map;
            this.onPaintingCluster = onPaintingCluster;
            this.onPaintingClusterableMarker = onPaintingClusterableMarker;
            this.projection = map.getProjection();

        }

        private boolean isInDistance(Point origin, Point other) {
            return origin.x >= other.x - gridInPixels && origin.x <= other.x + gridInPixels && origin.y >= other.y - gridInPixels
                    && origin.y <= other.y + gridInPixels;
        }

        @Override
        protected HashMap<Point, Cluster> doInBackground(List<T>... params) {

            HashMap<Point, Cluster> clusters = new HashMap<Point, Cluster>();

            for (Clusterable marker : params[0]) {
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
