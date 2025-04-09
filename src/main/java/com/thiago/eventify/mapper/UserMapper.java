package com.thiago.eventify.mapper;

import com.thiago.eventify.dto.CreateUserDTO;
import com.thiago.eventify.dto.UpdateUserDTO;
import com.thiago.eventify.dto.UserDTO;
import com.thiago.eventify.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class UserMapper {

    public User toEntity(CreateUserDTO data){
        User user = new User();
        user.setId(null);
        user.setName(data.name());
        user.setCpf(data.cpf());
        user.setEmail(data.email());
        user.setPin(data.pin());
        return user;
    }

    public void updateEntity(UpdateUserDTO data, User user){
        if (Objects.nonNull(data.name())) user.setName(data.name());
        if (Objects.nonNull(data.email())) user.setEmail(data.email());
        if (Objects.nonNull(data.pin())) user.setPin(data.pin());
    }

    public UserDTO toDTO(User user){
        return new UserDTO(user.getId(), user.getName(), user.getCpf(), user.getEmail(), user.getPin());
    }

    public List<UserDTO> toDTOList(List<User> users){
        return users.stream().map(this::toDTO).toList();
    }
}
