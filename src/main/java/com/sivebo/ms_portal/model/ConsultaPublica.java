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
@Table(name = "consulta_publica")
public class ConsultaPublica {
        
        @Id
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        Long id; 

        @Column(name = "codigo_tracking_consultado", nullable = false, length=12)
        String codigo_tracking_consultado;

        @Column(name = "ip_usuario", nullable = false, length=15)
        String ip_usuario;

        @Column(name = "fecha_hora", nullable = false)
        LocalDateTime fecha_hora;
}
