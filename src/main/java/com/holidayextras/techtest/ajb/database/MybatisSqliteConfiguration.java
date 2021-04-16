package com.holidayextras.techtest.ajb.database;

import java.io.File;

import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class houses the {@code SqlSessionFactory} bean which is used for all MyBatis operations. It also handles database creation if it does not already exist.
 */
@Configuration
public class MybatisSqliteConfiguration {
	private static final String DRIVER = "org.sqlite.JDBC";
	private static final String USER_DB_FILENAME = "chta-users.sqlite3";
	
	public static interface TableCreationMapper {
	    @Update("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL, given_name TEXT NOT NULL, family_name TEXT NOT NULL, created INTEGER NOT NULL)")
	    void createUsersTableIfMissing();
	}

	private String getJdbcConnectionString() {
		var userHomePath = System.getProperty("user.home");
		var userDb = new File(userHomePath, USER_DB_FILENAME);
		return "jdbc:sqlite:" + userDb.getAbsolutePath();
	}
	
	private void createTablesIfMissing(SqlSessionFactory sessionFactory) {
        try (SqlSession session = sessionFactory.openSession()) { // this will also create the DB if it doesn't exist
        	var mapper = session.getMapper(TableCreationMapper.class);
        	mapper.createUsersTableIfMissing();
            session.commit();
        }
	}
	
	@Bean
	public SqlSessionFactory buildSqlSessionFactory() {
		var dataSource = new PooledDataSource(DRIVER, getJdbcConnectionString(), null, null);
		var environment = new Environment("Main", new JdbcTransactionFactory(), dataSource);
		var configuration = new org.apache.ibatis.session.Configuration(environment);
		configuration.addMapper(UserMapper.class);
		configuration.addMapper(TableCreationMapper.class);
		var builder = new SqlSessionFactoryBuilder();
		var sessionFactory = builder.build(configuration);
		createTablesIfMissing(sessionFactory);
		return sessionFactory;
	}
}
