import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.*;
import java.util.*;
import java.lang.Math;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class PathPlanner extends JFrame {
	private Point start;
	private Point goal;
	private Obstacle world;
	private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	PointsPanel pp;
	
	/* Assumes world/obstacle file is in the format specified by the assignment. */
	private void getWorldAndObstacles(String file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line;
		int n = Integer.parseInt(br.readLine());
		Obstacle curr = null;
		
		assert(n > 0);
		
		while ((line = br.readLine()) != null) {
			String[] parts = line.split(" ");
			if (parts.length == 1) {
				if (curr != null)
					obstacles.add(curr);
				curr = new Obstacle();
			} else {
				curr.addVertex(Point.pointFromStringArray(parts));
			}
		}
		obstacles.add(curr);
		br.close();
		
		/* The world is the first obstacle in the text file. */
		world = obstacles.remove(0);
	}
	
	/* Assumes goal/start file is in the format specified by the assignment. */
	private void getStartAndGoal(String file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		for (int i = 0; i < 2; i++) {
			String line = br.readLine();
			String[] parts = line.split(" ");
			Point p = Point.pointFromStringArray(parts);
			if (i == 0)
				start = p;
			else
				goal = p;
		}
		
		br.close();
	}
	
	/*
	 * Assumes the following two arguments (file names) are passed in:
	 * Arg0: The file containing the world and obstacle vertices
	 * Arg1: The file containing the start and goal vertices
	 */
	public static void main(String[] args) throws IOException {
		PathPlanner planner = new PathPlanner(args[0], args[1]);
	}

	public PathPlanner(String world_file, String start_goal_file) throws IOException {
		super("Robotics HW4");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		getWorldAndObstacles(world_file);
		getStartAndGoal(start_goal_file);

		// Set up points panel
		pp = new PointsPanel(start, goal, world, obstacles);

		// Add points panel to frame
		this.add(pp, BorderLayout.CENTER);
		this.pack();
		this.setVisible(true);
		
		/* Print out parsed data to ensure correctness. */
		System.out.println("Start:\n\t" + start);
		System.out.println("Goal:\n\t" + goal);
		System.out.println("World:\n" + world);
		for (Obstacle o : obstacles)
			System.out.println("Obstacle:\n" + o);
	}
}

/**
 * Class that displays the world and obstacles.
 */
class PointsPanel extends JPanel
{   
	Point start;
	Point goal;
	Obstacle world;
	ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	ArrayList<Obstacle> grownObstacles = new ArrayList<Obstacle>();
	final int SCALE_FACTOR = 50;
	final int ROBOT_SIZE = (int)(0.4 * SCALE_FACTOR);

    public PointsPanel(Point st, Point gl, Obstacle wd, ArrayList<Obstacle> obs) {
		setBackground(getBackground());
        setForeground(Color.black);
        setPreferredSize(new Dimension(1000, 700));
        start = st;
    	goal = gl;
        world = wd;
    	obstacles = obs;
    }

    @Override
    public void paint(Graphics g) {
        BufferedImage im = new BufferedImage(this.getWidth(), this.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR);
        // Paint normally but on the image
        super.paint(im.getGraphics());

        // Reverse the image
        AffineTransform tx = AffineTransform.getScaleInstance(1, 1);
        tx.rotate(Math.PI);
        tx.translate(-im.getWidth(), -im.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        im = op.filter(im, null);

        // Draw the reversed image on the screen
        g.drawImage(im, 0, 0, null);
    }

    public void paintComponent(Graphics g) {
        // Draw world
        g.setColor(getBackground()); //colors the window
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground()); //set color and fonts
        drawBorders(g, world.getVerticies(), Color.blue, true);
        drawObstacles(g, obstacles, Color.blue);
        drawStartGoal(g);

        // Grow and draw grown obstacles
        growObstacles(g);
        drawObstacles(g, grownObstacles, Color.yellow);
        revalidate();

        // Set up for visibility graph
        ArrayList<Point> points = new ArrayList<Point>();
        points.add(start);
        points.add(goal);
        for (Obstacle o : grownObstacles){
            for (Point pt : o.getVerticies()){
                points.add(pt);
            }
        }

        // Visibility Graph and Dijkstras Algorithm
        ArrayList<Vertex> graph = genVisGraph(points);
        ArrayList<Vertex> path = dijkstras(graph, graph.get(0), graph.get(1));
        printPathToFile("path.txt", path);

        ArrayList<Point> path_points = new ArrayList<Point>();
        for (Vertex v : path){
            path_points.add(v.getPt());
        }

        // Draw Visibility Graph
        ArrayList<Point> adjList_points = new ArrayList<Point>();
        for (Vertex v1 : graph){
            for (Vertex v2 : v1.getAdjList()){
                adjList_points.add(v2.getPt());
            }
            connectPoints(g, v1.getPt(), adjList_points, Color.orange);
            adjList_points.clear();
        }

        // Draw Dijkstras's shortest path
        drawBorders(g, path_points, Color.red, false);
    }

