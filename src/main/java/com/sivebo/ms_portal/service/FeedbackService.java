package com.sivebo.ms_portal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        @Qualifier("clientesWebClient")
        private final WebClient clientesWebClient;

        private FeedbackResponseDTO mapToDTO(Feedback feedback) {
                String codigoTracking = webClientUtil
                        .resolveFieldById(feedback.getIdGuiaTracking(), "guias", "codigoTracking", trackingWebClient)
                        .orElse(null);
                String nroDocumentoCliente = feedback.getIdCliente() == null ? null : webClientUtil
                        .resolveFieldById(feedback.getIdCliente(), "clientes", "nroDocumento", clientesWebClient)
                        .orElse(null);
                return new FeedbackResponseDTO(
                        codigoTracking,
                        nroDocumentoCliente,
                        feedback.getCalificacion(),
                        feedback.getComentario(),
                        feedback.getFechaHora()
                );
        }

        @Transactional(readOnly = true)
        public List<FeedbackResponseDTO> getAll() {
                return feedbackRepository.findAll()
                        .stream()
                        .map(this::mapToDTO).toList();
        }

        @Transactional(readOnly = true)
        public Optional<FeedbackResponseDTO> getById(Long id) {
                return feedbackRepository.findById(id).map(this::mapToDTO);
        }

        @Transactional(readOnly = true)
        public List<FeedbackResponseDTO> getByCalificacion(Integer calificacion){
                return feedbackRepository.findByCalificacion(calificacion)
                        .stream().map(this::mapToDTO).toList();
        }

        @Transactional(readOnly = true)
        public List<FeedbackResponseDTO> getbyIdGuiaTracking(Long idGuiaTracking){
                return feedbackRepository.findByIdGuiaTracking(idGuiaTracking)
                        .stream().map(this::mapToDTO).toList();
        }

        @Transactional
        public FeedbackResponseDTO create(FeedbackRequestDTO dto){
                webClientUtil.validateMicroServiceById(dto.getIdGuiaTracking(), "guias", trackingWebClient);
                return mapToDTO(feedbackRepository.save(
                        new Feedback(
                                null,
                                dto.getIdGuiaTracking(),
                                dto.getIdCliente(),
                                dto.getCalificacion(),
                                dto.getComentario(),
                                dto.getFechaHora()
                        )
                ));
        }

        @Transactional
        public Boolean delete(Long id){
                feedbackRepository.deleteById(id);
                return !feedbackRepository.existsById(id);
        }
}
