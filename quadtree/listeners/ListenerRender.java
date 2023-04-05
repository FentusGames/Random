package com.abhorrentdestruction.core.quadtree.listeners;

import com.abhorrentdestruction.core.quadtree.QTNode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface ListenerRender {
	void render(SpriteBatch batch, QTNode node);
}
