package pl.hycom.login.user.controller;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserEditFormObject {

    @NotEmpty
    private String name;
    @NotEmpty
    private String lastName;
}
