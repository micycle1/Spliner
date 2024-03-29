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
package micycle.spliner.interpolation;

import java.util.ArrayList;
import java.util.List;

import micycle.spliner.CurveCreationException;
import micycle.spliner.geom.CubicCurve;
import micycle.spliner.geom.Line;
import processing.core.PVector;

public class LeastSquareBezier extends Interpolation {

	private static final boolean DYNAMIC = true;
	double[] a1;
	double[] a2;
	double[] a12;

	public LeastSquareBezier(List<PVector> pts_, List<Integer> index) throws CurveCreationException {
		setData(pts_, index);
	}

	private double A1(int init, int end) {
		double ti = 0;
		double A1 = 0;
		if (DYNAMIC) {
			if (a1[end - init] > 0) {
				return a1[end - init] * 9.0;
			}
		}
		for (int i = 1; i <= (end - init); i++) {
			ti = (double) i / (double) (end - init);
			A1 += Math.pow(ti, 2) * Math.pow(1 - ti, 4);
		}
		if (DYNAMIC) {
			a1[end - init] = A1;
		}
		return 9 * A1;
	}

	private double A12(int init, int end) {
		double ti = 0;
		double A1 = 0;
		if (DYNAMIC) {
			if (a12[end - init] > 0) {
				return a12[end - init] * 9.0;
			}
		}
		for (int i = 1; i <= (end - init); i++) {
			ti = (double) i / (double) (end - init);
			A1 += Math.pow(ti, 3) * Math.pow(1 - ti, 3);
		}
		if (DYNAMIC) {
			a12[end - init] = A1;
		}
		return 9 * A1;
	}

	private double A2(int init, int end) {
		double ti = 0;
		double A1 = 0;
		if (DYNAMIC) {
			if (a2[end - init] > 0) {
				return a2[end - init] * 9.0;
			}
		}
		for (int i = 1; i <= (end - init); i++) {
			ti = (double) i / (double) (end - init);
			A1 += Math.pow(ti, 4) * Math.pow(1 - ti, 2);
		}
		if (DYNAMIC) {
			a2[end - init] = A1;
		}
		return 9 * A1;
	}

	@Override
	public int AddIndex(int i) {
		if (i < 0) {
			return -1;
		}
		if (this.index == null) {
			this.index = new ArrayList<>();
			this.index.add(0);
			this.index.add(points.size() - 1);
		}
		for (int j = 0; j < index.size(); j++) {
			if (index.get(j) == i) {
				return -1;
			}
			if (index.get(j) < i) {
				continue;
			} else {
				index.add(j, i);
				compute(j - 1);
				compute(j);
				return j;
			}
		}
		return -1;
	}

	public int AddIndex(int i, PVector p1, PVector p2, PVector p3, PVector p4) {
		if (i < 0) {
			return -1;
		}
		if (this.index == null) {
			this.index = new ArrayList<>();
			this.index.add(0);
			this.index.add(points.size() - 1);
		}
		for (int j = 0; j < index.size(); j++) {
			if (index.get(j) == i) {
				return -1;
			}
			if (index.get(j) < i) {
				continue;
			} else {
				index.add(j, i);
				cP[2 * getIndex(j)] = p1;
				cP[2 * getIndex(j) + 1] = p2;

				cP[2 * getIndex(j - 1)] = p3;
				cP[2 * getIndex(j - 1) + 1] = p4;

				return j;
			}
		}
		return -1;
	}

	private PVector C1(int init, int end) {
//		if (DYNAMIC)
//			if (_c1[init][end]!=null) return _c1[init][end];
		PVector P0 = points.get(init);
		PVector P3 = points.get(end);
		float c1x = 0;
		float c1y = 0;
		float ti = 0;

		for (int i = 1; i <= (end - init); i++) {
//			if (DYNAMIC)
//				if (_c1[init][init+i]!=null) {
//					c1x += _c1[init][init+i].x;
//					c1y += _c1[init][init+i].y;
//					continue;
//				}
			ti = (float) i / (float) (end - init);
			c1x += 3 * ti * Math.pow(1 - ti, 2) * (points.get(init + i).x - Math.pow(1 - ti, 3) * P0.x - Math.pow(ti, 3) * P3.x);
			c1y += 3 * ti * Math.pow(1 - ti, 2) * (points.get(init + i).y - Math.pow(1 - ti, 3) * P0.y - Math.pow(ti, 3) * P3.y);
//			if (DYNAMIC){
//				_c1[init][init+i]=new PVector(c1x,c1y);
//			}
		}
		return new PVector(c1x, c1y);
	}

