package com.cooksys.groupfinal.services.impl;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;
import com.cooksys.groupfinal.entities.Project;
import com.cooksys.groupfinal.entities.Team;
import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotAuthorizedException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.ProjectMapper;
import com.cooksys.groupfinal.repositories.ProjectRepository;
import com.cooksys.groupfinal.repositories.TeamRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.services.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
	
	private final ProjectRepository projectRepository;
	private final TeamRepository teamRepository;
	private final UserRepository userRepository;
	
	private final ProjectMapper projectMapper;
	
	private Team findTeam(Long teamId) {
		Optional<Team> optionalTeam = teamRepository.findById(teamId);
		if(optionalTeam.isEmpty()) {
			throw new NotFoundException("The specified team could not be found");
		}
		return optionalTeam.get();
	}
	
	private Project findProject(Long id) {
		Optional<Project> optionalProject = projectRepository.findById(id);
		if(optionalProject.isEmpty()) {
			throw new NotFoundException("The specified project could not be found");
		}
		return optionalProject.get();
	}
	
	private void checkUserPermission(CredentialsDto credentialsDto) {
		if(credentialsDto.getUsername() == null || credentialsDto.getPassword() == null) {
			throw new BadRequestException("You must supply both a username and password");
		}
		Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndActiveTrue(credentialsDto.getUsername());
		if(optionalUser.isEmpty()) {
			throw new NotFoundException("The supplied credentials do not match a user on file");
		}
		User user = optionalUser.get();
		if(!credentialsDto.getPassword().equals(user.getCredentials().getPassword())) {
			throw new NotAuthorizedException("The supplied credentials do not match our records");
		}
		if(!user.isAdmin()) {
			throw new NotAuthorizedException("You do not have the required permissions to make this change");
		}
	}
	
	private void checkBasicPermission(CredentialsDto credentialsDto) {
		if(credentialsDto.getUsername() == null || credentialsDto.getPassword() == null) {
			throw new BadRequestException("You must supply both a username and password");
		}
		Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndActiveTrue(credentialsDto.getUsername());
		if(optionalUser.isEmpty()) {
			throw new NotFoundException("The supplied credentials do not match a user on file");
		}
		User user = optionalUser.get();
		if(!credentialsDto.getPassword().equals(user.getCredentials().getPassword())) {
			throw new NotAuthorizedException("The supplied credentials do not match our records");
		}
	}

	@Override
	public ProjectDto createProject(Long teamId, ProjectRequestDto projectRequestDto) {
		checkUserPermission(projectRequestDto.getCredentials());
		Team projectTeam = findTeam(teamId);
		Project projectToAdd = projectMapper.dtoToEntity(projectRequestDto);
		projectToAdd.setTeam(projectTeam);
		return projectMapper.entityToDto(projectRepository.saveAndFlush(projectToAdd));
		
		// we dont really need to do this below since we're mapping we don't need to set the individual fields
		// and ideally we want to save to the repo before adding it to the team since we need it to have an id to append
		
//		// Create a new Project entity and set the fields from the ProjectDto
//		Project project = new Project();
//		project.setId(projectDto.getId());
//		project.setName(projectDto.getName());
//		project.setDescription(projectDto.getDescription());
//		project.setActive(projectDto.isActive());
//
//		// Set the team id on the Project entity
//		if (projectDto.getTeam() != null) {
//			Team team = new Team();
//			team.setId(projectDto.getTeam().getId());
//			project.setTeam(team);
//		}
//		// Save the project using the ProjectRepository
//		project = projectRepository.save(project);
//		// Map the saved Project entity back to a ProjectDto and return it
//		return projectMapper.entityToDto(project);
	}
	
	// dont need this route as of now

//	@Override
//	public ProjectDto getProjectById(Long id) {
//		// Retrieve the Project entity from the ProjectRepository
//		Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
//		// Map the Project entity to a ProjectDto and return it
//		return projectMapper.entityToDto(project);
//	}

	@Override
	public ProjectDto updateProject(Long id, ProjectRequestDto projectRequestDto) {
		checkBasicPermission(projectRequestDto.getCredentials());
		Project existingProject = findProject(id);
		// Retrieve the existing Project entity from the ProjectRepository
//		Project existingProject = projectRepository.findById(id)
//				.orElseThrow(() -> new RuntimeException("Project not found"));
		// Update the fields of the existing Project entity with the values from the
		// ProjectDto
		if(projectRequestDto.getName() != null) {
			existingProject.setName(projectRequestDto.getName());			
		}
		if(projectRequestDto.getDescription() != null) {
			existingProject.setDescription(projectRequestDto.getDescription());
		}
//		existingProject.setActive(projectRequestDto.isActive());
		// Set the team id on the Project entity if provided
//		if (projectRequestDto.getTeam() != null) {
//			Team team = new Team();
//			team.setId(projectRequestDto.getTeam().getId());
//			// Set other fields of the Team object if needed
//			existingProject.setTeam(team);
//		} else {
//			existingProject.setTeam(null); // If team id is not provided, set it to null
//		}
	    // Update the lastEdited field with the current timestamp
		existingProject.setLastEdited(new Timestamp(System.currentTimeMillis()));
		// Save the updated project using the ProjectRepository
		existingProject = projectRepository.save(existingProject);
		// Map the saved Project entity back to a ProjectDto and return it
		return projectMapper.entityToDto(existingProject);
	}

	@Override
	public void deleteProject(Long id, CredentialsDto credentialsDto) {
		checkUserPermission(credentialsDto);
		Project projectToDelete = findProject(id);
		projectToDelete.setActive(false);
		projectMapper.entityToDto(projectRepository.saveAndFlush(projectToDelete));
//		// Check if the project exists in the repository
//		if (!projectRepository.existsById(id)) {
//			throw new RuntimeException("Project not found");
//		}
//		// Delete the project by ID using the ProjectRepository
//		projectRepository.deleteById(id);
		
		
	}

//	@Override
//	public List<ProjectDto> getAllProjects() {
//		// Retrieve all projects from the ProjectRepository
//		List<Project> projects = projectRepository.findAll();
//		// Map the list of projects to a list of ProjectDto using the ProjectMapper
//		List<ProjectDto> projectDtos = projectMapper.entityListToDtoList(projects);
//		return projectDtos;
//	}

}
