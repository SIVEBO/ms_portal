package com.sivebo.ms_portal.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sivebo.ms_portal.model.Consulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
        
        List<Consulta> findByIpUsuario(String ipUsuario);

        List<Consulta> findByCodigoTrackingConsultado(String codigoTrackingConsultado);

        @Query("Select date FROM consulta WHERE DATE(date.fecha_hora) = :fecha")
        List<Consulta> findByFecha(LocalDate fecha);
}
