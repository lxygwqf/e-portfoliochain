package com.wisedu.eportfoliochain.service;

import com.wisedu.eportfoliochain.bean.SchoolUser;
import com.wisedu.eportfoliochain.repository.SchoolUserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SchoolUserService {
	
	// 注入UserRepository
	@Resource
	private SchoolUserRepository schooluserRepository;

	public SchoolUser selectPasswordByUsername(String loginName){
		return schooluserRepository.selectPasswordByloginName(loginName);
	}


}