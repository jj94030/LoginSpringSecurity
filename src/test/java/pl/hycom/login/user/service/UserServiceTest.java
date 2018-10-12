package pl.hycom.login.user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.hycom.login.verificationToken.VerificationTokenService;
import pl.hycom.login.role.service.RoleService;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private User user;
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private VerificationTokenService tokenService;

    @Before
    public void setup() {
        userService = new UserService(userRepository, roleService, bCryptPasswordEncoder, tokenService);

        user = new User();
        user.setEmail("test@test.pl");

        Mockito.when(userService.findUserByEmail(Mockito.anyString())).thenReturn(user);
    }

    @Test
    public void findUserByEmail() {
        // given
        final String email = "test@test.pl";

        // when
        final User user = userService.findUserByEmail(email);

        // then
        assertEquals(email, user.getEmail());
    }

    @Test
    public void saveUser() {
        // given
        final String email = "test@test.pl";

        // when
        userService.saveUser(user);
        final User savedUser = userService.findUserByEmail(email);

        // then
        assertEquals(email, savedUser.getEmail());
    }
}