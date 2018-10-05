package pl.hycom.login.user.service;

import pl.hycom.login.mail.VerificationToken;

public interface IUserService {

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String token);

    void saveRegisteredUser(User user);

    User getUser(String verificationToken);
}
