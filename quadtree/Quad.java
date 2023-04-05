package com.abhorrentdestruction.core.quadtree;

import com.abhorrentdestruction.core.quadtree.listeners.ListenerQuad;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

public class Quad extends Rectangle {
	private ListenerQuad[] listeners = new ListenerQuad[0];
	private int capacity = 1;
	private int depth = 0;

	private QTNode[] children = new QTNode[0];

	private Quad ne;
	private Quad nw;
	private Quad se;
	private Quad sw;

	private boolean divided = false;

	public Quad(float x, float y, float width, float height) {
		this(x, y, width, height, 0, 1);
	}

	public Quad(float x, float y, float width, float height, int depth, int capacity) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.capacity = capacity;
	}

	private void subdivide() {
		ne = new Quad(x + width / 2, y + height / 2, width / 2, height / 2, depth + 1, capacity);
		nw = new Quad(x + width / 2, y, width / 2, height / 2, depth + 1, capacity);
		se = new Quad(x, y + height / 2, width / 2, height / 2, depth + 1, capacity);
		sw = new Quad(x, y, width / 2, height / 2, depth + 1, capacity);

		divided = true;
	}

	public QTNode[] insert(QTNode[] nodes) {
		QTNode[] contains = new QTNode[nodes.length];
		int count = 0;

		for (int i = 0; i < nodes.length; i++) {
			if (!nodes[i].isRemove() && contains(nodes[i].getX(), nodes[i].getY())) {
				insert(nodes[i]);
				contains[count++] = nodes[i];
			}
		}

		QTNode[] newNodes = new QTNode[count];

		System.arraycopy(contains, 0, newNodes, 0, count);

		return newNodes;
	}

	private void insert(QTNode node) {
		if (!contains(node.getX(), node.getY())) {
			return;
		}

		if (children.length < capacity || depth >= QuadTree.MAX_DEPTH) {
			children = add(node);
		} else {
			if (!divided) {
				subdivide();
			}

			ne.insert(node);
			nw.insert(node);
			se.insert(node);
			sw.insert(node);
		}
	}

	public QTNode[] add(QTNode newNode) {
		QTNode[] result = new QTNode[children.length + 1];
		System.arraycopy(children, 0, result, 0, children.length);
		result[result.length - 1] = newNode;
		return result;
	}

	public QTNode[] add(QTNode[] newNode) {
		QTNode[] result = new QTNode[children.length + newNode.length];
		System.arraycopy(children, 0, result, 0, children.length);
		System.arraycopy(newNode, 0, result, children.length, newNode.length);
		return result;
	}

	public void addListener(ListenerQuad listener) {
		ListenerQuad[] result = new ListenerQuad[listeners.length + 1];
		System.arraycopy(listeners, 0, result, 0, listeners.length);
		result[result.length - 1] = listener;
		listeners = result;
	}

	public void debug(ShapeRenderer shapeRenderer, boolean quads, boolean shape, boolean search) {
		if (divided) {
			ne.debug(shapeRenderer, quads, shape, search);
			nw.debug(shapeRenderer, quads, shape, search);
			se.debug(shapeRenderer, quads, shape, search);
			sw.debug(shapeRenderer, quads, shape, search);
		}

		if (quads) {
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.rect(x, y, width, height);
		}

		for (int i = 0; i < children.length; i++) {
			children[i].debug(shapeRenderer, shape, search);
		}
	}

	public Collisions query(Polygon polygon, ListenerQuad listenerQuad) {
		Collisions collisions = new Collisions();

		if (Intersector.overlapConvexPolygons(new Polygon(new float[] { x, y, x + width, y, x + width, y + height, x, y + height }), polygon)) {
			for (QTNode node : children) {
				if (polygon.contains(node.getX(), node.getY())) {
					collisions.add(listenerQuad.contains(node));
				}
			}

			if (divided) {
				collisions.add(ne.query(polygon, listenerQuad));
				collisions.add(nw.query(polygon, listenerQuad));
				collisions.add(se.query(polygon, listenerQuad));
				collisions.add(sw.query(polygon, listenerQuad));
			}
		}

		return collisions;
	}

	public Collisions query(Rectangle rectangle, ListenerQuad listenerQuad) {
		Collisions collisions = new Collisions();

		if (Intersector.overlaps(rectangle, this)) {
			for (QTNode node : children) {
				if (rectangle.contains(node.getX(), node.getY())) {
					collisions.add(listenerQuad.contains(node));
				}
			}

			if (divided) {
				collisions.add(ne.query(rectangle, listenerQuad));
				collisions.add(nw.query(rectangle, listenerQuad));
				collisions.add(se.query(rectangle, listenerQuad));
				collisions.add(sw.query(rectangle, listenerQuad));
			}
		}

		return collisions;
	}

	public Collisions query(Circle circle, ListenerQuad listenerQuad) {
		Collisions collisions = new Collisions();

		if (Intersector.overlaps(circle, this)) {
			for (QTNode node : children) {
				if (circle.contains(node.getX(), node.getY())) {
					collisions.add(listenerQuad.contains(node));
				}
			}

			if (divided) {
				collisions.add(ne.query(circle, listenerQuad));
				collisions.add(nw.query(circle, listenerQuad));
				collisions.add(se.query(circle, listenerQuad));
				collisions.add(sw.query(circle, listenerQuad));
			}
		}

		return collisions;
	}

	public void render(SpriteBatch batch, QuadTree quadtree) {
		if (divided) {
			ne.render(batch, quadtree);
			nw.render(batch, quadtree);
			se.render(batch, quadtree);
			sw.render(batch, quadtree);
		}

		for (int i = 0; i < children.length; i++) {
			children[i].update(quadtree);
			children[i].render(batch);
		}
	}
}
