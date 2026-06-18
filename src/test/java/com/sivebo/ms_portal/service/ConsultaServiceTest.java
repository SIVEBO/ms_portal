package com.sivebo.ms_portal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

	@Mock
	ConsultaRepository consultaRepository;

	@Mock
	WebClientUtil webClientUtil;

	@Mock
	WebClient trackingWebClient;

	@InjectMocks
	ConsultaService consultaService;

	@Test
	void getAll_mapsEntityFieldsToDto() {
		LocalDateTime now = LocalDateTime.now();
		Consulta consulta = new Consulta(1L, "ABC123456789", 99L, "127.0.0.1", now);
		when(consultaRepository.findAll()).thenReturn(List.of(consulta));

		List<ConsultaResponseDTO> result = consultaService.getAll();

		assertThat(result).hasSize(1);
		ConsultaResponseDTO dto = result.get(0);
		assertThat(dto.getCodigoTrackingConsultado()).isEqualTo("ABC123456789");
		assertThat(dto.getCodigoTrackingGuia()).isEqualTo("99");
		assertThat(dto.getIpUsuario()).isEqualTo("127.0.0.1");
		assertThat(dto.getFechaHora()).isEqualTo(now);
	}

	@Test
	void getById_returnsMappedDto() {
		LocalDateTime now = LocalDateTime.now();
		when(consultaRepository.findById(1L))
				.thenReturn(Optional.of(new Consulta(1L, "ABC123456789", 7L, "10.0.0.1", now)));

		Optional<ConsultaResponseDTO> result = consultaService.getById(1L);

		assertThat(result).isPresent();
		assertThat(result.get().getCodigoTrackingGuia()).isEqualTo("7");
	}

	@Test
	void create_validatesTrackingThenPersists() {
		LocalDateTime now = LocalDateTime.now();
		ConsultaRequestDTO request = new ConsultaRequestDTO("ABC123456789", 99L, "127.0.0.1", now);
		when(consultaRepository.save(any(Consulta.class)))
				.thenReturn(new Consulta(1L, "ABC123456789", 99L, "127.0.0.1", now));

		ConsultaResponseDTO result = consultaService.create(request);

		verify(webClientUtil).validateMicroServiceByQuery(
				eq("portal"), eq("codigo_tracking"), eq("ABC123456789"), eq(trackingWebClient));
		assertThat(result.getCodigoTrackingConsultado()).isEqualTo("ABC123456789");
		assertThat(result.getCodigoTrackingGuia()).isEqualTo("99");
	}
}
