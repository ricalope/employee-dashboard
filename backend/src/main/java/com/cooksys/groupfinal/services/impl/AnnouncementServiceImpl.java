package com.cooksys.groupfinal.services.impl;

import com.cooksys.groupfinal.dtos.AnnouncementDto;
import com.cooksys.groupfinal.dtos.AnnouncementRequestDto;
import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.entities.Announcement;
import com.cooksys.groupfinal.entities.Company;
import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.AnnouncementMapper;
import com.cooksys.groupfinal.repositories.AnnouncementRepository;
import com.cooksys.groupfinal.repositories.CompanyRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.services.AnnouncementService;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementMapper announcementMapper;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    // GET - ALL ANNOUNCEMENTS //
    @Override
    public Set<AnnouncementDto> getAllAnnouncements() {
        return announcementMapper.entitiesToDtos(new HashSet<>(announcementRepository.findAllByDeletedFalse()));
    }

    // POST - CREATE A NEW ANNOUNCEMENT //
    @Override
    public AnnouncementDto createAnnouncement(AnnouncementRequestDto announcementRequestDto) {

        Optional <User> optionalAuthor = userRepository.findByCredentialsUsernameAndActiveTrue(announcementRequestDto.getCredentials().getUsername());

        // if optional author is empty throw not found exception //
        if (optionalAuthor.isEmpty()) {
            throw new NotFoundException("user not found");
        }
        User author = optionalAuthor.get();

        // if the credentials do not match the Dto throw bad request exception //
        if (!author.isAdmin() || !author.getCredentials().getPassword().equals(announcementRequestDto.getCredentials().getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        Optional <Company> optionalCompany = companyRepository.findById(announcementRequestDto.getCompanyId());

        // if the optional company is empty throw not found exception //
        if (optionalCompany.isEmpty()) {
            throw new NotFoundException("company not found");
        }
        Company company = optionalCompany.get();

        // map announcement from Dto back to entity //
        Announcement announcement = announcementMapper.dtoToEntity(announcementRequestDto);

        // set author and company of announcement //
        announcement.setAuthor(author);
        announcement.setCompany(company);

        // save announcement to repository and save it //
        Announcement savedAnnouncement = announcementRepository.saveAndFlush(announcement);

        // map entity back to a Dto and return it //
        return announcementMapper.entityToDto(savedAnnouncement);
    }

    // DELETE - DELETE EXISTING ANNOUNCEMENT //
    @Override
    public void deleteAnnouncement(Long id, CredentialsDto credentialsDto) {

        Optional<User> optionalAuthor = userRepository.findByCredentialsUsernameAndActiveTrue(credentialsDto.getUsername());

        // if optional author is empty throw not found exception //
        if (optionalAuthor.isEmpty()) {
            throw new NotFoundException("user not found");
        }
        User author = optionalAuthor.get();

        // if the credentials do not match throw bad request exception //
        if (!author.isAdmin() || !author.getCredentials().getPassword().equals(credentialsDto.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        Optional <Announcement> optionalAnnouncement = announcementRepository.findById(id);

        // if optional announcement is empty throw not found exception //
        if (optionalAnnouncement.isEmpty()) {
            throw new NotFoundException("announcement not found or is deleted");
        }
        Announcement announcement = optionalAnnouncement.get();

        // delete announcement from repository and save it //
        announcement.setDeleted(true);
        Announcement savedAnnouncement = announcementRepository.saveAndFlush(announcement);

        // map entity back to a Dto and return it //
        announcementMapper.entityToDto(savedAnnouncement);
    }

}