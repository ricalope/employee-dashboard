package com.cooksys.groupfinal.services.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.TeamDto;
import com.cooksys.groupfinal.dtos.TeamRequestDto;
import com.cooksys.groupfinal.entities.Company;
import com.cooksys.groupfinal.entities.Project;
import com.cooksys.groupfinal.entities.Team;
import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotAuthorizedException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.TeamMapper;
import com.cooksys.groupfinal.repositories.CompanyRepository;
import com.cooksys.groupfinal.repositories.ProjectRepository;
import com.cooksys.groupfinal.repositories.TeamRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.services.TeamService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
	
	private final TeamRepository teamRepository;
	private final CompanyRepository companyRepository;
	private final UserRepository userRepository;
	private final ProjectRepository projectRepository;
	
	private final TeamMapper teamMapper;
	
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
	
	private Team getTeam(Long id) {
		Optional<Team> optionalTeam = teamRepository.findById(id);
		if(optionalTeam.isEmpty()) {
			throw new NotFoundException("The specified team could not be found");
		}
		return optionalTeam.get();
	}
	
	private Company getCompany(Long id) {
		Optional<Company> optionalCompany = companyRepository.findById(id);
		if(optionalCompany.isEmpty()) {
			throw new NotFoundException("The specified company could not be found.");
		}
		return optionalCompany.get();
	}
	
	@Override
	public TeamDto createTeam(Long companyId, TeamRequestDto teamRequestDto) {
		checkUserPermission(teamRequestDto.getCredentials());
		Team teamToCreate = teamMapper.dtoToEntity(teamRequestDto);
		Company company = getCompany(companyId);
		teamToCreate.setCompany(company);
		Set<User> teammates = new HashSet<>();
//		List<User> requestTeammates = new ArrayList<>(teamToCreate.getTeammates());
		for(User teammate : teamToCreate.getTeammates()) {
			Optional<User> optionalUser = userRepository.findById(teammate.getId());
			if(optionalUser.isPresent()) {
				if(optionalUser.get().isActive()) {
					teammates.add(optionalUser.get());
				}
			}
		}
		
		teamToCreate.setTeammates(teammates);	
		return teamMapper.entityToDto(teamRepository.saveAndFlush(teamToCreate));
	}
	
	@Override
	public TeamDto updateTeam(Long id, TeamRequestDto teamRequestDto) {
		checkUserPermission(teamRequestDto.getCredentials());
		Team teamToUpdate = getTeam(id);
		Team updateTeam = teamMapper.dtoToEntity(teamRequestDto);
		Company teamCompany = getCompany(teamToUpdate.getCompany().getId());
		teamToUpdate.setName(updateTeam.getName());
		teamToUpdate.setDescription(updateTeam.getDescription());
		teamToUpdate.setCompany(teamCompany);
		Set<User> teammates = new HashSet<>();
		for(User teammate : updateTeam.getTeammates()) {
			Optional<User> optionalUser = userRepository.findById(teammate.getId());
			if(optionalUser.isPresent() && optionalUser.get().isActive()) {
				teammates.add(optionalUser.get());
			}
		}
		Set<Project> projects = new HashSet<>();
		for(Project project : updateTeam.getProjects()) {
			Optional<Project> optionalProject = projectRepository.findById(project.getId());
			if(optionalProject.isPresent() && optionalProject.get().isActive()) {
				projects.add(project);
			}
		}
		teamToUpdate.setProjects(projects);
		teamToUpdate.setTeammates(teammates);
		return teamMapper.entityToDto(teamRepository.saveAndFlush(teamToUpdate));
	}
	
	@Override
	public void deleteTeam(Long id, CredentialsDto credentialsDto) {
		Optional<Team> optionalTeam = teamRepository.findById(id);
		if(optionalTeam.isEmpty()) {
			throw new NotFoundException("The specified team could not be found");
		}
		checkUserPermission(credentialsDto);
		Team teamToDelete = optionalTeam.get();
		teamToDelete.setActive(false);
		teamMapper.entityToDto(teamRepository.saveAndFlush(teamToDelete));
	}

}
