package com.abhorrentdestruction.core.quadtree.entities;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class AnimationFrames {
	private String name;
	private String animation;
	private int angles;
	private float speed;
	private boolean scaleWithSpeed = false;
	private PlayMode mode;

	public AnimationFrames(String name, String animation, int angles, float speed, boolean scaleWithSpeed, PlayMode mode) {
		this.setName(name);
		this.setAnimation(animation);
		this.setAngles(angles);
		this.setSpeed(speed);
		this.setScaleWithSpeed(scaleWithSpeed);
		this.setMode(mode);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnimation() {
		return animation;
	}

	public void setAnimation(String animation) {
		this.animation = animation;
	}

	public PlayMode getMode() {
		return mode;
	}

	public void setMode(PlayMode mode) {
		this.mode = mode;
	}

	public int getAngles() {
		return angles;
	}

	public void setAngles(int angles) {
		this.angles = angles;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public boolean getScaleWithSpeed() {
		return scaleWithSpeed;
	}

	public void setScaleWithSpeed(boolean scaleWithSpeed) {
		this.scaleWithSpeed = scaleWithSpeed;
	}
}
