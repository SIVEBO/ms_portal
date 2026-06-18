package com.sivebo.ms_portal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

	@Mock
	FeedbackRepository feedbackRepository;

	@Mock
	WebClientUtil webClientUtil;

	@Mock
	WebClient trackingWebClient;

	@InjectMocks
	FeedbackService feedbackService;

	@Test
	void getAll_mapsEntityFieldsToDto() {
		Feedback feedback = new Feedback(1L, 50L, 1234L, 5, "Excelente");
		when(feedbackRepository.findAll()).thenReturn(List.of(feedback));

		List<FeedbackResponseDTO> result = feedbackService.getAll();

		assertThat(result).hasSize(1);
		FeedbackResponseDTO dto = result.get(0);
		assertThat(dto.getCodigoTracking()).isEqualTo("50");
		assertThat(dto.getNroDocumentoCliente()).isEqualTo("1234");
		assertThat(dto.getCalificacion()).isEqualTo(5);
		assertThat(dto.getComentario()).isEqualTo("Excelente");
	}

	@Test
	void getById_returnsMappedDto() {
		when(feedbackRepository.findById(1L))
				.thenReturn(Optional.of(new Feedback(1L, 50L, 1234L, 3, "Regular")));

		Optional<FeedbackResponseDTO> result = feedbackService.getById(1L);

		assertThat(result).isPresent();
		assertThat(result.get().getNroDocumentoCliente()).isEqualTo("1234");
	}

	@Test
	void create_validatesTrackingThenPersists() {
		FeedbackRequestDTO request = new FeedbackRequestDTO(50L, 1234L, 4, "Bien");
		when(feedbackRepository.save(any(Feedback.class)))
				.thenReturn(new Feedback(1L, 50L, 1234L, 4, "Bien"));

		FeedbackResponseDTO result = feedbackService.create(request);

		verify(webClientUtil).validateMicroServiceByQuery(
				eq("portal"), eq("codigo_tracking"), eq("50"), eq(trackingWebClient));
		assertThat(result.getCodigoTracking()).isEqualTo("50");
		assertThat(result.getNroDocumentoCliente()).isEqualTo("1234");
	}
}
