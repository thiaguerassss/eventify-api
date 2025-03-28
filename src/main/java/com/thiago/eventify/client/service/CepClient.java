package com.thiago.eventify.client.service;

import com.thiago.eventify.client.dto.CepResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "CepClient",
        url = "viacep.com.br/ws"
)
public interface CepClient {

    @GetMapping("/{cep}/json/")
    CepResponseDTO addressInfo(@PathVariable("cep") String cep);
}
