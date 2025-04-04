package com.thiago.eventify.service;

import com.thiago.eventify.client.dto.AwesomeApiResponseDTO;
import com.thiago.eventify.client.dto.WeatherForecastApiResponseDTO;
import com.thiago.eventify.client.service.AwesomeApiClient;
import com.thiago.eventify.client.service.WeatherForecastApiClient;
import com.thiago.eventify.dto.CreateEventDTO;
import com.thiago.eventify.dto.UpdateEventDTO;
import com.thiago.eventify.entity.Event;
import com.thiago.eventify.entity.User;
import com.thiago.eventify.exception.*;
import com.thiago.eventify.mapper.EventMapper;
import com.thiago.eventify.repository.EventRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.HOURS;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final AwesomeApiClient awesomeApiClient;
    private final WeatherForecastApiClient weatherForecastApiClient;

    public EventService(EventRepository eventRepository, UserService userService, AwesomeApiClient awesomeApiClient,
                        WeatherForecastApiClient weatherForecastApiClient){
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.awesomeApiClient = awesomeApiClient;
        this.weatherForecastApiClient = weatherForecastApiClient;
    }

    public Event findById(UUID id){
        return this.eventRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                "Evento não encontrado.", id));
    }

    public List<Event> findAll(){
        return this.eventRepository.findAll();
    }

    @Transactional
    public Event create(CreateEventDTO data, String pin){
        this.userService.findByIdAndValidate(data.ownerId(), pin);
        Event event = EventMapper.toEntity(data);
        AwesomeApiResponseDTO addressData = this.getAddressInfo(event);
        this.setAddressInfo(addressData, event);
        return this.eventRepository.save(event);
    }

    @Transactional
    public Event update(UUID id, UUID ownerId, String ownerPin, UpdateEventDTO data){
        Event event = this.findEventAndValidateOwner(id, ownerId, ownerPin);
        this.validateEventUpdate(event, data);
        EventMapper.updateEntity(data, event);
        if (!event.getCep().equals(data.cep())){
            AwesomeApiResponseDTO addressData = this.getAddressInfo(event);
            this.setAddressInfo(addressData, event);
        }
        return this.eventRepository.save(event);
    }

    @Transactional
    public void delete(UUID id, UUID ownerId, String ownerPin){
        Event event = this.findEventAndValidateOwner(id, ownerId, ownerPin);
        this.eventRepository.deleteById(event.getId());
    }

    public WeatherForecastApiResponseDTO getEventWeatherInfo(Event event){
        AwesomeApiResponseDTO addressData = this.getAddressInfo(event);
        Double latitude = addressData.lat();
        Double longitude = addressData.lng();
        return this.weatherForecastApiClient.weatherInfo(latitude, longitude);
    }

    @Transactional
    public void registerParticipant(UUID eventId, UUID userId, String userPin){
        Event event = this.findById(eventId);
        User user = this.userService.findByIdAndValidate(userId, userPin);
        this.validateEventRegistration(event, user);
        event.getParticipants().add(user);
        user.getParticipatingEvents().add(event);
        this.eventRepository.save(event);
    }

    @Transactional
    public void unregisterParticipant(UUID eventId, UUID userId, String userPin){
        Event event = this.findById(eventId);
        User user = this.userService.findByIdAndValidate(userId, userPin);
        this.validateEventUnregistration(event, user);
        event.getParticipants().remove(user);
        user.getParticipatingEvents().remove(event);
        this.eventRepository.save(event);
    }

    private Event findEventAndValidateOwner(UUID id, UUID ownerId, String ownerPin){
        Event event = this.findById(id);
        this.userService.findByIdAndValidate(ownerId, ownerPin);
        if (!event.getOwnerId().equals(ownerId)) throw new AccessDeniedException(
                "Acesso negado: o usuário informado não é o dono do evento.");
        return event;
    }

    private void validateEventUpdate(Event event, UpdateEventDTO data){
        LocalDate today = LocalDate.now();
        LocalDate eventDay = event.getDateTime().toLocalDate();
        LocalDate newEventDay = data.dateTime().toLocalDate();
        if (today.isEqual(eventDay)){
            if (!newEventDay.isEqual(eventDay)){
                throw new ForbiddenEventUpdateException(
                        "Não é permitido alterar a data no dia do evento, somente o horário.");
            }
            LocalTime now = LocalTime.now();
            LocalTime eventTime = event.getDateTime().toLocalTime();
            if(HOURS.between(now, eventTime) < 4){
                throw new ForbiddenEventUpdateException(
                        "Não é permitido alterar o horário do evento sem no mínimo 4 horas de antecedência.");
            }
        }
    }

    private void validateEventRegistration(Event event, User user) {
        if (event.getParticipants().contains(user)){
            throw new ForbiddenRegisterException("Usuário já inscrito no evento.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(event.getDateTime())) {
            throw new ForbiddenRegisterException("Inscrição para o evento proibida: o evento já aconteceu.");
        }
        if (now.isAfter(event.getDateTime().minusMinutes(30))) {
            throw new ForbiddenRegisterException("Inscrição para o evento proibida: as inscrições estão fechadas.");
        }
    }

    private void validateEventUnregistration(Event event, User user){
        if (!event.getParticipants().contains(user)){
            throw new ImpossibleUnregisterException("Usuário não está inscrito neste evento.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(event.getDateTime())){
            throw new ImpossibleUnregisterException("Não é possível cancelar a inscrição: o evento já passou.");
        }
        if (now.isAfter(event.getDateTime().minusMinutes(15))){
            throw new ImpossibleUnregisterException("Não é possível cancelar a inscrição faltando 15 minutos para " +
                    "o início do evento.");
        }
    }

    private AwesomeApiResponseDTO getAddressInfo(Event event){
        String eventCep = event.getCep().replace("-", "");
        AwesomeApiResponseDTO addressData = this.awesomeApiClient.addressInfo(eventCep);
        if (Objects.nonNull(addressData.status())){
            if (addressData.status().equals(404)) throw new InvalidInputException(
                    "O CEP informado não existe na base de dados.");
        }
        return addressData;
    }

    private void setAddressInfo(AwesomeApiResponseDTO addressData, Event event){
        event.setAddress(addressData.address());
        event.setCity(addressData.city());
        event.setState(addressData.state());
        event.setDistrict(addressData.district());
    }
}
