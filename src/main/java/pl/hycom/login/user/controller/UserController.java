package pl.hycom.login.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.hycom.login.user.service.UserDTO;
import pl.hycom.login.user.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Controller
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/details")
    public String details(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("currentUser");
        UserDetailsFormObject userDetails = userService.findUserById(userId)
                .map(userService::getUserDetails)
                .orElseThrow(RuntimeException::new);
        model.addAttribute("userDetails", userDetails);
        return "users/user_details";
    }

    @GetMapping("/edit")
    public String getEditUser(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("currentUser");
        UserEditFormObject userEditFormObject = userService.findUserById(userId)
                .map(userService::getUserEdit)
                .orElseThrow(RuntimeException::new);
        model.addAttribute("userEditFormObject", userEditFormObject);
        return "users/user_edit";
    }

    @PostMapping("/edit")
    public String postEditUser(@ModelAttribute @Valid UserEditFormObject userEditFormObject,
                               HttpSession session, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "users/user_edit";
        }
        Integer userId = (Integer) session.getAttribute("currentUser");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setName(userEditFormObject.getName());
        userDTO.setLastName(userEditFormObject.getLastName());
        userService.editUser(userDTO);
        return "redirect:/details";
    }


}
