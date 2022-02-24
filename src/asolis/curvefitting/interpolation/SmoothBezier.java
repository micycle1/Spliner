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
package asolis.curvefitting.interpolation;

import java.util.ArrayList;
import java.util.List;

import asolis.curvefitting.CurveCreationException;
import processing.core.PVector;

public class SmoothBezier extends Interpolation {
	// First derivative and second derivative are equals.
	public SmoothBezier() {
		cP = new PVector[0];
		points = new ArrayList<>();
		index = new ArrayList<>();
	}

	public SmoothBezier(List<PVector> points, List<Integer> index) throws CurveCreationException {
		setData(points, index);
	}

	@Override
	protected void compute() {
		int n = N() - 1;
		if (n == 1) {
			cP = new PVector[2];
			// 3P1 = 2P0 + P3
			cP[0] = new PVector((2 * get(0).x + get(1).x) / 3, (2 * get(0).y + get(1).y) / 3);
			// P2 = 2P1 + P0
			cP[1] = new PVector((2 * cP[0].x - get(0).x), (2 * cP[0].y - get(0).y));
			return;
		}

		cP = new PVector[2 * n];
		cP[0] = new PVector(get(0).x + 2 * get(1).x, get(0).y + 2 * get(1).y);
		for (int i = 1; i < n - 1; ++i) {
			cP[2 * i] = new PVector(4 * get(i).x + 2 * get(i + 1).x, 4 * get(i).y + 2 * get(i + 1).y);
		}
		cP[2 * (n - 1)] = new PVector((8 * get(n - 1).x + get(n).x) / 2.0f, (8 * get(n - 1).y + get(n).y) / 2.0f);

		// Compute first right end points
		getControlPoints(cP);
		for (int i = 0; i < n; ++i) {
			if (i < n - 1) {
				cP[2 * i + 1] = new PVector(2 * get(i + 1).x - cP[2 * (i + 1)].x, 2 * get(i + 1).y - cP[2 * (i + 1)].y);
			} else {
				cP[2 * i + 1] = new PVector((get(n).x + cP[2 * (n - 1)].x) / 2, (get(n).y + cP[2 * (n - 1)].y) / 2);
			}
		}

	}

	private void getControlPoints(PVector[] data) {
		int n = data.length / 2;
		float[] tmp = new float[n];
		float b = 2f;
		data[0].set(data[0].x / b, data[0].y / b);
		for (int i = 1; i < n; i++) {
			tmp[i] = 1 / b;
			b = (i < n - 1 ? 4.0f : 3.5f) - tmp[i];
			data[2 * i].set((data[2 * i].x - data[2 * (i - 1)].x) / b, (data[2 * i].y - data[2 * (i - 1)].y) / b);
		}
		for (int i = 1; i < n; i++) {

			data[2 * (n - i - 1)].set(data[2 * (n - i - 1)].x - tmp[n - i] * data[2 * (n - i)].x,
					data[2 * (n - i - 1)].y - tmp[n - i] * data[2 * (n - i)].y);
		}
	}

}
