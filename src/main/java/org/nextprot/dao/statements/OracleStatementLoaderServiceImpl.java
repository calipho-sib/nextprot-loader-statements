package org.nextprot.dao.statements;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.nextprot.commons.statements.RawStatement;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;

@Service
public class OracleStatementLoaderServiceImpl implements InitializingBean, StatementLoaderService {

	OracleConnectionPoolDataSource ocpds = null;

	@Override
	public void load(List<RawStatement> statements) {

		Connection conn;
		try {
			conn = ocpds.getPooledConnection().getConnection();
			Statement statement = conn.createStatement();

			String columnNames = Arrays.asList(StatementField.values()).stream().map(f -> f.name()).collect(Collectors.joining(", "));
			String bindVariableNames = Arrays.asList(StatementField.values()).stream().map(f -> ":" + f.name()).collect(Collectors.joining(", "));

			statements.stream().forEach(s -> {
				String fieldValues = Arrays.asList(StatementField.values()).stream().map(v -> {
					String value = s.getValue(v);
					String result = null;
					if (value != null) {
						// This done because of single quotes in the text
						result = "'" + value.replaceAll("'", "''") + "'";
					}
					return result;
				}).collect(Collectors.joining(", "));

				String sqlStatement = "INSERT INTO MAPPED_STATEMENTS_NEXT (" + columnNames + ") VALUES ( " + fieldValues + ")";
				try {
					statement.addBatch(sqlStatement);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			statement.executeBatch();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void deleteAll() {

		try {

			Connection conn = ocpds.getPooledConnection().getConnection();
			Statement statement = conn.createStatement();
			statement.executeQuery("DELETE FROM MAPPED_STATEMENTS_NEXT");
			statement.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		ocpds = new OracleConnectionPoolDataSource();

		ocpds.setDriverType("thin");
		ocpds.setServerName("fou");
		ocpds.setNetworkProtocol("tcp");
		ocpds.setDatabaseName("SIBTEST3");
		ocpds.setPortNumber(1526);
		ocpds.setUser("nxbed");
		ocpds.setPassword("juventus");

		ocpds.setImplicitCachingEnabled(true);

	}

}