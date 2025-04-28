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

@Tag(name = "Usuário", description = "API para gerenciamento de usuários e suas ações relacionadas.")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public UserController(UserService userService, UserMapper userMapper, EventMapper eventMapper){
        this.userService = userService;
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
    }

    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna um usuário baseado no ID e valida o PIN informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "PIN inválido ou erro de validação"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable("id") UUID id, @RequestParam("pin") String pin){
        User user = this.userService.findByIdAndValidate(id, pin);
        UserDTO userDTO = this.userMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(
            summary = "Criar novo usuário",
            description = "Cria um novo usuário a partir dos dados informados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação dos dados enviados"),
            @ApiResponse(responseCode = "409", description = "Violação de integridade de dados (ex: email duplicado)"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody @Valid CreateUserDTO data){
        User user = this.userService.create(data);
        UserDTO userDTO = this.userMapper.toDTO(user);
        URI location = URI.create("/user/" + user.getId());
        return ResponseEntity.created(location).body(userDTO);
    }

    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza as informações de um usuário existente com base no ID e PIN fornecidos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "PIN inválido ou erro de validação"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Violação de integridade de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable("id") UUID id, @RequestParam("pin") String pin,
                                          @RequestBody @Valid UpdateUserDTO data){
        User user = this.userService.update(id, pin, data);
        UserDTO userDTO = this.userMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(
            summary = "Excluir usuário",
            description = "Exclui um usuário existente com base no ID e valida o PIN informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "PIN inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @RequestParam("pin") String pin){
        this.userService.delete(id, pin);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar eventos de um usuário",
            description = "Retorna todos os eventos que o usuário está participando, validando o PIN informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos encontrados com sucesso"),
            @ApiResponse(responseCode = "400", description = "PIN inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}/events")
    public ResponseEntity<List<EventDTO>> findAllEventsByUser(@PathVariable("id") UUID id, @RequestParam("pin") String pin){
        Set<Event> events = this.userService.findAllEvents(id, pin);
        List<EventDTO> eventsDTO = this.eventMapper.toDTOList(events.stream().toList());
        return ResponseEntity.ok(eventsDTO);
    }
}
