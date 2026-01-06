package com.collab.evaluationservice.repository;

import com.collab.evaluationservice.entity.PeerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface PeerReviewRepository
        extends JpaRepository<PeerReview, Long> {

    List<PeerReview> findByToStudentId(Long toStudentId);
}
