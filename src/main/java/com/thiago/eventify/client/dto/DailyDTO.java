package com.thiago.eventify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record DailyDTO(
        List<LocalDate> time,

        @JsonProperty("temperature_2m_max")
        List<Double> maxTemperature,

        @JsonProperty("temperature_2m_min")
        List<Double> minTemperature,

        @JsonProperty("apparent_temperature_max")
        List<Double> apparentMaxTemperature,

        @JsonProperty("apparent_temperature_min")
        List<Double> apparentMinTemperature,

        @JsonProperty("precipitation_probability_max")
        List<Integer> maxPrecipitationProbability,

        @JsonProperty("wind_speed_10m_max")
        List<Double> maxWindSpeed,

        @JsonProperty("wind_gusts_10m_max")
        List<Double> maxWindGusts,

        @JsonProperty("wind_direction_10m_dominant")
        List<Integer> dominantWindDirection,

        @JsonProperty("precipitation_hours")
        List<Integer> precipitationHours
) {
}
