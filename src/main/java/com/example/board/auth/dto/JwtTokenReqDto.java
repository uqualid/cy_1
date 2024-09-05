package com.example.board.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtTokenReqDto {
    private String accessToken;
    private String refreshToken;
}
