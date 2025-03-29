package com.thiago.eventify.service;

import com.thiago.eventify.client.dto.CepResponseDTO;
import com.thiago.eventify.client.service.CepClient;
import com.thiago.eventify.dto.CreateEventDTO;
import com.thiago.eventify.dto.UpdateEventDTO;
import com.thiago.eventify.entity.Event;
import com.thiago.eventify.exception.AccessDeniedException;
import com.thiago.eventify.exception.InvalidInputException;
import com.thiago.eventify.mapper.EventMapper;
import com.thiago.eventify.repository.EventRepository;
import jakarta.validation.Valid;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CepClient cepClient;

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
        CepResponseDTO addressData = this.getAddressInfo(event);
        this.setAddressInfo(addressData, event);
        return this.eventRepository.save(event);
    }

    @Transactional
    public Event update(UUID id, UUID ownerId, String ownerPin, @Valid UpdateEventDTO data){
        Event event = this.findEventAndValidateOwner(id, ownerId, ownerPin);
        EventMapper.updateEntity(data, event);
        CepResponseDTO addressData = this.getAddressInfo(event);
        this.setAddressInfo(addressData, event);
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

    private CepResponseDTO getAddressInfo(Event event){
        String eventCep = event.getCep().replace("-", "");
        CepResponseDTO addressData = this.cepClient.addressInfo(eventCep);
        if (addressData.erro()) throw new InvalidInputException("O CEP informado não existe na base de dados.");
        return addressData;
    }

    private void setAddressInfo(CepResponseDTO addressData, Event event){
        event.setAddress(addressData.logradouro());
        event.setCity(addressData.localidade());
        event.setState(addressData.uf());
    }
}
