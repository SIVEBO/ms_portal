package com.sivebo.ms_portal.utils;

import java.util.Map;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.sivebo.ms_portal.exception.MicroserviceUnavailableException;
import com.sivebo.ms_portal.exception.MicroserviceValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebClientUtil {

	public void validateMicroServiceById(Long id, String name_service, WebClient webClient) {
                try {
			webClient.get()
				.uri("/api/v1/" + name_service + "/{id}", id)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		log.info(">>> " + name_service + " {} validada correctamente (WebCliente)", id);

                } catch (WebClientResponseException.NotFound webException) {
			throw new MicroserviceValidationException(
				name_service + " con id " + id + " no existe en el microservicio.");
                } catch (Exception exception) {
			throw new MicroserviceUnavailableException(
				"No se pudo conectar con el microservicio: " + exception.getMessage());
		}
	}

	public Optional<String> resolveFieldById(Long id, String servicePath, String fieldName, WebClient webClient) {
		if (id == null) return Optional.empty();
		try {
			Map<String, Object> response = webClient.get()
					.uri("/api/v1/{path}/{id}", servicePath, id)
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
					.block();
			return Optional.ofNullable(response)
					.map(r -> r.get(fieldName))
					.map(Object::toString);
		} catch (WebClientResponseException.NotFound e) {
			return Optional.empty();
		} catch (Exception e) {
			log.warn("No se pudo resolver {}/{}/{}: {}", servicePath, id, fieldName, e.getMessage());
			return Optional.empty();
		}
	}

	public Optional<Long> resolveGuiaIdByCodigoTracking(String codigoTracking, WebClient webClient) {
		try {
			Map<String, Object> response = webClient.get()
					.uri("/api/v1/guias/buscar?codigo_tracking={ct}", codigoTracking)
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
					.block();
			return Optional.ofNullable(response)
					.map(r -> ((Number) r.get("id")).longValue());
		} catch (WebClientResponseException.NotFound e) {
			return Optional.empty();
		} catch (Exception e) {
			throw new MicroserviceUnavailableException(
					"No se pudo conectar con el microservicio de tracking: " + e.getMessage());
		}
	}

	public void validateMicroServiceByQuery(String name_service, String query, String value, WebClient webClient) {
                try {
			webClient.get()
				.uri("/api/v1/" + name_service + "/search?" + query + "=" + value)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		log.info(">>> " + name_service + " {} validada correctamente (WebCliente)", value);

                } catch (WebClientResponseException.NotFound webException) {
			throw new MicroserviceValidationException(
				name_service + " con " + query + "=" + value + " no existe en el microservicio.");
                } catch (Exception exception) {
			throw new MicroserviceUnavailableException(
				"No se pudo conectar con el microservicio: " + exception.getMessage());
		}
	}
}
