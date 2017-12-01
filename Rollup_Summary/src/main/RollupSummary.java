package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import core.Row;
import core.RowComparator;


public class RollupSummary {
    
    private static final String COLUMN_DELIMITER = "\t";
    
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
    
    
    // POSTCONDITIONS: 
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
    private static final int[] getGroupingColumnIndices(
        String[] columnNames, 
        String[] groupingColumnNames)
    {
        int numDimensionalColumns = columnNames.length - 1;
        
        if (groupingColumnNames.length == 0)
        {
            // Sort by all dimensional columns, in the order that they appear
            
            int[] groupingColumnIndices = new int[numDimensionalColumns];
            
            for (int i = 0; i < numDimensionalColumns; i++)
            {
                groupingColumnIndices[i] = i;
            }
            
            return groupingColumnIndices;
        }
        
        HashMap<String, Integer> columnName_Index_Map = 
            new HashMap<String, Integer>(numDimensionalColumns);
        
        for (int i = 0; i < numDimensionalColumns; i++)
        {
            columnName_Index_Map.put(columnNames[i], i);
        }
        
        int[] groupingColumnIndices = new int[groupingColumnNames.length];
        
        for (int i = 0; i < groupingColumnNames.length; i++)
        {
            String groupingColumnName = groupingColumnNames[i];
            
            Integer groupingColumnIndex = 
                columnName_Index_Map.get(groupingColumnName);
            
            if (groupingColumnIndex == null)
            {
                throw new RuntimeException(
                    "Grouping column name " + 
                    groupingColumnName +
                    " does not match any dimensional column in the input data");
            }
            
            groupingColumnIndices[i] = groupingColumnIndex;
        }
        
        return groupingColumnIndices;
    }
    
    
    // PRECONDITIONS:
    // POSTCONDITIONS:
    private static final Row[] getRows(
        ArrayList<String> inputDataLines,
        int expectedNumColumns)
    {
        int numInputDataLines = inputDataLines.size();
        
        ArrayList<Row> rows = new ArrayList<Row>(numInputDataLines);
        
        for (int i = 0; i < numInputDataLines; i++)
        {
            String line = inputDataLines.get(i);
            
            String[] values = line.split(COLUMN_DELIMITER);
            
            if (values.length != expectedNumColumns)
            {
                throw new RuntimeException(
                    "Input data invalid: Row " +
                    i +
                    " does not contain " +
                    expectedNumColumns +
                    " columns");
            }
            
            double value;
            
            try
            {
                value = Double.parseDouble(values[values.length - 1]);
            }
            
            catch (NumberFormatException e)
            {
                throw new RuntimeException(
                    "Input data invalid: The value in the value column of row " +
                    i +
                    " cannot be parsed to a number (double)",
                    e);
            }
            
            String[] dimensionalColumnValues =
                Arrays.copyOf(values, values.length - 1);
            
            Row row = new Row(dimensionalColumnValues, value);
            
            rows.add(row);
        }
        
        return rows.toArray(new Row[rows.size()]);
    }
    
    
    // PRECONDITIONS:
    private static final Row[] sortRows(
        Row[] rows,
        int[] columnIndicesSortedForGrouping)
    {
        RowComparator rowComparator = 
            new RowComparator(columnIndicesSortedForGrouping);
        
        Row[] rowsSorted = Arrays.copyOf(rows, rows.length);
        
        Arrays.sort(rowsSorted, rowComparator);
        
        return rowsSorted;
    }
    
    
    private static final void printRollupSummaryColumnNames(
        String[] groupingColumnNames, 
        String valueColumnName)
    {
        int numGroupingColumns = groupingColumnNames.length;
        
        StringBuilder stringBuilder = 
            new StringBuilder((numGroupingColumns + 1) * 20);
        
        for (int i = 0; i < numGroupingColumns; i++)
        {
            stringBuilder.append(groupingColumnNames[i]);
            stringBuilder.append(" ");
        }
        
        stringBuilder.append(valueColumnName);
        
        System.out.println(stringBuilder.toString());
    }
    
    
    // PRECONDITIONS:
    private static final void printRollupSummary(
        Row[] rowsSorted,
        int[] groupingColumnIndices)
    {      
        Row previousRow = rowsSorted[0];
        
        int numGroupingColumns = groupingColumnIndices.length;
        
        double[] groupSums = new double[numGroupingColumns];
        Arrays.fill(groupSums, previousRow.getValue());
        
        double total = previousRow.getValue();
        
        // For each sorted row, not including the first
        for (int rowIndex = 1; rowIndex < rowsSorted.length; rowIndex++)
        {
            Row currentRow = rowsSorted[rowIndex];
            double currentRowValue = currentRow.getValue();
            
            total += currentRowValue;
            
            // For each grouping column
            for (int groupingColumnIndex = 0; 
                 groupingColumnIndex < numGroupingColumns; 
                 groupingColumnIndex++)
            {
                int columnIndex = groupingColumnIndices[groupingColumnIndex];
                
                String previousColumnValue = 
                    previousRow.getDimensionalColumnValue(columnIndex);
                
                String currentColumnValue = 
                    currentRow.getDimensionalColumnValue(columnIndex);
                
                /* If grouping column values up to and including the current
                 * grouping column have not changed from the previous row  */
                if (currentColumnValue.equals(previousColumnValue))
                {
                    // Increment the sum for this group
                    groupSums[groupingColumnIndex] = 
                        groupSums[groupingColumnIndex] + currentRowValue;
                }
                
                /* If the value of the current grouping column has changed from 
                 * the previous row  */
                else
                {
                    // Summarize the group(s) that ended on the previous row
                    summarizeGroups(
                        previousRow,
                        groupingColumnIndices, 
                        groupingColumnIndex, 
                        groupSums);
                    
                    /* Reset the sum for the current grouping column and all
                     * subsequent grouping columns, and add the value of the
                     * current row  */
                    for (int i = groupingColumnIndex; 
                         i < numGroupingColumns; 
                         i++)
                    {
                        groupSums[i] = currentRowValue;
                    }
                    
                    break;
                }
            }
            
            previousRow = currentRow;
        }
        
        // Summarize the group(s) that ended on the last row
        summarizeGroups(
            previousRow,
            groupingColumnIndices, 
            0, 
            groupSums);
        
        // Print the total value
        
        StringBuilder stringBuilder = 
            new StringBuilder(numGroupingColumns + 100);
        
        for (int i = 0; i < numGroupingColumns; i++)
        {
            stringBuilder.append('\t');
        }
        
        stringBuilder.append(total);
        
        System.out.println(stringBuilder.toString());
    }
    
    
    public static final void summarizeGroups(
        Row row,
        int[] groupingColumnIndices, 
        int valueChangedIndex, 
        double[] groupSums)
    {
        int numGroupingColumns = groupingColumnIndices.length;
        
        // row's grouping column values, in grouping order
        String[] groupingColumnValues = new String[numGroupingColumns];
        
        for (int i = 0; i < numGroupingColumns; i++)
        {
            groupingColumnValues[i] =
                row.getDimensionalColumnValue(groupingColumnIndices[i]);
        }
        
        // For each group that just ended (from largest to smallest)
        for (int i = numGroupingColumns - 1; i >= valueChangedIndex; i--)
        {
            StringBuilder stringBuilder = new StringBuilder();
            
            for (int j = 0; j <= i; j++)
            {
                stringBuilder.append(groupingColumnValues[j]);
                stringBuilder.append('\t');
            }
            
            // Add tab characters for aggregated grouping columns
            for (int j = i + 1; 
                 j < numGroupingColumns; 
                 j++)
            {
                stringBuilder.append('\t');
            }
            
            // Add the group sum (aggregated value)
            stringBuilder.append(groupSums[i]);
            
            // Print the group summary
            System.out.println(stringBuilder.toString());
        }
    }
    
    
    public static void main(String[] args) {
        
        printWelcomeMessage();
        
        String[] groupingColumnNames = args;
        validateGroupingColumnNames(groupingColumnNames);
        
        ArrayList<String> inputDataLines = readInputData();

        String columnNamesLine = inputDataLines.remove(0);
        String[] columnNames = getAndValidateColumnNames(columnNamesLine);
        
        int[] groupingColumnIndices = 
            getGroupingColumnIndices(columnNames, groupingColumnNames);
        
        Row[] rows = getRows(inputDataLines, columnNames.length);
        Row[] rowsSorted = sortRows(rows, groupingColumnIndices);
        
        printRollupSummaryColumnNames(groupingColumnNames, VALUE_COLUMN_NAME);
        
        printRollupSummary(
            rowsSorted,
            groupingColumnIndices);
    }
}
