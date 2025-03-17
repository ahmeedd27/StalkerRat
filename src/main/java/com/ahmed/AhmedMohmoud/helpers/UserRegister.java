package com.ahmed.AhmedMohmoud.helpers;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegister {
    private String name;
    private String email;
    @NotNull(message = "Password cannot be null")
    private String password;
}
