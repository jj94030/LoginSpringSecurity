package pl.hycom.login.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.hycom.login.mail.VerificationToken;
import pl.hycom.login.mail.VerificationTokenService;
import pl.hycom.login.role.service.Role;
import pl.hycom.login.role.service.RoleService;

import java.util.Arrays;
import java.util.HashSet;

@Service
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final VerificationTokenService tokenService;


    @Autowired
    public UserService(UserRepository userRepository,
                       RoleService roleService,
                       BCryptPasswordEncoder bCryptPasswordEncoder,VerificationTokenService tokenService ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenService = tokenService;
    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(0);
        Role userRole = roleService.findRole("ADMIN");
        user.setRoles(new HashSet<>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenService.saveToken(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String token) {
        return tokenService.findByToken(token);
    }

    @Override
    public void saveRegisteredUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        Role userRole = roleService.findRole("ADMIN");
        user.setRoles(new HashSet<>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

    @Override
    public User getUser(String verificationToken) {
        User user = tokenService.findByToken(verificationToken).getUser();
        return user;
    }
}
