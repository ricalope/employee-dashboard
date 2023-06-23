package com.cooksys.groupfinal.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.FullUserDto;
import com.cooksys.groupfinal.dtos.UserCreateDto;
import com.cooksys.groupfinal.entities.Company;
import com.cooksys.groupfinal.entities.Credentials;
import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotAuthorizedException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.CredentialsMapper;
import com.cooksys.groupfinal.mappers.FullUserMapper;
import com.cooksys.groupfinal.repositories.CompanyRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final CompanyRepository companyRepository;
	
	private final FullUserMapper fullUserMapper;
	private final CredentialsMapper credentialsMapper;
	
	private User findUser(String username) {
        Optional<User> user = userRepository.findByCredentialsUsernameAndActiveTrue(username);
        if (user.isEmpty()) {
            throw new NotFoundException("The username provided does not belong to an active user.");
        }
        return user.get();
    }
	
	private User findUserById(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		if(optionalUser.isEmpty()) {
			throw new NotFoundException("The specified user could not be found.");
		}
		return optionalUser.get();
	}
	
	private Company getCompany(Long id) {
		Optional<Company> optionalCompany = companyRepository.findById(id);
		if(optionalCompany.isEmpty()) {
			throw new NotFoundException("The specified company could not be found.");
		}
		return optionalCompany.get();
	}
	
	private void checkUserPermissions(CredentialsDto credentialsDto) {
		if(credentialsDto.getUsername() == null || credentialsDto.getPassword() == null) {
			throw new BadRequestException("You must supply both a username and password.");
		}
		Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndActiveTrue(credentialsDto.getUsername());
		if(optionalUser.isEmpty()) {
			throw new NotFoundException("The supplied credentials do not match a user on file.");
		}
		User user = optionalUser.get();
		if(!credentialsDto.getPassword().equals(user.getCredentials().getPassword())) {
			throw new NotAuthorizedException("The supplied credentials do not match our records.");
		}
		if(!user.isAdmin()) {
			throw new NotAuthorizedException("You do not have the required permissions to make this change");
		}
	}
	
	private void checkUsername(String username) {
		Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
		if(optionalUser.isPresent() && optionalUser.get().isActive()) {
			throw new BadRequestException("The specified username for the new user already exists.");
		}
	}
	
	@Override
	public FullUserDto login(CredentialsDto credentialsDto) {
		if (credentialsDto == null || credentialsDto.getUsername() == null || credentialsDto.getPassword() == null) {
            throw new BadRequestException("A username and password are required.");
        }
        Credentials credentialsToValidate = credentialsMapper.dtoToEntity(credentialsDto);
        User userToValidate = findUser(credentialsDto.getUsername());
        if (!userToValidate.getCredentials().equals(credentialsToValidate)) {
            throw new NotAuthorizedException("The provided credentials are invalid.");
        }
        if (userToValidate.getStatus().equals("PENDING")) {
        	userToValidate.setStatus("JOINED");
        	userRepository.saveAndFlush(userToValidate);
        }
        return fullUserMapper.entityToFullUserDto(userToValidate);
	}
	
	@Override
	public FullUserDto createUser(Long companyId, UserCreateDto userCreateDto) {
		checkUserPermissions(userCreateDto.getCredentials());
		Company userCompany = getCompany(companyId);
		User userToAdd = fullUserMapper.requestDtoToEntity(userCreateDto.getUser());
		checkUsername(userCreateDto.getUser().getCredentials().getUsername());
		userRepository.save(userToAdd);
		userCompany.getEmployees().add(userToAdd);
		return fullUserMapper.entityToFullUserDto(userToAdd);
	}
	
	@Override
	public FullUserDto updateUser(Long id, UserCreateDto userCreateDto) {
		checkUserPermissions(userCreateDto.getCredentials());
		User userToUpdate = findUserById(id);
		User updateUser = fullUserMapper.requestDtoToEntity(userCreateDto.getUser());
		userToUpdate.setAdmin(updateUser.isAdmin());
		userToUpdate.setProfile(updateUser.getProfile());
		userToUpdate.setCredentials(updateUser.getCredentials());
		return fullUserMapper.entityToFullUserDto(userRepository.saveAndFlush(userToUpdate));
	}
	
	@Override
	public void deleteUser(Long id, CredentialsDto credentialsDto) {
		checkUserPermissions(credentialsDto);
		User userToDelete = findUserById(id);
		userToDelete.setActive(false);
		userRepository.saveAndFlush(userToDelete);
	}
	
	// just for testing
//	@Override
//	public List<FullUserDto> getAllUsers(CredentialsDto credentialsDto) {
//		checkUserPermissions(credentialsDto);
//		List<User> allUsers = userRepository.findAll();
//		return fullUserMapper.entitiesToUserListDtos(allUsers);
//	}
	
}
