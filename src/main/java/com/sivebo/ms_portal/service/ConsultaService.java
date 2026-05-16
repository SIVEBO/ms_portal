package com.sivebo.ms_portal.service;

import org.springframework.stereotype.Service;

import com.sivebo.ms_portal.repository.ConsultaRepository;
import com.sivebo.ms_portal.utils.WebClientUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaService {
        
        private final ConsultaRepository consultaRepository;

        private final WebClientUtil webClientUtil;

}
