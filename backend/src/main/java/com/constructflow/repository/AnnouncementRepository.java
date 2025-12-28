package com.constructflow.repository;

import com.constructflow.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {}
