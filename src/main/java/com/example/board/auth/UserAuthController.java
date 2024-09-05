package com.example.board.auth;

import com.example.board.security.JwtTokenProvider;
import com.example.board.user.dto.LoginReqDto;
import com.example.board.user.dto.LoginResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


@Slf4j
@Tag(name = "JWT User Controller", description = "Token 발급 로그인/로그아웃")
@RequiredArgsConstructor
@RestController
@RequestMapping("/JwtTokenUse")
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Operation(summary = "Login with JWT Token", description = "Login(JWT 토큰 발행)")
    @PostMapping(value = "/login")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<LoginResDto> loginByUserIdAndPassword(
            @Parameter(required = true, name = "reqDto", description = "로그인 정보")
            @RequestBody LoginReqDto loginReqDto){

        LoginResDto responseDto = userAuthService.loginByUserIdAndPassword(loginReqDto);

        HttpHeaders httpheaders = new HttpHeaders();
        httpheaders.add("Authorization", "Bearer " + responseDto.getAccessToken());

        return new ResponseEntity<>(responseDto, httpheaders, HttpStatus.OK);
    }

    @Operation(summary = "Logout with JWT Token", description = "Logout(JWT 토큰)")
    @PostMapping(value = "/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization") String accessToken) {
        String token = accessToken.replace("Bearer ", "").trim();
        log.info("Logout request received with token: {}", token);

        if (!jwtTokenProvider.validateToken(token)) {
            log.error("Invalid or expired token: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String userId = jwtTokenProvider.getUserPk(token);
        log.info("User ID extracted from token: {}", userId);

        userAuthService.logout(userId);
        log.info("Logout successful for user ID: {}", userId);

        return ResponseEntity.ok("Logout Success");
    }

    @Operation(summary = "Refresh JWT Token")
    @PostMapping(value = "/refresh-token/{userId}")
    public ResponseEntity<LoginResDto> refreshToken(
            @Parameter(required = true, name = "userId", description = "사용자 아이디")
            @PathVariable String userId,
            @RequestHeader(value = "x-api-token") String accessToken,
            @RequestHeader(value = "x-refresh-token") String refreshToken) {

        LoginResDto responseDto = userAuthService.refreshToken(userId, accessToken, refreshToken);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
