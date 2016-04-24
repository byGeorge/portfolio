package teacherapp;
import javax.swing.table.DefaultTableModel;

public class teacherTableModel extends DefaultTableModel{
	public teacherTableModel (Object[][] data, String[] columns){
		super(data,columns);
	}
	@Override
	public Class<?> getColumnClass(int c) {
		if (c == 1)
			return Boolean.class;
		else
			return String.class;
	}
	@Override 
	public boolean isCellEditable(int row, int column)
    {
        if(column > 0)
        	return true;
        else
        	return false;
    }
}