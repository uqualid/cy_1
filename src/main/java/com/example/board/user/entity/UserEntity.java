package com.example.board.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_num", nullable = false, updatable = false, unique = true)
    private Long ownNum;


    @Column(name = "user_id", nullable = false, updatable = false, unique = true)
    private String userId;

    @Column(name = "user_password", nullable = false)
    private String password;

    @Column(name = "user_email", nullable = false, unique = true)
    private String email;

    @Builder
    public UserEntity(String userId, String password, String email) {
        this.userId = userId;
        this.password = password;
        this.email = email;
    }
}
