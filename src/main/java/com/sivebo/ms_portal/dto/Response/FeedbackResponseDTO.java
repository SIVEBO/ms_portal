package com.sivebo.ms_portal.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDTO {
        
        String codigoTracking; 
        String nroDocumentoCliente;
        Integer calificacion; 
        String comentario;      
}
