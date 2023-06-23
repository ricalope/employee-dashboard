package com.cooksys.groupfinal.controllers;

import org.springframework.web.bind.annotation.*;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;
import com.cooksys.groupfinal.services.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {
	
	private final ProjectService projectService;

    @PostMapping("/{teamId}")
    public ProjectDto createProject(@PathVariable Long teamId, @RequestBody ProjectRequestDto projectRequestDto) {
        return projectService.createProject(teamId, projectRequestDto);
    }

//    
//    @GetMapping("/{id}")
//    public ProjectDto getProjectById(@PathVariable Long id) {
//        return projectService.getProjectById(id);
//    }

    @PatchMapping("/{id}")
    public ProjectDto updateProject(@PathVariable Long id, @RequestBody ProjectRequestDto projectRequestDto) {
        return projectService.updateProject(id, projectRequestDto);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
        projectService.deleteProject(id, credentialsDto);
    }

//    @GetMapping
//    public List<ProjectDto> getAllProjects() {
//        return projectService.getAllProjects();
//    }
}
