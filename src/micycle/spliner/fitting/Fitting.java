/**************************************************************************************************
 **************************************************************************************************

     BSD 3-Clause License (https://www.tldrlegal.com/l/bsd3)

     Copyright (c) 2012 Andrés Solís Montero <http://www.solism.ca>, All rights reserved.


     Redistribution and use in source and binary forms, with or without modification,
     are permitted provided that the following conditions are met:

     1. Redistributions of source code must retain the above copyright notice,
        this list of conditions and the following disclaimer.
     2. Redistributions in binary form must reproduce the above copyright notice,
        this list of conditions and the following disclaimer in the documentation
        and/or other materials provided with the distribution.
     3. Neither the name of the copyright holder nor the names of its contributors
        may be used to endorse or promote products derived from this software
        without specific prior written permission.

     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
     AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
     IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
     ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
     LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
     DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
     LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
     THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
     OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
     OF THE POSSIBILITY OF SUCH DAMAGE.

 **************************************************************************************************
 **************************************************************************************************/
package micycle.spliner.fitting;

import java.util.ArrayList;
import java.util.List;

import micycle.spliner.NearestPoint;
import micycle.spliner.geom.CubicCurve;
import micycle.spliner.geom.Segment;
import micycle.spliner.geom.Line;
import micycle.spliner.interpolation.Interpolation;
import processing.core.PVector;

public abstract class Fitting {

	double THRESHOLD = 25;
	double PIXEL_STEPS = 5;
	protected Interpolation curve;

	protected List<Integer> idxs = new ArrayList<>();
	protected List<Integer> knots = new ArrayList<>();
	protected List<PVector> points;

	private boolean check() {
		for (int j = 0; j < knots.size() - 1; j++) {
			if (maxIndex(points, knots.get(j), knots.get(j + 1), curve.getCurveAt(j)) != -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Computes a collection of lines that fit the list of points.
	 * 
	 * @param pts the list points to fit a curve to
	 * @return a list of lines; PolygonFitting returns
	 *         {@link micycle.spliner.geom.Segment Segments}; all other fittings
	 *         return {@link micycle.spliner.geom.CubicCurve CubicCurves}.
	 */
	public abstract List<Line> fitCurve(List<PVector> pts);

	public abstract String getLabel();

	protected int maxIndex(List<PVector> p, int init, int end, CubicCurve curve) {
		int index = -1;
		if ((init > end) || (end - init <= PIXEL_STEPS)) {
			return index;
		}
		double max = 0;
		for (int i = init + 1; i < end; i++) {
			PVector pn = new PVector();
			double sqDis = NearestPoint.onCurve(curve, p.get(i), pn);
			if (sqDis > max) {
				max = sqDis;
				index = i;
			}
		}
		return (max > THRESHOLD) ? index : -1;
	}

	protected int maxIndex(List<PVector> p, int init, int end, Segment segment) {
		int index = -1;
		if ((init > end) || (end - init <= PIXEL_STEPS)) {
			return index;
		}
		double max = 0;
		for (int i = init + 1; i < end; i++) {
			PVector pn = new PVector();
			double sqDis = NearestPoint.onLine(segment.getP1(), segment.getP2(), p.get(i), pn);
			if (sqDis > max) {
				max = sqDis;
				index = i;
			}
		}
		return (max > THRESHOLD) ? index : -1;
	}

	protected void removeUnnecessaryPoints() {
		int index = 0;
		for (int j = 1; j < knots.size() - 1; j++) {
			index = knots.get(j);
			curve.RemoveIndex(knots.get(j));
			if (check()) {
				j--;
			} else {
				curve.AddIndex(index);
			}
		}
	}
}
