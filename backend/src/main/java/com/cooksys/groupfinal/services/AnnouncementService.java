package com.cooksys.groupfinal.services;

import com.cooksys.groupfinal.dtos.AnnouncementDto;
import com.cooksys.groupfinal.dtos.AnnouncementRequestDto;
import com.cooksys.groupfinal.dtos.CredentialsDto;

import java.util.Set;

public interface AnnouncementService {

    AnnouncementDto createAnnouncement(AnnouncementRequestDto announcementRequestDto);

    Set<AnnouncementDto> getAllAnnouncements();

    void deleteAnnouncement(Long id, CredentialsDto credentialsDto);
}
