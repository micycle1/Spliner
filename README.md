[![](https://jitpack.io/v/micycle1/Spliner.svg)](https://jitpack.io/#micycle1/Spliner)

# Spliner
Piecewise curve approximation for Processing 4+.

*Spliner* constructs piecewise curves that have the best fit to a list of points. Four algorithms are implemented:

- Quad + Bezier curve fitting
- Smooth Fitting
- Least-square fitting
- Polygon fitting

Algorithms by [Andrés Solís Montero](https://github.com/asolis/curveFitting).

<p float="middle">
  <img src="resources/spliner.gif" width=500px/>
</p>

## Usage
```
List<PVector> points;
Fitting fitting = new BezierFitting(); // or new LeastSquareFitting(), etc.
List<Shape> beziers = fitting.fitCurve(points);

for (var curve : curves) {
    if (curve instanceof CubicCurve) {
        var c = (CubicCurve) curve;
        bezier(c.getX1(), c.getY1(), c.getCtrlX1(), c.getCtrlY1(), c.getCtrlX2(), c.getCtrlY2(), c.getX2(), c.getY2());
    } else { // straight-line segments
        var l = (Segment) curve;
        line(l.getX1(), l.getY1(), l.getX2(), l.getY2());
    }
}
```

## Example
<details><summary>Processing Code...</summary>

```
import processing.javafx.*;
import micycle.spliner.fitting.*;
import micycle.spliner.geom.*;
import java.util.ArrayList;
import java.util.List;

List<PVector> points;
List<Line> curves;
int fittingType = 0;

void setup() {
  size(800, 800, FX2D);
  colorMode(HSB, 1, 1, 1);

  populatePoints();
  fit();
}

void draw() {
  background(0, 0, 1);

  int i = 0;
  for (var curve : curves) {
    stroke(color(i * (1f / curves.size()), 1, 1));
    strokeWeight(10);
    if (curve instanceof CubicCurve) {
      var c = (CubicCurve) curve;
      bezier(c.getX1(), c.getY1(), c.getCtrlX1(), c.getCtrlY1(), c.getCtrlX2(), c.getCtrlY2(), c.getX2(), c.getY2());
      stroke(color(0.4, 1, 0.8));
      strokeWeight(2);
      line(c.getX1(), c.getY1(), c.getCtrlX1(), c.getCtrlY1()); // cp line
      line(c.getX2(), c.getY2(), c.getCtrlX2(), c.getCtrlY2()); // cp line
    } else { // straight-line segments
      var l = (Segment) curve;
      line(l.getX1(), l.getY1(), l.getX2(), l.getY2());
    }
    i++;
  }
}

void fit() {
  Fitting f = null;
  switch (fittingType % 4) {
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
  curves = f.fitCurve(points);
}

void populatePoints() {
  points = new ArrayList<>();
  for (int x = 0; x < width; x+=20) {
    points.add(new PVector(x, noise(x+frameCount) * height));
  }
}

void mousePressed() { // click to fit a new point set
  populatePoints();
  fit();
}

void keyPressed() { // press to switch fitting algorithm
  fittingType++;
  fit();
}

```
</details>