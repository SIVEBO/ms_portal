package com.sivebo.ms_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_portal.model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

        List<Feedback> findByIdGuiaTracking(Long idGuiaTracking);

        List<Feedback> findByCalificacion(Integer calificacion);
}
