package com.thiago.eventify.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.thiago.eventify.client.dto.AwesomeApiResponseDTO;
import com.thiago.eventify.client.service.AwesomeApiClient;
import com.thiago.eventify.client.service.WeatherForecastApiClient;
import com.thiago.eventify.dto.*;
import com.thiago.eventify.entity.Event;
import com.thiago.eventify.entity.User;
import com.thiago.eventify.mapper.EventMapper;
import com.thiago.eventify.repository.EventRepository;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private UserService userService;
    @Mock private EventMapper eventMapper;
    @Mock private AwesomeApiClient awesomeApiClient;
    @Mock private WeatherForecastApiClient weatherForecastApiClient;

    @InjectMocks
    private EventService eventService;

    private UUID eventId;
    private UUID userId;
    private String pin;
    private Event event;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();
        pin = "1234";
        event = new Event();
        event.setId(eventId);
        event.setOwnerId(userId);
        event.setTitle("Sample");
        event.setDescription("Sample Desc");
        event.setDateTime(LocalDateTime.now().plusDays(1));
        event.setCep("12345-678");
        event.setParticipants(new HashSet<>());
    }

    @Nested
    class findById {
        @Test
        @DisplayName("Should find event by id")
        void shouldFindById() {
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            Event output = eventService.findById(eventId);

            assertNotNull(output);
            assertEquals(eventId, output.getId());
        }

        @Test
        @DisplayName("Should throw ObjectNotFoundException if event not found")
        void shouldThrowWhenEventNotFound() {
            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            assertThrows(ObjectNotFoundException.class, () -> eventService.findById(eventId));
        }
    }

    @Nested
    class findAll {

        @Test
        @DisplayName("Should return all events")
        void shouldReturnAllEvents() {
            List<Event> events = List.of(event);
            when(eventRepository.findAll()).thenReturn(events);

            List<Event> result = eventService.findAll();

            assertEquals(1, result.size());
            assertTrue(result.contains(event));
        }
    }

    @Nested
    class create {

        @Test
        @DisplayName("Should create event successfully")
        void shouldCreateEvent() {
            CreateEventDTO dto = new CreateEventDTO(userId, "Title", "Desc",
                    LocalDateTime.now().plusDays(1), "12345-678", "100");

            when(userService.findByIdAndValidate(userId, pin)).thenReturn(new User());
            when(eventMapper.toEntity(dto)).thenReturn(event);
            when(awesomeApiClient.addressInfo(any())).thenReturn(new AwesomeApiResponseDTO("Address",
                    "District", "State", "City", 200, 1.0, 1.0));
            when(eventRepository.save(any())).thenReturn(event);

            Event output = eventService.create(dto, pin);

            assertNotNull(output);
            verify(eventRepository).save(event);
        }
    }

    @Nested
    class update {

        @Test
        @DisplayName("Should update event successfully")
        void shouldUpdateEvent() {
            UpdateEventDTO data = new UpdateEventDTO("New Title", "New Desc",
                    event.getDateTime().plusHours(5), "87654-321", "200");

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(userService.findByIdAndValidate(userId, pin)).thenReturn(new User());
            doNothing().when(eventMapper).updateEntity(any(), any());
            when(awesomeApiClient.addressInfo(any())).thenReturn(new AwesomeApiResponseDTO("Addr", "Dist",
                    "St", "City", 200, 1.0, 1.0));
            when(eventRepository.save(any())).thenReturn(event);

            Event updated = eventService.update(eventId, userId, pin, data);

            assertNotNull(updated);
            verify(eventMapper).updateEntity(data, event);
            verify(eventRepository).save(event);
        }
    }

    @Nested
    class delete {

        @Test
        @DisplayName("Should delete event successfully")
        void shouldDeleteEvent() {
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(userService.findByIdAndValidate(userId, pin)).thenReturn(new User());

            eventService.delete(eventId, userId, pin);

            verify(eventRepository).deleteById(eventId);
        }
    }

    @Nested
    class registerParticipant {

        @Test
        @DisplayName("Should register participant successfully")
        void shouldRegisterParticipant() {
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setParticipatingEvents(new HashSet<>());

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(userService.findByIdAndValidate(user.getId(), pin)).thenReturn(user);
            when(eventRepository.save(event)).thenReturn(event);

            eventService.registerParticipant(eventId, user.getId(), pin);

            assertTrue(event.getParticipants().contains(user));
            assertTrue(user.getParticipatingEvents().contains(event));
        }
    }

    @Nested
    class unregisterParticipant {

        @Test
        @DisplayName("Should unregister participant successfully")
        void shouldUnregisterParticipant() {
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setParticipatingEvents(new HashSet<>(List.of(event)));
            event.setParticipants(new HashSet<>(List.of(user)));

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(userService.findByIdAndValidate(user.getId(), pin)).thenReturn(user);
            when(eventRepository.save(event)).thenReturn(event);

            eventService.unregisterParticipant(eventId, user.getId(), pin);

            assertFalse(event.getParticipants().contains(user));
            assertFalse(user.getParticipatingEvents().contains(event));
        }
    }

    @Nested
    class findAllParticipants {

        @Test
        @DisplayName("Should return all participants")
        void shouldReturnAllParticipants() {
            User participant = new User();
            participant.setId(UUID.randomUUID());
            Set<User> participants = new HashSet<>(List.of(participant));
            event.setParticipants(participants);
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            Set<User> output = eventService.findAllParticipants(eventId);

            assertEquals(1, output.size());
            assertTrue(output.contains(participant));
        }
    }
}
