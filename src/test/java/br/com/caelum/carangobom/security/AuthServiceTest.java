package br.com.caelum.carangobom.security;

import br.com.caelum.carangobom.auth.AuthService;
import br.com.caelum.carangobom.user.User;
import br.com.caelum.carangobom.user.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ActiveProfiles("test")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        authService = new AuthService(userRepository);
    }

    @Test
    void shouldReturnTheUser(){

        User newUser = new User(1L, "username", "password");

        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(java.util.Optional.of(newUser));

        UserDetails authAction = authService.loadUserByUsername(newUser.getUsername());

        Assert.assertEquals(authAction.getUsername(), newUser.getUsername());
    }

    @Test
    void shouldReturnUsernameNotFoundException(){
        String userName = "any_string";

        when(userRepository.findByUsername(userName)).thenReturn(java.util.Optional.empty());

        Assert.assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername(userName);
        });
    }
}
