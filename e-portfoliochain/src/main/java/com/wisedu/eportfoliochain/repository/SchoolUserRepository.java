package com.wisedu.eportfoliochain.repository;

import com.wisedu.eportfoliochain.bean.SchoolUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolUserRepository {

	@Select("select password from schoolAccout where loginname = #{loginName}")
	// 引用id="passwordResult"的@Results
	@Results(id="passwordResult",value={
			@Result(id=true,column="id",property="id"),
			@Result(column="loginname",property="loginName"),
			@Result(column="password",property="password"),
			@Result(column="name",property="username")
	})
	public SchoolUser selectPasswordByloginName(@Param("loginName") String loginName);

}
