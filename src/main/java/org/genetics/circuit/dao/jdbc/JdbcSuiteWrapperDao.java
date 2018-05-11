package org.genetics.circuit.dao.jdbc;

import org.genetics.circuit.dao.ProblemDao;
import org.genetics.circuit.dao.SuiteWrapperDao;
import org.genetics.circuit.entity.Problem;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.problem.Suite;
import org.genetics.circuit.utils.IoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

@Repository
public class JdbcSuiteWrapperDao implements SuiteWrapperDao {
	
	@Autowired
	private ProblemDao problemDao;
	
	private JdbcTemplate jdbcTemplate;
	
	private final static String SQL_INSERT = "insert into suite (problem_id, object, created) values (?, ?, ?)";
	private final static String SQL_SELECT_LAST = "select suite_id, problem_id, object, created from suite where problem_id = ? order by created desc FETCH FIRST ROW ONLY";

	private RowMapper<SuiteWrapper> rowMapper = new RowMapper<SuiteWrapper>() {
		@Override
		public SuiteWrapper mapRow(ResultSet resultSet, int i) throws SQLException {
			SuiteWrapper suiteWrapper = new SuiteWrapper();
			suiteWrapper.setId(resultSet.getInt("suite_id"));
			suiteWrapper.setProblem(problemDao.getById(resultSet.getInt("problem_id")));
			suiteWrapper.setSuite(IoUtils.base64ToObject(resultSet.getString("object"), Suite.class));
			suiteWrapper.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
			return suiteWrapper;
		}
	};


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	@Override
	public SuiteWrapper create(Problem problem, Suite suite) {
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
		    new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps = connection.prepareStatement(SQL_INSERT, new String[] {"suite_id"});
		            ps.setInt(1, problem.getId());
		            ps.setString(2, IoUtils.objectToBase64(suite));
		            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
		            return ps;
		        }
		    },
		    keyHolder);
		
		SuiteWrapper suiteWrapper = new SuiteWrapper();
		suiteWrapper.setId(keyHolder.getKey().intValue());
		suiteWrapper.setSuite(suite);
		
		return suiteWrapper;
	}

	@Override
	public SuiteWrapper findLatest(Problem problem) {
		if (problem == null) {
			throw new RuntimeException("Cannot query SuiteWrapper with a null problem.");
		}
    	return this.jdbcTemplate.queryForObject(SQL_SELECT_LAST, new Object[]{Integer.valueOf(problem.getId())}, rowMapper);
	}

}
