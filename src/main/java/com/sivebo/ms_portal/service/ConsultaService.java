package com.sivebo.ms_portal.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.sivebo.ms_portal.dto.Request.ConsultaRequestDTO;
import com.sivebo.ms_portal.dto.Response.ConsultaPublicaResponseDTO;
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
                String codigoTrackingGuia = consulta.getIdGuia() == null ? null : webClientUtil
                        .resolveFieldById(consulta.getIdGuia(), "guias", "codigoTracking", trackingWebClient)
                        .orElse(null);
                String estadoActual = webClientUtil
                        .resolveEstadoActualByGuiaId(consulta.getIdGuia(), trackingWebClient)
                        .orElse(null);
                return new ConsultaResponseDTO(
                        consulta.getCodigoTrackingConsultado(),
                        codigoTrackingGuia,
                        estadoActual,
                        consulta.getIpUsuario(),
                        consulta.getFechaHora()
                );
        }

        @Transactional(readOnly = true)
        public List<ConsultaResponseDTO> getAll() {
                return consultaRepository.findAll()
                        .stream()
                        .map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public Optional<ConsultaResponseDTO> getById(Long id) {
                return consultaRepository.findById(id).map(this::mapToDTO);
        }

        @Transactional(readOnly = true)
        public List<ConsultaResponseDTO> getByIpUsuario(String ipUsuario) {
                return consultaRepository.findByIpUsuario(ipUsuario)
                        .stream().map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<ConsultaResponseDTO> getByCodigoTrackingConsultado(String codigoTrackingConsultado) {
                return consultaRepository.findByCodigoTrackingConsultado(codigoTrackingConsultado)
                        .stream().map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<ConsultaResponseDTO> getByFecha(LocalDate fecha) {
                LocalDateTime comienzo = fecha.atStartOfDay();
                LocalDateTime fin = fecha.atTime(LocalTime.MAX);
                return consultaRepository.findByFechaHoraBetween(comienzo, fin)
                        .stream().map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<ConsultaPublicaResponseDTO> getAllPublica() {
                return consultaRepository.findAll().stream()
                        .map(c -> new ConsultaPublicaResponseDTO(
                                c.getCodigoTrackingConsultado(),
                                c.getIdGuia() != null,
                                c.getIpUsuario(),
                                c.getFechaHora()))
                        .toList();
        }

        @Transactional
        public ConsultaResponseDTO create(ConsultaRequestDTO dto) {
                Long idGuia = webClientUtil.resolveGuiaIdByCodigoTracking(
                                dto.getCodigoTrackingConsultado(), trackingWebClient)
                                .orElse(null);
                return mapToDTO(consultaRepository.save(
                        new Consulta(
                                null,
                                dto.getCodigoTrackingConsultado(),
                                idGuia,
                                dto.getIpUsuario(),
                                dto.getFechaHora()
                        )
                ));
        }

        @Transactional
        public Boolean delete(Long id) {
                consultaRepository.deleteById(id);
                return !consultaRepository.existsById(id);
        }
}
