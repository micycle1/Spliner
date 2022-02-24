package micycle.spliner.geom;

import processing.core.PVector;

public class Segment implements Line {

	/**
	 * The X coordinate of the start point of the line segment.
	 */
	public float x1;

	/**
	 * The Y coordinate of the start point of the line segment.
	 */
	public float y1;

	/**
	 * The X coordinate of the end point of the line segment.
	 */
	public float x2;

	/**
	 * The Y coordinate of the end point of the line segment.
	 */
	public float y2;

	public Segment(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public float getX1() {
		return x1;
	}

	public float getY1() {
		return y1;
	}

	public PVector getP1() {
		return new PVector(x1, y1);
	}

	public float getX2() {
		return x2;
	}

	public float getY2() {
		return y2;
	}

	public PVector getP2() {
		return new PVector(x2, y2);
	}

}
