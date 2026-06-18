package com.sivebo.ms_portal.dto.Request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaRequestDTO {

        @Size(min = 12, max = 12, message = "El código de tracking debe tener exactamente 12 caracteres")
        @NotBlank(message = "El código de tracking es obligatorio")
        String codigoTrackingConsultado;

        @NotBlank(message = "La IP del usuario es obligatoria")
        String ipUsuario;

        @NotNull(message = "La fecha y hora de la consulta es obligatoria")
        LocalDateTime fechaHora;
        
}
