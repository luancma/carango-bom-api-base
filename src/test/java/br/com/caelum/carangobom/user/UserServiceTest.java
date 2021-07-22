package br.com.caelum.carangobom.user;

import br.com.caelum.carangobom.exception.BadRequestException;
import br.com.caelum.carangobom.exception.NotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ActiveProfiles("test")
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    User newUser = new User(1L, "username1", "password1");
    UserForm userForm = new UserForm("username1", "password1");

    @BeforeEach
    public void mockConfig() {
        openMocks(this);
        userService = new UserService(userRepository);
    }


    @Test
    void shouldNotValidateUsernameAlreadyInUse(){
        User user = new User(1L, "username", "password");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        Assert.assertThrows(
                BadRequestException.class, ()->
                        userService.usernameAlreadyInUse(user)
        );
    }

    @Test
    void shouldPassUsernameAlreadyInUse(){
        User user = new User(1L, "username", "password");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        Assertions.assertNull(userService.usernameAlreadyInUse(user));
    }

    @Test
    void shouldRemoveUser(){
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(newUser));
        boolean userServiceAction = userService.removeUserById(newUser.getId());
        Assertions.assertTrue(userServiceAction);
    }

    @Test
    void shouldNotRemoveUser(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assert.assertThrows(
                NotFoundException.class, ()->
                userService.removeUserById(1L)
        );
    }

    @Test
    void shouldCreateOneUser(){
        when(userRepository.save(newUser)).thenReturn(newUser);
        User userServiceAction = userService.createNewUser(newUser);
        Assertions.assertEquals(userServiceAction.getUsername(), newUser.getUsername());
        Assertions.assertEquals(userServiceAction.getId(), newUser.getId());
    }

    @Test
    void shouldNotCreateOneUser(){
        when(userRepository.save(newUser))
                .thenThrow(new BadRequestException());
        Assert.assertThrows(
                BadRequestException.class, ()->
                        userService.createNewUser(newUser)
        );
    }

    @Test
    void shouldFindOneUser(){
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(newUser));
        User userServiceAction = userService.findById(newUser.getId());
        Assertions.assertEquals(userServiceAction.getId(), newUser.getId());
    }

    @Test
    void shouldNotFindOneUser(){
        when(userRepository.save(newUser))
                .thenThrow(new NotFoundException());
        Assert.assertThrows(
                NotFoundException.class, ()->
                        userService.createNewUser(newUser)
        );
    }


}