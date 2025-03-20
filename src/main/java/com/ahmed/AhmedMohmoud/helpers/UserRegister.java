package com.ahmed.AhmedMohmoud.helpers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegister {
    @NotEmpty(message = "should not being empty")
    @NotBlank(message = "not mandatory")
    private String name;
    @NotEmpty(message = "should not being empty")
    @NotBlank(message = "not mandatory")
    @Email(message="Email is not formatted")
    private String email;
    @NotEmpty(message = "should not being empty")
    @NotBlank(message = "not mandatory")
    @Size(min=8 , message="Password should b characters long minimum")
    private String password;
}
