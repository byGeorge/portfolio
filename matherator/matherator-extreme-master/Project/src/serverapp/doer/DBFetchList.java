package serverapp.doer;

import java.sql.*;
import java.util.*;
import common.doer.*;

public abstract class DBFetchList<T> extends DBInteraction {
	
	public DBFetchList(Connection dattabazzz, String query, Object... values) {
		super(dattabazzz, query, values);
	}
	
	
	
	
	/**
	 * Prepare and execute the database fetch.
	 * 
	 * @return a list of the results of calling mapper() for each DB entry.
	 */
	public List<T> ex() {
		List<T> result = new ArrayList<T>();
		PreparedStatement fetcher = null;
		
		try {
			
			// Prepare the statement with the objects given in the constructor.
			fetcher = dattabazzz.prepareStatement(query);  int validex = 1;
			for (Object value : values)
				fetcher.setObject(validex++, value);
			
			// Execute the query.
			// For each result, add to the list the result of calling mapper().
			ResultSet iter = fetcher.executeQuery();
			while (iter.next())
				result.add(
						this.mapper(iter)
						);
			
			
		} catch (SQLException sqe) {
			return null;
			
		} catch (StopIteration si) {
			// expected.
			
		} finally {
			try { fetcher.close(); } catch (NullPointerException|SQLException except) { }
			
		}
		
		return result;
	}
	
	
	
	
	
	
	
	public abstract T mapper(ResultSet rest) throws SQLException, StopIteration;

}
