package br.com.caelum.carangobom.user;

import br.com.caelum.carangobom.exception.BadRequestException;
import br.com.caelum.carangobom.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findById(Long id){
        Optional<User> validatedUser = userRepository.findById(id);
        if(validatedUser.isPresent()){
            return validatedUser.get();
        }
        throw new NotFoundException("Usuário não encontrado.");
    }

    public Object usernameAlreadyInUse(User user){
        Optional<User> isCreated = userRepository.findByUsername(user.getUsername());
        if (isCreated.isPresent()) {
            throw  new BadRequestException("Usuário já cadastrado");
        }
        return null;
    }

    public User createNewUser(User user){
        usernameAlreadyInUse(user);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    public User removeUserById(Long id){
        User user = findById(id);
        userRepository.deleteById(user.getId());
        return user;
    }
}