package com.cooksys.groupfinal.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectRequestDto {
	
	private CredentialsDto credentials;
	
	private String name;
	
	private String description;
	
//	private TeamDto team;
	
	private boolean active;

}
