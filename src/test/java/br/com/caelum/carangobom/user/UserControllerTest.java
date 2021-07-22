package br.com.caelum.carangobom.user;

import br.com.caelum.carangobom.exception.BadRequestException;
import br.com.caelum.carangobom.exception.NotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ActiveProfiles("test")
class UserControllerTest {

    private UserController userController;
    private UriComponentsBuilder uriBuilder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @BeforeEach
    public void mockConfig() {
        openMocks(this);
        userController = new UserController(userRepository);
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080");
    }

    @Test
    void shouldCreateANewUser(){
        UserForm userForm = new UserForm("username", "validaPassword");
        User user  = userForm.toUser();
        when(this.userService.createNewUser(user)).thenReturn(user);
        ResponseEntity<UserDTO> createUserController = userController.create(userForm, uriBuilder);
        assertEquals(createUserController.getStatusCodeValue(), 201);
    }

    @Test
    void shouldNotCreateANewUserWithTheSameUsername() {
        UserForm userForm = new UserForm("1", "validaPassword");
        User user  = userForm.toUser();
        when(userRepository.findByUsername(user.getUsername())).thenThrow(NotFoundException.class);
        doThrow(BadRequestException.class).when(this.userService).createNewUser(any());
        Assert.assertThrows(NotFoundException.class, () -> {
            userController.create(userForm, uriBuilder);
        });
    }


    @Test
    void shouldTestUserDTO_Convert() {
        List<User> userList = Arrays.asList(
                new User(1L, "username1", "password1"),
                new User(2L, "username2", "password2")
        );

        List<UserDTO> userConvertDTO = UserDTO.toUserList(userList);
        assertEquals(userConvertDTO.size(), userList.size());
    }

    @Test
    void shouldTestListAll() {
        List<User> userList = Arrays.asList(
                new User(1L, "username1", "password1"),
                new User(2L, "username2", "password2")
        );

        when(userRepository.findAll()).thenReturn(userList);

        List<UserDTO> userListController = userController.listAll();

        assertEquals(userList.size(),userListController.size());
    }

    @Test
    void shouldFindUserWithPathId(){
        User user = new User(1L, "username1", "password1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<UserDTO> findById = userController.details(user.getId());
        assertEquals(findById.getStatusCodeValue(), 200);
    }

    @Test
    void shouldNotFindUserWithInvalidPathId(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assert.assertThrows(NotFoundException.class, () -> {
            userController.details(1L);
        });
    }

    @Test
    void shouldDeleteUserWithPathId(){
        User newUser = new User(1L, "username1", "password1");

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(newUser));

        ResponseEntity<UserDTO> userControllerDelete = userController.delete(1L);

        assertEquals(userControllerDelete.getStatusCodeValue(), 200);
    }

    @Test
    void shouldNotDeleteUserWithInvalidPathId(){
        User newUser = new User(1L, "username1", "password1");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> {
            userController.delete(1L);
        });
    }
}
