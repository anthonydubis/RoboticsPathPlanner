import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.*;
import java.util.*;

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
		//pp.setLayout(null);

		// Add points panel to frame
		//this.getContentPane().add(pp, BorderLayout.CENTER);
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

    public PointsPanel(Point st, Point gl, Obstacle wd, ArrayList<Obstacle> obs) {
		setBackground(getBackground());
        setForeground(Color.black);
        setPreferredSize(new Dimension(1000, 700));
        start = st;
    	goal = gl;
        world = wd;
    	obstacles = obs;
    }

    public void paintComponent(Graphics g) {
        g.setColor(getBackground()); //colors the window
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground()); //set color and fonts
        drawBorders(g, world.getVerticies());
        drawObstacles(g);
        drawStartGoal(g);
        revalidate();
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

    public void drawObstacles(Graphics g) {
    	for (int a = 0; a < obstacles.size(); a++) {
    		drawBorders(g, obstacles.get(a).getVerticies());
    	}
    }

    public void drawBorders(Graphics g, ArrayList<Point> vertices) {
    	for (int i = 0; i < vertices.size()-1; i++) {
        	Point a = vertices.get(i);
        	int x1 = dataToMapCoord(a.getX());
			int y1 = dataToMapCoord(a.getY());

			Point b = vertices.get(i+1);
			int x2 = dataToMapCoord(b.getX());
			int y2 = dataToMapCoord(b.getY());

			// Connect the first point to the last point
    		if (i == vertices.size()-2) {
    			Point c = vertices.get(0);
    			int x3 = dataToMapCoord(c.getX());
				int y3 = dataToMapCoord(c.getY());
				g.drawLine(y3, x3, y2, x2);
    		}

			// Switching x and y so it can fit the screen better
            g.setColor(Color.blue);
            g.drawLine(y1, x1, y2, x2);
        }
    }

    /**
     * FillRect only takes int, so need to scale up (by 50) for accuracy 
     * Add 300 to make origin in the center of the panel
     */
    public int dataToMapCoord(double value){
    	return (int)(value*50 + 300);
    }
}
