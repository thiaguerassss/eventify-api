package com.thiago.eventify.client.service;

import com.thiago.eventify.client.dto.WeatherForecastApiResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "WeatherForecastApiClient",
        url = "https://api.open-meteo.com"
)
public interface WeatherForecastApiClient {

    @GetMapping("/v1/forecast?daily=temperature_2m_max,apparent_temperature_max,apparent_temperature_min," +
            "precipitation_probability_max,wind_speed_10m_max,wind_gusts_10m_max,wind_direction_10m_dominant," +
            "precipitation_hours,temperature_2m_min&timezone=auto&forecast_days=14")
    WeatherForecastApiResponseDTO weatherInfo(@RequestParam("latitude") Double latitude,
                                              @RequestParam("longitude") Double longitude);
}
