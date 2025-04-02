package com.thiago.eventify.client.dto;

public record AwesomeApiResponseDTO(String address, String district, String state, String city, Integer status,
                                    Double lat, Double lng) {
}
