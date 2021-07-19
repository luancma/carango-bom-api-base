package br.com.caelum.carangobom.user;

import br.com.caelum.carangobom.exception.BadRequestException;
import br.com.caelum.carangobom.exception.NotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ActiveProfiles("test")
class UserUnitTest {

    private UserController userController;
    private UriComponentsBuilder uriBuilder;
    private EntityManager entityManager;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void configuraMock() {
        openMocks(this);

        userController = new UserController(userRepository);
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080");
    }

    @Test
    void shouldCreateANewUser(){
        UserForm userForm = new UserForm("1", "validaPassword");
        User user = userForm.convert();
        when(userRepository.save(user)).thenReturn(user);
        ResponseEntity<UserDTO> createUserContorller = userController.create(userForm, uriBuilder);
        assertEquals(createUserContorller.getBody().getId(), user.getId());
        assertEquals(createUserContorller.getBody().getUsername(), user.getUsername());
    }

    @Test
    void shouldNotCreateANewUserWithTheSameUsername() {
        UserForm userForm = new UserForm("1", "validaPassword");
        User user = userForm.convert();

        when(userRepository.findByUsername(user.getUsername())).thenThrow(new BadRequestException());

        Assert.assertThrows(BadRequestException.class, () -> {
            userController.create(userForm, uriBuilder);
        });
    }

    @Test
    void shouldIncrementUserForm() {
        UserForm userForm = new UserForm();

        userForm.setUsername("username");
        userForm.setPassword("password");

        assertEquals("username", userForm.getUsername());
        assertEquals("password", userForm.getPassword());
    }

    @Test
    void shouldTestIncrementUserWithoutConstructor() {
        User newUser = new User();

        newUser.setId(1L);
        newUser.setUsername("username");
        newUser.setPassword("password");

        assertEquals(java.util.Optional.of(1L).get(), newUser.getId());
        assertEquals("username", newUser.getUsername());
        assertEquals("password", newUser.getPassword());
        assertTrue(newUser.isAccountNonExpired());
        assertTrue(newUser.isAccountNonLocked());
        assertTrue(newUser.isEnabled());
        assertTrue(newUser.isCredentialsNonExpired());
    }

    @Test
    void shouldTestIncrementUserWithConstructor() {
        User newUser = new User(1L, "username", "password");

        assertEquals(java.util.Optional.of(1L).get(), newUser.getId());
        assertEquals("username", newUser.getUsername());
        assertEquals("password", newUser.getPassword());
    }

    @Test
    void shouldTestUserDTO() {
        User newUser = new User(1L, "username", "password");
        UserDTO userDTO = new UserDTO(newUser);
        assertEquals(newUser.getId(), userDTO.getId());
        assertEquals(newUser.getUsername(), userDTO.getUsername());
    }

    @Test
    void shouldTestUserDTO_Convert() {
        List<User> userList = List.of(
                new User(1L, "username1", "password1"),
                new User(2L, "username2", "password2")
        );

        List<UserDTO> userConvertDTO = UserDTO.convert(userList);
        assertEquals(userConvertDTO.size(), userList.size());
    }

    @Test
    void shouldTestListAll() {
        List<User> userList = List.of(
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
        var findById = userController.details(user.getId());
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
    void shouldReceiveTheUserFormValues(){
        User newUser = new User(1L, "username", "password");
        Mockito.mock(UserDTO.class);

        UserDTO userDTO = new UserDTO(newUser);

        assertEquals(newUser.getId(), userDTO.getId());
        assertEquals(newUser.getUsername(), userDTO.getUsername());
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
