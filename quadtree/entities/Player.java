package com.abhorrentdestruction.core.quadtree.entities;

import java.util.HashMap;

import com.abhorrentdestruction.core.assets.GUI;
import com.abhorrentdestruction.core.cameras.CameraHelpers;
import com.abhorrentdestruction.core.cameras.CameraMap;
import com.abhorrentdestruction.core.cameras.CameraScreen;
import com.abhorrentdestruction.core.quadtree.QTNode;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

class Data {
	private Animation<TextureRegion> animation;
	private boolean scaleWithSpeed = false;

	public boolean isScaleWithSpeed() {
		return scaleWithSpeed;
	}

	public void setScaleWithSpeed(boolean scaleWithSpeed) {
		this.scaleWithSpeed = scaleWithSpeed;
	}

	public Animation<TextureRegion> getAnimation() {
		return animation;
	}

	public void setAnimation(Animation<TextureRegion> animation) {
		this.animation = animation;
	}
}

public class Player extends QTNode {
	private CameraScreen cameraScreen;
	private CameraMap cameraMap;

	private HashMap<String, HashMap<Float, Data>> animations = new HashMap<String, HashMap<Float, Data>>();

	private float stateTime;

	private Vector2 destination = getPos();

	private float deadZone = 15F;
	private float deadZoneMul = 10F;

	private AnimationFrames[] animationFrames;

	public Player(float x, float y, int radius, AnimationFrames[] animationFrames) {
		super(x, y, radius);

		for (int animation = 0; animation < animationFrames.length; animation++) {
			animations.putIfAbsent(animationFrames[animation].getAnimation(), new HashMap<Float, Data>());

			float step = 360F / animationFrames[animation].getAngles();

			for (float angle = 0F; angle <= 360F; angle += step) {
				Data data = new Data();

				data.setScaleWithSpeed(animationFrames[animation].getScaleWithSpeed());
				data.setAnimation(new Animation<TextureRegion>(animationFrames[animation].getSpeed(), GUI.get("graphics").getRegions(String.format("%s/%s/%s/", animationFrames[animation].getName(), animationFrames[animation].getAnimation(), angle == 360 ? 0 : angle)), animationFrames[animation].getMode()));

				animations.get(animationFrames[animation].getAnimation()).putIfAbsent(angle, data);
			}
		}

		this.animationFrames = animationFrames;
	}

	public void setSpeed(float speed) {
		super.setSpeed(speed);
	}

	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);

		OrthographicCamera camera = cameraMap.getCamera();
		camera.position.set(getX(), 0, getY());
		camera.update();

		Vector3 intersection = CameraHelpers.getIntersection(camera, Gdx.input.getX(), Gdx.input.getY());

		Vector2 velocity = new Vector2(destination).sub(getPos()).nor();

		TextureRegion currentFrame = null;

		if (Gdx.input.isButtonPressed(0)) {
			destination = new Vector2(intersection.x, intersection.z);

			if (getPos().dst(destination) <= getPos().dst(new Vector2(getPos()).add(new Vector2(velocity).scl(getSpeed()))) || getPos().dst(destination) < deadZone) {
				setVelocity(new Vector2(0, 0));

				currentFrame = getFrame("idle");
			} else {
				setVelocity(velocity.scl(getSpeed()));
				setRotationAngle(new Vector2(getPos()).add(getVelocity()).sub(getPos()).angle());

				currentFrame = getFrame("walking");
			}
		} else {
			if (getPos().dst(destination) <= getPos().dst(new Vector2(getPos()).add(new Vector2(velocity).scl(getSpeed()))) || getPos().dst(destination) < deadZone) {
				setVelocity(new Vector2(0, 0));

				currentFrame = getFrame("idle");
			} else {
				setVelocity(velocity.scl(getSpeed()));
				setRotationAngle(new Vector2(getPos()).add(getVelocity()).sub(getPos()).angle());

				currentFrame = getFrame("walking");
			}
		}

		stateTime += Gdx.graphics.getDeltaTime();

		Vector3 pos = CameraHelpers.getIntersection(camera, getX(), getY());

		batch.draw(currentFrame, pos.x - currentFrame.getRegionWidth() / 2, pos.y - currentFrame.getRegionHeight() / 16);
	}

	private TextureRegion getFrame(String name) {
		Data data = animations.get(name).get(Math.round(getRotationAngle() / (360.0F / (animations.get(name).size() - 1))) * (360.0F / (animations.get(name).size() - 1)));

		float speed = 1;

		if (data.isScaleWithSpeed()) {
			speed = getSpeed();
		}

		return data.getAnimation().getKeyFrame(speed * stateTime, true);
	}

	public void setCameraScreen(CameraScreen cameraScreen) {
		this.cameraScreen = cameraScreen;
	}

	public void setCameraMap(CameraMap cameraMap) {
		this.cameraMap = cameraMap;
	}
	
	public void setDestination(Vector2 destination) {
		this.destination = destination;
	}
}
