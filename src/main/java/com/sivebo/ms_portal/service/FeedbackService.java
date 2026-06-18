package com.sivebo.ms_portal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sivebo.ms_portal.dto.Request.FeedbackRequestDTO;
import com.sivebo.ms_portal.dto.Response.FeedbackResponseDTO;
import com.sivebo.ms_portal.model.Feedback;
import com.sivebo.ms_portal.repository.FeedbackRepository;
import com.sivebo.ms_portal.utils.WebClientUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {
        
        private final FeedbackRepository feedbackRepository;
        
        private final WebClientUtil webClientUtil;

        @Qualifier("trackingWebClient")
        private final WebClient trackingWebClient;

        private FeedbackResponseDTO mapToDTO(Feedback feedback) {
                return new FeedbackResponseDTO(
                        feedback.getIdGuiaTracking(),
                        feedback.getCalificacion(),
                        feedback.getComentario()
                );
        }

        public List<FeedbackResponseDTO> getAll() {
                return feedbackRepository.findAll()
                        .stream()
                        .map(this::mapToDTO).toList();
        }

        public Optional<FeedbackResponseDTO> getById(Long id) {
                return feedbackRepository.findById(id).map(this::mapToDTO);
        }

        public List<FeedbackResponseDTO> getByCalificacion(Integer calificacion){
                return feedbackRepository.findByCalificacion(calificacion)
                        .stream().map(this::mapToDTO).toList();
        }

        public List<FeedbackResponseDTO> getbyIdGuiaTracking(Long idGuiaTracking){
                return feedbackRepository.findByIdGuiaTracking(idGuiaTracking)
                        .stream().map(this::mapToDTO).toList();
        }

        public FeedbackResponseDTO create(FeedbackRequestDTO dto){
                webClientUtil.validateMicroServiceByQuery(
                        "portal", 
                        "codigo_tracking",
                        String.valueOf(dto.getIdGuiaTracking()),
                        trackingWebClient);
                return mapToDTO(feedbackRepository.save(
                        new Feedback(
                                null,
                                dto.getIdGuiaTracking(),
                                dto.getCalificacion(),
                                dto.getComentario()
                        )
                ));
        }

        public Boolean delete(Long id){
                feedbackRepository.deleteById(id);
                return !feedbackRepository.existsById(id);
        }
}
