package com.thiago.eventify.mapper;

import com.thiago.eventify.dto.CreateUserDTO;
import com.thiago.eventify.dto.UserDTO;
import com.thiago.eventify.entity.User;

public class UserMapper {

    public static User toEntity(CreateUserDTO data){
        User user = new User();
        user.setId(null);
        user.setName(data.name());
        user.setCpf(data.cpf());
        user.setEmail(data.email());
        user.setPin(data.pin());
        return user;
    }

    public static UserDTO toDTO(User user){
        return new UserDTO(user.getId(), user.getName(), user.getCpf(), user.getEmail(), user.getPin());
    }
}
