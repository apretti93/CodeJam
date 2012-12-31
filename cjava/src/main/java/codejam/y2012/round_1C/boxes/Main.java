package codejam.y2012.round_1C.boxes;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codejam.utils.main.DefaultInputFiles;
import codejam.utils.main.Runner.TestCaseInputScanner;
import codejam.utils.multithread.Consumer.TestCaseHandler;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

public class Main implements TestCaseHandler<InputData>, TestCaseInputScanner<InputData>, DefaultInputFiles {

    final static Logger log = LoggerFactory.getLogger(Main.class);

    @Override
    public String[] getDefaultInputFiles() {
         return new String[] {"sample.in"};
      // return new String[] { "A-small-practice.in", "A-large-practice.in" };
    }

    @Override
    public InputData readInput(Scanner scanner, int testCase) {

        InputData in = new InputData(testCase);
        in.N = scanner.nextInt();
        in.M = scanner.nextInt();
        
        in.a = Lists.newArrayList();
        in.b = Lists.newArrayList();
        
        for(int n = 0; n < in.N; ++n) {
            in.a.add(new ImmutablePair<>(scanner.nextLong(), scanner.nextInt()));
            
        }
        
        for(int m = 0; m < in.M; ++m) {
            in.b.add(new ImmutablePair<>(scanner.nextLong(), scanner.nextInt()));
        }

        return in;
    }

    public int[][] bruteForce(InputData in) {
        List<Integer> aExpanded = Lists.newArrayList();
        
        for(int n = 0; n < in.N; ++n) {
            Pair<Long,Integer> pair = in.a.get(n);
            for(long i = 0; i < pair.getLeft(); ++i) {
                aExpanded.add(pair.getRight());
            }
        }
        
        List<Integer> bExpanded = Lists.newArrayList();
        for(int m = 0; m < in.M; ++m) {
            Pair<Long,Integer> pair = in.b.get(m);
            for(long i = 0; i < pair.getLeft(); ++i) {
                bExpanded.add(pair.getRight());
            }
        }
        
        
        int m = aExpanded.size();
        int n = bExpanded.size();
        
        int[][] lcs = new int[m][n];
        
        for(int i= 0; i < m; ++i) {
            
            for(int j = 0; j < n; ++j) {
                
                if (aExpanded.get(i) == bExpanded.get(j)) {
                    if (i==0 || j == 0) {
                        lcs[i][j] = 1;
                    } else {
                        lcs[i][j] = 1 + lcs[i-1][j-1];
                    }
                    continue;
                } 
                
                int max = 0;
                
                if (i > 0 && lcs[i-1][j] > max) {
                    max = lcs[i-1][j];
                } 
                if (j > 0 && lcs[i][j-1] > max){
                    max = lcs[i][j-1];
                } 
                
                lcs[i][j] = max;
            }
        }
        
        return lcs;

    }
    
    
    private static class Block {
        /**
         * Where the block would start in the LCS array
         */
        long startingIndex;
        
        /**
         * How many items
         */
        long count;
        
        /**
         * Toy / Box type
         */
        int type;

        public Block(long startingIndex, long count, int type) {
            super();
            this.startingIndex = startingIndex;
            this.count = count;
            this.type = type;
        }

        @Override
        public String toString() {
            return "Block [startingIndex=" + startingIndex + ", count=" + count + ", type=" + type + "]";
        }
    }
    
    /**
     * 
     * @param indexType
     * @param maxIndex
     * @param currentIndex
     * @return pair.  count of ==> type
     */
    static Block getBlock(TreeMap<Long,Integer> indexType, long maxIndex, long currentIndex ) {
        //Get next highest value
        Map.Entry<Long,Integer> nextEntry = indexType.ceilingEntry(currentIndex+1);
        Map.Entry<Long,Integer> currentEntry = indexType.floorEntry(currentIndex);
        
        // 1 x 10 ; 2 x 20 ; 3 x 30 
        //0 -> 1  ; 10 -> 2 ; 30 -> 3
        if (nextEntry == null) {
            return new Block(currentIndex, maxIndex - currentIndex, currentEntry.getValue());            
        } else {
            return new Block(currentIndex, nextEntry.getKey() - currentIndex, currentEntry.getValue());            
        }
    }
    
//    private static long getBlockStartCount(TreeMap<Long,Long> prevRow, Block blockOfB) {
//        long prevCountLastRow = getMaxBeforeIndex(prevRow, blockOfB.startingIndex);
//        
//        return prevCountLastRow;
//    }
    
