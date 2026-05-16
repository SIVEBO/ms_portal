package com.sivebo.ms_portal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_portal.model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

        Optional<Feedback> findByIdGuiaTracking(Long id_guia_tracking);

        List<Feedback> findByCalificacion(Integer calificacion);
}
