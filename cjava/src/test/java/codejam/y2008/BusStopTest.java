package codejam.y2008;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import codejam.utils.datastructures.BitSetInt;
import codejam.y2008.round_emea.bus_stops.Main;

import com.google.common.base.Preconditions;

public class BusStopTest {

    
    public void testBruteForce() {
      int[] start = new int[] { 1,1,1,0 };
      int[] current = Arrays.copyOf(start, 4);
      int[] end = new int[] {0, 1,1,1 };
      
      int c = Main.countBruteForce(start, end, current, 3);
      Preconditions.checkState(c == 1);
      
      start = new int[] { 1,1,1,0,0,0,0,0 };
      current = Arrays.copyOf(start, start.length);
      end = new int[] {0,0,0,0,0, 1,1,1 };
      
      c = Main.countBruteForce(start, end, current, 3);
      Preconditions.checkState(c == 1);
      
      c = Main.countBruteForce(start, end, current, 4);
      Preconditions.checkState(c == 13);
      
      c = Main.countBruteForce(start, end, current, 5);
      Preconditions.checkState(c == 31);
      
      start = new int[] { 1,1,1,0,0,0,0,0,0,0,0,0,0 };
      current = Arrays.copyOf(start, start.length);
      end = new int[] {0,0,0,0,0,0,0,0,0,0, 1,1,1 };
      
      c = Main.countBruteForce(start, end, current, 10);
      assertEquals(12355, c);
      
      start = new int[] { 1,1,1,1,1,0,0,0,0,0,0,0,0,0,0 };
      current = Arrays.copyOf(start, start.length);
      end = new int[] {0,0,0,0,0,0,0,0,0,0,1,1,1,1,1 };
      
      c = Main.countBruteForce(start, end, current, 10);
      assertEquals(14576, c % 30031);
      
      start = new int[] { 1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0 };
      current = Arrays.copyOf(start, start.length);
      end = new int[] {0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1 };
      
      c = Main.countBruteForce(start, end, current, 10);
      assertEquals(12893, c % 30031);
    }
    
    @Test
    public void test() {
        
        int mod = 30031;
        int[] start;
        int[] current;
        int[] end;
        int c, r;
        
        BitSetInt startState = new BitSetInt();
        startState.set(0);
        startState.set(1);
        int[] results = Main.count(startState.getBits(),4,2,4,mod);
        
        BitSetInt endState = new BitSetInt();
        endState.set(2);
        endState.set(3);
        
        r = results[endState.getBits()];
        assertEquals(2, r);
        
        results = Main.count(startState.getBits(),5,2,5,mod);
        
        endState = new BitSetInt();
        endState.set(3);
        endState.set(4);
        
        r = results[endState.getBits()];
        assertEquals(4, r);
        
        start = new int[] { 1,1,0,0, 0,0,0,0 };
        current = Arrays.copyOf(start, start.length);
        end = new int[] {0,0,0,0, 0,0,1,1 };
        
        c = Main.countBruteForce(start,end,current,4);
        
        startState = new BitSetInt();
        startState.set(0);
        startState.set(1);
        results = Main.count(startState.getBits(),8,2,4,mod);
        
        endState = new BitSetInt();
        endState.set(7);
        endState.set(6);
        
        r = results[endState.getBits()];
        
        assertEquals(c,r);
        
        startState = new BitSetInt();
        startState.set(0);
        startState.set(1);
        startState.set(2);
        results = Main.count(startState.getBits(),13,3,10,mod);
        
        endState = new BitSetInt();
        endState.set(12);
        endState.set(11);
        endState.set(10);
        
        r = results[endState.getBits()];
        
        assertEquals(12355, r);
        
        results = Main.count(startState.getBits(),15,3,10,mod);
        
        endState = new BitSetInt();
        endState.set(14);
        endState.set(13);
        endState.set(12);
        
        r = results[endState.getBits()];
        
        assertEquals(16779, r);
        
        results = Main.count(startState.getBits(),17,3,10,mod);
        
        endState = new BitSetInt();
        endState.set(16);
        endState.set(15);
        endState.set(14);
        
        r = results[endState.getBits()];
        
        assertEquals(24610, r);
        
        startState = new BitSetInt();
        startState.set(0);
        startState.set(1);
        startState.set(2);
        startState.set(3);
        startState.set(4);
        
        results = Main.count(startState.getBits(),15,5,10,mod);
        
        endState = new BitSetInt();
        endState.set(14);
        endState.set(13);
        endState.set(12);
        endState.set(11);
        endState.set(10);
        
        r = results[endState.getBits()];
        
        assertEquals(14576, r);
        
        startState = new BitSetInt();
        startState.set(0);
        startState.set(1);
        startState.set(2);
        startState.set(3);
        startState.set(4);
        startState.set(5);
        startState.set(6);
        
        results = Main.count(startState.getBits(),17,7,10,mod);
        
        endState = new BitSetInt();
        for(int i = 16; i >= 10; --i)
            endState.set(i);
        
        r = results[endState.getBits()];
        
        assertEquals(12893, r);
        
    }

}
