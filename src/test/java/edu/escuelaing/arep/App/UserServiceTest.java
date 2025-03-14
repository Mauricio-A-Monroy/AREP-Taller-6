package edu.escuelaing.arep.App;

import edu.escuelaing.arep.App.model.User;
import edu.escuelaing.arep.App.service.UserService;
import edu.escuelaing.arep.App.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "test@example.com", "encodedPassword");
    }

    @Test
    void registerUser_WhenEmailNotInUse_ShouldSaveUser() {
        // Arrange
        String email = "newuser@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User registeredUser = userService.registerUser(email, password);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(email, registeredUser.getEmail());
        assertEquals(encodedPassword, registeredUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_WhenEmailAlreadyInUse_ShouldThrowException() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(email, password);
        });

        assertEquals("El email ya está en uso.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_WhenCredentialsAreCorrect_ShouldReturnTrue() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        // Act
        boolean result = userService.authenticateUser(email, password);

        // Assert
        assertTrue(result);
    }

    @Test
    void authenticateUser_WhenEmailNotFound_ShouldReturnFalse() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.authenticateUser(email, password);

        // Assert
        assertFalse(result);
    }

    @Test
    void authenticateUser_WhenPasswordIsIncorrect_ShouldReturnFalse() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // Act
        boolean result = userService.authenticateUser(email, password);

        // Assert
        assertFalse(result);
    }
}

