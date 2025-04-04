package com.thiago.eventify.service;

import com.thiago.eventify.dto.CreateUserDTO;
import com.thiago.eventify.dto.UpdateUserDTO;
import com.thiago.eventify.entity.User;
import com.thiago.eventify.exception.InvalidInputException;
import com.thiago.eventify.mapper.UserMapper;
import com.thiago.eventify.repository.UserRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findByIdAndValidate(UUID id, String pin){
        User user = this.userRepository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Usuário não encontrado.", id));
        if (!pin.equals(user.getPin())) throw new InvalidInputException("O PIN informado é inválido.");
        return user;
    }

    @Transactional
    public User create(CreateUserDTO data){
        User user = UserMapper.toEntity(data);
        return this.userRepository.save(user);
    }

    @Transactional
    public User update(UUID id, String pin, UpdateUserDTO data){
        User user = this.findByIdAndValidate(id, pin);
        UserMapper.updateEntity(data, user);
        return this.userRepository.save(user);
    }

    @Transactional
    public void delete(UUID id, String pin){
        User user = this.findByIdAndValidate(id, pin);
        this.userRepository.deleteById(user.getId());
    }
}
