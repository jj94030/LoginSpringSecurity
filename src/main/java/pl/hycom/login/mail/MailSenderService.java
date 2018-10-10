package pl.hycom.login.mail;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {

    private final JavaMailSender emailSender;

    @Autowired
    public MailSenderService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }


    public void sendSimpleMessage(final Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(mail.getSubject());
        message.setText(mail.getMessageBody());
        message.setTo(mail.getTo());
        message.setFrom(mail.getFrom());

        emailSender.send(message);
    }
}
