package br.com.caelum.carangobom.auth;

import br.com.caelum.carangobom.auth.token.TokenDTO;
import br.com.caelum.carangobom.auth.token.TokenService;
import br.com.caelum.carangobom.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ActiveProfiles("test")
class AuthControllerTest {

    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManagerMock;

    @Mock
    private TokenService tokenServiceMock;


    @BeforeEach
    void setUp() {
        openMocks(this);
        authController = new AuthController(authenticationManagerMock, tokenServiceMock);
    }

    @Test
    void whenAuthenticate_shouldReturnToken() {
        String userName = "username";
        String password =  "password";
        TokenDTO authRequest = new TokenDTO("123123", "123456");

        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(userName);
        loginForm.setPassword(password);


        when(tokenServiceMock.generateToken(Mockito.any()))
                .thenReturn("TOKEN");

        ResponseEntity<TokenDTO> response = authController.authentication(loginForm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginForm.getUsername(), userName);
        assertEquals(loginForm.getPassword(), password);

        verify(authenticationManagerMock).authenticate(Mockito.any());
        verify(tokenServiceMock).generateToken(Mockito.any());
    }

    @Test
    void shouldReturnErro(){
        TokenDTO authRequest = new TokenDTO("123123", "123456");

        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("123123");
        loginForm.setPassword("123456");

        doThrow(BadCredentialsException.class)
                .when(authenticationManagerMock)
                .authenticate(any());

        ResponseEntity<TokenDTO> response= authController.authentication(loginForm);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

}
