package com.cooksys.groupfinal.dtos;

import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamRequestDto {
	
	private CredentialsDto credentials;
	
	private String name;
	
	private String description;
	
	private Set<BasicUserDto> teammates;

}
