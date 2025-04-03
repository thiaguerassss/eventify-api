package com.thiago.eventify.mapper;

import com.thiago.eventify.client.dto.WeatherForecastApiResponseDTO;
import com.thiago.eventify.dto.CreateEventDTO;
import com.thiago.eventify.dto.EventDTO;
import com.thiago.eventify.dto.EventWithWeatherForecastDTO;
import com.thiago.eventify.dto.UpdateEventDTO;
import com.thiago.eventify.entity.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventMapper {

    public static Event toEntity(CreateEventDTO data){
        Event event = new Event();
        event.setId(null);
        event.setOwnerId(data.ownerId());
        event.setTitle(data.title());
        event.setDescription(data.description());
        event.setDateTime(data.dateTime());
        event.setCep(data.cep());
        event.setAddressNumber(data.addressNumber());
        return event;
    }

    public static void updateEntity(UpdateEventDTO data, Event event){
        if (Objects.nonNull(data.title())) event.setTitle(data.title());
        if (Objects.nonNull(data.description())) event.setDescription(data.description());
        if (Objects.nonNull(data.dateTime())) event.setDateTime(data.dateTime());
        if (Objects.nonNull(data.cep())) event.setCep(data.cep());
        if (Objects.nonNull(data.addressNumber())) event.setAddressNumber(data.addressNumber());
    }

    public static EventWithWeatherForecastDTO toDTO(Event event, WeatherForecastApiResponseDTO weatherData){
        return new EventWithWeatherForecastDTO(toDTO(event), weatherData.daily());
    }

    public static List<EventDTO> toDTOList(List<Event> events){
        return events.stream().map(EventMapper::toDTO).toList();
    }

    private static EventDTO toDTO(Event event){
        return new EventDTO(event.getId(), event.getTitle(), event.getDescription(),
                event.getDateTime(), event.getCep(), event.getAddress(), event.getAddressNumber(), event.getCity(),
                event.getState(), event.getDistrict());
    }
}
