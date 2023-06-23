package com.cooksys.groupfinal.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.FullUserDto;
import com.cooksys.groupfinal.dtos.UserCreateDto;
import com.cooksys.groupfinal.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

	private final UserService userService;

	@PostMapping("/login")
	@CrossOrigin(origins = "*")
	public FullUserDto login(@RequestBody CredentialsDto credentialsDto) {
		return userService.login(credentialsDto);
	}

	@PostMapping("/{companyId}")
	public FullUserDto createUser(@PathVariable Long companyId, @RequestBody UserCreateDto userCreateDto) {
		return userService.createUser(companyId, userCreateDto);
	}


	@PatchMapping("/{id}")
	public FullUserDto updateUser(@PathVariable Long id, @RequestBody UserCreateDto userCreateDto) {
		return userService.updateUser(id, userCreateDto);
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
		userService.deleteUser(id, credentialsDto);
	}

//	@GetMapping
//	public List<FullUserDto> getAllUsers(@RequestBody CredentialsDto credentialsDto) {
//		return userService.getAllUsers(credentialsDto);
//	}
	
}
