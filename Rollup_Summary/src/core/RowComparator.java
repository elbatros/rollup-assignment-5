package core;

import java.util.Comparator;


/**
 * Compares {@link Row}s by comparing dimensional column values 
 * (lexicographically) using the provided column ordering 
 * 
 * @author Dylan Jacobs
 */
public class RowComparator implements Comparator<Row>
{
    /**
     * The column indices to use for comparison, in comparison order
     */
    private final int[] columnIndicesToCompare;
    
    
    /**
     * Create a new {@link RowComparator}
     * 
     * @param columnIndicesToCompare {@link #columnIndicesToCompare}
     */
    public RowComparator(int[] columnIndicesToCompare)
    {
        this.columnIndicesToCompare = columnIndicesToCompare;
    }
    
    
    @Override
    public int compare(Row row1, Row row2)
    {
        for (int i = 0; i < columnIndicesToCompare.length; i++)
        {
            int columnIndex = columnIndicesToCompare[i];
            
            String row1ColumnValue = row1.getDimensionalColumnValue(columnIndex);
            String row2ColumnValue = row2.getDimensionalColumnValue(columnIndex);
            
            int result = row1ColumnValue.compareTo(row2ColumnValue);
            
            if (result != 0)
            {
                return result;
            }
        }
        
        return 0;
    }
}
