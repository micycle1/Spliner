package example;

import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import asolis.curvefitting.fitting.BezierFitting;
import asolis.curvefitting.fitting.Fitting;
import asolis.curvefitting.fitting.LeastSquareFitting;
import asolis.curvefitting.fitting.PolygonFitting;
import asolis.curvefitting.fitting.SmoothFitting;
import processing.core.PApplet;

/**
 * https://github.com/asolis/curveFitting
 * 
 * A library for Spline interpolation
 *
 * @author MCarleton
 *
 */
public class Example extends PApplet {

	public static void main(String[] args) {

		PApplet.main(Example.class);

		for (int i = 0; i < 10; i++) {
			new Point2D.Double(i * 50, 400 + ThreadLocalRandom.current().nextInt(-50, 50));
		}
	}

	ArrayList<Point2D> list = new ArrayList<Point2D>();
	ArrayList<Point2D> fitted = new ArrayList<Point2D>();

	@Override
	public void settings() {
		size(800, 800, FX2D);
	}

	@Override
	public void setup() {
		rectMode(CENTER);
	}

	@Override
	public void draw() {
		background(255);
		if (mousePressed) {
			if (pmouseX != mouseX && pmouseY != mouseY) {
				list.add(new Point2D.Double(mouseX, mouseY));
			}
		}

		stroke(0);
		strokeWeight(5);
		list.forEach(point -> point((float) point.getX(), (float) point.getY()));

		noFill();
		beginShape();
		stroke(125);
		strokeWeight(3);
		list.forEach(point -> curveVertex((float) point.getX(), (float) point.getY()));
		endShape();

		stroke(color(200, 0, 0));
		strokeWeight(4);
		fitted.forEach(point -> point((float) point.getX(), (float) point.getY()));

		var n = new Object() {
			int i = 0;
		};

		curves.forEach(curve -> {
			stroke(color(n.i * (255f / curves.size()), 100, 50));
			strokeWeight(10);
			if (curve instanceof CubicCurve2D) {
				var c = (CubicCurve2D) curve;

				bezier(c.getX1(), c.getY1(), c.getCtrlX1(), c.getCtrlY1(), c.getCtrlX2(), c.getCtrlY2(), c.getX2(),
						c.getY2());
				stroke(color(100, 100, 250));
				strokeWeight(2);
				line(c.getX1(), c.getY1(), c.getCtrlX1(), c.getCtrlY1()); // cp line
				line(c.getX2(), c.getY2(), c.getCtrlX2(), c.getCtrlY2()); // cp line
				fill(0, 255, 0);
				noStroke();
				rect((float) c.getX1(), (float) c.getY1(), 3, 3);
				noFill();
			} else {
				var l = (Line2D) curve;
				line(l.getX1(), l.getY1(), l.getX2(), l.getY2());

				fill(0, 255, 0);
				noStroke();
				rect((float) l.getX1(), (float) l.getY1(), 3, 3);
				noFill();
			}
			n.i++;
		});

		fill(0);
		text(typestr, 10, 10);
	}

	public void bezier(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		super.bezier((float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3, (float) x4, (float) y4);
	}

	public void line(double x1, double y1, double x2, double y2) {
		super.line((float) x1, (float) y1, (float) x2, (float) y2);
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

//	BezierFitting bf = ;
	ArrayList<Shape> curves = new ArrayList();

	@Override
	public void mouseReleased() {
		fit();
	}

	String typestr = "";

	void fit() {
		Fitting f = null;

		switch (type) {
			case 0:
				f = new BezierFitting();
				break;
			case 1:
				f = new LeastSquareFitting();
				break;
			case 2:
				f = new PolygonFitting();
				break;
			case 3:
				f = new SmoothFitting();
				break;
		}

		if (list.size() > 0) {

			typestr = f.getLabel();
			curves = f.fitCurve(list);
			fitted = new ArrayList<Point2D>(f.points);
		}

	}
}