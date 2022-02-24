package micycle.spliner;

import micycle.spliner.geom.CubicCurve;
import processing.core.PVector;

public class NearestPoint {

	/** Maximum depth for recursion */
	private static final int MAXDEPTH = 64;
	/** Flatness control value */
	private static final double EPSILON = 1.0 * Math.pow(2, -MAXDEPTH - 1);
	private static final int DEGREE = 3; // Cubic Bezier curve
	/** Degree of eqn to find roots of */
	private static final int W_DEGREE = 5;

	private static final float[][] cubicZ = {
			/* Precomputed "z" for cubics */
			{ 1.0f, 0.6f, 0.3f, 0.1f }, { 0.4f, 0.6f, 0.6f, 0.4f }, { 0.1f, 0.3f, 0.6f, 1.0f }, };

	private static PVector bezier(PVector[] c, int degree, float t, PVector[] left, PVector[] right) {
		// FIXME WIRED-252, move outside the method and make static
		PVector[][] p = new PVector[W_DEGREE + 1][W_DEGREE + 1];

		/* Copy control points */
		for (int j = 0; j <= degree; j++) {
			p[0][j] = new PVector(c[j].x, c[j].y);
		}

		/* Triangle computation */
		for (int i = 1; i <= degree; i++) {
			for (int j = 0; j <= degree - i; j++) {
				p[i][j] = new PVector((1.0f - t) * p[i - 1][j].x + t * p[i - 1][j + 1].x,
						(1.0f - t) * p[i - 1][j].y + t * p[i - 1][j + 1].y);
			}
		}

		if (left != null) {
			for (int j = 0; j <= degree; j++) {
				left[j] = p[j][0];
			}
		}

		if (right != null) {
			for (int j = 0; j <= degree; j++) {
				right[j] = p[degree - j][j];
			}
		}

		return p[degree][0];
	}

	/*
	 * ComputeXIntercept : Compute intersection of chord from first control point to
	 * last with 0-axis.
	 *
	 */
	private static double computeXIntercept(PVector[] v, int degree) {

		double XNM = v[degree].x - v[0].x;
		double YNM = v[degree].y - v[0].y;
		double XMK = v[0].x;
		double YMK = v[0].y;

		double detInv = -1.0 / YNM;

		return (XNM * YMK - YNM * XMK) * detInv;
	}

	/*
	 * ControlPolygonFlatEnough : Check if the control polygon of a Bezier curve is
	 * flat enough for recursive subdivision to bottom out.
	 *
	 */
	private static boolean controlPolygonFlatEnough(PVector[] v, int degree) {

		// Find the perpendicular distance
		// from each interior control point to
		// line connecting v[0] and v[degree]

		// Derive the implicit equation for line connecting first
		// and last control points
		double a = v[0].y - v[degree].y;
		double b = v[degree].x - v[0].x;
		double c = v[0].x * v[degree].y - v[degree].x * v[0].y;

		double abSquared = (a * a) + (b * b);
		double[] distance = new double[degree + 1]; // Distances from pts to line

		for (int i = 1; i < degree; i++) {
			// Compute distance from each of the points to that line
			distance[i] = a * v[i].x + b * v[i].y + c;
			if (distance[i] > 0.0) {
				distance[i] = (distance[i] * distance[i]) / abSquared;
			}
			if (distance[i] < 0.0) {
				distance[i] = -((distance[i] * distance[i]) / abSquared);
			}
		}

		// Find the largest distance
		double maxDistanceAbove = 0.0;
		double maxDistanceBelow = 0.0;
		for (int i = 1; i < degree; i++) {
			if (distance[i] < 0.0) {
				maxDistanceBelow = Math.min(maxDistanceBelow, distance[i]);
			}
			if (distance[i] > 0.0) {
				maxDistanceAbove = Math.max(maxDistanceAbove, distance[i]);
			}
		}

		// Implicit equation for zero line
		double a1 = 0.0;
		double b1 = 1.0;
		double c1 = 0.0;

		// Implicit equation for "above" line
		double a2 = a;
		double b2 = b;
		double c2 = c + maxDistanceAbove;

		double det = a1 * b2 - a2 * b1;
		double dInv = 1.0 / det;

		double intercept1 = (b1 * c2 - b2 * c1) * dInv;

		// Implicit equation for "below" line
		a2 = a;
		b2 = b;
		c2 = c + maxDistanceBelow;

		det = a1 * b2 - a2 * b1;
		dInv = 1.0 / det;

		double intercept2 = (b1 * c2 - b2 * c1) * dInv;

		// Compute intercepts of bounding box
		double leftIntercept = Math.min(intercept1, intercept2);
		double rightIntercept = Math.max(intercept1, intercept2);

		double error = 0.5 * (rightIntercept - leftIntercept);

		return error < EPSILON;
	}

