package com.sivebo.ms_portal.dto.Request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDTO {
        
        @NotNull(message = "El id de la guía de tracking es obligatorio")
        Long idGuiaTracking;

        @Min(value = 1, message = "La calificación debe ser al menos 1")
        @Max(value = 5, message = "La calificación debe ser como máximo 5")
        @NotNull(message = "La calificación es obligatoria")
        Integer calificacion;

        @Nullable()
        String comentario;
}
