package com.example.clusteringtest.model;

import com.google.android.gms.maps.model.LatLng;

import in.nlopez.clustering.Clusterable;

public class PointOfInterest implements Clusterable {
	
	private LatLng locationLatLng;
	private String name;
	private String description;
	
	public PointOfInterest(LatLng locationLatLng, String name, String description) {
		this.locationLatLng = locationLatLng;
		this.name = name;
		this.description = description;
	}

	public LatLng getLocationLatLng() {
		return locationLatLng;
	}

	public void setLocationLatLng(LatLng locationLatLng) {
		this.locationLatLng = locationLatLng;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public LatLng getPosition() {
		return getLocationLatLng();
	}

}
