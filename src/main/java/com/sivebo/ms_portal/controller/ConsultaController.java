package com.sivebo.ms_portal.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import com.sivebo.ms_portal.dto.Request.ConsultaRequestDTO;
import com.sivebo.ms_portal.dto.Response.ConsultaPublicaResponseDTO;
import com.sivebo.ms_portal.dto.Response.ConsultaResponseDTO;
import com.sivebo.ms_portal.service.ConsultaService;

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
@RequestMapping("api/v1/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas Públicas", description = "Operaciones relacionadas con las consultas públicas de tracking")
public class ConsultaController {

        private final ConsultaService consultaService;

        @Operation(
                summary = "Obtener todas las consultas registradas",
                description = "Obtiene una lista de todas las consultas públicas de tracking"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Consultas obtenidas exitosamente",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ConsultaResponseDTO.class)
                                )
                        )
        })
        @GetMapping
        public List<ConsultaResponseDTO> getAll() {
                return consultaService.getAll();
        }

        @Operation(
                summary = "Obtener una consulta por ID",
                description = "Obtiene una consulta pública por su ID interno"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Consulta obtenida exitosamente",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ConsultaResponseDTO.class)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "404",
                                description = "Consulta no encontrada",
                                content = @Content(mediaType = "application/json")
                        )
        })
        @GetMapping("{id}")
        public ResponseEntity<ConsultaResponseDTO> getById(@PathVariable Long id) {
                return consultaService.getById(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        }

        @Operation(
                summary = "Obtener vista pública de consultas",
                description = "Retorna todas las consultas con el campo 'guiaEncontrada' derivado (equivale a V_CONSULTA_PUBLICA)"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Consultas públicas obtenidas exitosamente",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ConsultaPublicaResponseDTO.class)
                                )
                        )
        })
        @GetMapping("/publica")
        public List<ConsultaPublicaResponseDTO> getAllPublica() {
                return consultaService.getAllPublica();
        }

        @Operation(
                summary = "Buscar consultas por atributo",
                description = "Obtiene consultas por 'search?codigoTracking=*', 'search?ipUsuario=*' o 'search?fecha=dd-MM-yyyy'"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Consultas obtenidas exitosamente",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ConsultaResponseDTO.class)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "400",
                                description = "Número de parámetros inválido",
                                content = @Content(mediaType = "application/json")
                        ),
                        @ApiResponse(
                                responseCode = "404",
                                description = "No se encontraron consultas con los criterios indicados",
                                content = @Content(mediaType = "application/json")
                        )
        })
        @GetMapping("/search")
        public ResponseEntity<?> getByAtribute(
                        @RequestParam(required = false) String codigoTracking,
                        @RequestParam(required = false) String ipUsuario,
                        @RequestParam(required = false) String fecha) {

                long provided = Stream.of(codigoTracking, ipUsuario, fecha)
                        .filter(Objects::nonNull)
                        .count();

                if (provided == 0) {
                        return ResponseEntity.badRequest().body("Debe proporcionar un atributo de búsqueda valido");
                } else if (provided > 1) {
                        return ResponseEntity.badRequest().body("Solo se permite un atributo de búsqueda a la vez");
                } else if (codigoTracking != null) {
                        log.info(">>> Buscando consulta por codigo de tracking: {}", codigoTracking);
                        return ResponseEntity.ok(consultaService.getByCodigoTrackingConsultado(codigoTracking));
                } else if (ipUsuario != null) {
                        log.info(">>> Buscando consulta por ip de usuario: {}", ipUsuario);
                        return ResponseEntity.ok(consultaService.getByIpUsuario(ipUsuario));
                } else {
                        LocalDate fecha_formateada = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        log.info(">>> Buscando consulta por fecha: {}", fecha);
                        return ResponseEntity.ok(consultaService.getByFecha(fecha_formateada));
                }
        }

        @Operation(
                summary = "Registrar una consulta pública",
                description = "Registra una consulta de tracking. Si el código no existe en ms_tracking, se guarda con idGuia nulo"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "201",
                                description = "Consulta registrada exitosamente",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ConsultaResponseDTO.class)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "400",
                                description = "Datos inválidos",
                                content = @Content(mediaType = "application/json")
                        )
        })
        @PostMapping
        public ResponseEntity<ConsultaResponseDTO> create(@Valid @RequestBody ConsultaRequestDTO dto) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(consultaService.create(dto));
        }

        @Operation(
                summary = "Eliminar una consulta",
                description = "Elimina una consulta pública por su ID"
        )
        @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Consulta eliminada exitosamente",
                                content = @Content(mediaType = "application/json")
                        ),
                        @ApiResponse(
                                responseCode = "404",
                                description = "Consulta no encontrada",
                                content = @Content(mediaType = "application/json")
                        )
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<String> delete(@PathVariable Long id) {
                if (consultaService.delete(id)) {
                        return ResponseEntity.status(HttpStatus.OK).body("Consulta eliminada");
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consulta no encontrada o no se pudo eliminar");
                }
        }
}
