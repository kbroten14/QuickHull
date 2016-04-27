/**
 * PointSet.java
 *
 * A class representing a set of points in a collection.
 *
 * Greg Gagne and Helen Hu
 * 
 **/

import java.util.*;
import java.awt.Point;
import java.awt.Polygon;

public class PointSet {
	// all the points in the collection
	private ArrayList<Point> points;
	// the polygon that makes up the convex hull using the quick hull method
	private Polygon quickHull;
	// the polygon that makes up the convex hull using the brute force method
	private Polygon hull;
	// a boolean to check if the convex hull needs to be recalculated
	private boolean hullCalculated = true;
	// flag to help debug
	private boolean DEBUG = false;

	// constructor
	public PointSet() {
		points = new ArrayList<Point>();
		quickHull = new Polygon();
		hull = new Polygon();
	}

	/**
	 * Adds a single point to the collection
	 * @param point
	 */
	public void addPoint(Point point) {
		if (!this.points.contains(point)){
			this.points.add(point);	
			hullCalculated = false;	
		}
	}		

	/** returns a specific point from the collection
	 * 
	 * @param i: a number between 0 and the number of points
	 * @return the Point indexed
	 */
	public Point getPoint(int i){
		if (0 <= i && i < points.size())
			return points.get(i);
		else
			throw new NoSuchElementException();
	}

	/** 
	 * 
	 * @return an ArrayList with all the Points in the collection 
	 */
	public ArrayList<Point> getPoints() {
		return points;
	}

	/**
	 * removes all the points from the collection
	 */
	public void clear() {
		if (DEBUG)
			System.out.println("\n\n\n");
		points.clear();
		quickHull.reset();
		hullCalculated = true;  // convex hull is currently empty
	}

	/** returns the number of points in the current collection */
	public int getNumber() {
		return points.size();
	}

	/** returns the convex hull for the current set of points */
	public Polygon getHull() {
		if (!hullCalculated) {
			hullCalculated = true;
			calculateBruteForceConvexHull();
			calculateQuickHull();
		}
		return hull;
	}

	/** returns the convex hull for the current set of points */
	public Polygon getQuickHull() {
		if (!hullCalculated) {
			hullCalculated = true;
			calculateBruteForceConvexHull();
			calculateQuickHull();
		}
		return quickHull;
	}

	public Point[] getClosestPoints() {
		if (points.size() >= 2) {
			Point[] array = new Point[2];
			array[0] = points.get(0);
			array[1] = points.get(1);
			return array;
		}
		return new Point[0];
	}

	/** returns all points' positions as a String */
	public String toString() {
		String returnString = "Points:\n";
		for (int i=0; i<quickHull.npoints; i++){
			returnString = returnString + " Point " + i + ": (" +
					points.get(i).x + "," + points.get(i).y;
		}
		return returnString;
	}

	/** returns convex hull points */
	public String convexHullToString() {
		String hullPoints = "Convex Hull:\n";
		if (!hullCalculated) {
			calculateBruteForceConvexHull();
			calculateQuickHull();
		}
		for (int i=0; i<quickHull.npoints; i++){
			hullPoints = hullPoints + "\t(" + quickHull.xpoints[i] +"," 
					+ quickHull.ypoints[i] + ")\n";	
		}
		return hullPoints;
	}	

	// this method calculates the convex hull of the point set, using a divide and conquer technique
	// see pages 195 - 197 of your textbook
	private void calculateQuickHull(){
		quickHull.reset();

		// find left most point and right most point
		Point[] leftRight = this.findLeftRight();
		Point leftPoint = leftRight[0];
		Point rightPoint = leftRight[1];

		// add all remaining points to set
		ArrayList<Point> set = new ArrayList<Point>();
		for (Point pt : points)
			if (!pt.equals(rightPoint) && !pt.equals(leftPoint))
				set.add(pt);

		if (DEBUG) {
			System.out.println("Left: " + leftPoint + "\tRight: " + rightPoint);
			System.out.println("Set of remaining points: ");
			for (Point pt : set)
				System.out.println("\t" + pt);
		}

		ArrayList<Point> convexHull = calculateConvexHull(leftPoint, rightPoint, set);

		// add points in convex hull to Polygon to be drawn by GUI
		for (Point pt:convexHull)
			quickHull.addPoint(pt.x,pt.y);
	}

	// given (1) the left-most point and (2) the right-most point, and 
	// (3) all the remaining points
	// this method returns a list of all the points in the convex hull in counter-clockwise order 
	private ArrayList<Point> calculateConvexHull(Point left, Point right, ArrayList<Point> set) {
		ArrayList<Point> convexHull = new ArrayList<Point>();
		ArrayList<Point> upperHull, lowerHull;

		Line middleLine = new Line(left, right);

		ArrayList<Point> ptsAboveMiddle = this.getPointsAbove(middleLine, set);

		/**
		 * Removes upper points from set leaving behind the lower points.
		 */
		for(Point p : ptsAboveMiddle)
		{
			set.remove(p);
		}

		upperHull = this.calculateUpperHull(middleLine, ptsAboveMiddle);
		lowerHull = this.calculateLowerHull(middleLine, set);

		/**
		 * Adds points from upper hull to convex hull. 
		 */
		for(Point q : upperHull)
		{
			convexHull.add(q);
		}
		for(Point r : lowerHull)
		{
			convexHull.add(r);
		}

		return convexHull;
	}

