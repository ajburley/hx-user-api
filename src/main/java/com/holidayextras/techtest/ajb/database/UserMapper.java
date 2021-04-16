package com.holidayextras.techtest.ajb.database;

import java.time.Instant;
import java.util.List;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

import com.holidayextras.techtest.ajb.domain.ProtoUser;
import com.holidayextras.techtest.ajb.domain.User;

/**
 * MyBatis mapper interface for user CRUD operations.
 */
public interface UserMapper {
	@Select("SELECT id,email,given_name,family_name,created FROM users ORDER BY id asc")
	@ConstructorArgs(value = {
			@Arg(column="id",javaType=int.class,jdbcType=JdbcType.INTEGER),
			@Arg(column="email",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="given_name",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="family_name",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="created",javaType=Instant.class,jdbcType=JdbcType.INTEGER,typeHandler=LongInstantTypeHandler.class)
	})
	List<User> getAllUsers();
	
	@Select("SELECT id,email,given_name,family_name,created FROM users ORDER BY id asc LIMIT #{limit}")
	@ConstructorArgs(value = {
			@Arg(column="id",javaType=int.class,jdbcType=JdbcType.INTEGER),
			@Arg(column="email",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="given_name",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="family_name",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="created",javaType=Instant.class,jdbcType=JdbcType.INTEGER,typeHandler=LongInstantTypeHandler.class)
	})
	List<User> getAllUsersWithLimit(@Param("limit") int limit);
	
	@Select("SELECT id,email,given_name,family_name,created FROM users ORDER BY id asc LIMIT -1 OFFSET #{offset}")
	@ConstructorArgs(value = {
			@Arg(column="id",javaType=int.class,jdbcType=JdbcType.INTEGER),
			@Arg(column="email",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="given_name",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="family_name",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="created",javaType=Instant.class,jdbcType=JdbcType.INTEGER,typeHandler=LongInstantTypeHandler.class)
	})
	List<User> getUsersFromOffset(@Param("offset") int offset);
	
	@Select("SELECT id,email,given_name,family_name,created FROM users ORDER BY id asc LIMIT #{limit} OFFSET #{offset}")
	@ConstructorArgs(value = {
			@Arg(column="id",javaType=int.class,jdbcType=JdbcType.INTEGER),
			@Arg(column="email",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="given_name",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="family_name",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="created",javaType=Instant.class,jdbcType=JdbcType.INTEGER,typeHandler=LongInstantTypeHandler.class)
	})
	List<User> getUsersFromOffsetWithLimit(@Param("offset") int offset, @Param("limit") int limit);
	
	@Select("SELECT id,email,given_name,family_name,created FROM users WHERE id = #{id}")
	@ConstructorArgs(value = {
			@Arg(column="id",javaType=int.class,jdbcType=JdbcType.INTEGER),
			@Arg(column="email",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="given_name",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="family_name",javaType=String.class,jdbcType=JdbcType.VARCHAR),
			@Arg(column="created",javaType=Instant.class,jdbcType=JdbcType.INTEGER,typeHandler=LongInstantTypeHandler.class)
	})
	User getUser(@Param("id") int id);
	
	@Insert("INSERT INTO users(email,given_name,family_name,created) VALUES(#{user.email},#{user.givenName},#{user.familyName},#{created})")
	void addUser(@Param("user") ProtoUser user, @Param("created") long created);
	
	@Select("SELECT seq FROM sqlite_sequence WHERE name='users'")
	int getLatestKey();
	
	@Update("UPDATE users SET email = #{user.email}, given_name = #{user.givenName}, family_name = #{user.familyName} WHERE id = #{id}")
	void updateUser(@Param("id") int id, @Param("user") ProtoUser user);
	
	@Delete("DELETE FROM users")
	void deleteAllUsers();
	
	@Delete("DELETE FROM users WHERE id = #{id}")
	void deleteUser(@Param("id") int id);
}
