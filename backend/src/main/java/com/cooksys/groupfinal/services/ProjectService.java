package com.cooksys.groupfinal.services;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;

public interface ProjectService {

	ProjectDto createProject(Long teamid, ProjectRequestDto projectRequestDto);

//	ProjectDto getProjectById(Long id);

	ProjectDto updateProject(Long id, ProjectRequestDto projectRequestDto);

	void deleteProject(Long id, CredentialsDto credentialsDto);

//	List<ProjectDto> getAllProjects();

}
