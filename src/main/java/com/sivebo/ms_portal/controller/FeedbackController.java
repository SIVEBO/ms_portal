package com.sivebo.ms_portal.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Operaciones relacionadas con el feedback de clientes")
public class FeedbackController {

        private final FeedbackService feedbackService;

        @Operation(
                summary = "Obtener todos los feedbacks registrados",
                description = "Obtiene una lista de todos los feedbacks de clientes"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Feedbacks obtenidos exitosamente",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = FeedbackResponseDTO.class)
                                )
                        )
        })
        @GetMapping
        public List<FeedbackResponseDTO> getAll() {
                return feedbackService.getAll();
        }

        @Operation(
                summary = "Obtener un feedback por ID",
                description = "Obtiene un feedback por su ID interno"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Feedback obtenido exitosamente",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = FeedbackResponseDTO.class)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "404",
                                description = "Feedback no encontrado",
                                content = @Content(mediaType = "application/json")
                        )
        })
        @GetMapping("{id}")
        public ResponseEntity<FeedbackResponseDTO> getById(@PathVariable Long id) {
                return feedbackService.getById(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        }

        @Operation(
                summary = "Buscar feedbacks por atributo",
                description = "Obtiene feedbacks por 'search?idGuiaTracking=*' o 'search?calificacion=*'"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Feedbacks obtenidos exitosamente",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = FeedbackResponseDTO.class)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "400",
                                description = "Número de parámetros inválido",
                                content = @Content(mediaType = "application/json")
                        )
        })
        @GetMapping("/search")
        public ResponseEntity<?> getByAtribute(
                        @RequestParam(required = false) String idGuiaTracking,
                        @RequestParam(required = false) String calificacion) {

                long provided = Stream.of(idGuiaTracking, calificacion)
                        .filter(Objects::nonNull)
                        .count();

                if (provided == 0) {
                        return ResponseEntity.badRequest().body("Debe proporcionar un atributo de búsqueda valido");
                } else if (provided > 1) {
                        return ResponseEntity.badRequest().body("Solo se permite un atributo de búsqueda a la vez");
                } else if (idGuiaTracking != null) {
                        log.info(">>> Buscando feedback por id de guia de tracking: {}", idGuiaTracking);
                        return ResponseEntity.ok(feedbackService.getbyIdGuiaTracking(Long.valueOf(idGuiaTracking)));
                } else {
                        log.info(">>> Buscando feedback por calificacion: {}", calificacion);
                        return ResponseEntity.ok(feedbackService.getByCalificacion(Integer.valueOf(calificacion)));
                }
        }

        @Operation(
                summary = "Registrar un feedback",
                description = "Registra el feedback de un cliente sobre un envío. El id del cliente es opcional (feedback anónimo)"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "201",
                                description = "Feedback registrado exitosamente",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = FeedbackResponseDTO.class)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "400",
                                description = "Datos inválidos",
                                content = @Content(mediaType = "application/json")
                        ),
                        @ApiResponse(
                                responseCode = "404",
                                description = "Guía de tracking no encontrada en ms_tracking",
                                content = @Content(mediaType = "application/json")
                        )
        })
        @PostMapping
        public ResponseEntity<FeedbackResponseDTO> create(@Valid @RequestBody FeedbackRequestDTO dto) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(feedbackService.create(dto));
        }

        @Operation(
                summary = "Eliminar un feedback",
                description = "Elimina un feedback por su ID"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Feedback eliminado exitosamente",
                                content = @Content(mediaType = "application/json")
                        ),
                        @ApiResponse(
                                responseCode = "404",
                                description = "Feedback no encontrado",
                                content = @Content(mediaType = "application/json")
                        )
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<String> delete(@PathVariable Long id) {
                if (feedbackService.delete(id)) {
                        return ResponseEntity.status(HttpStatus.OK).body("Feedback eliminada");
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback no encontrada o no se pudo eliminar");
                }
        }
}
