package edu.escuelaing.arep.App.service;

import edu.escuelaing.arep.App.model.User;
import edu.escuelaing.arep.App.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya está en uso.");
        }

        String encryptedPassword = passwordEncoder.encode(password);
        User newUser = new User(null, email, encryptedPassword);
        return userRepository.save(newUser);
    }

    public boolean authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return passwordEncoder.matches(password, userOptional.get().getPassword());
        }

        return false;
    }
}