	// returns a list of all the points on the upper hull in counter-clockwise order
	// does NOT return the two end-points of the line
	private ArrayList<Point> calculateUpperHull(Line line, ArrayList<Point> pointsAbove) {

		Line line1, line2;
		ArrayList<Point> pointsAbove1, pointsAbove2;

		ArrayList<Point> convexHull = new ArrayList<Point>(pointsAbove.size());
		
		convexHull.add(line.getFirstPoint()); 
		
		if(!pointsAbove.isEmpty())
		{
			Point maxPoint = findMaxPoint(line, pointsAbove); 
			line1 = new Line(line.getFirstPoint(), maxPoint); 
			line2 = new Line(maxPoint, line.getSecondPoint()); 
			pointsAbove1 = getPointsAbove(line1, pointsAbove); 
			pointsAbove2 = getPointsAbove(line2, pointsAbove); 
			convexHull.addAll(calculateUpperHull(line1, pointsAbove1)); 
			convexHull.addAll(calculateUpperHull(line2, pointsAbove2));
		}
		
		convexHull.add(line.getSecondPoint());
		
		return convexHull;	
	}

	// returns a list of all the points on the lower hull in counter-clockwise order
	// does NOT return the two end-points of the line
	private ArrayList<Point> calculateLowerHull(Line line, ArrayList<Point> pointsBelow) {
		// this method will work if calculateUpperHull is correct
		Line reversedLine = line.reverseLine();
		return calculateUpperHull(reversedLine, pointsBelow);
	}

	// returns a list of points that are above the line
	// these points are also removed from the set parameter
	private ArrayList<Point> getPointsAbove(Line line, ArrayList<Point> set) {
		ArrayList<Point> pointsAbove = new ArrayList<Point>(set.size());

		// determine which lines fall above the line
		for (int i=0; i<set.size(); i++){
			Point pt = set.get(i);		
			if (line.isBelow(pt))	{	     // point falls above line
				pointsAbove.add(pt);
				set.remove(pt);
				i--;
			}
		}
		return pointsAbove;
	}

	// returns the point from the set that is farthest from the line 
	// book refers to this as Pmax
	private Point findMaxPoint(Line line, ArrayList<Point> set) {
		if (set.size() == 0)
			return null;

		Point maxPoint = set.get(0);
		double maxArea = Math.abs(line.getDeterminant(maxPoint));
		for (int i=1; i<set.size(); i++) {
			// find point farthest from line
			double newArea = Math.abs(line.getDeterminant(set.get(i)));
			if (newArea > maxArea) {
				maxArea = newArea;
				maxPoint = set.get(i);
			}
		}
		return maxPoint;
	}		

	// returns an array with leftmost and rightmost points
	// leftmost is stored in first array position [0]
	// rightmost is stored in second array position [1]
	private Point[] findLeftRight() {
		return findLeftRight(0,points.size()-1);
	}

	// recursive helper method
	private Point[] findLeftRight(int start, int end) {
		Point[] leftRight = new Point[2];		
		// base case #1: only one point
		if (start == end) {
			leftRight[0] = points.get(start);
			leftRight[1] = points.get(start);
		}
		// base case #2: only two points
		else if (start == end-1) {
			if (points.get(start).x < points.get(end).x){
				leftRight[0] = points.get(start);
				leftRight[1] = points.get(end);
			}
			else {
				leftRight[0] = points.get(end);
				leftRight[1] = points.get(start);
			}
		}
		// recursive case
		else if (start < end) {
			int mid = (start + end)/2;
			Point[] firstHalf = findLeftRight(start, mid);
			Point[] secondHalf = findLeftRight(mid+1, end);
			// find leftmost point
			if (firstHalf[0].x < secondHalf[0].x)
				leftRight[0] = firstHalf[0];
			else
				leftRight[0] = secondHalf[0];
			// find rightmost point
			if (firstHalf[1].x < secondHalf[1].x)
				leftRight[1] = secondHalf[1];
			else 
				leftRight[1] = firstHalf[1];
		}
		return leftRight;	
	}	

	// calculates the convex hull from the current set of points
	// this method currently does not work!
	// You may NOT use the Polygon contains method in your final solution.
	private void calculateBruteForceConvexHull() {
		hull.reset();
		hullCalculated = false;


		Line currentEdge;
		Point nextPoint;

		Point[] maxPoints = this.findLeftRight();

		Point currentPoint = maxPoints[1];

		ArrayList<Point> hullPoints = new ArrayList<Point>();

		hull.addPoint(currentPoint.x, currentPoint.y);
		if(points.size() > 4)
		{
			while(hullCalculated == false) 
			{
				if (currentPoint.equals(points.get(0)))
					nextPoint = points.get(1);
				else
					nextPoint = points.get(0);
				currentEdge = new Line(currentPoint, nextPoint);
				for(int i = 0; i < points.size(); i++)
				{

					if(currentEdge.plugInPoint(points.get(i)) >= 0 && !points.get(i).equals(currentPoint) && !points.get(i).equals(nextPoint))
					{
						nextPoint = points.get(i);
						currentEdge = new Line(currentPoint, nextPoint);
					} 
				}
				if (hullPoints.contains(nextPoint))
				{
					hullCalculated = true;
				}
				else
				{
					hull.addPoint((int)nextPoint.getX(), (int)nextPoint.getY());
					hullPoints.add(nextPoint);
				}
				currentPoint = nextPoint;
			}
		}
	}


}