    public void drawPoints(Graphics g, ArrayList<Point> points) {
        for ( Point p : points ) {
            int start_x = dataToMapCoord(p.getX());
            int start_y = dataToMapCoord(p.getY());
            g.setColor(Color.green);
            g.fillRect(start_y, start_x, 5, 5);
        }
    }

    public void connectPoints(Graphics g, Point root, ArrayList<Point> connect, Color color) {
        int x1 = dataToMapCoord(root.getX());
        int y1 = dataToMapCoord(root.getY());
        for (int i = 0; i < connect.size(); i++) {
            g.setColor(color);
            int x2 = dataToMapCoord(connect.get(i).getX());
            int y2 = dataToMapCoord(connect.get(i).getY());
            g.drawLine(y1, x1, y2, x2);
        }
    }

    public void drawStartGoal(Graphics g) {
    	int start_x = dataToMapCoord(start.getX());
    	int start_y = dataToMapCoord(start.getY());
    	g.setColor(Color.green);
    	g.fillRect(start_y, start_x, 5, 5);

    	int goal_x = dataToMapCoord(goal.getX());
    	int goal_y = dataToMapCoord(goal.getY());
    	g.setColor(Color.red);
    	g.fillRect(goal_y, goal_x, 5, 5);
    }

    public void drawObstacles(Graphics g, ArrayList<Obstacle> obs, Color color) {
    	for (int a = 0; a < obs.size(); a++) {
    		drawBorders(g, obs.get(a).getVerticies(), color, true);
    	}
    }

    public void drawBorders(Graphics g, ArrayList<Point> vertices, Color color, boolean complete) {
    	for (int i = 0; i < vertices.size()-1; i++) {
        	Point a = vertices.get(i);
        	int x1 = dataToMapCoord(a.getX());
			int y1 = dataToMapCoord(a.getY());

			Point b = vertices.get(i+1);
			int x2 = dataToMapCoord(b.getX());
			int y2 = dataToMapCoord(b.getY());

			// Connect the first point to the last point
    		if (i == vertices.size()-2 && complete) {
    			Point c = vertices.get(0);
    			int x3 = dataToMapCoord(c.getX());
				int y3 = dataToMapCoord(c.getY());
				g.drawLine(y3, x3, y2, x2);
    		}

			// Switching x and y so it can fit the screen better
            g.setColor(color);
            g.drawLine(y1, x1, y2, x2);
        }
    }

    /**
     * Reflection algorithm, this reflects the robot about the center of the
     * robot (which means the verticies will grow by half of the robot size).
     */
    public void growObstacles(Graphics g) {
    	// Iterate through each obstacle
    	for (int a = 0; a < obstacles.size(); a++) {
    		// Iterate through each vertex
    		ArrayList<Point> verticies = obstacles.get(a).getVerticies();
    		Obstacle grownObstacle = new Obstacle();
    		for (int b = 0; b < verticies.size(); b++) {
    			Point vertex = verticies.get(b);
    			int x = dataToMapCoord(vertex.getX());
        		int y = dataToMapCoord(vertex.getY());

    			grownObstacle.addVertex(new Point(y + ROBOT_SIZE / 2, x - ROBOT_SIZE / 2));
    			grownObstacle.addVertex(new Point(y + ROBOT_SIZE / 2, x + ROBOT_SIZE / 2));
    			grownObstacle.addVertex(new Point(y - ROBOT_SIZE / 2, x - ROBOT_SIZE / 2));
    			grownObstacle.addVertex(new Point(y - ROBOT_SIZE / 2, x + ROBOT_SIZE / 2));
    		}
    		grahamAlg(grownObstacle.getVerticies());
    	}
    }

