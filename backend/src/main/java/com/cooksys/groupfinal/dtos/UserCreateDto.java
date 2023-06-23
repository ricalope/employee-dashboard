package com.cooksys.groupfinal.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreateDto {
	
	private CredentialsDto credentials;
	
	private UserRequestDto user;

}
