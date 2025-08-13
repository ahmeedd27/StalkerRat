package com.ahmed.AhmedMohmoud.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isRevoked;
    @ManyToOne
    @JoinColumn(nullable = false , name="user_id")
    private User user;
}