    /**
     * Graham's algorithm to find the convex hull of a graph. It first finds the lowest point on the graph (looks
     * at the y value). It then sorts the other points based on its angular relationship with the first point.
     * Then, it pushes the left points onto a stack and creates the convex hull.
     */
    public void grahamAlg(ArrayList<Point> list)
    {
    	// finds the bottom & right most point
        Point first = findFirst(list); 
        ArrayList<Point> convex = new ArrayList<Point>(list);
        // sets the data ready for convex hull
        for (int x = 0; x < convex.size(); x++) {
            convex.get(x).setFirst(first);
            convex.get(x).findAngle(first);
        }

        //sorts the points in angular order
        insertionSort(convex); 

        Stack<Point> stack = new Stack<Point>();
        stack.push(convex.get(convex.size()-1));
        stack.push(first);

        int i = 1;
        while (i < convex.size()) {
            Point temp = convex.get(i);
            Point top = stack.pop();
            Point secondTop = stack.peek();
            stack.push(top);

            double sign = (secondTop.getY()-top.getY())*(temp.getX()-secondTop.getX())-(secondTop.getX()-top.getX())*(temp.getY()-secondTop.getY());
            if (sign <= 0) {
                stack.push(temp);
                i++;
            } else {
                stack.pop();
            }
        }

        ArrayList<Point> temp = new ArrayList<Point>(stack);
        Obstacle grownObstacle = new Obstacle();
        for (i = 0; i < temp.size(); i++) {
        	double x = mapToDataCoord((int)temp.get(i).getX());
        	double y = mapToDataCoord((int)temp.get(i).getY());
        	grownObstacle.addVertex(new Point(y, x));
        }
        grownObstacles.add(grownObstacle);
    }

    /**
     * Finds the first (right and lowermost) point on the graph to start Graham's algorithm.
     */
    public Point findFirst(ArrayList<Point> list)
    {
        Point temp = list.get(0);
        int x1 = dataToMapCoord(temp.getX());
        int y1 = dataToMapCoord(temp.getY());
        for (int a = 1; a < list.size(); a++) {
            Point i = list.get(a);
            int x2 = dataToMapCoord(i.getX());
        	int y2 = dataToMapCoord(i.getY());
            if (x2 > x1 || (x2 == x1 && y2 > y1)) {
                temp = i;
            }
        }
        return temp;
    }
 
    public ArrayList<Vertex> genVisGraph(ArrayList<Point> a) {
        ArrayList<Vertex> graph = new ArrayList<Vertex>();
        for (Point pt : a){
            graph.add(new Vertex(pt));
        }
        for (int i = 0; i < graph.size(); i++){
            for (int j = 0; j < i; j++){
                if (isVisible(graph.get(i).getPt(), graph.get(j).getPt())){
                    graph.get(i).addNeighbor(graph.get(j));
                    graph.get(j).addNeighbor(graph.get(i));
                }
            }
        }
        return graph;
    }

    public boolean isVisible(Point p1, Point p2){
        boolean out = true;
        grownObstacles.add(world);
        for (Obstacle obstacle : grownObstacles){
            Point temp1 = null;
            Point temp2 = null;
            int x = Math.abs(obstacle.getVerticies().indexOf(p2) - obstacle.getVerticies().indexOf(p1));
            if (obstacle.getVerticies().contains(p1) && obstacle.getVerticies().contains(p2) && ((x == 1) || (x == obstacle.getVerticies().size() - 1))) {
                for(int i = 0; i < world.getVerticies().size() - 1; i++){
                    if(doesIntersect(p1, p2, world.getVerticies().get(i), 
                    world.getVerticies().get(i + 1))){
                        return false;
                    }
                }
                if(doesIntersect(p1, p2, world.getVerticies().get(0), 
                    world.getVerticies().get(world.getVerticies().size() - 1))){
                        return false;
                }
                return true;
            }
            if (obstacle.getVerticies().contains(p1) && obstacle.getVerticies().contains(p2)){
                return false;
            }
            if (obstacle.getVerticies().contains(p1)){
                temp2 = p2.add(p1.mult(-1)).mult(100).add(p1);
            }
            if (obstacle.getVerticies().contains(p2)){
                temp1 = p1.add(p2.mult(-1)).mult(100).add(p2);
            }
            temp1 = (temp1 == null) ? p1 : temp1;
            temp2 = (temp2 == null) ? p2 : temp2;
            for(int i = 0; i < obstacle.getVerticies().size() - 1; i++){
                out = out && !doesIntersect(temp1, temp2, obstacle.getVerticies().get(i), 
                    obstacle.getVerticies().get(i + 1));
            }
            out = out && !doesIntersect(temp1, temp2, obstacle.getVerticies().get(0), 
                obstacle.getVerticies().get(obstacle.getVerticies().size() - 1));
        }       
        grownObstacles.remove(world);
        return out;
    }

