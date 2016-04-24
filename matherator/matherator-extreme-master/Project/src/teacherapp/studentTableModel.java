package teacherapp;
import javax.swing.table.DefaultTableModel;

public class studentTableModel extends DefaultTableModel{
	public studentTableModel (Object[][] data, String[] columns){
		super(data,columns);
	}
	/*@Override
	public Class<?> getColumnClass(int c) {
		if (c == 1)
			return Boolean.class;
		else
			return String.class;
	}*/
	@Override 
	public boolean isCellEditable(int row, int column)
    {
        if(column > 0)
        	return false;
        else
        	return false;
    }
}