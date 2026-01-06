package com.collab.evaluationservice.repository;

import com.collab.evaluationservice.entity.MemberEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface MemberEvaluationRepository
        extends JpaRepository<MemberEvaluation, Long> {

    List<MemberEvaluation> findByMemberId(Long memberId);
}
