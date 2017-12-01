package core;


/**
 * A row in a table.  Contains N dimensional column values (String) and 1
 * numerical value (double).  Represents a mapping from the dimensional column
 * values to the numerical value.
 * 
 * @author Dylan Jacobs
 */
public class Row
{
    /**
     * This {@link Row}'s dimensional column values
     */
    private final String[] dimensionalColumnValues;
    
    /**
     * This {@link Row}'s numerical value
     */
    private final double value;
    
    
    /**
     * Create a new {@link Row}
     * 
     * @param dimensionalColumnValues {@link #dimensionalColumnValues}
     * @param value {@link #value}
     */
    public Row(String[] dimensionalColumnValues, double value)
    {
        this.dimensionalColumnValues = dimensionalColumnValues;
        this.value = value;
    }
    
    
    /**
     * @param columnIndex
     * The index of the dimensional column value to retrieve
     * 
     * @return The dimensional column value at index columnIndex
     * 
     * @throws IndexOutOfBoundsException If columnIndex is invalid
     */
    public String getDimensionalColumnValue(int columnIndex)
    {
        return dimensionalColumnValues[columnIndex];
    }
    
    
    /**
     * @return {@link #value}
     */
    public double getValue()
    {
        return value;
    }
    
    
    @Override
    public String toString()
    {
        StringBuilder stringBuilder = 
            new StringBuilder((dimensionalColumnValues.length + 1) * 10);
        
        for (int i = 0; i < dimensionalColumnValues.length; i++)
        {
            stringBuilder.append(dimensionalColumnValues[i]);
            stringBuilder.append(" ");
        }
        
        stringBuilder.append(value);
        
        return stringBuilder.toString();
    }
}
