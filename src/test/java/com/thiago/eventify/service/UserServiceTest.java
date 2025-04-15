package com.thiago.eventify.service;

import com.thiago.eventify.dto.CreateUserDTO;
import com.thiago.eventify.dto.UpdateUserDTO;
import com.thiago.eventify.entity.Event;
import com.thiago.eventify.entity.User;
import com.thiago.eventify.exception.type.InvalidInputException;
import com.thiago.eventify.mapper.UserMapper;
import com.thiago.eventify.repository.UserRepository;

import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    @Spy
    private UserService userService;

    @Nested
    class create {

        @Test
        @DisplayName("Should create user with success")
        void shouldCreateUserWithSuccess(){
            CreateUserDTO data = new CreateUserDTO("John", "12345678900", "john@gmail.com", "1234");
            User user = new User(null, "John", "12345678900", "john@gmail.com", "1234");
            User userSaved = new User(UUID.randomUUID(), "John", "12345678900", "john@gmail.com", "1234");
            when(userMapper.toEntity(any())).thenReturn(user);
            when(userRepository.save(any())).thenReturn(userSaved);

            User output = userService.create(data);

            assertNotNull(output);
            assertNotNull(output.getId());
            assertEquals("John", output.getName());
            assertEquals("12345678900", output.getCpf());
            assertEquals("john@gmail.com", output.getEmail());
            assertEquals("1234", output.getPin());
        }

        @Test
        @DisplayName("Should throw RuntimeException when saving fails")
        void shouldThrowRuntimeExceptionWhenSavingFails(){
            CreateUserDTO data = new CreateUserDTO("John", "12345678900", "john@gmail.com", "1234");
            User user = new User(null, "John", "12345678900", "john@gmail.com", "1234");

            when(userMapper.toEntity(any())).thenReturn(user);
            when(userRepository.save(any())).thenThrow(new RuntimeException());

            assertThrows(RuntimeException.class, () -> userService.create(data));
        }
    }

    @Nested
    class findByIdAndValidate {

        @Test
        @DisplayName("Should return user with success")
        void shouldReturnUserWithSuccess(){
            UUID id = UUID.randomUUID();
            String pin = "1234";
            User user = new User(id, "John", "12345678900", "john@gmail.com", "1234");
            when(userRepository.findById(any())).thenReturn(Optional.of(user));

            User output = userService.findByIdAndValidate(id, pin);

            assertNotNull(output);
            assertEquals(id, output.getId());
            assertEquals("John", output.getName());
            assertEquals("12345678900", output.getCpf());
            assertEquals("john@gmail.com", output.getEmail());
            assertEquals("1234", output.getPin());
        }

        @Test
        @DisplayName("Should throw ObjectNotFoundException when user does not exist")
        void shouldThrowExceptionWhenUserNotFound(){
            UUID id = UUID.randomUUID();
            String pin = "1234";
            when(userRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(ObjectNotFoundException.class, () -> userService.findByIdAndValidate(id, pin));
        }

        @Test
        @DisplayName("Should throw InvalidInputException when pin is incorrect")
        void shouldThrowExceptionWhenPinIsInvalid(){
            UUID id = UUID.randomUUID();
            String pin = "4321";
            User user = new User(id, "John", "12345678900", "john@gmail.com", "1234");
            when(userRepository.findById(any())).thenReturn(Optional.of(user));

            assertThrows(InvalidInputException.class, () -> userService.findByIdAndValidate(id, pin));
        }
    }

    @Nested
    class update {

        @Test
        @DisplayName("Should update user with success")
        void shouldUpdateUserWithSuccess(){
            UUID id = UUID.randomUUID();
            String pin = "1234";
            UpdateUserDTO data = new UpdateUserDTO("Mike", "mike@gmail.com", "4321");
            User user = new User(id, "John", "12345678900", "john@gmail.com", "1234");
            doReturn(user).when(userService).findByIdAndValidate(any(), any());
            doAnswer(invocation -> {
                UpdateUserDTO dto = invocation.getArgument(0);
                User u = invocation.getArgument(1);
                u.setName(dto.name());
                u.setEmail(dto.email());
                u.setPin(dto.pin());
                return null;
            }).when(userMapper).updateEntity(any(), any());
            when(userRepository.save(any())).thenReturn(user);

            User output = userService.update(id, pin, data);

            assertNotNull(output);
            verify(userService).findByIdAndValidate(id, pin);
            verify(userMapper).updateEntity(data, user);
            assertEquals("Mike", output.getName());
            assertEquals("mike@gmail.com", output.getEmail());
            assertEquals("4321", output.getPin());
        }

        @Test
        @DisplayName("Should throw RuntimeException when saving fails")
        void shouldThrowRuntimeExceptionWhenSavingFails(){
            UUID id = UUID.randomUUID();
            String pin = "1234";
            UpdateUserDTO data = new UpdateUserDTO("Mike", "mike@gmail.com", "4321");
            User user = new User(id, "John", "12345678900", "john@gmail.com", "1234");
            doReturn(user).when(userService).findByIdAndValidate(any(), any());
            doNothing().when(userMapper).updateEntity(any(), any());
            when(userRepository.save(any())).thenThrow(new RuntimeException());

            assertThrows(RuntimeException.class, () -> userService.update(id, pin, data));
        }
    }

    @Nested
    class delete {

        @Test
        @DisplayName("Should delete user with success")
        void shouldDeleteUserWithSucess(){
            UUID id = UUID.randomUUID();
            String pin = "1234";
            User user = new User(id, "John", "12345678900", "john@gmail.com", "1234");
            doReturn(user).when(userService).findByIdAndValidate(any(), any());

            userService.delete(id,pin);

            verify(userRepository, times(1)).deleteById(id);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("Should throw RuntimeException when deleting fails")
        void shouldThrowRuntimeExceptionWhenDeletingFails(){
            UUID id = UUID.randomUUID();
            String pin = "1234";
            User user = new User(id, "John", "12345678900", "john@gmail.com", "1234");
            doReturn(user).when(userService).findByIdAndValidate(any(), any());
            doThrow(new RuntimeException()).when(userRepository).deleteById(any());

            assertThrows(RuntimeException.class, () -> userService.delete(id, pin));
        }
    }

    @Nested
    class findAllEvents {

        @Test
        @DisplayName("Should return a set of Event with success")
        void shouldReturnSetOfEventWithSuccess(){
            UUID id = UUID.randomUUID();
            String pin = "1234";
            User user = new User(id, "John", "12345678900", "john@gmail.com", "1234");
            Event event1 = new Event();
            Set<Event> events = new HashSet<>(List.of(event1));
            user.setParticipatingEvents(events);
            doReturn(user).when(userService).findByIdAndValidate(any(), any());

            Set<Event> output = userService.findAllEvents(id, pin);

            assertNotNull(output);
            assertEquals(1, output.size());
            assertTrue(output.contains(event1));
        }

        @Test
        @DisplayName("Should return an empty set")
        void shouldReturnEmptySet(){
            UUID id = UUID.randomUUID();
            String pin = "1234";
            User user = new User(id, "John", "12345678900", "john@gmail.com", "1234");
            Set<Event> events = new HashSet<>();
            user.setParticipatingEvents(events);
            doReturn(user).when(userService).findByIdAndValidate(any(), any());

            Set<Event> output = userService.findAllEvents(id, pin);

            assertNotNull(output);
            assertEquals(0, output.size());
        }
    }
}
