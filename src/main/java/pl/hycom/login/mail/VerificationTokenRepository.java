package pl.hycom.login.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.hycom.login.user.service.User;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
