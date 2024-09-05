package com.example.board.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.Id;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class UserAuthEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_num", nullable = false, updatable = false, unique = true)
    private Long ownNum;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;


    @Column(name = "logout", nullable = false)
    private Boolean logout= Boolean.FALSE;

    public void setLogout() {
        this.logout = true;
    }
}
