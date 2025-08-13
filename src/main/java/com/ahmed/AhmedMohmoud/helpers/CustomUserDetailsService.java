package com.ahmed.AhmedMohmoud.helpers;

import com.ahmed.AhmedMohmoud.dao.UserRepo;
import com.ahmed.AhmedMohmoud.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      return userRepo.findByEmail(username)
              .orElseThrow(() -> new UsernameNotFoundException("NOT FOUND THIS EMAIL"));

    }
}
