package com.thiago.eventify.mapper;

import com.thiago.eventify.client.dto.DailyDTO;
import com.thiago.eventify.client.dto.WeatherForecastApiResponseDTO;
import com.thiago.eventify.dto.CreateEventDTO;
import com.thiago.eventify.dto.EventDTO;
import com.thiago.eventify.dto.EventWithWeatherForecastDTO;
import com.thiago.eventify.dto.UpdateEventDTO;
import com.thiago.eventify.entity.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventMapperTest {

    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        eventMapper = new EventMapper();
    }

    @Nested
    class toEntity {

        @Test
        @DisplayName("Should return an event with success")
        void shouldReturnAnEventWithSuccess() {
            CreateEventDTO data = new CreateEventDTO(
                    UUID.randomUUID(),
                    "Barbecue",
                    "Sunday BBQ with friends",
                    LocalDateTime.of(2025, 5, 20, 14, 0),
                    "123456-78",
                    "123"
            );

            Event output = eventMapper.toEntity(data);

            assertNotNull(output);
            assertNull(output.getId());
            assertEquals(data.ownerId(), output.getOwnerId());
            assertEquals(data.title(), output.getTitle());
            assertEquals(data.description(), output.getDescription());
            assertEquals(data.dateTime(), output.getDateTime());
            assertEquals(data.cep(), output.getCep());
            assertEquals(data.addressNumber(), output.getAddressNumber());
        }
    }

    @Nested
    class updateEntity {

        @Test
        @DisplayName("Should update event with success")
        void shouldUpdateEventWithSuccess() {
            UpdateEventDTO updateData = new UpdateEventDTO(
                    "Updated Title",
                    "Updated description",
                    LocalDateTime.of(2025, 5, 25, 16, 0),
                    "876543-21",
                    "456"
            );
            Event event = new Event(UUID.randomUUID(),UUID.randomUUID(), "Old Title", "Old Description",
                    LocalDateTime.of(2025, 4, 25, 15, 0), "123456-78",
                    "Old Address","123", "Old City", "Old State", "Old District");

            eventMapper.updateEntity(updateData, event);

            assertEquals(updateData.title(), event.getTitle());
            assertEquals(updateData.description(), event.getDescription());
            assertEquals(updateData.dateTime(), event.getDateTime());
            assertEquals(updateData.cep(), event.getCep());
            assertEquals(updateData.addressNumber(), event.getAddressNumber());
        }

        @Test
        @DisplayName("Should not update any field when all values are null")
        void shouldNotUpdateAnyField() {
            UpdateEventDTO updateData = new UpdateEventDTO(null, null, null, null, null);
            Event event = new Event(UUID.randomUUID(),UUID.randomUUID(), "Original Title", "Original description",
                    LocalDateTime.of(2025, 5, 20, 14, 0), "123456-78",
                    "Old Address","123", "Old City", "Old State", "Old District");

            eventMapper.updateEntity(updateData, event);

            assertEquals("Original Title", event.getTitle());
            assertEquals("Original description", event.getDescription());
            assertEquals(LocalDateTime.of(2025, 5, 20, 14, 0), event.getDateTime());
            assertEquals("123456-78", event.getCep());
            assertEquals("123", event.getAddressNumber());
        }
    }

    @Nested
    class toDTO {

        @Test
        @DisplayName("Should return EventWithWeatherForecastDTO with success")
        void shouldReturnEventWithWeatherForecastDTOWithSuccess() {
            Event event = new Event(UUID.randomUUID(),UUID.randomUUID(), "Title", "Description",
                    LocalDateTime.of(2025, 5, 20, 14, 0), "123456-78",
                    "Address","123", "City", "State", "District");
            List<LocalDate> localDateList = new ArrayList<>();
            List<Double> doubleList = new ArrayList<>();
            List<Integer> integerList = new ArrayList<>();
            DailyDTO weatherData = new DailyDTO(localDateList, doubleList, doubleList, doubleList, doubleList,
                    integerList, doubleList, doubleList, integerList, integerList);
            WeatherForecastApiResponseDTO weather = new WeatherForecastApiResponseDTO(weatherData);

            EventWithWeatherForecastDTO output = eventMapper.toDTO(event, weather);

            assertEquals(event.getId(), output.event().id());
            assertEquals(event.getTitle(), output.event().title());
            assertEquals(event.getDescription(), output.event().description());
            assertEquals(event.getDateTime(), output.event().dateTime());
            assertEquals(event.getCep(), output.event().cep());
            assertEquals(event.getAddress(), output.event().address());
            assertEquals(event.getAddressNumber(), output.event().addressNumber());
            assertEquals(event.getCity(), output.event().city());
            assertEquals(event.getState(), output.event().state());
            assertEquals(event.getDistrict(), output.event().district());
            assertEquals(weather.daily(), output.weatherForecast());
        }
    }

    @Nested
    class toDTOList {

        @Test
        @DisplayName("Should return a list of EventDTO with success")
        void shouldReturnListOfEventDTOWithSuccess() {
            Event event1 = new Event(UUID.randomUUID(),UUID.randomUUID(), "Title 1", "Description 1",
                    LocalDateTime.of(2025, 5, 20, 14, 0), "123456-78",
                    "Address 1","123", "City 1", "State 1", "District 1");
            Event event2 = new Event(UUID.randomUUID(),UUID.randomUUID(), "Title 2", "Description 2",
                    LocalDateTime.of(2025, 5, 21, 14, 0), "876543-21",
                    "Address 2","321", "City 2", "State 2", "District 2");
            List<Event> events = List.of(event1, event2);

            List<EventDTO> dtoList = eventMapper.toDTOList(events);

            assertEquals(event1.getId(), dtoList.get(0).id());
            assertEquals(event1.getTitle(), dtoList.get(0).title());
            assertEquals(event1.getDescription(), dtoList.get(0).description());
            assertEquals(event1.getDateTime(), dtoList.get(0).dateTime());
            assertEquals(event1.getCep(), dtoList.get(0).cep());
            assertEquals(event1.getAddress(), dtoList.get(0).address());
            assertEquals(event1.getAddressNumber(), dtoList.get(0).addressNumber());
            assertEquals(event1.getCity(), dtoList.get(0).city());
            assertEquals(event1.getState(), dtoList.get(0).state());
            assertEquals(event1.getDistrict(), dtoList.get(0).district());
            assertEquals(event2.getId(), dtoList.get(1).id());
            assertEquals(event2.getTitle(), dtoList.get(1).title());
            assertEquals(event2.getDescription(), dtoList.get(1).description());
            assertEquals(event2.getDateTime(), dtoList.get(1).dateTime());
            assertEquals(event2.getCep(), dtoList.get(1).cep());
            assertEquals(event2.getAddress(), dtoList.get(1).address());
            assertEquals(event2.getAddressNumber(), dtoList.get(1).addressNumber());
            assertEquals(event2.getCity(), dtoList.get(1).city());
            assertEquals(event2.getState(), dtoList.get(1).state());
            assertEquals(event2.getDistrict(), dtoList.get(1).district());
        }
    }
}