    /**
     * 
     * @param row index ==> count
     * @param blockStartIndex
     * @param blockEndIndex
     * @param blockStartCount
     * @param nextBlockRowIndex
     * @param bruteForce
     */
    private static void updateRow(List<UpdateRowInfo> row,  UpdateRowInfo ri, 
            long nextBlockRowIndex, int[][] bruteForce) {
        
        long blockLength = ri.stopIndex - ri.startIndex + 1;
        long blockEndCount = ri.stopIndexCount;
        
        row.add(ri);
        
        long checkRowIndex = nextBlockRowIndex ;
        
        log.debug("Update current block row. adding row info {}", 
                ri
                );
        
        log.debug("Brute force checks at row index {}. \nStart index {} should be {}, end index {} should be {}",
                checkRowIndex,
                ri.startIndex, 
                bruteForce[(int)checkRowIndex][(int) ri.startIndex-1],
                ri.stopIndex,
                bruteForce[(int)checkRowIndex][(int) ri.stopIndex-1]
                );
        Preconditions.checkState(bruteForce[(int)checkRowIndex][(int) ri.startIndex-1] == (int)ri.startIndexCount);            
        Preconditions.checkState(bruteForce[(int)checkRowIndex][(int) ri.stopIndex-1] == (int)ri.stopIndexCount);
        
    }
    
    
    static class UpdateRowInfo implements Comparable<UpdateRowInfo> {
        final long startIndex;
        final long startIndexCount;
        final long stopIndex;
        final long stopIndexCount;
        
        public UpdateRowInfo(long startIndex, long startIndexCount, long stopIndex, long stopIndexCount) {
            super();
            
            if(startIndex == 0)
                startIndex = 1;
            
            if  (startIndexCount == 0)
                startIndexCount = 1;
            
            this.startIndex = startIndex;
            this.startIndexCount = startIndexCount;
            this.stopIndex = stopIndex;
            this.stopIndexCount = stopIndexCount;
            
            Preconditions.checkArgument(stopIndex - startIndex == stopIndexCount - startIndexCount);
            Preconditions.checkArgument(stopIndex >= startIndex);
            Preconditions.checkArgument(startIndex >= 0);
        }
        
        public int compareTo(UpdateRowInfo o) {

            return ComparisonChain.start()
                    .compare(startIndex, o.startIndex)
                    .compare(stopIndex, o.stopIndex)
                    .compare(startIndexCount, o.startIndexCount)
                    .compare(stopIndexCount, o.stopIndexCount)
                    .result();
        }

        @Override
        public String toString() {
            return "Uri [ idx=" + startIndex + " cnt=" + startIndexCount + 
                    ", stopIdx=" + stopIndex + " cnt="
                    + stopIndexCount + "]\n";
        }
       
    }
    
    void addToUpList(UpdateRowInfo point, List<UpdateRowInfo> listSoFar) {
        listSoFar.add(point);
    }
    
