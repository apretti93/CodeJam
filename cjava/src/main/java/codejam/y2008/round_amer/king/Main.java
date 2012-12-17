package codejam.y2008.round_amer.king;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codejam.utils.datastructures.ArticulationPoint;
import codejam.utils.datastructures.Bridge;
import codejam.utils.datastructures.GraphInt;
import codejam.utils.datastructures.TreeInt;
import codejam.utils.datastructures.TreeInt.Node;
import codejam.utils.main.DefaultInputFiles;
import codejam.utils.main.Runner.TestCaseInputScanner;
import codejam.utils.multithread.Consumer.TestCaseHandler;
import codejam.utils.utils.Direction;
import codejam.utils.utils.GridChar;
import codejam.y2008.KingTest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class Main implements TestCaseHandler<InputData>,
        TestCaseInputScanner<InputData>, DefaultInputFiles {

    final static Logger log = LoggerFactory.getLogger(Main.class);
    
    @Override
    public String[] getDefaultInputFiles() {
        return new String[] { "sample.in"};
       // return new String[] { "D-small-practice.in" };
       // return new String[] { "D-large-practice.in" };
        //return new String[] { "B-small-practice.in", "B-large-practice.in" };
    }

    @Override
    public InputData readInput(Scanner scanner, int testCase) {
        InputData input = new InputData(testCase);
        input.row = scanner.nextInt();
        input.col = scanner.nextInt();
        GridChar grid = GridChar.buildFromScanner(scanner,input.row,input.col, '#');
        input.grid = grid;
        
        return input;
    }
    
    public static boolean skipDebug = false;
    
    
    
    public Integer getOpenNodeNextToKing(GridChar grid, int loc) {
        for(Direction dir : Direction.values()) {
            Integer childIdx = grid.getIndex(loc,dir);
            if (childIdx == null)
                continue;
            
            char sq = grid.getEntry(childIdx);
            
            if (sq == '.') {
                return childIdx;
            }
        }
        
        return null;
    }
    
    public boolean reduceGrid(GridChar grid, final int kingLoc) {
        //Create a graph corresponding to grid
        GraphInt graph = new GraphInt();
        
        int startingLoc = kingLoc; //getOpenNodeNextToKing(grid, kingLoc);
        
        Set<Integer> visitedNodes = Sets.newHashSet();
        
        LinkedList<Integer> toVisit = new LinkedList<>();
        toVisit.add(startingLoc);
        
        while(!toVisit.isEmpty()) {
            
            Integer loc = toVisit.poll();
            
            if (visitedNodes.contains(loc))
                continue;
            
            visitedNodes.add(loc);
                        
            for(Direction dir : Direction.values()) {
                Integer childIdx = grid.getIndex(loc,dir);
                if (childIdx == null)
                    continue;
                
                char sq = grid.getEntry(childIdx);
                
                if (sq == '#' || sq == 'K' || sq == 'V' || sq == 'T')
                    continue;
                
                if (loc == kingLoc)
                    //graph.addOneWayConnection(loc,childIdx);
                    graph.addConnection(loc, childIdx);
                else
                    graph.addConnection(loc, childIdx);
                
                toVisit.add(childIdx);
            }
        }
        
        ArticulationPoint ap = new ArticulationPoint(graph);
        //Find bridges
        List<Integer> artPoints = ap.getArticulationPoints();
        
        
        
        for(Integer aPoint : artPoints) {

            if (aPoint == kingLoc)
                continue;
            Set<Integer> adjNodes = graph.getNeighbors(aPoint);
            
            Set<Integer> isolatedNodes = Sets.newHashSet();
            Set<Integer> nonIsolatedNodes = Sets.newHashSet();
            
            adjNodeLoop:
            for(Integer adjNode : adjNodes) {
                if (isolatedNodes.contains(adjNode) || nonIsolatedNodes.contains(adjNode))
                    continue;
                
                Set<Integer> nodes = graph.getConnectedNodesWithoutNode(adjNode, aPoint);
                
                //Isolated set must not contain other articulation points
                for(Integer aPointToTest : artPoints) {
                    if (nodes.contains(aPointToTest)) {
                        nonIsolatedNodes.addAll(nodes);
                        continue adjNodeLoop;
                    }
                }
                
                if (!nodes.contains(startingLoc)) {
                    isolatedNodes = nodes;
                    break;
                }
            }
            
            //No suitable isolated set found, continue to next articulation point
            if (isolatedNodes == null || isolatedNodes.isEmpty())
                continue;
            
            if (isolatedNodes.size() % 2 == 0) {
                //All the isolated nodes are traps
                for(Integer isoNode : isolatedNodes) {
                    grid.setEntry(isoNode, 'T');
                }
                return true;
            } else {
                //The articulation point itself is a trap, as moving to it means
                //the other player can move into an odd numbered field, which is always
                //losing
                grid.setEntry(aPoint, 'T');
                return true;
            }
        }
        
        
        
        return false;

    }
    
    public int getConnectedSquareCount(GridChar grid, int startingLoc) {

        Set<Integer> visitedNodes = Sets.newHashSet();
        
        LinkedList<Integer> toVisit = new LinkedList<>();
        toVisit.add(startingLoc);
        
        while(!toVisit.isEmpty()) {
            
            Integer loc = toVisit.poll();
            
            if (visitedNodes.contains(loc))
                continue;
            
            visitedNodes.add(loc);
                        
            for(Direction dir : Direction.values()) {
                Integer childIdx = grid.getIndex(loc,dir);
                if (childIdx == null)
                    continue;
                
                char sq = grid.getEntry(childIdx);
                
                if (sq == '#' || sq == 'K' || sq == 'T')
                    continue;
                
                toVisit.add(childIdx);
            }
        }

        return visitedNodes.size();
    }
    
    //Returns true if B wins
    public boolean tryFirstMove(GridChar grid, int aKingLoc, int bKingLoc) {
        
        grid.setEntry(aKingLoc,  '#');
        grid.setEntry(bKingLoc, 'K');
        
        boolean r = true;
        while(r) {
            //r = findLoserSquares(input.grid);
            r = reduceGrid(grid, bKingLoc);
        }
        
        for(Direction dir : Direction.values()) {
            Integer childIdx = grid.getIndex(bKingLoc,dir);
            if (childIdx == null)
                continue;
            
            char sq = grid.getEntry(childIdx);
            
            if (sq == 'V') {
                return true;
            }
            
            if (sq == '#' || sq == 'T') {
                continue;
            }
            
            int size = 1 + getConnectedSquareCount(grid, childIdx);
            
            if (size % 2 == 0) {
                return true;
            }
    
        }
        
        
        return false;

    }
    public String awinsIfEven(InputData input) {
        Set<Integer> kingLocs = input.grid.getIndexesOf('K');
        int kingLoc = kingLocs.iterator().next();
        
        boolean r = true;
        
        GridChar gridOrig = new GridChar(input.grid);
        
      //Further reduce the grid in case B can win after A's first move
        for(Direction dir : Direction.values()) {
            Integer childIdx = input.grid.getIndex(kingLoc,dir);
            if (childIdx == null)
                continue;
            
            char sq = input.grid.getEntry(childIdx);
            
            if (sq != '.') 
                continue;
        
            GridChar gridTry = new GridChar(gridOrig);
            
            if (tryFirstMove(gridTry, kingLoc, childIdx)) {
                input.grid.setEntry(childIdx, 'T');
            } else {
                return String.format("Case #%d: %s", input.testCase, "A" );
            }
            
        }
        
        while(r) {
            //r = findLoserSquares(input.grid);
            r = reduceGrid(input.grid, kingLoc);
        }
        
        
        
                        
        for(Direction dir : Direction.values()) {
            Integer childIdx = input.grid.getIndex(kingLoc,dir);
            if (childIdx == null)
                continue;
            
            char sq = input.grid.getEntry(childIdx);
            
            if (sq == 'V') {
                return String.format("Case #%d: %s", input.testCase, "A" );
            }
            
            if (sq == '#' || sq == 'T') {
                continue;
            }
            
            int size = 1 + getConnectedSquareCount(input.grid, childIdx);
            
            if (size % 2 == 0) {
                return String.format("Case #%d: %s", input.testCase, "A" );
            }
    
        }
        
        
        return String.format("Case #%d: %s", input.testCase, "B");
        
    }

    @Override
    public String handleCase(InputData input) {
        return awinsIfEven(input) +
         bruteForce(input);
    }
    public String bruteForce(InputData input) {
        Set<Integer> kingLocs = input.grid.getIndexesOf('K');
        int kingLoc = kingLocs.iterator().next();
        
        TreeInt<Boolean> tree = new TreeInt<Boolean>(kingLoc);
        tree.setStats(true);
        tree.setUniqueNodeIds(false);
        
        PriorityQueue<TreeInt<Boolean>.Node> toVisit = new PriorityQueue<>(1000, new Comparator<TreeInt<Boolean>.Node>() {

            @Override
            public int compare(Node o1, Node o2) {
                return Integer.compare(o2.getDepth(), o1.getDepth());
            }
            
        });
        
        toVisit.add(tree.getRoot());
        
        int maxDepth = 0;
        
        while(!toVisit.isEmpty()) {
            
            TreeInt<Boolean>.Node thisNode = toVisit.poll();
            final int loc = thisNode.getId();
                        
            for(Direction dir : Direction.values()) {
                Integer childIdx = input.grid.getIndex(loc,dir);
                if (childIdx == null)
                    continue;
                
                char sq = input.grid.getEntry(childIdx);
                
                if (sq == '#')
                    continue;
                
                TreeInt<Boolean>.Node node = thisNode;
                boolean alreadyHitSq = false;
                
                while(node.getParent() != null) {
                    node = node.getParent();
                    if(node.getId() == childIdx) {
                        alreadyHitSq = true;
                        break;
                    }
                }
                
                if (alreadyHitSq)
                    continue;
                
                TreeInt<Boolean>.Node child = thisNode.addChild(childIdx);
                if (child.getDepth() > maxDepth) {
                    log.debug("Child height {}", child.getDepth() );
                    maxDepth = child.getDepth();
                }
                toVisit.add(child);
            }
        }
        
        //Now traverse
        Stack<TreeInt<Boolean>.Node> toVisitStack = new Stack<>();
        
        Set<TreeInt<Boolean>.Node> visited = Sets.newHashSet();
        
        toVisitStack.add(tree.getRoot());
        
        while(!toVisitStack.empty()) {
            TreeInt<Boolean>.Node node = toVisitStack.peek();
            
            if (node.getChildren().isEmpty()) {
                node.setData(false);
                visited.add(node);
                toVisitStack.pop();
                continue;
            }
            
            Iterator<TreeInt<Boolean>.Node> childIt = node.getChildren().iterator(); 
            TreeInt<Boolean>.Node child = childIt.next(); 
            if (!visited.contains(child)) {
                //Add all children to stack
                toVisitStack.add(child);
                while(childIt.hasNext()) {
                    child = childIt.next();
                    toVisitStack.add(child);
                }
                continue;
            }
            
            boolean isLoserNode = child.getData();
            while(childIt.hasNext()) {
                child = childIt.next();
                isLoserNode = isLoserNode && child.getData();
            }
            
            node.setData(!isLoserNode);
            visited.add(node);
            toVisitStack.pop();
        }
        

        //String resp = awinsIfEven(input);
        
        return String.format("Case #%d: %s", input.testCase, tree.getRoot().getData() ? "A" : "B");
    }

}
