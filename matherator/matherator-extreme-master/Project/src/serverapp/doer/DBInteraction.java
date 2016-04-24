package serverapp.doer;

import java.sql.Connection;

public abstract class DBInteraction {
	Connection dattabazzz;
	String query;
	Object[] values;

	public DBInteraction(Connection dattabazzz, String query, Object... values) {
		this.dattabazzz = dattabazzz;
		this.query = query;
		this.values = values;
	}
	
	
	public abstract Object ex();
	

}
