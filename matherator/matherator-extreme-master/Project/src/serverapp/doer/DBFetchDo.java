package serverapp.doer;

import java.sql.*;

import common.doer.StopIteration;

public abstract class DBFetchDo<T> extends DBInteraction {

	public DBFetchDo(Connection dattabazzz, String query, Object... values) {
		super(dattabazzz, query, values);
	}
	
	
	/**
	 * Prepare and execute the database fetch.
	 * @return the result of the last block run.
	 */
	public T ex() {
		T result = null;
		PreparedStatement fetcher = null;
		
		try {
			
			fetcher = dattabazzz.prepareStatement(query);  int validex = 1;
			for (Object value : values)
				fetcher.setObject(validex++, value);
			
			ResultSet iter = fetcher.executeQuery();
			while (iter.next()) {
				try {
					result = this.mapper(iter);
					
				} catch (StopIteration si) {
					result = (T)si.lastResult();
					break;
					
				}
			}
			
		} catch (SQLException sqe) {
			return null;
			
		}
		
		return result;
	}
	
	
	public abstract T mapper(ResultSet rest) throws SQLException, StopIteration;

}
