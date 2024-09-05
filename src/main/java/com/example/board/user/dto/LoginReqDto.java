package com.example.board.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class LoginReqDto {
    @NotBlank
    private String userId;

    @NotBlank
    private String password;
}
