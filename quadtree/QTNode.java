package com.abhorrentdestruction.core.quadtree;

import com.abhorrentdestruction.core.logic.Sizes;
import com.abhorrentdestruction.core.quadtree.listeners.ListenerQuad;
import com.abhorrentdestruction.core.quadtree.listeners.ListenerRender;
import com.abhorrentdestruction.core.quadtree.listeners.ListenerUpdate;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;

public class QTNode extends Entity {
	private ListenerUpdate[] listeners = new ListenerUpdate[0];
	private ListenerRender[] renderers = new ListenerRender[0];

	private boolean remove = false;

	private Shape2D shape;
	private Shape2D shapeSearch;

	private float speed = 1;

	private float precision = 8;

	private float PUSH_MAX = 4;

	private float pushMin = 1;
	private float pushMaxCurrent = PUSH_MAX;

	private boolean blocking = false;

	public QTNode(float x, float y, int radius) {
		super(x, y);

		Sizes.setLargestCircle(radius * 2);

		shape = new Circle(x, y, radius);
		shapeSearch = new Circle(x, y, radius * 2);
	}

	public QTNode(float x, float y, int width, int height) {
		super(x, y);

		shape = new Rectangle(x, y, width, height);
		shapeSearch = new Rectangle(x - (Sizes.getLargestCircle() + 1), y - (Sizes.getLargestCircle() + 1), width + (Sizes.getLargestCircle() + 1) * 2, height + (Sizes.getLargestCircle() + 1) * 2);
	}

	public void update(QuadTree quadtree) {
		for (int p = 0; p < precision; p++) {
			incrementAngle(getSpin(), precision);
			incrementPos(getVelocity(), precision);
			setMovingAngle(getRotationAngle());

			Collisions collisions = collisions(quadtree);

			if (collisions.count > 0) {
				decrementPos(getVelocity(), precision * clamp(pushMaxCurrent - collisions.count, pushMin, pushMaxCurrent));
			}
		}

		for (int l = 0; l < listeners.length; l++) {
			listeners[l].update(this);
		}
	}

	private Collisions collisions(QuadTree quadtree) {
		Collisions collisions = new Collisions();

		QTNode thiz = (QTNode) this;

		// CIRCLE
		if (thiz.getShape() instanceof Circle && thiz.getShapeSearch() instanceof Circle) {
			Circle thisCircle = (Circle) thiz.getShape();
			Circle thisCircleSearch = (Circle) thiz.getShapeSearch();

			thisCircle.x = thiz.getX();
			thisCircle.y = thiz.getY();

			thisCircleSearch.x = thiz.getX();
			thisCircleSearch.y = thiz.getY();

			collisions.add(quadtree.query(thisCircleSearch, new ListenerQuad() {
				@Override
				public Collisions contains(QTNode that) {
					Collisions collisions = new Collisions();

					if (that.getShape() instanceof Circle) {
						Circle thatCircle = (Circle) that.getShape();

						thatCircle.x = that.getX();
						thatCircle.y = that.getY();

						if (that.isBlocking()) {
							// CIRCLE vs CIRCLE
							if (thiz != that) {
								if (thisCircle.overlaps(thatCircle)) {
									float distance = distance(thatCircle.x, thatCircle.y, thisCircle.x, thisCircle.y);
									float overlap = (distance - thisCircle.radius - thatCircle.radius) / precision;

									thiz.getPos().add(overlap * (thatCircle.x - thisCircle.x) / distance, overlap * (thatCircle.y - thisCircle.y) / distance);

									collisions.count++;
								}
							}
						}

						if (thiz.isBlocking()) {
							// CIRCLE vs CIRCLE
							if (thiz != that) {
								if (thisCircle.overlaps(thatCircle)) {
									float distance = distance(thatCircle.x, thatCircle.y, thisCircle.x, thisCircle.y);
									float overlap = (distance - thisCircle.radius - thatCircle.radius) / precision;

									that.getPos().sub(overlap * (thatCircle.x - thisCircle.x) / distance, overlap * (thatCircle.y - thisCircle.y) / distance);

									collisions.count++;
								}
							}
						}

						// CIRCLE vs CIRCLE
						if (thiz != that && !that.isBlocking() && !thiz.isBlocking()) {
							if (thisCircle.overlaps(thatCircle)) {
								float distance = distance(thatCircle.x, thatCircle.y, thisCircle.x, thisCircle.y);
								float overlap = (0.5F * (distance - thisCircle.radius - thatCircle.radius)) / precision;

								that.getPos().sub(overlap * (thatCircle.x - thisCircle.x) / distance, overlap * (thatCircle.y - thisCircle.y) / distance);
								thiz.getPos().add(overlap * (thatCircle.x - thisCircle.x) / distance, overlap * (thatCircle.y - thisCircle.y) / distance);

								collisions.count++;
							}
						}
					}

					// CIRCLE vs RECTANGLE
					if (that.getShape() instanceof Rectangle) {
						Rectangle thatRectangle = (Rectangle) that.getShape();

						thatRectangle.x = that.getX() - thatRectangle.width / 2;
						thatRectangle.y = that.getY() - thatRectangle.height / 2;

						float closestX = clamp(thisCircle.x, thatRectangle.x, thatRectangle.x + thatRectangle.width);
						float closestY = clamp(thisCircle.y, thatRectangle.y, thatRectangle.y + thatRectangle.height);

						float distanceX = thisCircle.x - closestX;
						float distanceY = thisCircle.y - closestY;

						float distance = distance(closestX, closestY, thisCircle.x, thisCircle.y);
						float overlap = (distance - thisCircle.radius) / precision;

						if ((distanceX * distanceX) + (distanceY * distanceY) <= (thisCircle.radius * thisCircle.radius)) {
							thiz.getPos().add(overlap * (closestX - thisCircle.x) / distance, overlap * (closestY - thisCircle.y) / distance);

							collisions.count++;
						}
					}

					return collisions;
				}
			}));
		}

		// RECTANGLE
		if (thiz.getShape() instanceof Rectangle && thiz.getShapeSearch() instanceof Rectangle) {
			Rectangle thisRectangle = (Rectangle) thiz.getShape();
			Rectangle thisRectangleSearch = (Rectangle) thiz.getShapeSearch();

			thisRectangle.x = thiz.getX() - thisRectangle.width / 2;
			thisRectangle.y = thiz.getY() - thisRectangle.height / 2;

			thisRectangleSearch.x = thiz.getX() - thisRectangleSearch.width / 2;
			thisRectangleSearch.y = thiz.getY() - thisRectangleSearch.height / 2;

			collisions.add(quadtree.query(thisRectangleSearch, new ListenerQuad() {
				@Override
				public Collisions contains(QTNode that) {
					Collisions collisions = new Collisions();

					// RECTANGLE vs CIRCLE
					if (that.getShape() instanceof Circle) {
						Circle thatCircle = (Circle) that.getShape();

						thatCircle.x = that.getX();
						thatCircle.y = that.getY();

						float closestX = clamp(thatCircle.x, thisRectangle.x, thisRectangle.x + thisRectangle.width);
						float closestY = clamp(thatCircle.y, thisRectangle.y, thisRectangle.y + thisRectangle.height);

						float distanceX = thatCircle.x - closestX;
						float distanceY = thatCircle.y - closestY;

						float distance = distance(closestX, closestY, thatCircle.x, thatCircle.y);
						float overlap = (distance - thatCircle.radius) / precision;

						if ((distanceX * distanceX) + (distanceY * distanceY) <= (thatCircle.radius * thatCircle.radius)) {
							that.getPos().add(overlap * (closestX - thatCircle.x) / distance, overlap * (closestY - thatCircle.y) / distance);

							collisions.count++;
						}
					}

					return collisions;
				}
			}));
		}

		return collisions;
	}

	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		if (blocking) {
			pushMaxCurrent = 20;
		} else {
			pushMaxCurrent = PUSH_MAX;
		}

