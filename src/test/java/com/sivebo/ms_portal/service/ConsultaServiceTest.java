package com.sivebo.ms_portal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.sivebo.ms_portal.dto.Request.ConsultaRequestDTO;
import com.sivebo.ms_portal.dto.Response.ConsultaResponseDTO;
import com.sivebo.ms_portal.model.Consulta;
import com.sivebo.ms_portal.repository.ConsultaRepository;
import com.sivebo.ms_portal.utils.WebClientUtil;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

        @Mock ConsultaRepository consultaRepository;
        @Mock WebClientUtil webClientUtil;
        @Mock WebClient trackingWebClient;

        @InjectMocks ConsultaService consultaService;

        private final LocalDateTime now = LocalDateTime.now();

        @Test
        void getAllMapsEntityFieldsToDto() {
                Consulta consulta = new Consulta(1L, "ABC123456789", 3L, "192.168.0.1", now);
                when(consultaRepository.findAll()).thenReturn(List.of(consulta));
                when(webClientUtil.resolveFieldById(anyLong(), anyString(), anyString(), any()))
                        .thenReturn(Optional.empty());
                when(webClientUtil.resolveEstadoActualByGuiaId(anyLong(), any()))
                        .thenReturn(Optional.empty());

                List<ConsultaResponseDTO> result = consultaService.getAll();

                assertThat(result).hasSize(1);
                assertThat(result.get(0).getCodigoTrackingConsultado()).isEqualTo("ABC123456789");
                assertThat(result.get(0).getIpUsuario()).isEqualTo("192.168.0.1");
                assertThat(result.get(0).getFechaHora()).isEqualTo(now);
        }

        @Test
        void getByIdPresentReturnsMappedDto() {
                Consulta consulta = new Consulta(1L, "ABC123456789", null, "10.0.0.1", now);
                when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

                Optional<ConsultaResponseDTO> result = consultaService.getById(1L);

                assertThat(result).isPresent();
                assertThat(result.get().getCodigoTrackingConsultado()).isEqualTo("ABC123456789");
                assertThat(result.get().getCodigoTrackingGuia()).isNull();
        }

        @Test
        void createExistingTrackingSavesWithGuiaId() {
                ConsultaRequestDTO dto = new ConsultaRequestDTO("ABC123456789", "192.168.1.1", now);
                Consulta saved = new Consulta(1L, "ABC123456789", 5L, "192.168.1.1", now);

                when(webClientUtil.resolveGuiaIdByCodigoTracking("ABC123456789", trackingWebClient))
                        .thenReturn(Optional.of(5L));
                when(consultaRepository.save(any())).thenReturn(saved);
                when(webClientUtil.resolveFieldById(anyLong(), anyString(), anyString(), any()))
                        .thenReturn(Optional.empty());
                when(webClientUtil.resolveEstadoActualByGuiaId(anyLong(), any()))
                        .thenReturn(Optional.empty());

                consultaService.create(dto);

                ArgumentCaptor<Consulta> captor = ArgumentCaptor.forClass(Consulta.class);
                verify(consultaRepository).save(captor.capture());
                assertThat(captor.getValue().getIdGuia()).isEqualTo(5L);
        }

        @Test
        void createUnknownTrackingSavesWithNullGuiaId() {
                ConsultaRequestDTO dto = new ConsultaRequestDTO("NOTFOUND12345", "10.0.0.1", now);
                Consulta saved = new Consulta(2L, "NOTFOUND12345", null, "10.0.0.1", now);

                when(webClientUtil.resolveGuiaIdByCodigoTracking("NOTFOUND12345", trackingWebClient))
                        .thenReturn(Optional.empty());
                when(consultaRepository.save(any())).thenReturn(saved);

                consultaService.create(dto);

                ArgumentCaptor<Consulta> captor = ArgumentCaptor.forClass(Consulta.class);
                verify(consultaRepository).save(captor.capture());
                assertThat(captor.getValue().getIdGuia()).isNull();
        }

        @Test
        void deleteExistingIdReturnsTrue() {
                when(consultaRepository.existsById(1L)).thenReturn(false);

                Boolean result = consultaService.delete(1L);

                verify(consultaRepository).deleteById(1L);
                assertThat(result).isTrue();
        }
}
