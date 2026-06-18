package com.sivebo.ms_portal.dto.Response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaResponseDTO {

        String codigoTrackingConsultado;
        String codigoTrackingGuia;
        String ipUsuario;
        LocalDateTime fechaHora;
}
