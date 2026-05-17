package com.sivebo.ms_portal.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sivebo.ms_portal.dto.Request.ConsultaRequestDTO;
import com.sivebo.ms_portal.dto.Response.ConsultaResponseDTO;
import com.sivebo.ms_portal.model.Consulta;
import com.sivebo.ms_portal.repository.ConsultaRepository;
import com.sivebo.ms_portal.utils.WebClientUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaService {
        
        private final ConsultaRepository consultaRepository;
        
        private final WebClientUtil webClientUtil;

        @Qualifier("trackingWebClient")
        private final WebClient trackingWebClient;

        private ConsultaResponseDTO mapToDTO(Consulta consulta) {
                return new ConsultaResponseDTO(
                        consulta.getId(),
                        consulta.getCodigoTrackingConsultado(),
                        consulta.getIpUsuario(),
                        consulta.getFechaHora()
                );
        }

        public List<ConsultaResponseDTO> getAll() {
                return consultaRepository.findAll()
                        .stream()
                        .map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        public Optional<ConsultaResponseDTO> getById(Long id) {
                return consultaRepository.findById(id).map(this::mapToDTO);
        }

        List<ConsultaResponseDTO> getByIpUsuario(String ipUsuario){
                return consultaRepository.findByIpUsuario(ipUsuario)
                        .stream().map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        List<ConsultaResponseDTO> getByCodigoTrackingConsultado(String codigoTrackingConsultado){
                return consultaRepository.findByCodigoTrackingConsultado(codigoTrackingConsultado)
                        .stream().map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        List<ConsultaResponseDTO> getByFecha(LocalDate fecha){
                return consultaRepository.findByFecha(fecha)
                        .stream().map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        public ConsultaResponseDTO create(ConsultaRequestDTO dto){
                webClientUtil.validateMicroServiceByQuery( 
                        "portal",
                        "codigo_tracking",
                        dto.getCodigoTrackingConsultado(),
                        trackingWebClient);
                return mapToDTO(consultaRepository.save(
                        new Consulta(
                        null,
                        dto.getCodigoTrackingConsultado(),
                        dto.getIpUsuario(),
                        dto.getFechaHora()
                        )
                ));
        }

        public Boolean delete(Long id){
                consultaRepository.deleteById(id);
                return !consultaRepository.existsById(id);
        }

}
