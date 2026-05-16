package com.sivebo.ms_portal.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaResponseDTO {

        Long id; 
        String codigoTrackingConsultado;
        String ipUsuario;
        LocalDateTime fechaHora;
}