	private PVector C2(int init, int end) {
		PVector P0 = points.get(init);
		PVector P3 = points.get(end);
		float c1x = 0;
		float c1y = 0;
		float ti = 0;
//		if (DYNAMIC)
//			if (_c2[init][end]!=null) return _c2[init][end];
		for (int i = 1; i <= (end - init); i++) {
			ti = (float) i / (float) (end - init);
			c1x += 3 * Math.pow(ti, 2) * (1 - ti) * (points.get(init + i).x - Math.pow(1 - ti, 3) * P0.x - Math.pow(ti, 3) * P3.x);
			c1y += 3 * Math.pow(ti, 2) * (1 - ti) * (points.get(init + i).y - Math.pow(1 - ti, 3) * P0.y - Math.pow(ti, 3) * P3.y);
//			if (DYNAMIC){
//				_c2[init][init+i]=new PVector(c1x,c1y);
//			}
		}
		return new PVector(c1x, c1y);
	}

	@Override
	protected void compute() {
//		if (DYNAMIC) initialize();
		for (int i = 0; i < N() - 1; i++) {
			cP[2 * getIndex(i)] = P1(getIndex(i), getIndex(i + 1));
			cP[2 * getIndex(i) + 1] = P2(getIndex(i), getIndex(i + 1));
		}
	}

	protected void compute(int first) {
		cP[2 * getIndex(first)] = P1(getIndex(first), getIndex(first + 1));
		cP[2 * getIndex(first) + 1] = P2(getIndex(first), getIndex(first + 1));
	}

	@Override
	public CubicCurve getCurveAt(int i) {
		if (i < 0 || i >= N() - 1) {
			throw new IndexOutOfBoundsException(String.format("Interpolation Class: cannot " + "retrieve curve with index : %d", i));
		}
		return new CubicCurve(get(i).x, get(i).y, cP[2 * getIndex(i)].x, cP[2 * getIndex(i)].y,
				cP[2 * getIndex(i) + 1].x, cP[2 * getIndex(i) + 1].y, get(i + 1).x, get(i + 1).y);
	}

	@Override
	public ArrayList<Line> getCurves() {
		ArrayList<Line> s = new ArrayList<>();

		for (int i = 0; i < N() - 1; i++) {
			CubicCurve cubic = new CubicCurve(get(i).x, get(i).y, cP[2 * getIndex(i)].x, cP[2 * getIndex(i)].y,
					cP[2 * getIndex(i) + 1].x, cP[2 * getIndex(i) + 1].y, get(i + 1).x, get(i + 1).y);
			s.add(cubic);
		}

		return s;
	}

	// Least square estimation of first control point
	// P1 = (A2*C1 - A12*C2)/ (A1*A2-A12*A12);
	private PVector P1(int init, int end) {
		if (end - init == 1) {
			return new PVector(points.get(init).x, points.get(init).y);
		}
		double a1 = A1(init, end);
		double a2 = A2(init, end);
		double a12 = A12(init, end);
		double den = (a1 * a2 - Math.pow(a12, 2));
		if (den == 0) {
			return new PVector(points.get(init).x, points.get(init).y);
		}
		PVector c1 = C1(init, end);
		PVector c2 = C2(init, end);
		double p1x = (a2 * c1.x - a12 * c2.x) / den;
		double p1y = (a2 * c1.y - a12 * c2.y) / den;

		return new PVector((float) p1x, (float) p1y);
	}

	// Least square stimation of second control point
	// P2 = (A1*C2 - A12*C1)/ (A1*A2-A12*A12);
	private PVector P2(int init, int end) {
		if (end - init == 1) {
			return new PVector(points.get(end).x, points.get(end).y);
		}

		double a1 = A1(init, end);
		double a2 = A2(init, end);
		double a12 = A12(init, end);
		double den = (a1 * a2 - Math.pow(a12, 2));
		if (den == 0) {
			return new PVector(points.get(end).x, points.get(end).y);
		}

		PVector c1 = C1(init, end);
		PVector c2 = C2(init, end);
		double p2x = (a1 * c2.x - a12 * c1.x) / den;
		double p2y = (a1 * c2.y - a12 * c1.y) / den;

		return new PVector((float) p2x, (float) p2y);
	}

	// Remove index from interpolation
	@Override
	public int RemoveIndex(int i) {
		if (i < 0) {
			return -1;
		}
		if (this.index == null || this.index.size() < 3) {

		} else {
			for (int j = 0; j < index.size(); j++) {
				if (index.get(j) == i) {
					index.remove(j);
					compute(j - 1);
					return j;
				}
			}
		}
		return -1;
	}

	@Override
	public void setData(List<PVector> pts_, List<Integer> idx) throws CurveCreationException {
		this.index = idx;
		this.points = pts_;
		if (DYNAMIC) {
			int size = this.points.size();
			a1 = new double[size];
			a2 = new double[size];
			a12 = new double[size];
		}
		if (points == null) {
			throw new CurveCreationException("Empty point set to interpolate");
		}
		if (index != null) {
			java.util.Collections.sort(index);
		}
		if (N() < 2) {
			throw new CurveCreationException("Two knots requiered to interpolate");
		}
		this.cP = new PVector[(pts_.size() - 1) * 2];
		compute(0);

	}

}
