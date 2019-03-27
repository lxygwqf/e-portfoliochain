package com.wisedu.eportfoliochain.repository;

import com.wisedu.eportfoliochain.bean.StudentUser;
import org.apache.ibatis.annotations.*;

public interface StudentUserRepository {

	@Select("select password from user where mobile = #{mobile}")
	// 引用id="passwordResult"的@Results
	@Results(id="passwordResult2",value={
			@Result(id=true,column="id",property="id"),
			@Result(column="mobile",property="mobile"),
			@Result(column="password",property="password")
	})
	public StudentUser selectPasswordByStudentMobile(@Param("mobile") String mobile);

	@Insert("insert into user(mobile ,password) "
			+ "values (#{mobile}, #{password})")
	@Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
	public int insertStudentUser(@Param("mobile") String mobile, @Param("password") String password);

	@Update("update user set userName=#{username}, idCard=#{idCard} where mobile = #{mobile}")
	@Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
	public int updateidCard(@Param("username") String username, @Param("idCard") String idCard, @Param("mobile") String mobile);

	@Update("update user set password=#{password} where mobile = #{mobile}")
	@Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
	public int updatePassword(@Param("password") String username, @Param("mobile") String mobile);

	@Select("select * from user where mobile = #{mobile}")
	// 引用id="passwordResult"的@Results
	@Results(id="passwordResult1",value={
			@Result(id=true,column="id",property="id"),
			@Result(column="mobile",property="mobile"),
			@Result(column="userName",property="username"),
			@Result(column="idCard",property="idCard"),
			@Result(column="password",property="password")
	})
	public StudentUser selectuserStudentByStudentMobile(@Param("mobile") String mobile);

	@Select("select did from user where studentID = #{studentID} AND InstitutionId = #{InstitutionId}")
	// 引用id="passwordResult"的@Results
	@Results(id="passwordResult3",value={
			@Result(column="did",property="did")
	})
	public StudentUser selectDIDByStudentID(@Param("studentID") String studentID, @Param("InstitutionId") String InstitutionId);

	@Update("update user set did=#{did}, InstitutionId=#{InstitutionId} where idCard = #{idCard}")
	@Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
	public int updateDID(@Param("did") String did, @Param("InstitutionId") String InstitutionId, @Param("idCard") String idCard);

}