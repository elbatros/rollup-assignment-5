package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Parses input data into a table and validates the table.  Validates provided 
 * grouping column names.  Given the provided grouping columns, computes and 
 * returns a ROLLUP summary view of the table.
 * 
 * @author Dylan Jacobs
 */    
public class RollupSummarizer
{
    /**
     * Get and validate the input data's column names from the column names 
     * header line <p>
     * 
     * PRECONDITIONS: <br>
     * 1) columnNamesLine contains at least two space-delimited column names <br>
     * 2) The last column name corresponds to the 'value' column <br>
     * 3) All column names are unique
     * 
     * @param columnNamesLine - The column names header line from the input data
     * 
     * @return The validated column names
     * 
     * @throws RuntimeException If any of the above preconditions are not met
     */
    protected static final String[] getAndValidateColumnNames(
        String columnNamesLine)
    {
        String[] columnNames = columnNamesLine.split(" ");
        int numColumnNames = columnNames.length;
        
        if (numColumnNames < 2)
        {
            throw new RuntimeException(
                "Input data column names invalid: At least two " +
                "space-separated column names must be provided");
        }
        
        HashSet<String> columnNamesSet = new HashSet<String>(numColumnNames);
        
        for (int i = 0; i < numColumnNames; i++)
        {
            String columnName = columnNames[i];
            
            if (! columnNamesSet.add(columnName))
            {
                throw new RuntimeException(
                    "Input data column names invalid: Duplicate column name " +
                    columnName +
                    " detected");
            }
        }
        
        return columnNames;
    }
    
    
    /**
     * Validate the user-specified grouping column names <p>
     * 
     * PRECONDITIONS: <br>
     * 1) groupingColumnNames does not contain the 'value' column name <br>
     * 2) groupingColumnNames does not contain duplicate column names
     * 
     * @param groupingColumnNames - The grouping column names to validate
     * @param valueColumnName - The name of the 'value' column
     * 
     * @throws RuntimeException If any of the above preconditions are not met
     */
    protected static final void validateGroupingColumnNames(
        String[] groupingColumnNames,
        String valueColumnName)
    {
        if (groupingColumnNames.length == 0)
        {   
            return;
        }
        
        HashSet<String> groupingColumnNamesSet = 
            new HashSet<String>(groupingColumnNames.length);
        
        for (int i = 0; i < groupingColumnNames.length; i++)
        {
            String groupingColumnName = groupingColumnNames[i];
            
            if (groupingColumnName.equals(valueColumnName))
            {
                throw new RuntimeException(
                    "Grouping column name arguments invalid: A grouping " +
                    "column name must not match the value column name (" +
                    valueColumnName +
                    ")");
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
    }    
    
    
    /**
     * Given a column schema (in order) and a set of grouping column names (in
     * the desired grouping order), match each grouping column name to the
     * corresponding column name, and create a column index ordering that
     * corresponds to the desired grouping order. <p>
     * 
     * PRECONDITIONS: <br>
     * 1) groupingColumnNames is a subset of columnNames <br>
     * 2) groupingColumnNames does not contain the 'value' column name <br>
     * 
     * @param columnNames
     * A column schema
     * 
     * @param groupingColumnNames
     * A desired subset and ordering of the column schema
     * 
     * @return
     * Column indices in an order corresponding to groupingColumnNames
     * 
     * @throws RuntimeException If any of the above preconditions are not met
     */
    protected static final int[] getGroupingColumnIndices(
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
    
    
    /**
     * Given raw lines from input data, parse each line into a {@link Row}, and
     * return the {@link Row}s <p>
     * 
     * PRECONDITIONS: <br>
     * 1) All lines in inputDataLines have expectedNumColumns elements,
     *    delimited by {@value #COLUMN_DELIMITER} <br>
     * 2) The last element in each line can be parsed to {@link Double} <p>
     * 
     * POSTCONDITION:
     * All returned {@link Row}s have the same number of columns
     * 
     * @param inputDataLines
     * The raw lines from the input data
     * 
     * @param columnDelimiter
     * The column delimiter character expected in the input data
     * 
     * @param expectedNumColumns
     * The expected number of delimited elements in each line
     * 
     * @return
     * A list of {@link Row}s, one {@link Row} per input data line, where all
     * {@link Row}s have the same number of columns
     * 
     * @throws RuntimeException If any of the above preconditions are not met
     */
    protected static final Row[] getRows(
        ArrayList<String> inputDataLines,
        String columnDelimiter,
        int expectedNumColumns)
    {
        int numInputDataLines = inputDataLines.size();
        
        ArrayList<Row> rows = new ArrayList<Row>(numInputDataLines);
        
        for (int i = 0; i < numInputDataLines; i++)
        {
            String line = inputDataLines.get(i);
            
            String[] values = line.split(columnDelimiter);
            
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
    
    
    /**
     * Sort the provided {@link Row}s in ascending order
     * 
     * @param rows
     * The {@link Row}s to sort
     * 
     * @param groupingColumnIndices
     * The column indices to use for grouping, in grouping order
     * 
     * @return
     * A new array containing the elements of rows, sorted in ascending order
     */
    protected static final Row[] sortRows(
        Row[] rows,
        int[] groupingColumnIndices)
    {
        RowComparator rowComparator = 
            new RowComparator(groupingColumnIndices);
        
        Row[] rowsSorted = Arrays.copyOf(rows, rows.length);
        
        Arrays.sort(rowsSorted, rowComparator);
        
        return rowsSorted;
    }
    
    
    /**
     * Append a number to a {@link StringBuilder}.  If the number converts 
     * cleanly to int, append the number as an int, otherwise, append the 
     * number as a double with all significant digits shown.
     * 
     * @param stringBuilder - The {@link StringBuilder} to append number to
     * @param number - The number to append
     */
    protected static final void appendNumber(
        StringBuilder stringBuilder, 
        double number)
    {
        int numberInt = (int) number;
        
        if (numberInt == number)
        {
            stringBuilder.append(numberInt);
        }
        
        else
        {
            stringBuilder.append(number);
        }
    }

    
    /**
     * Create a new {@link RollupSummarizer}
     */
    public RollupSummarizer()
    {}
    
    
    /**
     * Get the ROLLUP summary column header line <p>
     * 
     * Override to customize
     * 
     * @param groupingColumnNames
     * The names of the grouping columns, in grouping order
     * 
     * @param valueColumnName
     * The name of the 'value' column
     * 
     * @return The ROLLUP summary column header line
     */
    protected String getRollupSummaryColumnHeader(
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
        
        return stringBuilder.toString();
    }
    
    
    /**
     * Compute and return a ROLLUP summary of the provided, sorted {@link Row}s
     * given the provided grouping column indices <p>
     * 
     * PRECONDITIONS: <br>
     * 1) rowsSorted is not empty <br>
     * 2) groupingColumnIndices are valid with respect to the {@link Row} schema <p>
     * 
     * Override to customize
     * 
     * @param rowsSorted
     * A list of {@link Row}s, sorted per groupingColumnIndices
     * 
     * @param groupingColumnIndices
     * The column indices to use for grouping, in grouping order
     * 
     * @return A ROLLUP summary of rowsSorted given groupingColumnIndices
     * 
     * @throws RuntimeException If any of the above preconditions are not met
     */
    protected String getRollupSummary(
        Row[] rowsSorted,
        int[] groupingColumnIndices)
    {      
        Row previousRow = rowsSorted[0];
        
        int numGroupingColumns = groupingColumnIndices.length;
        
        double[] groupSums = new double[numGroupingColumns];
        Arrays.fill(groupSums, previousRow.getValue());
        
        double total = previousRow.getValue();
        
        StringBuilder stringBuilder = new StringBuilder(1000);
        
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
                    String groupSummary =
                        summarizeGroups(
                            previousRow,
                            groupingColumnIndices, 
                            groupingColumnIndex, 
                            groupSums);
                    
                    stringBuilder.append(groupSummary);
                    
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
        String groupSummary =
            summarizeGroups(
                previousRow,
                groupingColumnIndices, 
                0, 
                groupSums);
        
        stringBuilder.append(groupSummary);
        
        // Append the total value line
        
        for (int i = 0; i < numGroupingColumns; i++)
        {
            stringBuilder.append('\t');
        }
        
        appendNumber(stringBuilder, total);
        
        return stringBuilder.toString();
    }
    
    
    /**
     * Given a row where at least one grouping column value changed, compute
     * and return a summary of the groups that ended on this row <p>
     * 
     * Override to customize
     * 
     * @param row
     * A {@link Row} where at least one grouping column value changed (with
     * respect to the previous row)
     * 
     * @param groupingColumnIndices
     * The column indices to use for grouping, in grouping order
     * 
     * @param valueChangedIndex
     * The first (most significant, smallest) grouping column index whose
     * grouping column's value changed (with respect to the previous row)
     * 
     * @param groupSums
     * The sum of values accumulated for each group size
     * 
     * @return 
     * A summary of the groups that ended on row.  Ends with a newline character.
     */
    protected String summarizeGroups(
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
        
        StringBuilder stringBuilder = new StringBuilder();
        
        // For each group that just ended (from largest to smallest)
        for (int i = numGroupingColumns - 1; i >= valueChangedIndex; i--)
        {
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
            appendNumber(stringBuilder, groupSums[i]);
            stringBuilder.append(System.lineSeparator());
        }
        
        return stringBuilder.toString();
    }    
    
    
    /**
     * Parse the input data into a table and validate the table.  Validate the
     * provided grouping column names.  Given the provided grouping columns, 
     * compute and return a ROLLUP summary view of the table. <p>
     * 
     * Override to customize
     * 
     * @param inputDataLines
     * The raw lines from the input data (contains a minimum of two lines)
     * 
     * @param columnDelimiter
     * The column delimiter character expected in the input data
     * 
     * @param groupingColumnNames
     * The names of the grouping columns, in grouping order
     * 
     * @return A ROLLUP summary view of the table parsed from the input data
     */
    public String summarize(
        ArrayList<String> inputDataLines,
        String columnDelimiter,
        String[] groupingColumnNames)
    {
        String columnNamesLine = inputDataLines.remove(0);
        String[] columnNames = getAndValidateColumnNames(columnNamesLine);
        String valueColumnName = columnNames[columnNames.length - 1];
        
        validateGroupingColumnNames(groupingColumnNames, valueColumnName);        
        
        int[] groupingColumnIndices = 
            getGroupingColumnIndices(columnNames, groupingColumnNames);
        
        Row[] rows = getRows(inputDataLines, columnDelimiter, columnNames.length);
        Row[] rowsSorted = sortRows(rows, groupingColumnIndices);

        String columnHeader = 
            getRollupSummaryColumnHeader(groupingColumnNames, valueColumnName);
        
        String rollupSummary =
            getRollupSummary(rowsSorted, groupingColumnIndices);
        
        return (columnHeader + System.lineSeparator() + rollupSummary);
    }
}
