package com.thiago.eventify.service;

import com.thiago.eventify.entity.Event;
import com.thiago.eventify.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> findAll(){
        return this.eventRepository.findAll();
    }
}