    void checkList(List<UpdateRowInfo> list) {
        Collections.sort(list, new Comparator<UpdateRowInfo>() {

            @Override
            public int compare(UpdateRowInfo o1, UpdateRowInfo o2) {
                
                //Put indexes first and lower count first
                return ComparisonChain.start().compare(o1.startIndex, o2.startIndex)
                        .compare(o2.startIndexCount, o1.startIndexCount).result()
                        ;
            }
            
        });
        
        log.debug("List to add {}", list);
        
        ListIterator<UpdateRowInfo> it = list.listIterator();
        
        UpdateRowInfo prev = it.next();
        while(it.hasNext()) {
            UpdateRowInfo next = it.next();
            
            if ( (prev.startIndex - prev.startIndexCount >
            next.startIndex - next.startIndexCount) || prev.startIndexCount > next.startIndexCount) {
                log.debug("Discarding point {}", next);
                it.remove();
            } else {
                prev = next;
            }
        }
    }
    
    
    /*
    private static UpdateRowInfo getMidBlockStart(TreeMap<Long,Long> prevRow, 
            long maxLength, Block blockOfB) {
        //Find defined points in the block, we are looking for a point that 
        //means the count is no longer increasing.  We dont need to worry
        //about points outside because either it means the #'s are constant or they
        //are increasing.  Both mean that we cannot improve further by matching mid block.
        
        //-1 since if increasing stops at end of block, we cannot improve
        NavigableSet<Long> keys = prevRow.subMap(blockOfB.startingIndex, 
                true, blockOfB.startingIndex + blockOfB.count - 1, false).navigableKeySet();
        
        //Should be at most 2 key, when increasing starts at front of block (possible) and 1 
        //when the increasing stops
        Preconditions.checkState(keys.size() <= 2);
        
        if (keys.isEmpty())
            return null;
        
        long index = keys.last() + 1;
        
        Preconditions.checkState(index < blockOfB.startingIndex+blockOfB.count);
        
        long count = prevRow.get(keys.last()) + 1;
            
        long stopIndex = Math.min(blockOfB.startingIndex+blockOfB.count - 1, index + maxLength - 1);
        
        UpdateRowInfo uri = new UpdateRowInfo(index, stopIndex, count);
        
        return uri;
    }*/
    
