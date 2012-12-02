package com.eric.codejam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.codejam.InputData;
import com.eric.codejam.Main;
import com.eric.codejam.geometry.PointInt;
import com.eric.codejam.main.Runner;
import com.eric.codejam.multithread.Consumer.TestCaseHandler;
import com.eric.codejam.multithread.Producer.TestCaseInputReader;
import com.eric.codejam.utils.GridChar;
import com.google.common.base.Preconditions;


public class Main implements TestCaseHandler<InputData>, TestCaseInputReader<InputData> {

    final static Logger log = LoggerFactory.getLogger(Main.class);

    boolean testSize(int newSize, InputData input) {
        if (newSize < input.k) {
            return false;
        }
        for(int offsetRow = 0; offsetRow <= newSize-input.k; ++offsetRow) {
            for(int offsetCol = 0; offsetCol <= newSize - input.k; ++offsetCol) {
                GridChar grid = GridChar.buildEmptyGrid(newSize, newSize, '.');
                
                //copy input.grid
                for(int r = 0; r < input.grid.getRows(); ++r) {
                    for(int c = 0; c < input.grid.getCols(); ++c) {
                        grid.setEntry(r+offsetRow, c+offsetCol, input.grid.getEntry(r,c));
                    }
                }
                
                if (isPerfect(grid)) {
                    //log.debug("Grid {}", grid);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public String handleCase(int caseNumber, InputData input) {

        log.info("Starting calculating case {}", caseNumber);
        
        //log.debug("Grid {}", input.grid);
        //double ans = DivideConq.findMinPerimTriangle(input.points);

        /*
         * Max size is k + 2(k - 1).  See sample
         */
        
        int lowerBound = input.k;
        int upperBound = input.k + 2 * (input.k-1);
        
        
        //lb <= ans <= up
        while(upperBound > lowerBound) {
            int midPoint = (upperBound + lowerBound) / 2;
            log.debug("Mid point {} lb {} ub {}", midPoint, lowerBound, upperBound);
            //This is because we can have the situation where midPoint does not match but midPoint+1 does
            boolean hasMatchLower = testSize(midPoint - 1, input);
            boolean hasMatch = testSize(midPoint, input);
            boolean hasMatchUpper = testSize(midPoint + 1, input);
            if (hasMatchLower) {
                upperBound = midPoint - 1;
            } else if (hasMatch) {
                upperBound = midPoint;
            } else if (hasMatchUpper) {
                upperBound = midPoint+1;
                //Make sure it terminates
                if (upperBound - lowerBound <= 2) {
                    lowerBound = midPoint + 1;
                }
            } else if (!hasMatch && !hasMatchLower && !hasMatchUpper) {
                lowerBound = midPoint+2;
            }
        }
        
        Preconditions.checkState(upperBound == lowerBound);
        //test upperBound - 1 as a perfect diamond of size n does not fit in size n + 1
        
        int newSize = upperBound;
          
        int cost = newSize * newSize - input.k * input.k;
        return ("Case #" + caseNumber + ": " + cost);
        
        //log.debug("Is perfect {}", isPerfect(input.grid));
        
        //DecimalFormat decim = new DecimalFormat("0.00000000000");
        //decim.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        
        
    }
    
    boolean isPerfect(GridChar grid) {
        //r, c == c, r
        //r, c == (k-1) - c, (k-1) - r
        
        for(int r = 0; r < grid.getRows(); ++r) {
            for(int c = 0; c < grid.getCols(); ++c) {
                char ch = grid.getEntry(r, c);
                if (ch == '.')
                    continue;
                
                //Via 'horizontal' in the diamond symmetry 0,0 to r,c
                char chHorizontal =  grid.getEntry(c, r);
                char chVertial = grid.getEntry(grid.getCols() - 1- c, grid.getRows() - 1 - r);
                if ( (chHorizontal != '.' && ch != chHorizontal ) ||
                        (chVertial != '.' && ch != chVertial) ) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    
    
    @Override
    public InputData readInput(BufferedReader br, int testCase) throws IOException {
        
    
        InputData  input = new InputData(testCase);
        
        input.k = Integer.parseInt(br.readLine());
        
        //map to a grid.  
        GridChar grid = GridChar.buildEmptyGrid(input.k, input.k, '.');
        
        int startingRow = 0;
        for(int startingCol = input.k - 1; startingCol >= 0; --startingCol) {
            String[] line = br.readLine().trim().split("\\s+");
            for(int i = 0; i < line.length; ++i) {
                int row = startingRow + i;
                int col = startingCol + i;
                grid.setEntry(row, col, line[i].charAt(0));
            }
        }
        
        int startingCol = 0;
        for(startingRow = 1; startingRow < input.k; ++startingRow) {
            String[] line = br.readLine().trim().split("\\s+");
            for(int i = 0; i < line.length; ++i) {
                int row = startingRow + i;
                int col = startingCol + i;
                grid.setEntry(row, col, line[i].charAt(0));
            }
        }
        
        input.grid = grid;
        
        return input;
        
    }

    


    public Main() {
        super();
    }
    
    
    public static void main(String args[]) throws Exception {

        if (args.length < 1) {
           // args = new String[] { "sample.txt" };
        //    args = new String[] { "A-small-practice.in" };
            args = new String[] { "A-large-practice.in" };
         }
         log.info("Input file {}", args[0]);

         Main m = new Main();
         Runner.goSingleThread(args[0], m, m);
         //Runner.go(args[0], m, m, new InputData(-1));

        
       
    }

    
}