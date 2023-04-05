package com.abhorrentdestruction.core.chunkmanager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Tile {
	private int x;
	private int y;
	private int width;
	private int height;
	private Color color;

	public Tile(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void debug(ShapeRenderer shapeRenderer) {
		shapeRenderer.rect(x + 4, y + 4, width - 8, height - 8);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
}
