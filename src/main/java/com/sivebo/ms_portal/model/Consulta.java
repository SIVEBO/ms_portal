package com.sivebo.ms_portal.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CONSULTA_PUBLICA")
public class Consulta {
        
        @Id
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        Long id; 

        @Column(name = "codigo_tracking_consultado", nullable = false, length=12)
        String codigoTrackingConsultado;

        @Column(name = "id_guia")
        Long idGuia;

        @Column(name = "ip_usuario", nullable = false, length=45)
        String ipUsuario;

        @Column(name = "fecha_hora", nullable = false)
        LocalDateTime fechaHora;
}
