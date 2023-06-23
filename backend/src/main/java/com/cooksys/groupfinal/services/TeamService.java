package com.cooksys.groupfinal.services;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.TeamDto;
import com.cooksys.groupfinal.dtos.TeamRequestDto;

public interface TeamService {

	TeamDto createTeam(Long companyId, TeamRequestDto teamDto);

	TeamDto updateTeam(Long id, TeamRequestDto teamRequestDto);

	void deleteTeam(Long id, CredentialsDto credentialsDto);

}
