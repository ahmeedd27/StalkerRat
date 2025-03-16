package com.ahmed.AhmedMohmoud.controllers;

import com.ahmed.AhmedMohmoud.helpers.UserRegister;
import com.ahmed.AhmedMohmoud.helpers.UserLogin;
import com.ahmed.AhmedMohmoud.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<String> signUpUser(
            @RequestBody UserRegister user
    ) {
        return userService.saveUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> logInUser(
            @RequestBody UserLogin userLogin
    ){
        return userService.loginUser(userLogin);
    }
    @GetMapping("/hello")
    public  String sec(){
        return "hello from the secured endpoint";
    }

}
