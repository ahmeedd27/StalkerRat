package com.ahmed.AhmedMohmoud.dao;

import com.ahmed.AhmedMohmoud.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<Token, Integer> {

    @Query("select t from Token t where t.user.id=:id")
     List<Token> findAllTokensByUserId(Integer id);

    @Query("select t from Token t where t.name=:jwt")
    Optional<Token> findTokenByName(String jwt);
}
