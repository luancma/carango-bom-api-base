package br.com.caelum.carangobom.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.net.URI;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldReturn200WhenGetTheListOfUser() throws Exception {
        URI uri = new URI("/users/paged");
        mockMvc.perform(MockMvcRequestBuilders
                .get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    public void shouldReturn400WhenTryToCreateUserWithSameUsername() throws Exception {
        URI uri = new URI("/users");
        UserForm newUserForm = new UserForm("username", "password");
        User converted = newUserForm.toUser();
        entityManager.persist(converted);
        entityManager.flush();

        String json = "{\"username\": \"username\", \"password\": \"newpassword\"}";
        mockMvc.perform(MockMvcRequestBuilders
                .post(uri)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenTryToCreateUserWithEmptyUsername() throws Exception {
        URI uri = new URI("/users");

        String json = "{\"username\": \"\", \"password\": \"newpassword\"}";

        mockMvc.perform(MockMvcRequestBuilders
                .post(uri)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void shouldReturn400WhenTryToCreateUserWithEmptyPassword() throws Exception {
        URI uri = new URI("/users");

        String json = "{\"username\": \"username\", \"password\": \"\"}";

        mockMvc.perform(MockMvcRequestBuilders
                .post(uri)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldReturnTheListOfUsers() throws Exception {

        URI uri = new URI("/users/paged");
        mockMvc.perform(MockMvcRequestBuilders
                .get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Transactional
    @Test
    public void shouldReturn200WhenFindUser() throws Exception {
        UserForm newUserForm = new UserForm("username", "password");
        User converted = newUserForm.toUser();
        entityManager.persist(converted);
        entityManager.flush();

        URI uri = new URI("/users/" + converted.getId());

        mockMvc.perform(MockMvcRequestBuilders
                .get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    public void shouldReturn200WhenDeleteUser() throws Exception {
        UserForm newUserForm = new UserForm("username", "password");
        User converted = newUserForm.toUser();
        converted.setPassword(new BCryptPasswordEncoder().encode(converted.getPassword()));
        entityManager.persist(converted);
        entityManager.flush();

        String accessToken = new BCryptPasswordEncoder().encode(converted.getPassword());
        URI uri = new URI("/users/" + converted.getId());

        mockMvc.perform(MockMvcRequestBuilders
                .delete(uri)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturn404WhenTryToDeleteNonexistentID() throws Exception {
        URI uri = new URI("/users/" + 1L);

        mockMvc.perform(MockMvcRequestBuilders
                .delete(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


}
