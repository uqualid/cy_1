package com.example.board.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResDto {
    private Long ownNum;
    private String userId;

    private String accessToken;
    private String accessExpireAt;

    private String refreshToken;
    private String refreshExpireAt;
}
