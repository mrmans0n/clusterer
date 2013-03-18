package com.example.clusteringtest;

import in.nlopez.clustering.Clusterable;
import in.nlopez.clustering.Clusterer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.example.clusteringtest.model.PointOfInterest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

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
		initClusterer();
	}

	private void createDummyLocations() {
		pointsOfInterest = new ArrayList<PointOfInterest>();
		pointsOfInterest.add(new PointOfInterest(new LatLng(39.4094747, -7.24561540000002), "Plaza de toros (Valencia)", 
				"Very beautiful"));
		pointsOfInterest.add(new PointOfInterest(new LatLng(39.4701005, -0.3769916999999623), "Plaza del Ayuntamiento (Valencia)",
				"It's just testimonial"));
		pointsOfInterest.add(new PointOfInterest(new LatLng(38.6340369, -0.13612690000002203), "Plaza del Pilar (Valencia)",
				"Cluttered but has a great Falla"));
		pointsOfInterest.add(new PointOfInterest(new LatLng(39.4753029, -0.37543890000006286), "Catedral de Valencia",
				"Very beautiful, various styles"));
		pointsOfInterest.add(new PointOfInterest(new LatLng(39.48158069999999, -0.3436993000000257), "Universitat Politecnica de Valencia",
				"Best university in the region by far"));
		pointsOfInterest.add(new PointOfInterest(new LatLng(39.4699075, -0.3762881000000107), "Horchateria Daniel",
				"Greatest fartons I've ever tasted"));
	}

	private void initClusterer() {
		clusterer = new Clusterer(this, map);
		List<Clusterable> clusterables = new ArrayList<Clusterable>();
		for (PointOfInterest poi : pointsOfInterest) {
			clusterables.add(poi);
		}
		clusterer.addAll(clusterables);

	}
}