package com.cooksys.groupfinal.controllers;

import java.util.Set;

import org.springframework.web.bind.annotation.*;

import com.cooksys.groupfinal.dtos.AnnouncementDto;
import com.cooksys.groupfinal.dtos.AnnouncementRequestDto;
import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.services.AnnouncementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnnouncementController {
	
	private final AnnouncementService announcementService;

	@GetMapping
	public Set<AnnouncementDto> getAllAnnouncements() {
		return announcementService.getAllAnnouncements();
	}

	@PostMapping("/{companyId}")
	public AnnouncementDto createAnnouncement(@RequestBody AnnouncementRequestDto announcementRequestDto) {
		return announcementService.createAnnouncement(announcementRequestDto);
	}

	@DeleteMapping("/{id}")
	public void deleteAnnouncement(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
		announcementService.deleteAnnouncement(id, credentialsDto);
	}

}