	/***
	 * ConvertToBezierForm : Given a point and a Bezier curve, generate a 5th-degree
	 * Bezier-format equation whose solution finds the point on the curve nearest
	 * the user-defined point.
	 */
	private static PVector[] convertToBezierForm(PVector[] v, PVector pa) {

		PVector[] c = new PVector[DEGREE + 1]; // v(i) - pa
		PVector[] d = new PVector[DEGREE]; // v(i+1) - v(i)
		float[][] cdTable = new float[3][4]; // Dot product of c, d
		PVector[] w = new PVector[W_DEGREE + 1]; // Ctl pts of 5th-degree curve

		// Determine the c's -- these are vectors created by subtracting
		// point pa from each of the control points
		for (int i = 0; i <= DEGREE; i++) {
			c[i] = new PVector(v[i].x - pa.x, v[i].y - pa.y);
		}

		// Determine the d's -- these are vectors created by subtracting
		// each control point from the next
		float s = 3;
		for (int i = 0; i <= DEGREE - 1; i++) {
			d[i] = new PVector(s * (v[i + 1].x - v[i].x), s * (v[i + 1].y - v[i].y));
		}

		// Create the c,d table -- this is a table of dot products of the
		// c's and d's */
		for (int row = 0; row <= DEGREE - 1; row++) {
			for (int column = 0; column <= DEGREE; column++) {
				cdTable[row][column] = (d[row].x * c[column].x) + (d[row].y * c[column].y);
			}
		}

		// Now, apply the z's to the dot products, on the skew diagonal
		// Also, set up the x-values, making these "points"
		for (int i = 0; i <= W_DEGREE; i++) {
			w[i] = new PVector((float) (i) / W_DEGREE, 0);
		}

		int n = DEGREE;
		int m = DEGREE - 1;
		for (int k = 0; k <= n + m; k++) {
			int lb = Math.max(0, k - m);
			int ub = Math.min(k, n);
			for (int i = lb; i <= ub; i++) {
				int j = k - i;
				w[i + j].set(w[i + j].x, w[i + j].y + cdTable[j][i] * cubicZ[j][i]);
			}
		}

		return w;
	}

	/***
	 * CrossingCount : Count the number of times a Bezier control polygon crosses
	 * the 0-axis. This number is >= the number of roots.
	 *
	 */
	private static int crossingCount(PVector[] v, int degree) {
		int nCrossings = 0;
		int sign = v[0].y < 0 ? -1 : 1;
		int oldSign = sign;
		for (int i = 1; i <= degree; i++) {
			sign = v[i].y < 0 ? -1 : 1;
			if (sign != oldSign) {
				nCrossings++;
			}
			oldSign = sign;
		}
		return nCrossings;
	}

