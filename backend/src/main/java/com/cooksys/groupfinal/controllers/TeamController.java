package com.cooksys.groupfinal.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.TeamDto;
import com.cooksys.groupfinal.dtos.TeamRequestDto;
import com.cooksys.groupfinal.services.TeamService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeamController {
	
	private final TeamService teamService;
	
	@PostMapping("/{companyId}")
	@ResponseStatus(HttpStatus.CREATED)
	public TeamDto createTeam(@PathVariable Long companyId, @RequestBody TeamRequestDto teamRequestDto) {
		return teamService.createTeam(companyId, teamRequestDto);
	}
	
	@PatchMapping("/{id}")
	public TeamDto updateTeam(@PathVariable Long id, @RequestBody TeamRequestDto teamRequestDto) {
		return teamService.updateTeam(id, teamRequestDto);
	}
//	
	@DeleteMapping("/{id}")
	public void deleteTeam(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
		teamService.deleteTeam(id, credentialsDto);
	}
	
}