package com.thiago.eventify.mapper;

import com.thiago.eventify.client.dto.WeatherForecastApiResponseDTO;
import com.thiago.eventify.dto.CreateEventDTO;
import com.thiago.eventify.dto.EventDTO;
import com.thiago.eventify.dto.EventWithWeatherForecastDTO;
import com.thiago.eventify.dto.UpdateEventDTO;
import com.thiago.eventify.entity.Event;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class EventMapper {

    public Event toEntity(CreateEventDTO data){
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

    public void updateEntity(UpdateEventDTO data, Event event){
        if (Objects.nonNull(data.title())) event.setTitle(data.title());
        if (Objects.nonNull(data.description())) event.setDescription(data.description());
        if (Objects.nonNull(data.dateTime())) event.setDateTime(data.dateTime());
        if (Objects.nonNull(data.cep())) event.setCep(data.cep());
        if (Objects.nonNull(data.addressNumber())) event.setAddressNumber(data.addressNumber());
    }

    public EventWithWeatherForecastDTO toDTO(Event event, WeatherForecastApiResponseDTO weatherData){
        return new EventWithWeatherForecastDTO(toDTO(event), weatherData.daily());
    }

    public List<EventDTO> toDTOList(List<Event> events){
        return events.stream().map(this::toDTO).toList();
    }

    private EventDTO toDTO(Event event){
        return new EventDTO(event.getId(), event.getTitle(), event.getDescription(),
                event.getDateTime(), event.getCep(), event.getAddress(), event.getAddressNumber(), event.getCity(),
                event.getState(), event.getDistrict());
    }
}
