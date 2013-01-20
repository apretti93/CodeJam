package codejam.y2008.round_amer.mixing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codejam.utils.datastructures.GraphInt;
import codejam.utils.datastructures.TreeInt;
import codejam.utils.main.DefaultInputFiles;
import codejam.utils.main.Runner.TestCaseInputScanner;
import codejam.utils.multithread.Consumer.TestCaseHandler;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

public class Main implements TestCaseHandler<InputData>, TestCaseInputScanner<InputData>, DefaultInputFiles {

    @Override
    public String[] getDefaultInputFiles() {
        return new String[] { "A-small-practice.in", "A-large-practice.in" };
    }
    @Override
    public InputData readInput(Scanner scanner, int testCase) {
        int N = scanner.nextInt();
        InputData input = new InputData(testCase);
        input.ingredients = new LinkedHashMap<>();
        
        for(int i = 0; i < N; ++i) {
            String mixture = scanner.next();
            int M = scanner.nextInt();
            List<String> ingredients = new ArrayList<>(M);
            for(int m = 0; m < M; ++m) {
                ingredients.add(scanner.next());
            }
            input.ingredients.put(mixture,ingredients);
        }
        return input;
    }
    
    private int getNum(String mixtureName, Map<String, Integer> strIndex) {
        Integer idx = strIndex.get(mixtureName);
        if (idx == null) {
            idx = strIndex.size()+1;
            strIndex.put(mixtureName, idx);
        }
        return idx;
    }

    @Override
    public String handleCase(InputData input) {
        Map<String, Integer> strIndex = new HashMap<>();
        
        GraphInt graph = new GraphInt();
        
        for(String mixture : input.ingredients.keySet()) {
            List<String> ingredients = input.ingredients.get(mixture);
            
            /**
             * Assign a number to each mixture if necesary
             * and return it
             */
            int mixNum = getNum(mixture, strIndex);
            
            for(String ingredient : ingredients) {
                //Not a mixture
                if (ingredient.matches("[a-z]*")) {
                    continue;
                }
                
                int ingNum = getNum(ingredient, strIndex);
                /**
                 * Create a connection in the graph
                 */
                graph.addConnection(mixNum,ingNum);
            }
            
        }
        
        //The first node is the mixture we are making
        if (graph.getNeighbors(1) == null || graph.getNeighbors(1).isEmpty()) {
            return String.format("Case #%d: %d", input.testCase, 1);
        }
        
        TreeInt<Integer> tree = graph.convertToTree(1);
        
        //Do a child first traversal
        Stack<Integer> toVisit = new Stack<>();
        Set<Integer> visited = new HashSet<>();
        
        
        toVisit.add(1);
        
        
        while(!toVisit.isEmpty()) {
            Integer nodeInt = toVisit.peek();
            TreeInt<Integer>.Node node = tree.getNodes().get(nodeInt);
            
            /**
             * Base case, we know we need 1 bowl
             */
            if (node.getChildren().isEmpty()) {
                node.setData(1);
                visited.add(nodeInt);
                toVisit.pop();
                continue;
            }
            
            /**
             * Make sure children have been visited first
             */
            Iterator<TreeInt<Integer>.Node> childIt = node.getChildren().iterator(); 
            TreeInt<Integer>.Node child = childIt.next(); 
            if (!visited.contains(child.getId())) {
                //Add all children to stack
                toVisit.add(child.getId());
                while(childIt.hasNext()) {
                    child = childIt.next();
                    toVisit.add(child.getId());
                }
                continue;
            }
            
            //All children should be visited
            List<Integer> bowlsNeeded = new ArrayList<>();
            bowlsNeeded.add(child.getData());
            while(childIt.hasNext()) {
                child = childIt.next();
                bowlsNeeded.add(child.getData());
            }
            
            Collections.sort(bowlsNeeded, Ordering.natural().reverse());
            
            //Minimum is all the pre-req mixtures + 1 to add this ingredient            
            int bowlsUsed = 1+bowlsNeeded.size();
            //Mix most demanding ingredient first
            for(int ing = 0; ing < bowlsNeeded.size(); ++ing) {
                Preconditions.checkState(bowlsNeeded.get(ing) > 0);
                //To mix this ingredient, we need ing bowls to stock prereqs ingredients already mixed + 
                //the temporary need
                int usedBowls = ing + bowlsNeeded.get(ing);
                bowlsUsed = Math.max(usedBowls, bowlsUsed);
            }
            
            Preconditions.checkState(node.getData() == null);
            node.setData(bowlsUsed);
            
            visited.add(nodeInt);
            toVisit.pop();
        }
        
        
        return String.format("Case #%d: %d", input.testCase, tree.getRoot().getData());
    }

    final static Logger log = LoggerFactory.getLogger(Main.class);

}
