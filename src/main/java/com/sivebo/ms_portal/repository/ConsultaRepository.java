package com.sivebo.ms_portal.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_portal.model.Consulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
        
        List<Consulta> findByIpUsuario(String ipUsuario);

        List<Consulta> findByCodigoTrackingConsultado(String codigoTrackingConsultado);

        List<Consulta> findByFechaHoraBetween(LocalDateTime comienzo, LocalDateTime fin);
}
