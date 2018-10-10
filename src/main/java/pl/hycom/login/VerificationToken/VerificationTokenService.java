package pl.hycom.login.VerificationToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }


    public VerificationToken findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void save(VerificationToken token) {
        tokenRepository.save(token);
    }
}
