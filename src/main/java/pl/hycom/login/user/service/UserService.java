package pl.hycom.login.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.hycom.login.user.controller.UserDetailsFormObject;
import pl.hycom.login.user.controller.UserEditFormObject;
import pl.hycom.login.verificationToken.VerificationToken;
import pl.hycom.login.verificationToken.VerificationTokenService;
import pl.hycom.login.role.service.Role;
import pl.hycom.login.role.service.RoleService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final VerificationTokenService tokenService;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleService roleService,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       VerificationTokenService tokenService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenService = tokenService;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(0);
        Role userRole = roleService.findRole("ADMIN");
        user.setRoles(new HashSet<>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

    public User getUser(String verificationToken) {
        User user = tokenService.findByToken(verificationToken).getUser();
        return user;
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenService.save(myToken);
    }

    public VerificationToken getVerificationToken(String token) {
        return tokenService.findByToken(token);
    }

    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    public UserDetailsFormObject getUserDetails(User user) {
        UserDetailsFormObject userDetails = new UserDetailsFormObject();
        userDetails.setName(user.getName());
        userDetails.setLastName(user.getLastName());
        return userDetails;
    }

    public UserEditFormObject getUserEdit(User user) {
        UserEditFormObject userEdit = new UserEditFormObject();
        userEdit.setName(user.getName());
        userEdit.setLastName(user.getLastName());
        return userEdit;
    }

    public User findById(Integer userId) {
        return userRepository.findUserById(userId);
    }

    public void editUser(UserDTO userDTO) {
        User user = findById(userDTO.getId());
        user.setName(userDTO.getName());
        user.setLastName(userDTO.getLastName());
        userRepository.save(user);
    }
}
