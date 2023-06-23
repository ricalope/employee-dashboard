package com.cooksys.groupfinal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.groupfinal.entities.Announcement;

import java.util.Set;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Set<Announcement> findAllByDeletedFalse();
}
