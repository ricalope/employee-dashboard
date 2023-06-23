package com.cooksys.groupfinal.services;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.FullUserDto;
import com.cooksys.groupfinal.dtos.UserCreateDto;

public interface UserService {

	FullUserDto login(CredentialsDto credentialsDto);

	FullUserDto createUser(Long companyId, UserCreateDto userCreateDto);

	FullUserDto updateUser(Long id, UserCreateDto userCreateDto);

	void deleteUser(Long id, CredentialsDto credentialsDto);

//	List<FullUserDto> getAllUsers(CredentialsDto credentialsDto);
   
}
