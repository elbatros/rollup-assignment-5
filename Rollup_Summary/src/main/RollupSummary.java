package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class RollupSummary {
    
    private static final String VALUE_COLUMN_NAME = "value";
    
    
    private static final void printWelcomeMessage()
    {
        System.out.println("Welcome to Rollup_Summary!");
    }
    
    
    private static final boolean matchesValueColumnName(String columnName)
    {
        return VALUE_COLUMN_NAME.equalsIgnoreCase(columnName);
    }
    
    
    // POSTCONDITIONS:
    private static final void validateGroupingColumnNames(
        String[] groupingColumnNames)
    {
        System.out.println();
        
        if (groupingColumnNames.length == 0)
        {
            System.out.println(
                "No grouping column name arguments provided.  " +
                "Grouping using all dimensional columns, in column order.");
            
            return;
        }
        
        HashSet<String> groupingColumnNamesSet = 
            new HashSet<String>(groupingColumnNames.length);
        
        for (int i = 0; i < groupingColumnNames.length; i++)
        {
            String groupingColumnName = groupingColumnNames[i];
            
            if (matchesValueColumnName(groupingColumnName))
            {
                throw new RuntimeException(
                    "Grouping column name arguments invalid: A grouping " +
                    "column name must not match the value column name (" +
                    VALUE_COLUMN_NAME +
                    ") (case-insensitive)");
            }
            
            if (! groupingColumnNamesSet.add(groupingColumnName))
            {
                throw new RuntimeException(
                    "Grouping column name arguments invalid: Duplicate " +
                    "grouping column name " +
                    groupingColumnName +
                    " detected");
            }
        }
        
        System.out.println(
            "Grouping using the following dimensional columns and ordering:");
        
        for (int i = 0; i < groupingColumnNames.length; i++)   
        {
            System.out.println(groupingColumnNames[i]);
        }
    }
    
    
    private static final ArrayList<String> readInputData()
    {
        System.out.println();
        System.out.println("Reading input data...");
        
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
                "An error occurred while reading input data.  Exiting...",
                t);
        }
        
        return lines;
    }
    
    
    private static final void printInputData(ArrayList<String> inputDataLines)
    {
        System.out.println();
        System.out.println("Input Data:");
        System.out.println();
        
        for (int i = 0; i < inputDataLines.size(); i++)
        {
            System.out.println(inputDataLines.get(i));
        }
    }
    
    
    /**
     * Get and validate the input data's column names from the column names 
     * header line <p>
     * 
     * PRECONDITIONS: <br>
     * 1) columnNamesLine contains at least two space-delimited column names <br>
     * 2) The last column name matches {@value #VALUE_COLUMN_NAME} (case-insensitive) <br>
     * 3) Exactly one column name matches {@value #VALUE_COLUMN_NAME} (case-insensitive) <br>
     * 4) All column names are unique
     * 
     * @param columnNamesLine - The column names header line from the input data
     * 
     * @return The validated column names
     * 
     * @throws RuntimeException If any of the above preconditions are not met
     */
    private static final String[] getAndValidateColumnNames(String columnNamesLine)
    {
        String[] columnNames = columnNamesLine.split(" ");
        int numDimensionalColumns = columnNames.length - 1;
        
        if (columnNames.length < 2)
        {
            throw new RuntimeException(
                "Input data column names invalid: At least two " +
                "space-separated column names must be provided");
        }
        
        if (! matchesValueColumnName(columnNames[numDimensionalColumns])) 
        {
            throw new RuntimeException(
                "Input data column names invalid: The last column must be " +
                "named " +
                VALUE_COLUMN_NAME + 
                " (case-insensitive)");
        }
        
        for (int i = 0; i < numDimensionalColumns; i++)
        {
            if (matchesValueColumnName(columnNames[i]))
            {
                throw new RuntimeException(
                    "Input data column names invalid: Only the last column " +
                    "can be named " +
                    VALUE_COLUMN_NAME + 
                    " (case-insensitive)");
            }
        }
        
        HashSet<String> dimensionalColumnNamesSet = 
            new HashSet<String>(numDimensionalColumns);
        
        for (int i = 0; i < numDimensionalColumns; i++)
        {
            String dimensionalColumnName = columnNames[i];
            
            if (! dimensionalColumnNamesSet.add(dimensionalColumnName))
            {
                throw new RuntimeException(
                    "Input data column names invalid: Duplicate column name " +
                    dimensionalColumnName +
                    " detected");
            }
        }
        
        return columnNames;
    }
    
    
    // PRECONDITIONS: 
    private static final int[] getColumnIndicesSortedForGrouping(
        String[] columnNames, 
        String[] groupingColumnNames)
    {
        int numDimensionalColumns = columnNames.length - 1;
        
        if (groupingColumnNames.length == 0)
        {
            // Sort by all dimensional columns, in the order that they appear
            
            int[] columnIndices = new int[numDimensionalColumns];
            
            for (int i = 0; i < columnIndices.length; i++)
            {
                columnIndices[i] = i;
            }
            
            return columnIndices;
        }
        
        HashMap<String, Integer> columnName_Index_Map = 
            new HashMap<String, Integer>(numDimensionalColumns);
        
        for (int i = 0; i < numDimensionalColumns; i++)
        {
            columnName_Index_Map.put(columnNames[i], i);
        }
        
        int[] columnIndices = new int[groupingColumnNames.length];
        
        for (int i = 0; i < groupingColumnNames.length; i++)
        {
            String groupingColumnName = groupingColumnNames[i];
            
            Integer columnIndex = columnName_Index_Map.get(groupingColumnName);
            
            if (columnIndex == null)
            {
                throw new RuntimeException(
                    "Grouping column name " + 
                    groupingColumnName +
                    " does not match any dimensional column in the input data");
            }
            
            columnIndices[i] = columnIndex;
        }
        
        return columnIndices;
    }
    
    
    public static void main(String[] args) {
        
        printWelcomeMessage();
        
        String[] groupingColumnNames = args;
        validateGroupingColumnNames(groupingColumnNames);
        
        ArrayList<String> inputDataLines = readInputData();
        
        if (inputDataLines.isEmpty())
        {
            throw new RuntimeException(
                "No input data was provided via standard input.  Exiting...");
        }
        
        // TODO: Remove
        printInputData(inputDataLines);

        String columnNamesLine = inputDataLines.get(0);
        String[] columnNames = getAndValidateColumnNames(columnNamesLine);
        
        int[] columnIndicesSorted = 
            getColumnIndicesSortedForGrouping(columnNames, groupingColumnNames);
        
        int numDimensionalColumns = columnNames.length - 1;
        
        // TODO
    }
}
