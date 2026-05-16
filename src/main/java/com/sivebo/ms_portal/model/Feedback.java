package com.sivebo.ms_portal.model;

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
@Table(name = "feedback")
public class Feedback {
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        @Column(name = "id_guia_tracking", nullable = false)
        Long idGuiaTracking; 

        @Column(name = "calificacion", nullable = false)
        Integer calificacion;

        @Column(name = "comentario", nullable = true, length=255)
        String comentario;
}
