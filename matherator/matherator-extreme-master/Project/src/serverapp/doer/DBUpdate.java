package serverapp.doer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DBUpdate extends DBInteraction {

	public DBUpdate(Connection dattabazzz, String query, Object... values) {
		super(dattabazzz, query, values);
	}

	
	
	
	public Boolean ex() {
		Boolean success = false;
		PreparedStatement updater = null;
		
		try {
			
			updater = dattabazzz.prepareStatement(query);  int validex = 1;
			for (Object value : values)
				updater.setObject(validex++, value);
			
			updater.executeUpdate();
			success = true;
			
		} catch (SQLException sqex) {
			success = false;
			this.onError(sqex);
			
		} finally {
			try { updater.close(); } catch (NullPointerException|SQLException except) { }
			
		}
		
		
		return success;
	}
	
	
	
	
	public abstract void onError(SQLException sqex);
	
	

}
