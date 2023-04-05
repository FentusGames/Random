package com.abhorrentdestruction.core.quadtree;

public class Collisions {
	public int count;

	public void add(Collisions collisions) {
		count += collisions.count;
	}
}
