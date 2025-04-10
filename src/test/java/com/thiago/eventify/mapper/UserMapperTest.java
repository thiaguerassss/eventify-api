package com.thiago.eventify.mapper;

import com.thiago.eventify.dto.CreateUserDTO;
import com.thiago.eventify.dto.UpdateUserDTO;
import com.thiago.eventify.dto.UserDTO;
import com.thiago.eventify.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp(){
        userMapper = new UserMapper();
    }

    @Nested
    class toEntity {

        @Test
        @DisplayName("Should return a user with success")
        void shouldReturnAUserWithSuccess(){
            CreateUserDTO data = new CreateUserDTO("John", "12345678900", "john@gmail.com", "1234");

            User output = userMapper.toEntity(data);

            assertNotNull(output);
            assertNull(output.getId());
            assertEquals(data.name(), output.getName());
            assertEquals(data.cpf(), output.getCpf());
            assertEquals(data.email(), output.getEmail());
            assertEquals(data.pin(), output.getPin());
        }
    }

    @Nested
    class updateEntity {

        @Test
        @DisplayName("Should update the entity with success")
        void shouldUpdateEntityWithSuccess(){
            UpdateUserDTO updateUserDTO = new UpdateUserDTO("Barry", "barry@gmail.com", "1234");
            User user = new User(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), "John",
                    "12345678900", "john@gmail.com", "0123");

            userMapper.updateEntity(updateUserDTO, user);

            assertEquals(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), user.getId());
            assertEquals("12345678900", user.getCpf());
            assertEquals(updateUserDTO.name(), user.getName());
            assertEquals(updateUserDTO.email(), user.getEmail());
            assertEquals(updateUserDTO.pin(), user.getPin());
        }

        @Test
        @DisplayName("Should not update the name")
        void shouldNotUpdateTheName(){
            UpdateUserDTO updateUserDTO = new UpdateUserDTO(null, "barry@gmail.com", "1234");
            User user = new User(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), "John",
                    "12345678900", "john@gmail.com", "0123");

            userMapper.updateEntity(updateUserDTO, user);

            assertEquals(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), user.getId());
            assertEquals("12345678900", user.getCpf());
            assertEquals("John", user.getName());
        }

        @Test
        @DisplayName("Should not update the email")
        void shouldNotUpdateTheEmail(){
            UpdateUserDTO updateUserDTO = new UpdateUserDTO("Barry", null, "1234");
            User user = new User(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), "John",
                    "12345678900", "john@gmail.com", "0123");

            userMapper.updateEntity(updateUserDTO, user);

            assertEquals(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), user.getId());
            assertEquals("12345678900", user.getCpf());
            assertEquals("john@gmail.com", user.getEmail());
        }

        @Test
        @DisplayName("Should not update the pin")
        void shouldNotUpdateThePin(){
            UpdateUserDTO updateUserDTO = new UpdateUserDTO("Barry", "barry@gmail.com", null);
            User user = new User(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), "John",
                    "12345678900", "john@gmail.com", "0123");

            userMapper.updateEntity(updateUserDTO, user);

            assertEquals(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), user.getId());
            assertEquals("12345678900", user.getCpf());
            assertEquals("0123", user.getPin());
        }

        @Test
        @DisplayName("Should not update any field")
        void shouldNotUpdateAnyField(){
            UpdateUserDTO updateUserDTO = new UpdateUserDTO(null, null, null);
            User user = new User(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), "John",
                    "12345678900", "john@gmail.com", "0123");

            userMapper.updateEntity(updateUserDTO, user);

            assertEquals(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), user.getId());
            assertEquals("12345678900", user.getCpf());
            assertEquals("John", user.getName());
            assertEquals("john@gmail.com", user.getEmail());
            assertEquals("0123", user.getPin());
        }
    }

    @Nested
    class toDTO {

        @Test
        @DisplayName("Should return a DTO with success")
        void shouldReturnADTOWithSuccess(){
            User user = new User(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), "John",
                    "12345678900", "john@gmail.com", "0123");

            UserDTO output = userMapper.toDTO(user);

            assertEquals(user.getId(), output.id());
            assertEquals(user.getName(), output.name());
            assertEquals(user.getCpf(), output.cpf());
            assertEquals(user.getEmail(), output.email());
            assertEquals(user.getPin(), output.pin());
        }
    }

    @Nested
    class toDTOList {

        @Test
        @DisplayName("Should return a list of DTOs with success")
        void shouldReturnAListOfDTOsWithSuccess(){
            User user1 = new User(UUID.fromString("5680d7fa-764c-4562-9cbc-1ff9d26b92a8"), "John",
                    "12345678900", "john@gmail.com", "0123");
            User user2 = new User(UUID.fromString("ef7a094b-95b0-4930-af69-650b8b6dee5b"), "Barry",
                    "98765432100", "barry@gmail.com", "3210");
            List<User> users = new ArrayList<>();
            users.add(user1);
            users.add(user2);

            List<UserDTO> dtoList = userMapper.toDTOList(users);

            assertEquals(user1.getId(), dtoList.get(0).id());
            assertEquals(user1.getName(), dtoList.get(0).name());
            assertEquals(user1.getCpf(), dtoList.get(0).cpf());
            assertEquals(user1.getEmail(), dtoList.get(0).email());
            assertEquals(user1.getPin(), dtoList.get(0).pin());
            assertEquals(user2.getId(), dtoList.get(1).id());
            assertEquals(user2.getName(), dtoList.get(1).name());
            assertEquals(user2.getCpf(), dtoList.get(1).cpf());
            assertEquals(user2.getEmail(), dtoList.get(1).email());
            assertEquals(user2.getPin(), dtoList.get(1).pin());
        }
    }
}