package in.nlopez.clustering;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

	private List<Clusterable> markers = new ArrayList<Clusterable>();
	private LatLng center; 
		
	public Cluster(Clusterable marker) { 
		addMarker(marker);
	}
	
	public void addMarker(Clusterable marker) {
		markers.add(marker);
		if (center == null) {
			center = marker.getPosition();
		}
	}

	public List<Clusterable> getMarkers() {
		return markers;
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
	
}
