package pkr.history;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.TestPreproc;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class Preprocessor {
    private static Pattern patHandBoundary = Pattern.compile("_*");
    private final static Pattern COMMENT =
            Pattern.compile("//.*");
    private final static Pattern CUT_PASTE_BOUNDARY = Pattern.compile("\\*\\*");
    
    private static Logger log = LoggerFactory.getLogger(TestPreproc.class);
    
    public static void clean(File inputFile, File outputFile) throws IOException
    {
        List<String> inputLines = Files.readLines(inputFile, Charsets.UTF_8);
        List<String> outputLines = Lists.newArrayList();
        
        clean(inputLines, outputLines);
        
        outputFile.delete();
        boolean ok = outputFile.createNewFile();
        
        Preconditions.checkState(ok);
        
        BufferedWriter os = new BufferedWriter(new FileWriter(outputFile, false));
        
        for(String line : outputLines)
        {
            os.write(line);
            os.write("\n");
        }
        
        os.close();
    }
    
    private static class Block
    {
        List<String> lines;
        
        List<Integer> handStarts;
        
        private boolean startFound;
        
        Block()
        {
            lines = Lists.newArrayList();
            handStarts = Lists.newArrayList();
        }
        
        boolean containsSomething()
        {
            return handStarts.size() > 0;
        }
        
        void addLine(String line)
        {
            Matcher m = patHandBoundary.matcher(line);
            
            if (!m.matches())
            {
                if (startFound)
                {
                    lines.add(line);
                    return;
                }
                    
            } else {    
            
                if (!startFound)
                {
                    startFound = true;
                } else {
                    
                    
                }
                
                lines.add(line);
                handStarts.add(lines.size() - 1);
            }
        }
        
        void cleanUnfinished()
        {
            int lastIndex = handStarts.get(handStarts.size() - 1);
            
            for(int i = lines.size() - 1; i > lastIndex; --i)
            {
                lines.remove(i);
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Block [lines=" + Joiner.on("\n").join(lines) + ", handStarts=" + handStarts + "]";
        }
        
        
    }
    
    private static void addNextBlock(List<Block> blocks, Block newBlock)
    {
        newBlock.cleanUnfinished();
        log.debug("Adding block {}", newBlock);
        
        blocks.add(newBlock);
        
    }
    private static int getNextBlock(List<Block> blocks, int currentLine, List<String> inputLines)
    {
        int curLineIdx = currentLine;
        
        Block newBlock = new Block();
        
        while(true)
        {
            if (curLineIdx >= inputLines.size())
            {
                if (newBlock.containsSomething())
                {
                    addNextBlock(blocks, newBlock);
                    return curLineIdx;
                } else {
                    return -1;
                }
            }
            String curLine = inputLines.get(curLineIdx);
            log.debug("Processing line {} of {}.  [{}]", curLineIdx, inputLines.size(), curLine);
            ++curLineIdx;
            
            Matcher m = CUT_PASTE_BOUNDARY.matcher(curLine);
            
            if (m.matches())
            {
                addNextBlock(blocks, newBlock);
                return curLineIdx;
            }            
            
            newBlock.addLine(curLine);
            
            
            
        }
    }
    
    private static void cleanBlock( Block block)
    {
        
    }
    
    private static void removeRedundant( Block blockPrev, Block blockNext)
    {
        
    }
    
    public static void clean(List<String> inputLines, List<String> outputLines)
    {
        List<Block> blocks = Lists.newArrayList();
        
        int currentLine = 0;
        
        while( (currentLine = getNextBlock(blocks, currentLine, inputLines)) >= 0)
        {
            cleanBlock( blocks.get(blocks.size() - 1));
        }
        
        //int startCurrentBlock = -1;
        for(Block block : blocks)
        {
            outputLines.addAll(block.lines);
        }
    }
}
