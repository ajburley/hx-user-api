package com.holidayextras.techtest.ajb.database;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * MyBatis type handler for resolving database fields stored as numbers but resolved as {@code Instant} objects in Java code.
 */
public class LongInstantTypeHandler extends BaseTypeHandler<Instant> {
	@Override
	public Instant getNullableResult(ResultSet rs, String columnName) throws SQLException {
		var millis = rs.getLong(columnName);
		if (rs.wasNull()) return null;
		return Instant.ofEpochMilli(millis);
	}

	@Override
	public Instant getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		var millis = rs.getLong(columnIndex);
		if (rs.wasNull()) return null;
		return Instant.ofEpochMilli(millis);
	}

	@Override
	public Instant getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		var millis = cs.getLong(columnIndex);
		if (cs.wasNull()) return null;
		return Instant.ofEpochMilli(millis);
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Instant parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setLong(i, parameter.toEpochMilli());
	}
}
