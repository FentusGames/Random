package com.abhorrentdestruction.core.quadtree.listeners;

import com.abhorrentdestruction.core.quadtree.Collisions;
import com.abhorrentdestruction.core.quadtree.QTNode;

public interface ListenerQuad {
	Collisions contains(QTNode node);
}