		this.blocking = blocking;
	}

	public void render(SpriteBatch batch) {
		for (int l = 0; l < renderers.length; l++) {
			renderers[l].render(batch, this);
		}
	}

	public void debug(ShapeRenderer shapeRenderer, boolean shape, boolean search) {
		if (getShape() instanceof Circle && shape) {
			Circle circle = (Circle) getShape();
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.circle(circle.x, circle.y, circle.radius, 6);
		}

		if (getShape() instanceof Circle && search) {
			Circle circle = (Circle) getShapeSearch();
			shapeRenderer.setColor(Color.LIME);
			shapeRenderer.circle(circle.x, circle.y, circle.radius, 6);
		}

		if (getShape() instanceof Rectangle && shape) {
			Rectangle rectangle = (Rectangle) getShape();
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		}

		if (getShapeSearch() instanceof Rectangle && search) {
			Rectangle rectangle = (Rectangle) getShapeSearch();
			shapeRenderer.setColor(Color.LIME);
			shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		}
	}

	public void addListener(ListenerUpdate listenerUpdate) {
		ListenerUpdate[] result = new ListenerUpdate[listeners.length + 1];
		System.arraycopy(listeners, 0, result, 0, listeners.length);
		result[result.length - 1] = listenerUpdate;
		listeners = result;
	}

	public void addRenderer(ListenerRender listenerRender) {
		ListenerRender[] result = new ListenerRender[renderers.length + 1];
		System.arraycopy(renderers, 0, result, 0, renderers.length);
		result[result.length - 1] = listenerRender;
		renderers = result;
	}

	public static float clamp(float value, float min, float max) {
		float x = value;

		if (x < min) {
			x = min;
		} else if (x > max) {
			x = max;
		}

		return x;
	}

	public float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}

	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	public Shape2D getShape() {
		return shape;
	}

	public void setShape(Shape2D shape) {
		this.shape = shape;
	}

	public Shape2D getShapeSearch() {
		return shapeSearch;
	}

	public void setShapeSearch(Shape2D shapeSearch) {
		this.shapeSearch = shapeSearch;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
}