    /**
     * 
     * @return index --> count
     */
    private static List<UpdateRowInfo> getMaxBegEnd(List<UpdateRowInfo> prevRow,
            Block blockOfA, Block blockOfB) {
        
        List<UpdateRowInfo> ret = Lists.newArrayList();
        
        /*
         * Find value at start of B block and matching end of A block to end of B block
         * 
         *  if A block is larger, then we will make the entire block increasing
         *  
         *  if A block is smaller, find max value matching A end to B end
         */
        
        
        //Intialize with -1
        long countBeginning =  getMaxBeforeIndex(prevRow, blockOfB.startingIndex);
        
                     
        if (blockOfA.count >= blockOfB.count) {
            long countEnd = countBeginning + blockOfB.count ;
            long endIndex = blockOfB.startingIndex + blockOfB.count ;
            
            ret.add( new UpdateRowInfo(blockOfB.startingIndex, countBeginning, endIndex, countEnd) );            
            return ret;
        }


        
        
        {
            //blockA is smaller, so we add this point
            long startIndex = blockOfB.startingIndex-1;
            long endIndex = blockOfB.startingIndex + blockOfA.count - 1;
            long countEnd = countBeginning + blockOfA.count; 
            if (getMaxBeforeIndex(prevRow, endIndex + 1) < countEnd) {
                ret.add(new UpdateRowInfo(startIndex, countBeginning, 
                endIndex, 
                countEnd
                ));
            }
        }
        
        //find any keys between start of block inclusive and end of block index exclusive
        
        for(int riIdx = 0; riIdx < prevRow.size(); ++riIdx) {
            UpdateRowInfo ri = prevRow.get(riIdx);
            
            if (ri.stopIndex >= blockOfB.startingIndex && ri.stopIndex < blockOfB.startingIndex + blockOfB.count - 1) {
                long startIndex = ri.stopIndex;
                long countAtStart = ri.stopIndexCount;
            
                long stopIndex = Math.min(blockOfB.startingIndex + blockOfB.count - 1, 
                        startIndex + blockOfA.count);
                
                long countEnd = countAtStart + stopIndex - startIndex;
                ret.add( new UpdateRowInfo(startIndex, countAtStart, stopIndex, countEnd) );
                return ret;
            }
        }
        
        
        

        
        return ret;
    }
    
    
    private static UpdateRowInfo getMaxBegEndCurRow(List<UpdateRowInfo> prevRow,
            List<UpdateRowInfo> curRow,
            Block blockOfA, Block blockOfB, Block lastBlockOfB) {
        
        /*
         * Find value at start of B block and matching end of A block to end of B block
         * 
         *  if A block is larger, then we will make the entire block increasing
         *  
         *  if A block is smaller, find max value matching A end to B end
         */
        
        //TODO length used is wrong
                
        //Intialize with -1
        
        long countPrevRow = getMaxBeforeIndex(prevRow, blockOfB.startingIndex);
        long countCurRow = getMaxBeforeIndex(curRow, blockOfB.startingIndex);
        
        if (countCurRow <= countPrevRow) {
            return null;
        }
        
        long maxEndLastBlockPrev = getMaxBeforeIndex(prevRow, lastBlockOfB.startingIndex+ lastBlockOfB.count);
        long maxEndLastBlockCur = getMaxBeforeIndex(curRow, lastBlockOfB.startingIndex+ lastBlockOfB.count);
        
        
        
        long lengthUsed = maxEndLastBlockCur - maxEndLastBlockPrev;
        
        long aBlockLengthRemaining = blockOfA.count - lengthUsed;
        
        
        long startIndex = blockOfB.startingIndex-1;
        long countStart = countCurRow;
        
        long countEnd  = -1;
        if (aBlockLengthRemaining >= blockOfB.count) {
            countEnd = countStart + blockOfB.count - 1;            
        } else {        
            countEnd = countStart + aBlockLengthRemaining;            
        }
        
        return  new UpdateRowInfo( startIndex, countStart, 
                startIndex + countEnd - countStart, countEnd);
        
    }
     
    
    /**
     * 
     * @param row index -> count
     */
    static void removeAndMergeRedundantEntries(List<UpdateRowInfo> row) {
        log.debug("Cleaning / merging row {}", row);
        //slope between 2 points should alternate between flat and increasing 1 by 1
        
        
        Collections.sort(row);

        if (row.size() <= 1)
            return;
        
        ListIterator<UpdateRowInfo> li = row.listIterator(1);
        
        
        while(li.hasNext()) {

            UpdateRowInfo riPrev = row.get(li.previousIndex());
            UpdateRowInfo riCurrent = li.next();
           
            
            Preconditions.checkState(riCurrent.startIndexCount <= riPrev.stopIndexCount);
            
            if (riCurrent.stopIndexCount <= riPrev.stopIndexCount) {
                log.debug("Removing {} ", riCurrent);
                li.remove();
                continue;
            }
         
            if (riCurrent.startIndexCount < riPrev.stopIndexCount) {
                long diff = riPrev.stopIndexCount - riCurrent.startIndexCount;
                
                
                UpdateRowInfo replaceCur = new UpdateRowInfo(riCurrent.startIndex+diff, 
                        riCurrent.startIndexCount+diff, 
                        riCurrent.stopIndex, riCurrent.stopIndexCount);
                
                log.debug("Sliding {} to {}", riCurrent, replaceCur);
                
                if (replaceCur.startIndex == riPrev.stopIndex) {
                    UpdateRowInfo replacePrev = new UpdateRowInfo(riPrev.startIndex,
                            riPrev.startIndexCount,
                            replaceCur.stopIndex, replaceCur.stopIndexCount);
                    li.previous();
                    li.previous();
                    li.set(replacePrev);
                    
                    //Remove current as well
                    li.next();
                    li.next();
                    li.remove();                    
                    
                    continue;
                }
                
                li.set(replaceCur);
            }
            
        }
    }    
    
    //Index -> count
    static void processPrevRow(List<UpdateRowInfo> prevBlockRow, List<UpdateRowInfo> blockRow, int[][] bruteForce, long rowIndex) {
        
        
        log.debug("\n\nProcess prev row {}  current row {}\n\n", prevBlockRow, blockRow);
        
        prevBlockRow.addAll(blockRow);
        
        removeAndMergeRedundantEntries(prevBlockRow);
        
        
    }
    
