package com.thiago.eventify.service;

import com.thiago.eventify.client.dto.CepResponseDTO;
import com.thiago.eventify.client.service.CepClient;
import com.thiago.eventify.dto.CreateEventDTO;
import com.thiago.eventify.entity.Event;
import com.thiago.eventify.entity.User;
import com.thiago.eventify.exception.InvalidInputException;
import com.thiago.eventify.mapper.EventMapper;
import com.thiago.eventify.repository.EventRepository;
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

    public List<Event> findAll(){
        return this.eventRepository.findAll();
    }

    @Transactional
    public Event create(CreateEventDTO data, String pin){
        this.userService.findByIdAndValidate(data.ownerId(), pin);
        Event event = EventMapper.toEntity(data);
        String eventCep = event.getCep().replace("-", "");
        CepResponseDTO addressData = this.cepClient.addressInfo(eventCep);
        if (addressData.erro()) throw new InvalidInputException("O CEP informado não existe na base de dados.");
        event.setAddress(addressData.logradouro());
        event.setCity(addressData.localidade());
        event.setState(addressData.uf());
        return this.eventRepository.save(event);
    }
}
