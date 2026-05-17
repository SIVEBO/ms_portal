package com.sivebo.ms_portal.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import com.sivebo.ms_portal.dto.Request.ConsultaRequestDTO;
import com.sivebo.ms_portal.dto.Response.ConsultaResponseDTO;
import com.sivebo.ms_portal.service.ConsultaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/consultas")
@RequiredArgsConstructor
public class ConsultaController {

        private final ConsultaService consultaService;
        
        @GetMapping
        public List<ConsultaResponseDTO> getAll(){
                return consultaService.getAll();
        }
        
        @GetMapping("{id}")
        public ResponseEntity<ConsultaResponseDTO> getById(@PathVariable Long id) {
                return consultaService.getById(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/search")
        public ResponseEntity<?> getByAtribute(
                @RequestParam(required = false) String codigoTracking,
                @RequestParam(required = false) String ipUsuario,
                @RequestParam(required = false) String fecha){

                List<String> params = new ArrayList<>(List.of(codigoTracking, ipUsuario, fecha));

                int num_null = 0;
                for(String value: params){
                        if(value == null) num_null++;
                }
                
                if(num_null == params.size()) {
                        return ResponseEntity.badRequest().body("Debe proporcionar un atributo de búsqueda valido");
                }else if(num_null > 1){
                        return ResponseEntity.badRequest().body("Solo se permite un atributo de búsqueda a la vez");
                }else if(codigoTracking != null) {
                        log.info(">>> Buscando consulta por codigo de tracking: {}", codigoTracking);
                        return ResponseEntity.ok(consultaService.getByCodigoTrackingConsultado(codigoTracking));
                }else if(ipUsuario != null){
                        log.info(">>> Buscando consulta por ip de usuario: {}", ipUsuario);
                        return ResponseEntity.ok(consultaService.getByIpUsuario(ipUsuario));
                }else if(fecha != null){
                        LocalDate fecha_formateada = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd-MM-YY"));
                        log.info(">>> Buscando consulta por fecha: {}", fecha);
                        return ResponseEntity.ok(consultaService.getByFecha(fecha_formateada));
                }else{
                        return ResponseEntity.internalServerError().body("Solo se permite un atributo de búsqueda a la vez");
                }
        }

        @PostMapping 
        public ResponseEntity<ConsultaResponseDTO> create(@Valid @RequestBody ConsultaRequestDTO dto) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(consultaService.create(dto));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<String> delete(@PathVariable Long id) {
                if (consultaService.delete(id)) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Consulta eliminada");
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consulta no encontrada o no se pudo eliminar");
                }
        }
}
