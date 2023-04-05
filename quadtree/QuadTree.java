package com.abhorrentdestruction.core.quadtree;

import com.abhorrentdestruction.core.logic.Sizes;
import com.abhorrentdestruction.core.quadtree.listeners.ListenerQuad;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class QuadTree {
	public static final int MAX_DEPTH = 16;

	private Vector2 chunk;

	private QTNode[] nodes = new QTNode[0];
	private Quad root;

	public void debug(ShapeRenderer shapeRenderer, boolean quads, boolean shape, boolean search) {
		root.debug(shapeRenderer, quads, shape, search);
	}

	public void insert(QTNode[] newNodes) {
		QTNode[] result = new QTNode[nodes.length + newNodes.length];
		System.arraycopy(nodes, 0, result, 0, nodes.length);
		System.arraycopy(newNodes, 0, result, nodes.length, newNodes.length);
		nodes = result;
	}

	public void insert(QTNode newNode) {
		QTNode[] result = new QTNode[nodes.length + 1];
		System.arraycopy(nodes, 0, result, 0, nodes.length);
		result[result.length - 1] = newNode;
		nodes = result;
	}

	public int size() {
		return nodes.length;
	}

	public void update(Vector2 pos) {
		chunk = new Vector2(Sizes.CHUNK_SIZE * (Math.round((pos.x - Sizes.CHUNK_SIZE / 2) / Sizes.CHUNK_SIZE)) / Sizes.CHUNK_SIZE, Sizes.CHUNK_SIZE * (Math.round((pos.y - Sizes.CHUNK_SIZE / 2) / Sizes.CHUNK_SIZE)) / Sizes.CHUNK_SIZE);

		root = new Quad((chunk.x - Sizes.CHUNK_FACTOR) * Sizes.CHUNK_SIZE, (chunk.y - Sizes.CHUNK_FACTOR) * Sizes.CHUNK_SIZE, Sizes.CHUNK_SIZE * (Sizes.CHUNK_FACTOR * 2 + 1), Sizes.CHUNK_SIZE * (Sizes.CHUNK_FACTOR * 2 + 1));

		nodes = root.insert(nodes);
	}

	public Quad getRoot() {
		return root;
	}

	public Collisions query(Polygon polygon, ListenerQuad listenerQuad) {
		return root.query(polygon, listenerQuad);
	}

	public Collisions query(Rectangle rectangle, ListenerQuad listenerQuad) {
		return root.query(rectangle, listenerQuad);
	}

	public Collisions query(Circle circle, ListenerQuad listenerQuad) {
		return root.query(circle, listenerQuad);
	}

	public void render(SpriteBatch batch, QuadTree quadtree) {
		root.render(batch, quadtree);
	}
}
