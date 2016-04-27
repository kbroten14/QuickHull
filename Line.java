/**
 * Line.java
 *
 * Represents a line between a pair of points.
 *
 * Helen Hu and Greg Gagne
 **/
 
 
import java.awt.Point;

public class Line {
	private Point point1, point2;
	private double a, b, c;  // line equation: ax + by = c
	private int x1y2, x2y1;
	
	public Line(Point first, Point second){
		point1 = new Point(first);
		point2 = new Point(second);
		initLineEquation();
		initDeterminant();
	}
	
	// from page 113 of your textbook
	private void initLineEquation(){
			a = point2.y-point1.y;
			b = point1.x-point2.x;
			c = point1.x*point2.y - point1.y*point2.x;	
	}
	
	// from page 197 of your textbook
	private void initDeterminant() {
		x1y2 = point1.x*point2.y;
		x2y1 = point2.x*point1.y;
	}
	
	public Point getFirstPoint() {
		return new Point(point1);
	}
	
	public Point getSecondPoint() {
		return new Point(point2);
	}
	
	/* plugs in point to the general line equation and returns the result */
	public double plugInPoint(Point other){
		// from page 113 of your textbook
		return a*other.x + b*other.y - c;
	}
	
	/**
	 * @return the distance between the two points
	 */
	public double distance() {
		return Math.sqrt(distanceSquared());
	}
		
	/**
	 * @return the distance squared between the two points
	 */
	public double distanceSquared() {
		return ((point1.x - point2.x)*(point1.x - point2.x) +
				(point1.y - point2.y)*(point1.y - point2.y));
	}
	
	// this method is named "isBelow" so it reads smoothly when 
	// you call it, as in: line.isBelow(point)
	// the textbook uses the terminology "point p3 is to the left of the line p1p2"
	// which means the SAME THING as the line is below p3!
	
	/**
	 * returns true if the line p1p2 is BELOW the point p3
	 * @param other - third point p3
	 * @return true if the "other" point p3 is left of the line p1p2,
	 * false otherwise 
	 */
	public boolean isBelow(Point other) {
		return compareTo(other) > 0;
	}
	
	
	/**
	 * returns true if the line p1p2 is ABOVE the point p3
	 * @param other - third point p3
	 * @return true if the "other" point p3 is right of the line p1p2,
	 * false otherwise 
	 */
	public boolean isAbove(Point other) {
		return compareTo(other) < 0;
	}
	
	/**
	 * @param other - third point p3
	 * @return +1 if the "other" point p3 is left of the line p1p2,
	 * -1 if the "other" point is right of the line p1p2, 
	 * 0 if the "other" point is ON the line p1p2
	 */
	public int compareTo(Point other){
		// if the determinant is positive, the p3 is left of line
		double determinant = getDeterminant(other);
//		System.out.println("\tdeterminant for " + other + " is " + determinant);
		if (determinant > 0)
			return 1;
		else if (determinant < 0)
			return -1;
		else
			return 0;
	}
	
	/**
	 * Returns twice the area of the triangle p1p2p3, where p1p2 are the 
	 * endpoints of the line, and p3 is the parameter.
	 * @param other - third point p3
	 * @return the signed magnitude of the determinant 
	 */
	public double getDeterminant(Point other) {
		// equation is from page 153 of your textbook
		int x3y1 = other.x*point1.y;
		int x2y3 = point2.x*other.y;
		int x3y2 = other.x*point2.y;
		int x1y3 = point1.x*other.y;
		
		// have to reverse the determinant because Java using a downwards y axis.
		return -(x1y2+x3y1+x2y3-x3y2-x2y1-x1y3);
	}
	
	/**
	 * Returns the area of the triangle p1p2p3, where p1p2 are the 
	 * endpoints of the line, and p3 is the parameter.
	 * @param other - third point p3
	 * @return the area of the triangle p1p2p3 
	 */
	public double getTriangleArea(Point other) {
		return Math.abs(getDeterminant(other))/2.;
	}
	
	/**
	 * Returns the reverse line of this line 
	 * in other words, if this line is created from p1p2
	 * this method returns the line created from p2p1
	 * @return line p2p1
	 */
	public Line reverseLine() {
		return new Line(point2,point1);
	}
	
	public String toString() {
		return ("line between " + point1 + " and " + point2);
	}
	
	public static void main(String[] args){
		Point p1 = new Point(4,7);
		Point p2 = new Point(12,10);
		//Point p3 = new Point(6,2);
		Point p3 = new Point(5,10);
		Line line = new Line(p1,p2);
		System.out.println(line.getDeterminant(p3));
	}
	
}

