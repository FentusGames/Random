package com.abhorrentdestruction.core.quadtree;

import com.badlogic.gdx.math.Vector2;

public class Entity {
	private Vector2 velocity = new Vector2(0, 0);
	private Vector2 pos = new Vector2(0, 0);

	private float spin = 0F;

	private float rotationAngle = 0;
	private float movingAngle = 0;

	public Entity(float x, float y) {
		pos.x = x;
		pos.y = y;
	}

	public float getVX() {
		return velocity.x;
	}

	public float getVY() {
		return velocity.y;
	}

	public float getX() {
		return pos.x;
	}

	public float getY() {
		return pos.y;
	}

	public float setX(float x) {
		return pos.x = x;
	}

	public float setY(float y) {
		return pos.y = y;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public float getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(float rotationAngle) {
		this.rotationAngle = rotationAngle;
	}

	public float getMovingAngle() {
		return movingAngle;
	}

	public void setMovingAngle(float movingAngle) {
		this.movingAngle = movingAngle;
	}

	public void incrementPos(Vector2 incrementAmount, float precision) {
		pos.x += incrementAmount.x / precision;
		pos.y += incrementAmount.y / precision;
	}

	public void decrementPos(Vector2 incrementAmount, float precision) {
		pos.x -= incrementAmount.x / precision;
		pos.y -= incrementAmount.y / precision;
	}

	public void incrementXPos(float incrementAmount) {
		pos.x += incrementAmount;
	}

	public void incrementYPos(float incrementAmount) {
		pos.y += incrementAmount;
	}

	public void incrementVelocity(Vector2 incrementAmount) {
		velocity.x += incrementAmount.x;
		velocity.y += incrementAmount.y;
	}

	public void incrementXVelocity(float incrementAmount) {
		velocity.x += incrementAmount;
	}

	public void incrementYVelocity(float incrementAmount) {
		velocity.y += incrementAmount;
	}

	public void incrementMovingAngle(float incrementAmount) {
		movingAngle += incrementAmount;
	}

	public void incrementRotationAngle(float incrementAmount) {
		rotationAngle += incrementAmount;
	}

	public Vector2 incrementMovingAngleVector(float incrementAmount) {
		return new Vector2(getMovingAngleX() * incrementAmount, getMovingAngleY() * incrementAmount);
	}

	public float incrementMovingAngleX(float incrementAmount) {
		return getMovingAngleX() * incrementAmount;
	}

	public float incrementMovingAngleY(float incrementAmount) {
		return getMovingAngleY() * incrementAmount;
	}

	public Vector2 getMovingAngleVector() {
		return new Vector2(getMovingAngleX(), getMovingAngleY());
	}

	public float getMovingAngleX() {
		return (float) (Math.cos(getMovingAngle() * Math.PI / 180));
	}

	public float getMovingAngleY() {
		return (float) (Math.sin(getMovingAngle() * Math.PI / 180));
	}

	public void increaseRotationAngle(float incrementAmount) {
		if (getRotationAngle() > 359) {
			rotationAngle = 0;
		} else {
			rotationAngle += incrementAmount;
		}
	}

	public void decreaseRotationAngle(float incrementAmount) {
		if (getRotationAngle() < 0) {
			rotationAngle = 359;
		} else {
			rotationAngle -= incrementAmount;
		}
	}

	public Vector2 getPos() {
		return pos;
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;
	}

	public void incrementAngle(float incrementAmount, float precision) {
		rotationAngle += incrementAmount / precision;
	}

	public float getSpin() {
		return spin;
	}

	public void setSpin(float spin) {
		this.spin = spin;
	}
}