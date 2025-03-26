package com.thiago.eventify.mapper;

import com.thiago.eventify.dto.CreateEventDTO;
import com.thiago.eventify.dto.EventInListDTO;
import com.thiago.eventify.entity.Event;

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

    public static EventInListDTO toDTO(Event event){
        return new EventInListDTO(event.getId(), event.getTitle(), event.getDescription(), event.getDateTime(),
                event.getCep(), event.getAddress(), event.getAddressNumber(), event.getCity(), event.getState(),
                event.getCountry());
    }
}
