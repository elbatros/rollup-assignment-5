package core;

public class Row
{
    private final String[] dimensionalColumnValues;
    private final double value;
    
    
    public Row(String[] dimensionalColumnValues, double value)
    {
        this.dimensionalColumnValues = dimensionalColumnValues;
        this.value = value;
    }
    
    
    // @throws IndexOutOfBoundsException If columnIndex is invalid
    public String getDimensionalColumnValue(int columnIndex)
    {
        return dimensionalColumnValues[columnIndex];
    }
    
    
    public double getValue()
    {
        return value;
    }
    
    
    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder(50);
        
        for (int i = 0; i < dimensionalColumnValues.length; i++)
        {
            stringBuilder.append(dimensionalColumnValues[i]);
            stringBuilder.append(" ");
        }
        
        stringBuilder.append(value);
        
        return stringBuilder.toString();
    }
}
