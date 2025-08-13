package com.ahmed.AhmedMohmoud.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;
    private boolean isFavourite;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sender_id" , nullable = true)
    @JsonBackReference("sentMessages")
    private User sender;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiver_id" , nullable = false)
    @JsonBackReference("receivedMessages")
    private User receiver;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedAt;
    @CreatedBy
    @Column(nullable = true, updatable = false)
    private Integer createdBy;
    @LastModifiedBy
    @Column(insertable = false)
    private Integer lastModifiedBy;
}