    static List<Block> getMatchingBlocksOfB(long bMaxIndex, TreeMap<Long,Integer> bIndexType, Block blockOfA) {
        
        List<Block> blocksOfB = Lists.newArrayList();
        
        long currentColIndex = 1;
        while(currentColIndex < bMaxIndex ) {
            Block blockOfB = getBlock(bIndexType, bMaxIndex, currentColIndex);
           // log.debug("bPair count {} type {}  currentColIndex {}", bPair.getLeft(), bPair.getRight(), currentColIndex);
            
            if (blockOfB.type == blockOfA.type) {
                blocksOfB.add(blockOfB);
            }
            
            currentColIndex += blockOfB.count;
        }
        
        return blocksOfB;
    }
    
    
    static long getMaxBeforeIndex(List<UpdateRowInfo> row, long colIndex) {
        
        colIndex --;
        
        if (colIndex == 0)
            return 0;
        
        for(int i = 0; i < row.size(); ++i) {
            UpdateRowInfo ri = row.get(i);
            
            if (ri.startIndex <= colIndex && ri.stopIndex >= colIndex ) {
                //falls within
                return ri.startIndexCount + (colIndex - ri.startIndex);
            }
            
            if (ri.startIndex > colIndex) {
                //since we are going in order and this ri starts after the index
                return ri.startIndexCount;
            }
        }
        
        //Col index must be after all the ri
        if (!row.isEmpty()) {
            return row.get(row.size() - 1).stopIndexCount;
        }
        
        return 0;
    }
    
    /**
     * 
     * @param row index --> count
     * @param currentRowIndex
     * @return
     */
    static long getMaxBeforeIndexOld(TreeMap<Long,Long> row, long currentRowIndex) {
        Map.Entry<Long,Long> indexCount  = row.floorEntry(currentRowIndex - 1);
        Map.Entry<Long,Long> nextIndexCount = row.ceilingEntry(currentRowIndex);
        
        //If the next value is present, use it
        //say we want 5 and the row has 3 -> 5  10 - > 12, return value 7
        long max  = 0;
        
        if (nextIndexCount != null ) {
            long diffIndexes = nextIndexCount.getKey() - currentRowIndex;
            if (nextIndexCount.getValue() >= diffIndexes) { 
                // minus one because we want the value before current index
                max = nextIndexCount.getValue() - diffIndexes - 1;
            }
        }
        
        if (indexCount != null) {
            //Cannot be less than previous value either
            max = Math.max(max, indexCount.getValue());
        }
        
        return max;
    }
    
    private long[] buildIndexTypeArrays(TreeMap<Long,Integer> aIndexType, TreeMap<Long,Integer> bIndexType, InputData in) {
long currentColIndex = 1;
        
        for(int n = 0; n < in.N; ++n) {
            Pair<Long,Integer> pair = in.a.get(n);
            aIndexType.put(currentColIndex, pair.getValue());
                
            currentColIndex += pair.getLeft();
        }
        
        long aMaxIndex = currentColIndex;
        
        
        currentColIndex = 1;
        for(int m = 0; m < in.M; ++m) {
            Pair<Long,Integer> pair = in.b.get(m);
            bIndexType.put(currentColIndex, pair.getValue());
                
            currentColIndex += pair.getLeft();
        }
        long bMaxIndex = currentColIndex;
        
        return new long[] {aMaxIndex, bMaxIndex};
    }
    
