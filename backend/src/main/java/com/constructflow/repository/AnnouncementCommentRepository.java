package com.constructflow.repository;

import com.constructflow.model.AnnouncementComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnnouncementCommentRepository extends JpaRepository<AnnouncementComment, UUID> {

    List<AnnouncementComment> findByAnnouncementIdOrderByCreatedAtAsc(UUID announcementId);
}
