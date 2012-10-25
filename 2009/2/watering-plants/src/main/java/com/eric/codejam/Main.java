package com.eric.codejam;

import java.io.File;
import java.io.PrintStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class Main {


	int iterations ;
	
	int n;
	List<Circle> plants;
	
	Map<Circle, List<Integer>> plantsCovered = new HashMap<>();
	
	public double bruteForce() {
		
		plantsCovered = new HashMap<>();
		
		//all pairs
		Iterator<Long> it = new CombinationsIterator(n, 2);
		
		while(it.hasNext()) {
			final Long combin = it.next();
			
			List<Circle> chosenCircles = new ArrayList<>();
			for(int chosen = 0; chosen < n; ++chosen) {				
				if ((1 << chosen & combin) != 0) {
					chosenCircles.add(plants.get(chosen));
				}
			}
			
			Preconditions.checkState(chosenCircles.size() == 2);
			
			Circle sprinkler = Circle.getCircleContaining(chosenCircles.get(0), chosenCircles.get(1));
			
			List<Integer> plantsInside = new ArrayList<>();
			
			for(int plant = 0; plant < n; ++plant) {
				if (sprinkler.contains(plants.get(plant))) {
					plantsInside.add(plant);
				}
			}
			
			Preconditions.checkState(plantsInside.size() >= 2);
			plantsCovered.put(sprinkler, plantsInside);
		}
		
		
		double minRadius = Double.MAX_VALUE;
		
		for(Circle s1 : plantsCovered.keySet() ) {
			
			
			if (plantsCovered.get(s1).size() >= n - 1) {
				minRadius = Math.min(s1.getR(), minRadius);
				continue;
			}
			
			for(Circle s2 : plantsCovered.keySet() ) {
				Set<Integer> plants = new HashSet<>();
				plants.addAll(plantsCovered.get(s1));
				plants.addAll(plantsCovered.get(s2));
				if (plants.size() < n) {
					continue;
				}
				double r = Math.max(s1.getR(), s2.getR());
				minRadius = Math.min(r, minRadius);
			}
		}
		
		return minRadius;
	}
	
	/*
	 * Line from center through plant circle.  Find furthest point(s).
	 * 
	 * If 1, move center closer to that point
	 * If 2 or 3, if 180 then done, otherwise move towards them both (line between 2 more extreme points, halfway)
	 * 
	 * Make a triangle, if centre circle inside, then the points are not on the same side
	 */
	private void findFurthestIntersectionPoints() {
		
	}
	
	/*
	 * Take center, find most extreme point away from this center
	 */
	private void intersectionPointCircle() {
		
	}

	public static void handleCase(int caseNumber, Scanner scanner,
			PrintStream os) {

		Main m = Main.buildMain(scanner);

		double min = m.bruteForce();

		log.info("Starting case {}", caseNumber);
		
		DecimalFormat df = new DecimalFormat("0.######");
		df.setRoundingMode(RoundingMode.HALF_UP);

		os.println("Case #" + caseNumber + ": " + df.format(min));

		log.info("Finished Starting case {}.  Iterations {}", caseNumber,
				m.iterations);
	}

	private static Main buildMain(Scanner scanner) {
		Main m = new Main(scanner.nextInt());


		for(int i = 0; i < m.n; ++i) {
			m.plants.add(new Circle(scanner.nextInt(),scanner.nextInt(),scanner.nextInt()));
		}

		return m;
	}

	public Main(int n) {
		super();

		this.n = n;
		
		this.plants = new ArrayList<>();

	}

	final static Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String args[]) throws Exception {

		if (args.length < 1) {
			args = new String[] { "sample.txt" };
		}
		log.info("Input file {}", args[0]);

		Scanner scanner = new Scanner(new File(args[0]));

		
		// OutputStream os = new FileOutputStream("output.txt");

		// PrintStream pos = new PrintStream(os);

		int t = scanner.nextInt();

		for (int i = 1; i <= t; ++i) {

			handleCase(i, scanner, System.out);

		}

		scanner.close();
	}
}