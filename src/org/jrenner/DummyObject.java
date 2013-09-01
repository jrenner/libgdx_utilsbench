package org.jrenner;

import com.badlogic.gdx.math.Vector2;

public class DummyObject {
	private static long nextLong = 0;
	// non-statics
	private Vector2 pos;
	private String name;
	public Long longCode;

	public DummyObject(float x, float y, String name) {
		this.name = name;
		this.pos = new Vector2(x, y);
		this.longCode = nextLong++;
	}

	public String toString() {
		return name + " " + pos;
	}
}
