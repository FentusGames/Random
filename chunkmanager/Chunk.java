package com.abhorrentdestruction.core.chunkmanager;

import java.util.concurrent.CompletableFuture;

import com.abhorrentdestruction.core.libgdxextend.Vector2i;
import com.abhorrentdestruction.core.logic.Sizes;
import com.abhorrentdestruction.core.maze.Maze;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Chunk {
	private int x;
	private int y;
	private int width;
	private int height;

	private boolean loaded = false;

	private Tile[][] tiles;
	private Maze maze;

	public Chunk(Vector2i chunk) {
		this.setX(chunk.x);
		this.setY(chunk.y);
	}

	public Chunk(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		tiles = new Tile[(int) (width / Sizes.TILE_SIZE)][(int) (height / Sizes.TILE_SIZE)];
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Chunk) {
			Chunk c = (Chunk) obj;

			if (c.getX() == x && c.getY() == y) {
				return true;
			}
		}

		return false;
	}

	public long seed() {
		return (long) (x / Sizes.CHUNK_SIZE * Sizes.TILE_SIZE + y / Sizes.CHUNK_SIZE); // The seed is the x and y converted to an index long.
	}

	public void load() {
		CompletableFuture.runAsync(() -> {
			// Random random = new Random(seed());

			Color color = maze.getMaze()[(int) (x / Sizes.CHUNK_SIZE)][(int) (y / Sizes.CHUNK_SIZE)].color;

			for (int x = 0; x < tiles.length; x++) {
				for (int y = 0; y < tiles[x].length; y++) {
					Tile tile = new Tile(this.x + x * Sizes.TILE_SIZE, this.y + y * Sizes.TILE_SIZE, Sizes.TILE_SIZE, Sizes.TILE_SIZE);

					tile.setColor(color);

					tiles[x][y] = tile;
				}
			}
		}).whenCompleteAsync((task, throwable) -> {
			loaded = true;
		});
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public float getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void debug(ShapeRenderer shapeRenderer) {
		if (loaded) {
			for (int x = 0; x < tiles.length; x++) {
				for (int y = 0; y < tiles[x].length; y++) {
					if (tiles[x][y] != null) {
						shapeRenderer.setColor(tiles[x][y].getColor());
						tiles[x][y].debug(shapeRenderer);
					}
				}
			}
		}
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setMaze(Maze maze) {
		this.maze = maze;
	}
}
