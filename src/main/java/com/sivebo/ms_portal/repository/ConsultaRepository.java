package com.sivebo.ms_portal.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_portal.model.Consulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
        
        List<Consulta> findByIpUsuario(String ip_usuario);

        List<Consulta> findByCodigoTrackingConsultado(String codigo_tracking_consultado);

        Optional<Consulta> findByFechaHora(LocalDateTime fecha_hora);
}
