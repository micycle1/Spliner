package micycle.spliner.geom;

import processing.core.PVector;

public class CubicCurve implements Line {

	/**
	 * The X coordinate of the start point of the cubic curve segment.
	 */
	public float x1;

	/**
	 * The Y coordinate of the start point of the cubic curve segment.
	 */
	public float y1;

	/**
	 * The X coordinate of the first control point of the cubic curve segment.
	 */
	public float ctrlx1;

	/**
	 * The Y coordinate of the first control point of the cubic curve segment.
	 */
	public float ctrly1;

	/**
	 * The X coordinate of the second control point of the cubic curve segment.
	 */
	public float ctrlx2;

	/**
	 * The Y coordinate of the second control point of the cubic curve segment.
	 */
	public float ctrly2;

	/**
	 * The X coordinate of the end point of the cubic curve segment.
	 */
	public float x2;

	/**
	 * The Y coordinate of the end point of the cubic curve segment.
	 */
	public float y2;

	public CubicCurve(float x1, float y1, float ctrlx1, float ctrly1, float ctrlx2, float ctrly2, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.ctrlx1 = ctrlx1;
		this.ctrly1 = ctrly1;
		this.ctrlx2 = ctrlx2;
		this.ctrly2 = ctrly2;
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

	public float getCtrlX1() {
		return ctrlx1;
	}

	public float getCtrlY1() {
		return ctrly1;
	}

	public PVector getCtrlP1() {
		return new PVector(ctrlx1, ctrly1);
	}

	public float getCtrlX2() {
		return ctrlx2;
	}

	public float getCtrlY2() {
		return ctrly2;
	}

	public PVector getCtrlP2() {
		return new PVector(ctrlx2, ctrly2);
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
