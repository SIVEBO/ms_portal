package com.sivebo.ms_portal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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

import com.sivebo.ms_portal.dto.Request.FeedbackRequestDTO;
import com.sivebo.ms_portal.dto.Response.FeedbackResponseDTO;
import com.sivebo.ms_portal.model.Feedback;
import com.sivebo.ms_portal.repository.FeedbackRepository;
import com.sivebo.ms_portal.utils.WebClientUtil;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

        @Mock FeedbackRepository feedbackRepository;
        @Mock WebClientUtil webClientUtil;
        @Mock WebClient trackingWebClient;
        @Mock WebClient clientesWebClient;

        @InjectMocks FeedbackService feedbackService;

        private final LocalDateTime now = LocalDateTime.now();

        @Test
        void getAllMapsEntityFieldsToDto() {
                Feedback feedback = new Feedback(1L, 50L, 1234L, 5, "Excelente", now);
                when(feedbackRepository.findAll()).thenReturn(List.of(feedback));
                when(webClientUtil.resolveFieldById(anyLong(), anyString(), anyString(), any()))
                        .thenReturn(Optional.empty());

                List<FeedbackResponseDTO> result = feedbackService.getAll();

                assertThat(result).hasSize(1);
                assertThat(result.get(0).getCalificacion()).isEqualTo(5);
                assertThat(result.get(0).getComentario()).isEqualTo("Excelente");
                assertThat(result.get(0).getFechaHora()).isEqualTo(now);
        }

        @Test
        void getByCalificacionReturnsList() {
                Feedback f1 = new Feedback(1L, 10L, null, 4, null, now);
                Feedback f2 = new Feedback(2L, 20L, null, 4, "Bien", now);
                when(feedbackRepository.findByCalificacion(4)).thenReturn(List.of(f1, f2));
                when(webClientUtil.resolveFieldById(anyLong(), anyString(), anyString(), any()))
                        .thenReturn(Optional.empty());

                List<FeedbackResponseDTO> result = feedbackService.getByCalificacion(4);

                assertThat(result).hasSize(2);
        }

        @Test
        void createWithKnownGuiaSavesFeedback() {
                FeedbackRequestDTO dto = new FeedbackRequestDTO(50L, 1234L, 4, "Bien", now);
                Feedback saved = new Feedback(1L, 50L, 1234L, 4, "Bien", now);

                doNothing().when(webClientUtil).validateMicroServiceById(eq(50L), eq("guias"), any(WebClient.class));
                when(feedbackRepository.save(any())).thenReturn(saved);
                when(webClientUtil.resolveFieldById(anyLong(), anyString(), anyString(), any()))
                        .thenReturn(Optional.empty());

                FeedbackResponseDTO result = feedbackService.create(dto);

                verify(webClientUtil).validateMicroServiceById(eq(50L), eq("guias"), any(WebClient.class));
                verify(feedbackRepository).save(any());
                assertThat(result.getCalificacion()).isEqualTo(4);
        }

        @Test
        void createWithAnonymousClientSavesNullIdCliente() {
                FeedbackRequestDTO dto = new FeedbackRequestDTO(50L, null, 3, null, now);
                Feedback saved = new Feedback(2L, 50L, null, 3, null, now);

                doNothing().when(webClientUtil).validateMicroServiceById(eq(50L), eq("guias"), any(WebClient.class));
                when(feedbackRepository.save(any())).thenReturn(saved);
                when(webClientUtil.resolveFieldById(anyLong(), anyString(), anyString(), any()))
                        .thenReturn(Optional.empty());

                feedbackService.create(dto);

                ArgumentCaptor<Feedback> captor = ArgumentCaptor.forClass(Feedback.class);
                verify(feedbackRepository).save(captor.capture());
                assertThat(captor.getValue().getIdCliente()).isNull();
        }
}
