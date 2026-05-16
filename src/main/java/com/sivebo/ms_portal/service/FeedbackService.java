package com.sivebo.ms_portal.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sivebo.ms_portal.dto.FeedbackResponseDTO;
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

        private FeedbackResponseDTO mapTODTO(Feedback feedback) {
                return new FeedbackResponseDTO(
                        feedback.getId(),
                        feedback.getIdGuiaTracking(),
                        feedback.getCalificacion(),
                        feedback.getComentario()
                );
        }

        
}
