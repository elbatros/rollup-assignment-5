package core;

import java.util.Comparator;

public class RowComparator implements Comparator<Row>
{
    private final int[] dimensionalColumnIndicesSorted;
    
    
    public RowComparator(int[] dimensionalColumnIndicesSorted)
    {
        this.dimensionalColumnIndicesSorted = dimensionalColumnIndicesSorted;
    }
    
    
    @Override
    public int compare(Row row1, Row row2)
    {
        for (int i = 0; i < dimensionalColumnIndicesSorted.length; i++)
        {
            int columnIndex = dimensionalColumnIndicesSorted[i];
            
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
