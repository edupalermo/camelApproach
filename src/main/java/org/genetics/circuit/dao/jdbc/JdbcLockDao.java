package org.genetics.circuit.dao.jdbc;

import org.genetics.circuit.dao.LockDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Repository
public class JdbcLockDao implements LockDao {
	
	private JdbcTemplate jdbcTemplate;
	
	private final static String LOCK_SQL = "update property set val = ? where name = 'LOCK' and val is null";
	private final static String RELEASE_SQL = "update property set val = null where name = 'LOCK' and val = ?";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public String lock() {
		String uuid = UUID.randomUUID().toString();
		
		PreparedStatementCreator psc = new LockPreparedStatementCreator(LOCK_SQL, uuid);
		int rowsAffected = jdbcTemplate.update(psc);

		if (rowsAffected == 0) { // Was not able to lock the database
			uuid = null;
		}
		
		return uuid;
	}

	@Override
	public void release(String key) {
		PreparedStatementCreator psc = new LockPreparedStatementCreator(RELEASE_SQL, key);
		jdbcTemplate.update(psc);
		
	}
	
	private static class LockPreparedStatementCreator implements PreparedStatementCreator {
		
		private final String query;
		private final String uuid;
		
		public LockPreparedStatementCreator(String query, String uuid) {
			this.query = query;
			this.uuid = uuid;
		}
		
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, uuid);
            return ps;
        }
		
	}
	

}
