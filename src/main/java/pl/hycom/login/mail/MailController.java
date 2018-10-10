package pl.hycom.login.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Controller
public class MailController {

    private final MailSenderService mailSenderService;
    private final JavaMailSender javaMailSender;

    @Autowired
    public MailController(MailSenderService mailSenderService, JavaMailSender javaMailSender) {
        this.mailSenderService = mailSenderService;
        this.javaMailSender = javaMailSender;
    }

    @GetMapping("/sendEmail")
    @ResponseBody
    public String sendEmail() {
        Mail mail = new Mail();
        mail.setTo("test@test.pl");
        mail.setSubject("Sending Simple Email with JavaMailSender Example");
        mail.setMessageBody("This tutorial demonstrates how to send a simple email using Spring Framework.");

        mailSenderService.sendSimpleMessage(mail);

        return "Success";
    }

    @GetMapping("/sendMail")
    @ResponseBody
    public String sendMail() throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom("test@test.pl");
        message.setRecipients(MimeMessage.RecipientType.TO, "test@test.pl");
        message.setSubject("Mime message - example");
        message.setText("The tutorial demonstrates how to send a email using Mime Message class");

        javaMailSender.send(message);
        return "Success";
    }
}
