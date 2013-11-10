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

import io.nlopez.clusterer.Cluster;
import io.nlopez.clusterer.Clusterable;
import io.nlopez.clusterer.Clusterer;
import io.nlopez.clusterer.sample.model.PointOfInterest;

/**
 * Created by Nacho Lopez on 28/10/13.
 */
public class MainActivity extends Activity {

    GoogleMap map;
    List<PointOfInterest> pointsOfInterest;
    Clusterer clusterer;

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
        pointsOfInterest.add(new PointOfInterest(new LatLng(39.4094747, -7.24561540000002), "Perry's house", "Very beautiful"));
        pointsOfInterest.add(new PointOfInterest(new LatLng(39.4701005, -0.3769916999999623), "SCUMM bar",
                "It's just testimonial"));
        pointsOfInterest.add(new PointOfInterest(new LatLng(38.6340369, -0.13612690000002203), "The fifth pine",
                "Cluttered and always crowded"));
        pointsOfInterest.add(new PointOfInterest(new LatLng(39.4753029, -0.37543890000006286), "Bernarda's junk",
                "Very beautiful, various styles"));
        pointsOfInterest.add(new PointOfInterest(new LatLng(39.48158069999999, -0.3436993000000257), "Bar Cenas",
                "Best envelopes"));
        pointsOfInterest.add(new PointOfInterest(new LatLng(39.4699075, -0.3762881000000107), "Cottolengo",
                "Greatest munye-munye I've ever tasted"));
        pointsOfInterest.add(new PointOfInterest(new LatLng(39.5699075, -0.3762881000000107), "Perry Meison",
                "A test point"));
        pointsOfInterest.add(new PointOfInterest(new LatLng(39.4699075, -0.5762881000000107), "Meison Burgz",
                "Boooooring!"));
    }

    private void moveMap() {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(40.463667, -3.749220)).zoom(1).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void initClusterer() {
        clusterer = new Clusterer(this, map);
        List<Clusterable> clusterables = new ArrayList<Clusterable>();
        for (PointOfInterest poi : pointsOfInterest) {
            clusterables.add(poi);
        }
        clusterer.addAll(clusterables);

        clusterer.setOnPaintingMarkerListener(new Clusterer.OnPaintingClusterableMarkerListener() {

            @Override
            public void onMarkerCreated(Marker marker, Clusterable clusterable) {

            }

            @Override
            public MarkerOptions onCreateMarkerOptions(Clusterable clusterable) {
                PointOfInterest poi = (PointOfInterest) clusterable;
                return new MarkerOptions().position(clusterable.getPosition()).title(poi.getName()).snippet(poi.getDescription());
            }
        });

        clusterer.setOnPaintingClusterListener(new Clusterer.OnPaintingClusterListener() {

            @Override
            public void onMarkerCreated(Marker marker, Cluster cluster) {

            }

            @Override
            public MarkerOptions onCreateClusterMarkerOptions(Cluster cluster) {
                return new MarkerOptions()
                        .title("Clustering " + cluster.getWeight() + " items")
                        .position(cluster.getCenter())
                        .icon(BitmapDescriptorFactory.fromBitmap(getClusteredLabel(cluster.getWeight(),
                                MainActivity.this)));
            }
        });

    }

    private Bitmap getClusteredLabel(Integer count, Context ctx) {
        Resources r = ctx.getResources();
        Bitmap res = BitmapFactory.decodeResource(r, R.drawable.circle_red);
        res = res.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(res);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.WHITE);
        float density = getResources().getDisplayMetrics().density;
        textPaint.setTextSize(16 * density);

        c.drawText(String.valueOf(count.toString()), res.getWidth() / 2, res.getHeight() / 2 + textPaint.getTextSize() / 3, textPaint);

        return res;
    }

}