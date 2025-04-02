package com.thiago.eventify.service;

import com.thiago.eventify.client.dto.AddressDTO;
import com.thiago.eventify.client.service.AwesomeApiClient;
import com.thiago.eventify.dto.CreateEventDTO;
import com.thiago.eventify.dto.UpdateEventDTO;
import com.thiago.eventify.entity.Event;
import com.thiago.eventify.exception.AccessDeniedException;
import com.thiago.eventify.exception.InvalidInputException;
import com.thiago.eventify.mapper.EventMapper;
import com.thiago.eventify.repository.EventRepository;
import jakarta.validation.Valid;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final AwesomeApiClient awesomeApiClient;

    public EventService(EventRepository eventRepository, UserService userService, AwesomeApiClient awesomeApiClient){
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.awesomeApiClient = awesomeApiClient;
    }

    public Event findById(UUID id){
        return this.eventRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Evento não encontrado.", id));
    }

    public List<Event> findAll(){
        return this.eventRepository.findAll();
    }

    @Transactional
    public Event create(@Valid CreateEventDTO data, String pin){
        this.userService.findByIdAndValidate(data.ownerId(), pin);
        Event event = EventMapper.toEntity(data);
        AddressDTO addressData = this.getAddressInfo(event);
        this.setAddressInfo(addressData, event);
        return this.eventRepository.save(event);
    }

    @Transactional
    public Event update(UUID id, UUID ownerId, String ownerPin, @Valid UpdateEventDTO data){
        Event event = this.findEventAndValidateOwner(id, ownerId, ownerPin);
        EventMapper.updateEntity(data, event);
        if (event.getCep().equals(data.cep())){
            AddressDTO addressData = this.getAddressInfo(event);
            this.setAddressInfo(addressData, event);
        }
        return this.eventRepository.save(event);
    }

    @Transactional
    public void delete(UUID id, UUID ownerId, String ownerPin){
        Event event = this.findEventAndValidateOwner(id, ownerId, ownerPin);
        this.eventRepository.deleteById(event.getId());
    }

    private Event findEventAndValidateOwner(UUID id, UUID ownerId, String ownerPin){
        Event event = this.findById(id);
        this.userService.findByIdAndValidate(ownerId, ownerPin);
        if (!event.getOwnerId().equals(ownerId)) throw new AccessDeniedException("Acesso negado: o usuário informado não é o dono do evento.");
        return event;
    }

    private AddressDTO getAddressInfo(Event event){
        String eventCep = event.getCep().replace("-", "");
        AddressDTO addressData = this.awesomeApiClient.addressInfo(eventCep);
        if (addressData.status().equals(404)) throw new InvalidInputException("O CEP informado não existe na base de dados.");
        return addressData;
    }

    private void setAddressInfo(AddressDTO addressData, Event event){
        event.setAddress(addressData.address());
        event.setCity(addressData.city());
        event.setState(addressData.state());
        event.setDistrict(addressData.district());
    }
}
