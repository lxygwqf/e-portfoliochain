package com.wisedu.eportfoliochain.service;

import com.wisedu.eportfoliochain.bean.StudentUser;
import com.wisedu.eportfoliochain.repository.StudentUserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StudentUserService {

	// 注入UserRepository
	@Resource
	private StudentUserRepository studentuserRepository;

	public StudentUser selectPasswordByStudentMobile(String mobile){
		return studentuserRepository.selectPasswordByStudentMobile(mobile);
	}

//	public int insertStudentUser(String username, String password, String mobile, String idCard){
//		return srudentuserRepository.insertStudentUser(username, password, mobile, idCard);
//	}

	public int insertStudentGetKey(String mobile, String password) {
		return studentuserRepository.insertStudentUser(mobile, password);
	}

	public int updateStudentIDCardGetKey(String username, String idCard, String mobile) {
		return studentuserRepository.updateidCard(username, idCard, mobile);
	}

	public int updateStudentPasswordGetKey(String password, String mobile) {
		return studentuserRepository.updatePassword(password, mobile);
	}

	public StudentUser selectuserStudentByStudentMobile(String mobile){
		return studentuserRepository.selectuserStudentByStudentMobile(mobile);
	}

	public StudentUser selectDIDByStudentID(String studentID, String InstitutionId){
		return studentuserRepository.selectDIDByStudentID(studentID, InstitutionId);
	}

	public int updateDID(String did, String InstitutionId, String idCard) {
		return studentuserRepository.updateDID(did, InstitutionId, idCard);
	}
}