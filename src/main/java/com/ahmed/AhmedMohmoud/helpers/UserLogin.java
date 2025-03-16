package com.ahmed.AhmedMohmoud.helpers;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLogin {
    private String email;
    private String password;
}
