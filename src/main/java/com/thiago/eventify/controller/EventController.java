package com.thiago.eventify.controller;

import com.thiago.eventify.client.dto.WeatherForecastApiResponseDTO;
import com.thiago.eventify.dto.*;
import com.thiago.eventify.entity.Event;
import com.thiago.eventify.entity.User;
import com.thiago.eventify.mapper.EventMapper;
import com.thiago.eventify.mapper.UserMapper;
import com.thiago.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Tag(name = "Evento", description = "API para gerenciamento de eventos e seus participantes.")
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

    @Operation(
            summary = "Buscar todos os eventos",
            description = "Retorna uma lista de todos os eventos cadastrados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos retornados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/all")
    public ResponseEntity<List<EventDTO>> findAll(){
        List<Event> events = this.eventService.findAll();
        List<EventDTO> eventsDTO = this.eventMapper.toDTOList(events);
        return ResponseEntity.ok(eventsDTO);
    }

    @Operation(
            summary = "Buscar evento por ID",
            description = "Retorna um evento pelo ID, incluindo previsão do tempo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado"),
            @ApiResponse(responseCode = "502", description = "Erro ao buscar dados externos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventWithWeatherForecastDTO> findById(@PathVariable("id") UUID id){
        Event event = this.eventService.findById(id);
        WeatherForecastApiResponseDTO weatherForecast = this.eventService.getEventWeatherInfo(event);
        EventWithWeatherForecastDTO eventDTO = this.eventMapper.toDTO(event, weatherForecast);
        return ResponseEntity.ok(eventDTO);
    }

    @Operation(
            summary = "Criar novo evento",
            description = "Cria um novo evento e retorna os dados do evento com previsão do tempo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou PIN inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário dono do evento não encontrado"),
            @ApiResponse(responseCode = "502", description = "Erro ao buscar dados externos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    public ResponseEntity<EventWithWeatherForecastDTO> create(@RequestBody @Valid CreateEventDTO data,
                                                              @RequestParam("ownerPin") String pin){
        Event event = this.eventService.create(data, pin);
        WeatherForecastApiResponseDTO weatherForecast = this.eventService.getEventWeatherInfo(event);
        EventWithWeatherForecastDTO eventDTO = this.eventMapper.toDTO(event, weatherForecast);
        URI location = URI.create("/event/" + event.getId());
        return ResponseEntity.created(location).body(eventDTO);
    }

    @Operation(
            summary = "Atualizar evento",
            description = "Atualiza os dados de um evento existente. Apenas o dono pode atualizar."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou PIN inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado ou atualização proibida"),
            @ApiResponse(responseCode = "404", description = "Evento ou usuário não encontrado"),
            @ApiResponse(responseCode = "502", description = "Erro ao buscar dados externos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
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

    @Operation(
            summary = "Excluir evento",
            description = "Exclui um evento existente. Apenas o dono pode excluir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "PIN inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Evento ou usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @RequestParam("ownerId") UUID ownerId,
                                       @RequestParam("ownerPin") String ownerPin){
        this.eventService.delete(id, ownerId, ownerPin);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Registrar participante no evento",
            description = "Registra um usuário como participante em um evento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Participante registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "PIN inválido"),
            @ApiResponse(responseCode = "403", description = "Registro proibido"),
            @ApiResponse(responseCode = "404", description = "Evento ou usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/{id}/participant/{userId}")
    public ResponseEntity<Void> registerParticipant(@PathVariable UUID id, @PathVariable UUID userId,
                                                    @RequestParam("userPin") String userPin){
        this.eventService.registerParticipant(id, userId, userPin);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Cancelar inscrição do participante",
            description = "Remove um usuário participante de um evento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Participante removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "PIN inválido"),
            @ApiResponse(responseCode = "403", description = "Cancelamento proibido"),
            @ApiResponse(responseCode = "404", description = "Evento ou usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}/participant/{userId}")
    public ResponseEntity<Void> unregisterParticipant(@PathVariable UUID id, @PathVariable UUID userId,
                                                    @RequestParam("userPin") String userPin){
        this.eventService.unregisterParticipant(id, userId, userPin);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar participantes de um evento",
            description = "Retorna todos os usuários participantes de um evento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participantes retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<UserDTO>> findParticipantsByEvent(@PathVariable("id") UUID id){
        Set<User> participants = this.eventService.findAllParticipants(id);
        List<UserDTO> participantsDTO = this.userMapper.toDTOList(participants.stream().toList());
        return ResponseEntity.ok(participantsDTO);
    }
}
