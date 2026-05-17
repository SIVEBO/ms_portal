package com.sivebo.ms_portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sivebo.ms_portal.dto.Request.FeedbackRequestDTO;
import com.sivebo.ms_portal.dto.Response.FeedbackResponseDTO;
import com.sivebo.ms_portal.service.FeedbackService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/feebacks")
@RequiredArgsConstructor
public class FeedbackController {
        
        private final FeedbackService feedbackService;
        
        @GetMapping
        public List<FeedbackResponseDTO> getAll(){
                return feedbackService.getAll();
        }
        
        @GetMapping("{id}")
        public ResponseEntity<FeedbackResponseDTO> getById(@PathVariable Long id) {
                return feedbackService.getById(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/search")
        public ResponseEntity<?> getByAtribute(
                @RequestParam(required = false) String idGuiaTracking,
                @RequestParam(required = false) String calificacion){

                List<String> params = new ArrayList<>(List.of(idGuiaTracking, calificacion));

                int num_null = 0;
                for(String value: params){
                        if(value == null) num_null++;
                }
                
                if(num_null == params.size()) {
                        return ResponseEntity.badRequest().body("Debe proporcionar un atributo de búsqueda valido");
                }else if(num_null > 1){
                        return ResponseEntity.badRequest().body("Solo se permite un atributo de búsqueda a la vez");
                }else if(idGuiaTracking != null) {
                        log.info(">>> Buscando feedback por id de guia de tracking: {}", idGuiaTracking);
                        return ResponseEntity.ok(feedbackService.getbyIdGuiaTracking(Long.valueOf(idGuiaTracking)));
                }else if(calificacion != null){
                        log.info(">>> Buscando feedback por calificacion: {}", calificacion);
                        return ResponseEntity.ok(feedbackService.getByCalificacion(Integer.valueOf(calificacion)));
                }else{
                        return ResponseEntity.internalServerError().body("Solo se permite un atributo de búsqueda a la vez");
                }
        }

        @PostMapping 
        public ResponseEntity<FeedbackResponseDTO> create(@Valid @RequestBody FeedbackRequestDTO dto) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(feedbackService.create(dto));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<String> delete(@PathVariable Long id) {
                if (feedbackService.delete(id)) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Feedback eliminada");
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback no encontrada o no se pudo eliminar");
                }
        }

}
