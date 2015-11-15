public class Point {
	private double x;
    private double y;
    private Point first;
    private double angle;


    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public void setFirst(Point frst) {
        first = frst;
    }

    public void findAngle(Point point)
    {
        angle = (-1)*Math.toDegrees(Math.atan2(y-point.getY(), x-point.getX()));
        if (angle < 0)
            angle += 360;
    }

    public double findDist(Point point)
    {
        return Math.sqrt((x-point.getX())*(x-point.getX()) + (y-point.getY())*(y-point.getY()));
    }

    public int compareTo(Point point)
    {
        if (point.getAngle() > angle)
            return -1;
        else if (point.getAngle() < angle)
            return 1;
        else
        {
            double a = Math.sqrt((first.getY()-y)*(first.getY()-y) + (first.getX()-x)*(first.getX()-x));
            double b = Math.sqrt((first.getY()-point.getY())*(first.getY()-point.getY())+ (first.getX()-point.getX())*(first.getX()-point.getX()));
            if (a < b)
                return -1;
            else
                return 1;
        }
    }

    public boolean equals(Point p)
    {
        if (p.getX() == x && p.getY() == y)
            return true;
        else
            return false;
    }

    /*
	 * Returns a Point representing the vertex in parts.
	 * parts[0] is assumed to be x and parts[1] is assumed to be y
	 */
	public static Point pointFromStringArray(String[] parts) {
		return new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
	}

    public String toString() {
        return ("(" + x + ", " + y + ")"); 
    }
}
