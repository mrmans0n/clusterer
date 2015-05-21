package io.nlopez.clusterer.sample.application;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.nlopez.clusterer.Cluster;
import io.nlopez.clusterer.Clusterer;
import io.nlopez.clusterer.MarkerAnimation;
import io.nlopez.clusterer.OnPaintingClusterListener;
import io.nlopez.clusterer.OnPaintingClusterableMarkerListener;
import io.nlopez.clusterer.sample.model.PointOfInterest;

/**
 * Created by Nacho Lopez on 28/10/13.
 */
public class MainActivity extends Activity {

    private static final int CLUSTER_BASE_SIZE = 20;

    private GoogleMap map;
    private List<PointOfInterest> pointsOfInterest;
    private Clusterer<PointOfInterest> clusterer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        createDummyLocations();
        moveMap();
        initClusterer();
    }

    private void createDummyLocations() {
        pointsOfInterest = new ArrayList<PointOfInterest>();
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {

            double offsetLat = r.nextGaussian();
            double offsetLong = r.nextGaussian();

            pointsOfInterest.add(new PointOfInterest(new LatLng(39.4094747 + offsetLat, -7.24561540000002 + offsetLong), "Perry's house", "Very beautiful"));
            pointsOfInterest.add(new PointOfInterest(new LatLng(39.4701005 + offsetLat, -0.3769916999999623 + offsetLong), "SCUMM bar",
                    "It's just testimonial"));
            pointsOfInterest.add(new PointOfInterest(new LatLng(38.6340369 + offsetLat, -0.13612690000002203 + offsetLong), "The fifth pine",
                    "Cluttered and always crowded"));
            pointsOfInterest.add(new PointOfInterest(new LatLng(39.4753029 + offsetLat, -0.37543890000006286 + offsetLong), "Bernarda's junk",
                    "Very beautiful, various styles"));
            pointsOfInterest.add(new PointOfInterest(new LatLng(39.48158069999999 + offsetLat, -0.3436993000000257 + offsetLong), "Bar Cenas",
                    "Best envelopes"));
            pointsOfInterest.add(new PointOfInterest(new LatLng(39.4699075 + offsetLat, -0.3762881000000107 + offsetLong), "Cottolengo",
                    "Greatest munye-munye I've ever tasted"));
            pointsOfInterest.add(new PointOfInterest(new LatLng(39.5699075 + offsetLat, -0.3762881000000107 + offsetLong), "Perry Meison",
                    "A test point"));
            pointsOfInterest.add(new PointOfInterest(new LatLng(39.4699075 + offsetLat, -0.5762881000000107 + offsetLong), "Meison Burgz",
                    "Even more boring!"));
            pointsOfInterest.add(new PointOfInterest(new LatLng(40.4699075 + offsetLat, -0.6762881000000107 + offsetLong), "Murm",
                    "Don't know what to write!"));
            pointsOfInterest.add(new PointOfInterest(new LatLng(37.4699075 + offsetLat, -0.8762881000000107 + offsetLong), "Momansón",
                    "Ugh!"));

        }
    }

    private void moveMap() {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(40.463667, -3.749220)).zoom(1).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void initClusterer() {
        clusterer = new Clusterer<PointOfInterest>(this, map);
        clusterer.addAll(pointsOfInterest);

        clusterer.setAnimationEnabled(true);
        clusterer.setMarkerAnimation(new MarkerAnimation() {
            @Override
            public void animateMarker(Marker marker, float interpolation) {
                // Basic fading animation
                marker.setAlpha(interpolation);
            }
        });

        clusterer.setClustererListener(new Clusterer.Listener<PointOfInterest>() {
            @Override
            public void markerClicked(PointOfInterest marker) {
                Log.e("Clusterer", "marker clicked");
            }

            @Override
            public void clusterClicked(Cluster position) {
                Log.e("Clusterer", "cluster clicked");
            }
        });

        clusterer.setOnPaintingMarkerListener(new OnPaintingClusterableMarkerListener<PointOfInterest>() {

            @Override
            public void onMarkerCreated(Marker marker, PointOfInterest clusterable) {

            }

            @Override
            public MarkerOptions onCreateMarkerOptions(PointOfInterest poi) {
                return new MarkerOptions().position(poi.getPosition()).title(poi.getName()).snippet(poi.getDescription());
            }
        });

        clusterer.setOnPaintingClusterListener(new OnPaintingClusterListener<PointOfInterest>() {

            @Override
            public void onMarkerCreated(Marker marker, Cluster<PointOfInterest> cluster) {

            }

            @Override
            public MarkerOptions onCreateClusterMarkerOptions(Cluster<PointOfInterest> cluster) {
                return new MarkerOptions()
                        .title("Clustering " + cluster.getWeight() + " items")
                        .position(cluster.getCenter())
                        .icon(BitmapDescriptorFactory.fromBitmap(getClusteredLabel(cluster.getWeight(),
                                MainActivity.this)));
            }
        });

    }

    private Bitmap getClusteredLabel(Integer count, Context ctx) {

        float density = getResources().getDisplayMetrics().density;

        Resources r = ctx.getResources();
        Bitmap res = BitmapFactory.decodeResource(r, R.drawable.circle_red);
        res = res.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(res);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(16 * density);

        c.drawText(String.valueOf(count.toString()), res.getWidth() / 2, res.getHeight() / 2 + textPaint.getTextSize() / 3, textPaint);

        return res;
    }


}
