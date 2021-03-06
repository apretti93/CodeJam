package com.eric.codejam;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.codejam.geometry.PointInt;
import com.eric.codejam.main.Runner;
import com.eric.codejam.multithread.Consumer.TestCaseHandler;
import com.eric.codejam.multithread.Producer.TestCaseInputReader;

public class Main implements TestCaseHandler<InputData>, TestCaseInputReader<InputData>{

    final static Logger log = LoggerFactory.getLogger(Main.class);

    @Override
    public String handleCase(int caseNumber, InputData input) {

        
        
        log.info("Starting calculating case {}", caseNumber);
        
        double ans = DivideConq.findMinPerimTriangle(input.points);

        log.info("Done calculating answer case {}", caseNumber);
        
        DecimalFormat decim = new DecimalFormat("0.00000000000");
        decim.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        
        return ("Case #" + caseNumber + ": " + decim.format(ans));
                

    }
    
    
    @Override
    public InputData readInput(BufferedReader br, int testCase) throws IOException {
        
    
        String line = br.readLine();
        int n = Integer.parseInt(line);
        
        List<PointInt> points = new ArrayList<>(n);
        
        log.info("Reading data...Test case # {} ", testCase);
        Pattern split = Pattern.compile(" ");
        for (int i = 0; i < n; ++i) {
            String[] strArray = split.split(br.readLine());
            
            int x = Integer.parseInt(strArray[0]);
            int y = Integer.parseInt(strArray[1]);
            points.add(new PointInt(x,y));
        }
        log.info("Done Reading data...Test case # {} ", testCase);
        
        InputData  i = new InputData(testCase);
        i.points = points;
        return i;
        
    }

    


    public Main() {
        super();
    }
    
    
    public static void main(String args[]) throws Exception {

        if (args.length < 1) {
           args = new String[] { "sample.txt" };
           //args = new String[] { "smallInput.txt" };
           //args = new String[] { "largeInput.txt" };
        }
        log.info("Input file {}", args[0]);

        Main m = new Main();
        Runner.go(args[0], m, m, new InputData(-1));
        
       
    }
}