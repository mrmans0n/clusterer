Clustering for Maps v2 for Android
==================================

This is a working example that serve as proof of concept of poi clustering in Android Maps v2. 

Getting started
---------------

Of course, you must have a working **Google Play Services library** setup in your project, and do all this working from a **MapFragment** or wherever you have your **GoogleMap** object.

You have all the files you need in the [src/in/nlopez/clustering directory](https://github.com/mrmans0n/android-maps-v2-clustering/tree/master/src/in/nlopez/clustering) of the example project. **Just drop them in your project, and you're good to go.**

The objects you want to display in the map **must implement the Clusterable interface**. That interface only needs one method, getPosition() that returns a LatLng object (latitude and longitude).

An example:
´´´java
public class MyPoi implements Clusterable {
	private String name;
	private LatLng position;

	public MyPoi(String name, LatLng position) {
		this.name = name;
		this.position = position;
	}

	public String getName() { return name; }

	@Override
	public LatLng getPosition() { return position; }
}
´´´

TODO
----
Documentation on the works! 

License
=======

    Copyright 2013 Nacho Lopez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.