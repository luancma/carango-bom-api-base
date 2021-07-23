package br.com.caelum.carangobom.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.Converter;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  UserService userService;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userService = new UserService(this.userRepository);
    }

    @GetMapping("/users")
    @Cacheable("user-list")
    public Page<UserDTO> listAll(
            @PageableDefault(sort = "username", direction = Sort.Direction.ASC, page = 0, size = 10)
                    Pageable pageable
    ) {
        List<User> users = userRepository.findAll();
         UserDTO.toUserList(users);

        return userRepository.findAll(pageable).map(UserDTO::toUser);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> details(@PathVariable Long id){
        User getUser = userService.findById(id);
        UserDTO formatedUser = UserDTO.toUser(getUser);
        return ResponseEntity.ok(formatedUser);
    }

    @PostMapping("/users")
    @Transactional
    @CacheEvict(value = "user-list", allEntries = true)
    public ResponseEntity<UserDTO> create(@RequestBody @Valid UserForm userForm, UriComponentsBuilder uriBuilder) {
        User convertedReceivedUser = userForm.toUser();
        User createdUser = userService.createNewUser(convertedReceivedUser);
        URI uri = uriBuilder.path("/users/{id}").buildAndExpand(createdUser.getId()).toUri();
        UserDTO convertedUserDTO = UserDTO.toUser(createdUser);
        return ResponseEntity.created(uri).body(convertedUserDTO);
    }

    @DeleteMapping("/users/{id}")
    @Transactional
    @CacheEvict(value = "user-list", allEntries = true)
    public ResponseEntity<UserDTO>delete(@PathVariable Long id) {
        userService.removeUserById(id);
        return ResponseEntity.ok().build();
    }
}
