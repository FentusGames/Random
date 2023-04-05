package com.abhorrentdestruction.core.chunkmanager;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.abhorrentdestruction.core.libgdxextend.Vector2i;
import com.abhorrentdestruction.core.logic.Sizes;
import com.abhorrentdestruction.core.maze.Maze;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class ChunkManager {
	private ConcurrentLinkedQueue<Chunk> chunks = new ConcurrentLinkedQueue<Chunk>();

	private Vector2i currentChunk;
	private Vector2i chunk;

	private Maze maze;

	public void update(Vector2 pos) {
		chunk = new Vector2i(Sizes.CHUNK_SIZE * (Math.round((pos.x - Sizes.CHUNK_SIZE / 2) / Sizes.CHUNK_SIZE)) / Sizes.CHUNK_SIZE, Sizes.CHUNK_SIZE * (Math.round((pos.y - Sizes.CHUNK_SIZE / 2) / Sizes.CHUNK_SIZE)) / Sizes.CHUNK_SIZE);

		if (currentChunk == null || currentChunk.x != chunk.x || currentChunk.y != chunk.y) {
			currentChunk = chunk;

			ConcurrentLinkedQueue<Chunk> chunksToLoad = new ConcurrentLinkedQueue<Chunk>();

			for (int x = chunk.x - Sizes.CHUNK_FACTOR; x < chunk.x + Sizes.CHUNK_FACTOR + 1; x += 1) {
				for (int y = chunk.y - Sizes.CHUNK_FACTOR; y < chunk.y + Sizes.CHUNK_FACTOR + 1; y += 1) {
					Chunk chunk = new Chunk(x * Sizes.CHUNK_SIZE, y * Sizes.CHUNK_SIZE, Sizes.CHUNK_SIZE, Sizes.CHUNK_SIZE);

					chunk.setMaze(maze);

					chunksToLoad.add(chunk);
				}
			}

			chunksToLoad.forEach(chunk -> {
				if (!chunks.contains(chunk)) {
					chunks.add(chunk);
					chunk.load();
				}
			});

			chunks.forEach(chunk -> {
				if (!chunksToLoad.contains(chunk)) {
					chunks.remove(chunk);
				}
			});
		}
	}

	public void debug(ShapeRenderer shapeRenderer, boolean loaded) {
		chunks.forEach(chunk -> {
			if (loaded) {
				if (chunk.isLoaded()) {
					shapeRenderer.setColor(Color.GREEN);
				} else {
					shapeRenderer.setColor(Color.BLUE);
				}

				shapeRenderer.set(ShapeType.Line);
				shapeRenderer.rect(chunk.getX() + 2, chunk.getY() + 2, chunk.getWidth() - 4, chunk.getHeight() - 4);
			}

			shapeRenderer.setColor(Color.DARK_GRAY);
			chunk.debug(shapeRenderer);
		});
	}

	public void setMaze(Maze maze) {
		this.maze = maze;

		chunks.forEach(chunk -> {
			chunk.setMaze(maze);
			chunk.load();
		});
	}
}
