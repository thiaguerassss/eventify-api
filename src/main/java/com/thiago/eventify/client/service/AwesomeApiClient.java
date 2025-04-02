package com.thiago.eventify.client.service;

import com.thiago.eventify.client.dto.AddressDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "AwesomeApiClient",
        url = "https://cep.awesomeapi.com.br"
)
public interface AwesomeApiClient {

    @GetMapping("/json/{cep}")
    AddressDTO addressInfo(@PathVariable("cep") String cep);
}
