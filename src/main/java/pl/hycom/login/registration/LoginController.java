package pl.hycom.login.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import pl.hycom.login.mail.VerificationToken;
import pl.hycom.login.user.service.IUserService;
import pl.hycom.login.user.service.User;
import pl.hycom.login.user.service.UserService;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;

@Controller
public class LoginController {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final IUserService iUserService;
    private final MessageSource messageSource;

    @Autowired
    public LoginController(UserService userService,
                           ApplicationEventPublisher eventPublisher,
                           IUserService iUserService, MessageSource messageSource) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.iUserService = iUserService;
        this.messageSource = messageSource;
    }

    @GetMapping({"/", "/login"})
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/logout")
    public ModelAndView logout() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
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
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult, WebRequest webRequest) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            userService.saveRegisteredUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

            String appUrl = webRequest.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, Locale.getDefault(), appUrl));

        }
        return modelAndView;
    }

    @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(WebRequest webRequest, @RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView();

        Locale locale = webRequest.getLocale();
        VerificationToken verificationToken = iUserService.getVerificationToken(token);

        if (verificationToken == null) {
            String message = messageSource.getMessage("auth.message.invalidToken", null, locale);
            modelAndView.addObject("message", message);
            modelAndView.setViewName("redirect:/badUser.html?lang=" + Locale.getDefault());
            return modelAndView;
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = messageSource.getMessage("auth.message.expired", null, locale);
            modelAndView.addObject("message", messageValue);
            modelAndView.setViewName("redirect:/badUser.html?lang=" + Locale.getDefault());
            return modelAndView;
        }
        user.setEnabled(true);
        iUserService.saveRegisteredUser(user);
        modelAndView.setViewName("redirect:/login.html?lang=" + Locale.getDefault());
        return modelAndView;

    }

    @GetMapping("/admin/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
        modelAndView.setViewName("home");
        return modelAndView;
    }
}

