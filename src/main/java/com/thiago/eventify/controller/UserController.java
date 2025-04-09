package com.thiago.eventify.controller;

import com.thiago.eventify.dto.CreateUserDTO;
import com.thiago.eventify.dto.EventDTO;
import com.thiago.eventify.dto.UpdateUserDTO;
import com.thiago.eventify.dto.UserDTO;
import com.thiago.eventify.entity.Event;
import com.thiago.eventify.entity.User;
import com.thiago.eventify.mapper.EventMapper;
import com.thiago.eventify.mapper.UserMapper;
import com.thiago.eventify.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper){
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable("id") UUID id, @RequestParam("pin") String pin){
        User user = this.userService.findByIdAndValidate(id, pin);
        UserDTO userDTO = this.userMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody @Valid CreateUserDTO data){
        User user = this.userService.create(data);
        UserDTO userDTO = this.userMapper.toDTO(user);
        URI location = URI.create("/user/" + user.getId());
        return ResponseEntity.created(location).body(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable("id") UUID id, @RequestParam("pin") String pin,
                                          @RequestBody @Valid UpdateUserDTO data){
        User user = this.userService.update(id, pin, data);
        UserDTO userDTO = this.userMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @RequestParam("pin") String pin){
        this.userService.delete(id, pin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<EventDTO>> findAllEventsByUser(@PathVariable("id") UUID id, @RequestParam("pin") String pin){
        Set<Event> events = this.userService.findAllEvents(id, pin);
        List<EventDTO> eventsDTO = EventMapper.toDTOList(events.stream().toList());
        return ResponseEntity.ok(eventsDTO);
    }
}
