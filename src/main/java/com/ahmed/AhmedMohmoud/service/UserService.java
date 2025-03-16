package com.ahmed.AhmedMohmoud.service;


import com.ahmed.AhmedMohmoud.dao.RoleRepo;
import com.ahmed.AhmedMohmoud.dao.TokenRepo;
import com.ahmed.AhmedMohmoud.dao.UserRepo;
import com.ahmed.AhmedMohmoud.entities.Role;
import com.ahmed.AhmedMohmoud.entities.Token;
import com.ahmed.AhmedMohmoud.entities.User;
import com.ahmed.AhmedMohmoud.helpers.UserLogin;
import com.ahmed.AhmedMohmoud.helpers.UserRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public void revokeAllTokens(User user){
        List<Token> validTokens=tokenRepo.findAllTokensByUserId(user.getId());
        validTokens.forEach(r ->
                r.setRevoked(true)
                );
        tokenRepo.saveAll(validTokens);
    }

    public ResponseEntity<String> saveUser(UserRegister user){
       Role r=roleRepo.findById(1).orElseThrow();
       User u= User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
               .roles(List.of(r))
                .build();
       String generatedToken=jwtService.generateToken(u);
       Token t=Token.builder()
               .name(generatedToken)
               .user(u)
               .isRevoked(false)
               .build();
       userRepo.save(u);
       tokenRepo.save(t);
       return ResponseEntity.ok(generatedToken);
    }

    public ResponseEntity<String> loginUser(UserLogin userLogin) {
        var authUser=authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                  userLogin.getEmail() ,
                  userLogin.getPassword()
          )
        );
        User user=(User) authUser.getPrincipal();
        Map<String , Object> claims=new HashMap<>();
        claims.put("fullName" , user.getName());
        revokeAllTokens(user);
        String generatedToken= jwtService.generateToken(claims , user);
         Token t=Token.builder()
                 .isRevoked(false)
                 .name(generatedToken)
                 .user(user)
                 .build();
         tokenRepo.save(t);
         return ResponseEntity.ok(generatedToken);
    }
}
