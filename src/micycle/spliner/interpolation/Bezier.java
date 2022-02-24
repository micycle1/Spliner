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
import processing.core.PVector;

public class Bezier extends Interpolation {

	private static final float AP = 0.5f;

	public Bezier() {
		cP = new PVector[0];
		points = new ArrayList<>();
		index = new ArrayList<>();
	}

	public Bezier(List<PVector> pts, List<Integer> idxs) throws CurveCreationException {
		setData(pts, idxs);
	}

	@Override
	protected void compute() {
		int n = N();
		if (n == 2) {
			cP = new PVector[2];
			// 3P1 = 2P0 + P3
			cP[0] = new PVector((2 * get(0).x + get(1).x) / 3, (2 * get(0).y + get(1).y) / 3);

			// P2 = 2P1 + P0
			cP[1] = new PVector((2 * cP[0].x - get(0).x), (2 * cP[0].y - get(0).y));
			return;
		}

		cP = new PVector[2 * n - 2];
		double paX, paY;
		double pbX = get(0).x;
		double pbY = get(0).y;
		double pcX = get(1).x;
		double pcY = get(1).y;
		double p2X = 0, p2Y = 0, abX, abY, acX, acY, lac, proj, apX, apY, p1X, p1Y, cbX, cbY;

		for (int i = 1; i < n - 1; i++) {
			paX = pbX;
			paY = pbY;
			pbX = pcX;
			pbY = pcY;
			pcX = get(i + 1).x;
			pcY = get(i + 1).y;
			abX = pbX - paX;
			abY = pbY - paY;
			acX = pcX - paX;
			acY = pcY - paY;
			lac = Math.sqrt(acX * acX + acY * acY);
			acX = acX / lac;
			acY = acY / lac;

			proj = abX * acX + abY * acY;
			proj = proj < 0 ? -proj : proj;
			apX = proj * acX;
			apY = proj * acY;

			p1X = pbX - AP * apX;
			p1Y = pbY - AP * apY;
			if (i == 1) {
				cP[0] = new PVector((int) p1X, (int) p1Y);
			}
			cP[2 * i - 1] = new PVector((int) p1X, (int) p1Y);

			acX = -acX;
			acY = -acY;
			cbX = pbX - pcX;
			cbY = pbY - pcY;
			proj = cbX * acX + cbY * acY;
			proj = proj < 0 ? -proj : proj;
			apX = proj * acX;
			apY = proj * acY;

			p2X = pbX - AP * apX;
			p2Y = pbY - AP * apY;
			cP[2 * i] = new PVector((int) p2X, (int) p2Y);
		}
		cP[cP.length - 1] = new PVector((int) p2X, (int) p2Y);
	}

}
