package com.thiago.eventify.controller;

import com.thiago.eventify.client.dto.WeatherForecastApiResponseDTO;
import com.thiago.eventify.dto.*;
import com.thiago.eventify.entity.Event;
import com.thiago.eventify.entity.User;
import com.thiago.eventify.mapper.EventMapper;
import com.thiago.eventify.mapper.UserMapper;
import com.thiago.eventify.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    public EventController(EventService eventService, EventMapper eventMapper, UserMapper userMapper){
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventDTO>> findAll(){
        List<Event> events = this.eventService.findAll();
        List<EventDTO> eventsDTO = this.eventMapper.toDTOList(events);
        return ResponseEntity.ok(eventsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventWithWeatherForecastDTO> findById(@PathVariable("id") UUID id){
        Event event = this.eventService.findById(id);
        WeatherForecastApiResponseDTO weatherForecast = this.eventService.getEventWeatherInfo(event);
        EventWithWeatherForecastDTO eventDTO = this.eventMapper.toDTO(event, weatherForecast);
        return ResponseEntity.ok(eventDTO);
    }

    @PostMapping
    public ResponseEntity<EventWithWeatherForecastDTO> create(@RequestBody @Valid CreateEventDTO data,
                                                              @RequestParam("ownerPin") String pin){
        Event event = this.eventService.create(data, pin);
        WeatherForecastApiResponseDTO weatherForecast = this.eventService.getEventWeatherInfo(event);
        EventWithWeatherForecastDTO eventDTO = this.eventMapper.toDTO(event, weatherForecast);
        URI location = URI.create("/event/" + event.getId());
        return ResponseEntity.created(location).body(eventDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventWithWeatherForecastDTO> update(@PathVariable("id") UUID id,
                                                              @RequestBody @Valid UpdateEventDTO data,
                                                              @RequestParam("ownerId") UUID ownerId,
                                                              @RequestParam("ownerPin") String ownerPin){
        Event event = this.eventService.update(id, ownerId, ownerPin, data);
        WeatherForecastApiResponseDTO weatherForecast = this.eventService.getEventWeatherInfo(event);
        EventWithWeatherForecastDTO eventDTO = this.eventMapper.toDTO(event, weatherForecast);
        return ResponseEntity.ok(eventDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @RequestParam("ownerId") UUID ownerId,
                                       @RequestParam("ownerPin") String ownerPin){
        this.eventService.delete(id, ownerId, ownerPin);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/participant/{userId}")
    public ResponseEntity<Void> registerParticipant(@PathVariable UUID id, @PathVariable UUID userId,
                                                    @RequestParam("userPin") String userPin){
        this.eventService.registerParticipant(id, userId, userPin);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/participant/{userId}")
    public ResponseEntity<Void> unregisterParticipant(@PathVariable UUID id, @PathVariable UUID userId,
                                                    @RequestParam("userPin") String userPin){
        this.eventService.unregisterParticipant(id, userId, userPin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<UserDTO>> findParticipantsByEvent(@PathVariable("id") UUID id){
        Set<User> participants = this.eventService.findAllParticipants(id);
        List<UserDTO> participantsDTO = this.userMapper.toDTOList(participants.stream().toList());
        return ResponseEntity.ok(participantsDTO);
    }
}
