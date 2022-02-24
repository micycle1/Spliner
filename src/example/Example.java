package example;

import java.util.ArrayList;
import java.util.List;

import micycle.spliner.fitting.BezierFitting;
import micycle.spliner.fitting.Fitting;
import micycle.spliner.fitting.LeastSquareFitting;
import micycle.spliner.fitting.PolygonFitting;
import micycle.spliner.fitting.SmoothFitting;
import micycle.spliner.geom.CubicCurve;
import micycle.spliner.geom.Segment;
import micycle.spliner.geom.Line;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * Spliner demo.
 */
public class Example extends PApplet {

	public static void main(String[] args) {
		PApplet.main(Example.class);
	}

	List<PVector> list = new ArrayList<>();
	List<PVector> fitted = new ArrayList<>();
	List<Line> curves = new ArrayList<>();
	String fitLabel = "";

	@Override
	public void settings() {
		size(600, 600, FX2D);
	}

	@Override
	public void setup() {
		rectMode(CENTER);
		textAlign(LEFT, TOP);
		textSize(14);
	}

	@Override
	public void draw() {
		background(255);
		if (mousePressed) {
			if (pmouseX != mouseX && pmouseY != mouseY) {
				list.add(new PVector(mouseX, mouseY));
			}
		}

		stroke(0);
		strokeWeight(6);
		list.forEach(point -> point(point.x, point.y));

		// draw a simple curve between points
		noFill();
		beginShape();
		stroke(125);
		strokeWeight(3);
		list.forEach(point -> curveVertex(point.x, point.y));
		endShape();

		stroke(color(255, 255, 0));
		strokeWeight(3);
		fitted.forEach(point -> point(point.x, point.y));

		int i = 0;
		int curveCount = 0;
		int lineCount = 0;
		for (var curve : curves) {
			stroke(color(i * (255f / curves.size()), 100, 50));
			strokeWeight(10);
			if (curve instanceof CubicCurve) {
				curveCount++;
				var c = (CubicCurve) curve;
				bezier(c.getX1(), c.getY1(), c.getCtrlX1(), c.getCtrlY1(), c.getCtrlX2(), c.getCtrlY2(), c.getX2(), c.getY2());
				stroke(color(100, 100, 250));
				strokeWeight(2);
				line(c.getX1(), c.getY1(), c.getCtrlX1(), c.getCtrlY1()); // cp line
				line(c.getX2(), c.getY2(), c.getCtrlX2(), c.getCtrlY2()); // cp line
				fill(0, 255, 0);
				noStroke();
				rect((float) c.getX1(), (float) c.getY1(), 3, 3);
				noFill();
			} else {
				lineCount++;
				var l = (Segment) curve;
				line(l.getX1(), l.getY1(), l.getX2(), l.getY2());

				fill(0, 255, 0);
				noStroke();
				rect((float) l.getX1(), (float) l.getY1(), 3, 3);
				noFill();
			}
			i++;
		}

		fill(0);
		text(fitLabel, 5, 0);
		text(curveCount == 0 && lineCount == 0 ? "" : String.format("Curves: %s", curveCount), 5, 16);
		text(lineCount == 0 && curveCount == 0 ? "" : String.format("Lines: %s", lineCount), 5, 32);
		
//		List<PVector> points;
//		Fitting fitting = new BezierFitting();
//		List<Line> beziers = fitting.fitCurve(points);
		
	}

	@Override
	public void keyPressed() {
		type++;
		type %= 4;
		fit();
	}

	int type = 0;

	@Override
	public void mousePressed() {
		list.clear();
		fitted.clear();
	}

	@Override
	public void mouseReleased() {
		fit();
	}

	void fit() {
		Fitting f = null;
		switch (type) {
			case 1 :
				f = new BezierFitting();
				break;
			case 0 :
				f = new LeastSquareFitting();
				break;
			case 2 :
				f = new PolygonFitting();
				break;
			case 3 :
				f = new SmoothFitting();
				break;
		}

		if (!list.isEmpty()) {
			fitLabel = f.getLabel();
			curves = f.fitCurve(list);
		}

	}

}