package org.genetics.circuit.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.genetics.circuit.circuit.Circuit;
import org.genetics.circuit.dao.CircuitWrapperDao;
import org.genetics.circuit.dao.ProblemDao;
import org.genetics.circuit.entity.CircuitWrapper;
import org.genetics.circuit.entity.Problem;
import org.genetics.circuit.entity.SuiteWrapper;
import org.genetics.circuit.utils.IoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCircuitWrapperDao implements CircuitWrapperDao {
	
	@Autowired
	private ProblemDao problemDao;
	
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate ;
	
	private final static String INSERT_SQL = "insert into circuit (suite_id, position, object, created) values (?, ?, ?, ?)";
	
	private RowMapper<CircuitWrapper> circuitRowMapper = new RowMapper<CircuitWrapper>() {
        public CircuitWrapper mapRow(ResultSet rs, int rowNum) throws SQLException {
        	CircuitWrapper circuitWrapper = new CircuitWrapper();
        	circuitWrapper.setId(rs.getInt("circuit_id"));
        	circuitWrapper.setCircuit(IoUtils.base64ToObject(rs.getString("object"), Circuit.class));
        	circuitWrapper.setCreated(rs.getTimestamp("created").toLocalDateTime());
        	circuitWrapper.setProblem(problemDao.getById(rs.getInt("problem_id")));
            return circuitWrapper;
        }
    };

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
	
	@Override
	public CircuitWrapper create(SuiteWrapper suiteWrapper, Circuit circuit, int position) {
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
		    new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[] {"circuit_id"});
		            // ps.setInt(1, suiteWrapper.getProblem().getId());
		            ps.setInt(1, suiteWrapper.getId());
		            ps.setInt(2, position);
		            ps.setString(3, IoUtils.objectToBase64(circuit));
		            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
		            return ps;
		        }
		    },
		    keyHolder);
		
		CircuitWrapper circuitWrapper = new CircuitWrapper();
		circuitWrapper.setId(keyHolder.getKey().intValue());
		circuitWrapper.setCircuit(circuit);
		
		return circuitWrapper;
	}

	@Override
	public Circuit findByPosition(SuiteWrapper suiteWrapper, int position) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// parameters.addValue("problemId", suiteWrapper.getProblem().getId());
		parameters.addValue("suiteId", suiteWrapper.getId());
		parameters.addValue("position", position);
		
		String sql = "select c.*, s.problem_id from circuit c inner join suite s on c.suite_id = s.suite_id where c.suite_id = :suiteId  and c.position = :position ";
		
    	List<CircuitWrapper> list = this.namedParameterJdbcTemplate.query(sql, parameters, circuitRowMapper);
    	
    	Circuit circuit = null;
    	if (list.size() > 0) {
    		circuit = list.get(0).getCircuit();
    	}
    	return circuit;
	}

	@Override
	public void updatePositions(SuiteWrapper suiteWrapper, int position) {
		jdbcTemplate.update(
			    new PreparedStatementCreator() {
			        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			            PreparedStatement ps = connection.prepareStatement("update circuit set position = position + 1 where position >= ? and suite_id = ?");
			            ps.setInt(1, position);
			            // ps.setInt(2, suiteWrapper.getProblem().getId());
			            ps.setInt(2, suiteWrapper.getId());
			            return ps;
			        }
			    });
	}

	@Override
	public int getTotal(SuiteWrapper suiteWrapper) {
		String sql = "SELECT count(*) FROM circuit WHERE suite_id = ?";
		
        return jdbcTemplate.queryForObject(sql, new Object[] { suiteWrapper.getId() }, Integer.class);
	}

	@Override
	public void delete(SuiteWrapper suiteWrapper, int position) {
		String deleteSql = "DELETE FROM CIRCUIT WHERE SUITE_ID = ? AND POSITION = ?";
		jdbcTemplate.update(deleteSql, new Object[] {suiteWrapper.getId(), position});

		String updateSql = "UPDATE CIRCUIT SET POSITION = POSITION - 1 WHERE SUITE_ID = ? and POSITION >= ?";
		jdbcTemplate.update(updateSql, new Object[] {suiteWrapper.getId(), position});
	}
}
