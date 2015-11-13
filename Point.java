public class Point {
	private double x;
    private double y;

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
