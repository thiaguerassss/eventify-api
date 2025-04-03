package com.thiago.eventify.dto;

import com.thiago.eventify.client.dto.DailyDTO;

public record EventWithWeatherForecastDTO(EventDTO event, DailyDTO weatherForecast) {
}
