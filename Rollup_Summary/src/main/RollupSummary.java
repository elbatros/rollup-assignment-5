package main;

import java.util.ArrayList;
import java.util.Scanner;

import core.RollupSummarizer;


/**
 * The entry point for the Rollup_Summary application
 * 
 * @author Dylan Jacobs
 */
public final class RollupSummary 
{   
    /**
     * The column delimiter character expected in the input data
     */
    private static final String COLUMN_DELIMITER = "\t";

    
    /**
     * Read lines from standard input and store each line in a list.  Stop when
     * an empty line is encountered, or when standard input has no more data. <p>
     * 
     * PRECONDITION: At least two lines must be read
     * 
     * @return The lines read from standard input
     * 
     * @throws RuntimeException 
     * If an error occurred while reading input data, or if fewer than two lines
     * were read
     */
    private static final ArrayList<String> readInputData()
    {        
        ArrayList<String> lines = new ArrayList<String>();
        
        try (Scanner scanner = new Scanner(System.in))
        {           
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                
                if (line.isEmpty())
                {
                    break;
                }
                
                lines.add(line);
            }
        }
        
        catch (Throwable t)
        {
            throw new RuntimeException(
                "An error occurred while reading the input data.  Exiting...",
                t);
        }
        
        if (lines.size() < 2)
        {
            throw new RuntimeException(
                "The input data must contain at least two lines.  Exiting...");
        }
        
        return lines;
    }

    
    /**
     * The RollupSummary entry point
     * 
     * @param args
     * The column names that should be used for grouping, in grouping order
     */
    public static void main(String[] args) 
    {
        ArrayList<String> inputDataLines = readInputData();

        RollupSummarizer rollupSummarizer = new RollupSummarizer();
        
        String rollupSummary =
            rollupSummarizer.summarize(inputDataLines, COLUMN_DELIMITER, args);
        
        System.out.println(rollupSummary);
    }
    
    
    /**
     * Prevent instantiation
     */
    private RollupSummary()
    {}
}
