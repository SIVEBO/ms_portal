package com.sivebo.ms_portal.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDTO {
        
        Long id; 
        Long idGuiaTracking; 
        Integer calificacion; 
        String comentario;      
}
