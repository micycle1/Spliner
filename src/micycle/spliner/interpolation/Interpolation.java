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
import micycle.spliner.geom.Segment;
import micycle.spliner.geom.Line;
import processing.core.PVector;

public abstract class Interpolation {

	public PVector[] cP;
	List<PVector> points; // data points
	List<Integer> index; // index to interpolate

	// Add index to interpolation
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
				compute();
				return j;
			}
		}
		return -1;
	}

	protected abstract void compute();

	// Return the ith interpolation point.
	protected PVector get(int i) {
		if (i < 0 || i >= N()) {
			throw new IndexOutOfBoundsException(String.format("Interpolation Class: index %d is not valid", i));
		}

		if (index == null) {
			return points.get(i);
		} else {
			int temp = index.get(i);
			if (temp < 0 || temp >= points.size()) {
				throw new IndexOutOfBoundsException(
						String.format("Interpolation Class: index %d is not valid." + " Index list is not sync", i));
			}
			return points.get(index.get(i));
		}

	}

	public CubicCurve getCurveAt(int i) {
		if (i < 0 || i >= N() - 1) {
			throw new IndexOutOfBoundsException(String.format("Interpolation Class: cannot " + "retrieve curve with index : %d", i));
		}
		return new CubicCurve(get(i).x, get(i).y, cP[2 * i].x, cP[2 * i].y, cP[2 * i + 1].x, cP[2 * i + 1].y,
				get(i + 1).x, get(i + 1).y);
	}

	/**
	 * Returns lines or beziers
	 * 
	 * @return
	 */
	public List<Line> getCurves() {
		List<Line> s = new ArrayList<>();

		for (int i = 0; i < N() - 1; i++) {
			CubicCurve cubic = new CubicCurve(get(i).x, get(i).y, cP[2 * i].x, cP[2 * i].y, cP[2 * i + 1].x,
					cP[2 * i + 1].y, get(i + 1).x, get(i + 1).y);
			s.add(cubic);
		}

		return s;
	}

	public int getIndex(int i) {
		if (index == null) {
			return i;
		} else {
			return index.get(i);
		}
	}

	// Return the interpolation points
	public List<PVector> getKnots() {
		if (index != null) {
			ArrayList<PVector> tmp = new ArrayList<>(index.size());
			for (int i = 0; i < index.size(); i++) {
				tmp.add(get(i));
			}
			return tmp;
		} else {
			return points;
		}
	}

	public Segment getLineAt(int i) {
		if (i < 0 || i >= N() - 1) {
			throw new IndexOutOfBoundsException(String.format("Interpolation Class: cannot " + "retrieve line with index : %d", i));
		}
		return new Segment(get(i).x, get(i).y, get(i + 1).x, get(i + 1).y);
	}

	// Return the number of interpolation points
	protected int N() {
		return (index == null) ? points.size() : index.size();
	}

	// Remove index from interpolation
	public int RemoveIndex(int i) {
		int idx = index.indexOf(i);
		if (idx != -1) {
			index.remove(idx);
			compute();
		}
		return idx;
	}

	public void setData(List<PVector> pts_, List<Integer> idx) throws CurveCreationException {
		this.index = idx;
		this.points = pts_;

		if (points == null) {
			throw new CurveCreationException("Empty point set to interpolate");
		}
		if (index != null) {
			java.util.Collections.sort(index);
		}
		if (N() < 2) {
			throw new CurveCreationException("Two knots requiered to interpolate");
		}
		this.cP = new PVector[(pts_.size()) * 2];
		compute();

	}
}