    public boolean doesIntersect(Point p1, Point p2, Point p3, Point p4){
            if (p1.equals(p3) || p2.equals(p3) || p1.equals(p4) || p2.equals(p4))
                return false;

            if ((orientation(p1,p2,p4) != orientation(p1,p2,p3)) && (orientation(p3,p4,p1) != orientation(p3,p4,p2))){
                 return true;
            }

            if ((orientation(p1,p2,p4) == 0 && orientation(p1,p2,p3) == 0 
                && orientation(p3,p4,p1) == 0 && orientation(p3,p4,p2) == 0)){
                if (onSeg(p1,p2,p3) || onSeg(p1,p2,p4)){
                 return true;
                }  
            }
            return false;
    }

    public int orientation(Point p1, Point p2, Point p3){
        double cp = ((p2.getX() - p1.getX()) * (p3.getY() - p1.getY()))  -  ((p2.getY() - p1.getY()) * (p3.getX()- p1.getX()));
        if (cp == 0)
            return 0;
        if (cp > 0)
            return 1;
        else 
            return 2;
    }

    public boolean onSeg(Point p1, Point p2, Point p3){
        if((p3.getX() >= Math.min(p1.getX(),p2.getX())) && p3.getX() <= (Math.max(p1.getX(),p2.getX())))
            return true;
        return false;
    }

    public ArrayList<Vertex> dijkstras(ArrayList<Vertex> graph, Vertex from, Vertex to) {
        reset(graph);

        PriorityQueue<Vertex> vQ = new PriorityQueue<Vertex>();

        from.setMinDist(0.0);
        vQ.add(from);
 
        while (!vQ.isEmpty()) {
            Vertex v1 = vQ.poll();

            for (Vertex v2 : v1.getAdjList()) {
                double dist = v1.getPt().findDist(v2.getPt());

                double distanceThrough1 = v1.getMinDist() + dist;

                if (distanceThrough1 < v2.getMinDist()) {
                    vQ.remove(v2);
                    v2.setMinDist(distanceThrough1);
                    v2.setUpdatedBy(v1);
                    vQ.add(v2);
                }
            }
        }

        ArrayList<Vertex> path = new ArrayList<Vertex>();
        for (Vertex v = to; v != null; v = v.getUpdatedBy()) {
            path.add(v);
        }

        Collections.reverse(path);

        return path;
    }

    public void reset(ArrayList<Vertex> graph) {
        for (Vertex v : graph) {
            v.setMinDist(Double.POSITIVE_INFINITY);
            v.setUpdatedBy(null);
        }
    }

    public void printPathToFile(String filename, ArrayList<Vertex> path){
        try {
            PrintWriter writer = new PrintWriter(filename);
            for (Vertex v : path){
                writer.println(v.getPt().getX() + " " + v.getPt().getY());
            }
            writer.close();
        }
        catch(FileNotFoundException e) {
            System.err.println("FileNotFoundException");
        }  
    }

    public void insertionSort(ArrayList<Point> a)
    {
    	int left = 0;
    	int right = a.size();
        for( int p = left + 1; p < right; p++ )
        {
            Point tmp = a.get(p);
            int j;

            for( j = p; j > left && tmp.compareTo( a.get(j - 1 ) ) < 0; j-- )
                a.set(j, a.get( j - 1 ));
            a.set(j, tmp);
        }
    }

    /**
     * FillRect only takes int, so need to scale up (by 50) for accuracy 
     * Add 300 to make origin in the center of the panel
     */
    public int dataToMapCoord(double value) {
    	return (int)(value * SCALE_FACTOR + 350);
    }

    public double mapToDataCoord(int value) {
    	return ((double)value - 350) / SCALE_FACTOR;
    }
}