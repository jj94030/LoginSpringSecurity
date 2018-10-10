package pl.hycom.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import pl.hycom.login.VerificationToken.OnRegistrationCompleteEvent;
import pl.hycom.login.VerificationToken.VerificationToken;
import pl.hycom.login.user.service.User;
import pl.hycom.login.user.service.UserService;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public RegistrationController(UserService userService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/registration")
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @PostMapping("/registration")
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid User user, BindingResult result, WebRequest request, Errors errors) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            result.rejectValue("email", "error.user",
                    "There is already a user registered with the email provided");
        }
        if (result.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user);
            try {
                String appUrl = request.getContextPath();
                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl));
                modelAndView.setViewName("login");
            } catch (Exception me) {
                modelAndView.setViewName("registration");
            }
        }
        return modelAndView;
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(WebRequest request, @RequestParam("token") String token) {
        ModelAndView modelAndView = new ModelAndView();
        VerificationToken verificationToken = userService.getVerificationToken(token);
        User user = verificationToken.getUser();
        user.setActive(1);
        userService.saveRegisteredUser(user);
        modelAndView.setViewName("login");
        return modelAndView;
    }

}