	/***
	 * FindRoots : Given a 5th-degree equation in Bernstein-Bezier form, find all of
	 * the roots in the interval [0, 1]. Return the number of roots found.
	 */
	private static int findRoots(PVector[] w, int degree, float[] t, int depth) {

		switch (crossingCount(w, degree)) {
			case 0 : { // No solutions here
				return 0;
			}
			case 1 : { // Unique solution
				// Stop recursion when the tree is deep enough
				// if deep enough, return 1 solution at midpoint
				if (depth >= MAXDEPTH) {
					t[0] = (w[0].x + w[W_DEGREE].x) / 2.0f;
					return 1;
				}
				if (controlPolygonFlatEnough(w, degree)) {
					t[0] = (float) computeXIntercept(w, degree);
					return 1;
				}
				break;
			}
		}

		// Otherwise, solve recursively after
		// subdividing control polygon
		PVector[] left = new PVector[W_DEGREE + 1]; // New left and right
		PVector[] right = new PVector[W_DEGREE + 1]; // control polygons
		float[] leftT = new float[W_DEGREE + 1]; // Solutions from kids
		float[] rightT = new float[W_DEGREE + 1];

		bezier(w, degree, 0.5f, left, right);
		int leftCount = findRoots(left, degree, leftT, depth + 1);
		int rightCount = findRoots(right, degree, rightT, depth + 1);

		// Gather solutions together
		for (int i = 0; i < leftCount; i++) {
			t[i] = leftT[i];
		}
		for (int i = 0; i < rightCount; i++) {
			t[i + leftCount] = rightT[i];
		}

		// Send back total number of solutions */
		return leftCount + rightCount;
	}

	/***
	 * Return the nearest point (pn) on cubic bezier curve c nearest to point pa.
	 *
	 * @param c  cubice curve
	 * @param pa arbitrary point
	 * @param pn nearest point found (return param)
	 * @return distance squared between pa and nearest point (pn)
	 */
	public static double onCurve(CubicCurve c, PVector pa, PVector pn) {

		float[] tCandidate = new float[W_DEGREE]; // Possible roots
		PVector[] v = { c.getP1(), c.getCtrlP1(), c.getCtrlP2(), c.getP2() };

		// Convert problem to 5th-degree Bezier form
		PVector[] w = convertToBezierForm(v, pa);

		// Find all possible roots of 5th-degree equation
		int nSolutions = findRoots(w, W_DEGREE, tCandidate, 0);

		// Compare distances of P5 to all candidates, and to t=0, and t=1
		// Check distance to beginning of curve, where t = 0
		double minDistance = pa.dist(c.getP1());
		float t = 0.0f;

		// Find distances for candidate points
		for (int i = 0; i < nSolutions; i++) {
			PVector p = bezier(v, DEGREE, tCandidate[i], null, null);
			double distance = pa.dist(p);
			if (distance < minDistance) {
				minDistance = distance;
				t = tCandidate[i];
			}
		}

		// Finally, look at distance to end point, where t = 1.0
		double distance = pa.dist(c.getP2());
		if (distance < minDistance) {
			t = 1.0f;
		}

		// Return the point on the curve at parameter value t
		pn.set(bezier(v, DEGREE, t, null, null));

		return pn.dist(pa);
	}

	/***
	 * Returns the nearest point (pn) on line p1 - p2 nearest to point pa.
	 *
	 * @param p1 start point of line
	 * @param p2 end point of line
	 * @param pa arbitrary point
	 * @param pn nearest point (return param)
	 * @return distance squared between pa and nearest point (pn)
	 */
	public static double onLine(PVector p1, PVector p2, PVector pa, PVector pn) {
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;
		float dsq = dx * dx + dy * dy;
		if (dsq == 0) {
			pn.set(p1);
		} else {
			float u = ((pa.x - p1.x) * dx + (pa.y - p1.y) * dy) / dsq;
			if (u <= 0) {
				pn.set(p1);
			} else if (u >= 1) {
				pn.set(p2);
			} else {
				pn.set(p1.x + u * dx, p1.y + u * dy);
			}
		}
		return pn.dist(pa);
	}

	private NearestPoint() {
	}
}