    private List<UpdateRowInfo> buildFirstBlockRow(InputData in, TreeMap<Long,Integer> bIndexType, long bMaxIndex, int[][] bruteForce) {
      //Initialize first row
        List<UpdateRowInfo> blockRow = Lists.newArrayList();
        
        Pair<Long,Integer> aPair = in.a.get(0); //getPair(aIndexType, aMaxIndex, 0);
        long currentColIndex = 1;
        long aPairRemaining = aPair.getKey();
        
        long currentRowIndex = aPair.getKey() ;
        
        
        while(currentColIndex < bMaxIndex && aPairRemaining > 0) {
            Block bPair = getBlock(bIndexType, bMaxIndex, currentColIndex);
            
            if (bPair.type != aPair.getValue()) {
                currentColIndex += bPair.count;
                continue;
            }
            
            long overlap = Math.min(aPairRemaining, bPair.count);
            
            long prevCount = getMaxBeforeIndex(blockRow, currentColIndex);
            
            long blockStartCount = prevCount + 1;
            long blockEndCount = prevCount + overlap;
            long blockStartIndex = currentColIndex;
            long blockEndIndex = currentColIndex + overlap - 1;
            
            blockRow.add(new UpdateRowInfo(blockStartIndex, blockStartCount, blockEndIndex, blockEndCount));
            
            Preconditions.checkState(bruteForce[(int)currentRowIndex][(int)currentColIndex-1] == (int)prevCount + 1);            
            Preconditions.checkState(bruteForce[(int)currentRowIndex][(int) blockEndIndex-1] == (int)blockEndCount);
                        
            aPairRemaining -= overlap;
            currentColIndex += overlap;
        }
        
        return blockRow;
    }
    
    
    public String handleCase(InputData in) {

        int[][] bruteForce = bruteForce(in);

        // Count --> type
        TreeMap<Long, Integer> aIndexType = new TreeMap<>();
        TreeMap<Long, Integer> bIndexType = new TreeMap<>();

        long[] maxes = buildIndexTypeArrays(aIndexType, bIndexType, in);
        
        log.debug("A index ==> type {}", aIndexType);
        
        log.debug("B index ==> type {}", bIndexType);

        long aMaxIndex = maxes[0];
        long bMaxIndex = maxes[1];

        // Index --> Count
        List<UpdateRowInfo> blockRow = buildFirstBlockRow(in, bIndexType, bMaxIndex, bruteForce);

        // Index in bruteForceArray corresponding to when blockOfA has been fully processed
        long endBlockOfARowIndex = in.a.get(0).getLeft() - 1;

        log.debug("After first row {}", blockRow);

        List<UpdateRowInfo> prevBlockRow = blockRow;
        blockRow = Lists.newArrayList();

        // Go through each A block
        for (int i = 1; i < in.N; ++i) {

            Block blockOfA = getBlock(aIndexType, aMaxIndex, endBlockOfARowIndex+2);
            endBlockOfARowIndex += blockOfA.count;

            //TreeMap<Long, Long> prevRow = new TreeMap<Long, Long>();

            log.debug("\nProcessing block A {} i {} end at lcs row index {}\n ", blockOfA, i, 
                    endBlockOfARowIndex);

            List<Block> matchingBlocks = getMatchingBlocksOfB(bMaxIndex, bIndexType, blockOfA);


            for (int mbIndex = 0; mbIndex < matchingBlocks.size(); ++mbIndex) {

                Block blockOfB = matchingBlocks.get(mbIndex);
                
                log.debug("Matching block B {}", blockOfB);

                
                //Build 2 ranges.  Best match previous row and best match current row

                //List<UpdateRowInfo> maxBegEnd =
                        
                List<UpdateRowInfo> maxBegEnd = getMaxBegEnd(prevBlockRow, blockOfA, blockOfB);

                UpdateRowInfo maxBegEndCurRow =  null;
                if (mbIndex > 0) {
                 maxBegEndCurRow = getMaxBegEndCurRow
                        (prevBlockRow, blockRow, blockOfA, blockOfB, matchingBlocks.get(mbIndex-1));
                }
                
                
                //Preconditions.checkState(maxBegEnd[0].compareTo(maxBegEnd[1]) == 0);
                
                //Preconditions.checkState(maxBegEndCurRow[0].compareTo(maxBegEndCurRow[1]) == 0);
                
                List<UpdateRowInfo> list = Lists.newArrayList();
                
                list.addAll(maxBegEnd);
                
                
                if (maxBegEndCurRow != null) {
                    list.addAll(Arrays.asList(maxBegEndCurRow));
                }
                
                
                
                removeAndMergeRedundantEntries(list);
                
                
                
                for (UpdateRowInfo ui : list) {

                    updateRow(blockRow, ui, 
                            endBlockOfARowIndex, bruteForce);

                }

            }

            log.info("Row {} ", blockRow);
            // Index --> count

            processPrevRow(prevBlockRow, blockRow, bruteForce, endBlockOfARowIndex);
            
            
            log.info("\nNew prev row {}\n", prevBlockRow);
            blockRow = Lists.newArrayList();
        }

        long max = prevBlockRow.isEmpty() ? 0 : prevBlockRow.get(prevBlockRow.size() - 1).stopIndexCount;

        long maxBF = bruteForce[bruteForce.length - 1][bruteForce[0].length - 1];

        Preconditions.checkState(max == maxBF);
        return String.format("Case #%d: No", in.testCase);
    }

}
